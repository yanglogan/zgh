/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Alfresco. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.repo.search.impl;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.DocumentNavigator;
import org.alfresco.repo.search.NodeServiceXPath;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.XPathException;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.jaxen.JaxenException;

/**
 * Helper class that walks a node hierarchy.
 * <p>
 * Some searcher methods on {@link org.alfresco.service.cmr.search.SearchService} can use this
 * directly as its only dependencies are {@link org.alfresco.service.cmr.repository.NodeService},
 * {@link org.alfresco.service.cmr.dictionary.DictionaryService} and a
 * {@link org.alfresco.service.cmr.search.SearchService}
 *
 * @author Derek Hulley
 *		jcr search patch!
 */
public class NodeSearcher {
	
	private NodeService nodeService;

	private DictionaryService dictionaryService;

	private SearchService searchService;

	public NodeSearcher(NodeService nodeService, DictionaryService dictionaryService,
			SearchService searchService) {
		this.nodeService = nodeService;
		this.dictionaryService = dictionaryService;
		this.searchService = searchService;
	}

	/**
	 * @see NodeServiceXPath
	 */
	@SuppressWarnings("rawtypes")
	public List<NodeRef> selectNodes(NodeRef contextNodeRef, String xpathIn,
			QueryParameterDefinition[] paramDefs, NamespacePrefixResolver namespacePrefixResolver,
			boolean followAllParentLinks, String language) {
		try {
			String xpath = xpathIn;
			boolean useJCRXPath = language.equalsIgnoreCase(SearchService.LANGUAGE_JCR_XPATH);

			List<AttributeOrder> order = null;

			// replace element
			if (useJCRXPath) {
				order = new ArrayList<AttributeOrder>();
				// We do not allow variable substitution with this pattern
				xpath =
						xpath.replaceAll(
								"element\\(\\s*(\\*|\\w*:\\w*)\\s*,\\s*(\\*|\\w*:\\w*)\\s*\\)",
								"$1[subtypeOf(\"$2\")]");
				String split[] = xpath.split("order\\s*by\\s*", 2);
				xpath = split[0];

				if (split.length > 1 && split[1].length() > 0) {
					String clauses[] = split[1].split("\\s,\\s");

					for (String clause : clauses) {
						if (clause.startsWith("@")) {
							String attribute =
									clause.replaceFirst("@(\\p{Alpha}[\\w:]*)(?:\\s+(.*))?", "$1");
							String sort =
									clause.replaceFirst("@(\\p{Alpha}[\\w:]*)(?:\\s+(.*))?", "$2");

							if (sort.length() == 0) {
								sort = "ascending";
							}

							QName attributeQName =
									QName.createQName(attribute, namespacePrefixResolver);
							order.add(new AttributeOrder(attributeQName, attribute, sort
									.equalsIgnoreCase("ascending")));
						} else if (clause.startsWith("jcr:score")) {
							// ignore jcr:score ordering
						} else {
							throw new IllegalArgumentException("Malformed order by expression "
									+ split[1]);
						}
					}
				}
			}

			DocumentNavigator documentNavigator =
					new DocumentNavigator(dictionaryService, nodeService, searchService,
							namespacePrefixResolver, followAllParentLinks, useJCRXPath);
			NodeServiceXPath nsXPath = new NodeServiceXPath(xpath, documentNavigator, paramDefs);
			for (String prefix : namespacePrefixResolver.getPrefixes()) {
				nsXPath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
			}
			List list = nsXPath.selectNodes(nodeService.getPrimaryParent(contextNodeRef));
			HashSet<NodeRef> unique = new HashSet<NodeRef>(list.size());
			for (Object o : list) {
				if (o instanceof ChildAssociationRef) {
					unique.add(((ChildAssociationRef) o).getChildRef());
				} else if (o instanceof DocumentNavigator.Property) {
					unique.add(((DocumentNavigator.Property) o).parent);
				} else {
					throw new XPathException("Xpath expression must only select nodes");
				}
			}

			List<NodeRef> answer = new ArrayList<NodeRef>(unique.size());
			answer.addAll(unique);
			if (order != null) {
				orderNodes(answer, order);
				for (NodeRef node : answer) {
					StringBuffer buffer = new StringBuffer();
					for (AttributeOrder attOrd : order) {
						buffer.append(" ").append(nodeService.getProperty(node, attOrd.attribute));
					}
				}
			}
			return answer;
		} catch (JaxenException e) {
			throw new XPathException("Error executing xpath: \n" + "   xpath: " + xpathIn, e);
		}
	}

