package cn.incontent.afc.client.query.querycond;

import cn.incontent.afc.client.utils.MsgUtils;

/**
 *@author Val.(Valentine Vincent) E-mail:valer@126.com
 *@version 1.0
 *@date 2011-10-22
 *Instruction : 
 **/
public class ExactAspectCond extends AbstractQueryCondition {
	private static final String tpl = "EXACTASPECT:\"{0}\"";
	
	public ExactAspectCond(String aspectName) {
		super();
		if (aspectName == null || aspectName.trim().equals("")) {
			return;
		}
		
		query.append(MsgUtils.getString(tpl, new String[] {aspectName}));
	}
}