	private void orderNodes(List<NodeRef> answer, List<AttributeOrder> order) {
		Collections.sort(answer, new NodeRefComparator(nodeService, order));
	}

	static class NodeRefComparator implements Comparator<NodeRef> {
		List<AttributeOrder> order;
		NodeService nodeService;

		Collator collatorChina = Collator.getInstance(java.util.Locale.CHINA);

		NodeRefComparator(NodeService nodeService, List<AttributeOrder> order) {
			this.nodeService = nodeService;
			this.order = order;
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		public int compare(NodeRef n1, NodeRef n2) {
			
			for (AttributeOrder attributeOrder : order) {
				Serializable o1 = nodeService.getProperty(n1, attributeOrder.attribute);
				Serializable o2 = nodeService.getProperty(n2, attributeOrder.attribute);
				
				if ("size".equals(attributeOrder.attr)) {
					o1 = nodeService.getProperty(n1, ContentModel.PROP_CONTENT);
					o2 = nodeService.getProperty(n2, ContentModel.PROP_CONTENT);
					
					if (o1 != null) {
						o1 = getSize(o1.toString());
					}
					if (o2 != null) {
						o2 = getSize(o2.toString());
					}
				}

				if (o1 == null) {
					if (o2 == null) {
						continue;
					} else {
						return attributeOrder.ascending ? -1 : 1;
					}
				} else {
					if (o2 == null) {
						return attributeOrder.ascending ? 1 : -1;
					} else {
						if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
							if (!(o1 instanceof String) && !(o2 instanceof String)) {
								return (attributeOrder.ascending ? 1 : -1) * ((Comparable ) o1).compareTo((Comparable ) o2);
							}
							return (attributeOrder.ascending ? 1 : -1)
									* (collatorChina.compare(o1, o2));
						} else {
							continue;
						}
					}
				}

			}
			return 0;
		}
	}

	/**
	 * @see NodeServiceXPath
	 */
	@SuppressWarnings("rawtypes")
	public List<Serializable> selectProperties(NodeRef contextNodeRef, String xpath,
			QueryParameterDefinition[] paramDefs, NamespacePrefixResolver namespacePrefixResolver,
			boolean followAllParentLinks, String language) {
		try {
			boolean useJCRXPath = language.equalsIgnoreCase(SearchService.LANGUAGE_JCR_XPATH);

			DocumentNavigator documentNavigator =
					new DocumentNavigator(dictionaryService, nodeService, searchService,
							namespacePrefixResolver, followAllParentLinks, useJCRXPath);
			NodeServiceXPath nsXPath = new NodeServiceXPath(xpath, documentNavigator, paramDefs);
			for (String prefix : namespacePrefixResolver.getPrefixes()) {
				nsXPath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
			}
			List list = nsXPath.selectNodes(nodeService.getPrimaryParent(contextNodeRef));
			List<Serializable> answer = new ArrayList<Serializable>(list.size());
			for (Object o : list) {
				if (!(o instanceof DocumentNavigator.Property)) {
					throw new XPathException("Xpath expression must only select nodes");
				}
				answer.add(((DocumentNavigator.Property) o).value);
			}
			return answer;
		} catch (JaxenException e) {
			throw new XPathException("Error executing xpath", e);
		}
	}

	private static class AttributeOrder {
		QName attribute;
		String attr;

		boolean ascending;

		AttributeOrder(QName attribute, String attr, boolean ascending) {
			this.attribute = attribute;
			this.attr = attr;
			this.ascending = ascending;
		}
	}
	
	private static long getSize(String contentProp) {
		
		if (contentProp == null) {
			return 0;
		}
		
		int start = contentProp.indexOf("size=") + 5;
		int end = contentProp.indexOf("|", start);
		
		String length = contentProp.substring(start, end);
		
		return new Long(length);
	}
	
}
