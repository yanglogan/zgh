function() {
	
	
	if (typeof FileExplorer == 'undefined') {
        Utils.importCSS(['static/ext/fileexplorer/theme.css']);
        Utils.importJS(['static/ext/fileexplorer/fileexplorer.js', 'static/ext/fileexplorer/i18n/lang-' + localeString + '.js']);
    }

    FileExplorer.currentUserName = userLoginId;
    FileExplorer.thumbnailRootPath = 'static/images/thumbnail/';
    FileExplorer.iconRootPath = 'static/images/filetypes/';

//TODO add columns
    var tabs = Ext.create('Ext.tab.Panel', {
    	width: '100%',
	    height: '100%',
	    activeTab: 0,
	    tabPosition : 'top',
	    bodyPadding: 10,
	    cls : '.x-tab-bar-strip',
//	    headerCls : 'header-bg',
        items: [
                {
 	               title: '关键字检索',  
 	               xtype: 'form', 
 	               layout : {
 	            	 type : 'vbox',
 	            	 align : 'center',
 	               },
 	               defaults : {
// 	            	 padding : '0, 20, 0, 20',  
 	            	 width : '80%',
 	            	
 	               },
 	               items: [
                        {
                     	 xtype : 'label',
                     	 text : '项目代码：',
                        },
 	                   {  
 	                       xtype: 'textfield',  
 	                   },
 	                   {
                     	 xtype : 'label',
                     	 text : '文件源码：',
                        },
 	                   {  
 	                       xtype: 'textfield',  
 	                   },
 	                   {
 	                    	 xtype : 'label',
 	                    	 text : '文档名称：',
 	                   },
 	                   {  
 	                       xtype: 'textfield',  
 	                   },
 	                   {
                     	 xtype : 'label',
                     	 text : '中文标题：',
 	                   },
 	                   {  
 	                       xtype: 'textfield',  
 	                   }, 
 	                   {
 	                    	 xtype : 'label',
 	                    	 text : '英文标题：',
   	                   },
 	                   {  
 	                       xtype: 'textfield',  
 	                   }, 
 	                   {
                     	 xtype : 'label',
                     	 text : '部分通用：',
 	                   },
 	                   {  
 	                       xtype: 'textfield',  
 	                   }, 
 	                   {
 	                    	 xtype : 'label',
 	                    	 text : '要根据属：',
   	                   },
 	                   {  
 	                       xtype: 'textfield',  
 	                   }
 	               ],  
 	               buttons: [{
 	            	   btnType : 'info',
 	                   text : '搜索',
 	                   width : '100%',
 	               }]  
 	           
                },
                {
	               title: '属性检索',  
	               xtype: 'form', 
	               layout : {
	 	            	 type : 'vbox',
	 	            	 align : 'center',
	 	               },
	               defaults : {
//	            	 padding : '0, 20, 0, 20',  
	            	 width : '80%',
	               },
	               items: [
                      {
                   	 xtype : 'label',
                   	 text : '属性1：',
                      },
	                   {  
	                       xtype: 'textfield',  
	                   },
	                   {
                   	 xtype : 'label',
                   	 text : '属性2：',
                      },
	                   {  
	                       xtype: 'textfield',  
	                   },
	                   {
	                    	 xtype : 'label',
	                    	 text : '属性3：',
	                   },
	                   {  
	                       xtype: 'textfield',  
	                   },
	                   {
                   	 xtype : 'label',
                   	 text : '属性4：',
	                   },
	                   {  
	                       xtype: 'textfield',  
	                   }, 
	                   {
	                    	 xtype : 'label',
	                    	 text : '属性5：',
 	                   },
	                   {  
	                       xtype: 'textfield',  
	                   }, 
	                   {
                   	 xtype : 'label',
                   	 text : '属性6：',
	                   },
	                   {  
	                       xtype: 'textfield',  
	                   }, 
	                   {
	                    	 xtype : 'label',
	                    	 text : '属性7：',
 	                   },
	                   {  
	                       xtype: 'textfield',  
	                   }
	               ],  
	               buttons: [{
	            	   btnType : 'info',
	                   text : '搜索',
	                   width : '100%',
	               }]  
	           
              
                }
            ]
        });
    
    var searchPanel = Ext.create('Ext.panel.Panel', {
    border : false,
    region : 'west',
    bodyPadding : 0,
	width : '20%',
	height : '100%',
	items:[
	       tabs,
           {  
               xtype: 'form',  
               defaults : {
	            	 padding : '0 20 0 20', 
	            	 margin : '0 0 5 0',
	            	 width : '100%',
	               },
               items: [  
                       {
                    	 xtype : 'label',
                    	 text : '搜索选项：',
                       },
                   {  
                       xtype: 'combo',  
                       width : 197,
                       emptyText : '----请选择----',
                       store : Ext.create('Ext.data.Store',{
                    	   fields : ['id','name'],
                    	   data : [
                	           {'id':1,'name':'XX1'},
                	           {'id':2,'name':'XX2'},
                	           {'id':3,'name':'XX3'},
            	           ],
                       }),
                       displayField : 'name',
                   },{
                	   xtype : 'toolbar',
                	   layout : {
//                		 type :  'hbox',
                		 align : 'center',
                	   },
                	   items : [{
                		   btnType : 'info',
                		   text : '保存',
                		   margin : '0 0 0 40',
                	   },'->',{
                		   btnType : 'info',
                		   text : '取消',
                		   margin : '0 60 0 0',
                	   }],
                   } 
               ],  
           },{
        	   xtype : 'toolbar',
        	   defaults : {
        		 margin : '0 0 0 10',
        	   },
        	   items : [{
        		   btnType : 'success',
	        	   text : '导出搜索结果',
	        	   width : '90%',
        	   }],
    	}],
});
    
    var store = Ext.create('Ext.data.Store', {
        model : 'OBJECT',
        remoteSort : true,
        proxy : {
            type : 'ajax',
            reader : {
                type : 'json',
                root : 'results',
                totalProperty : 'total'
            },
            url : Utils.getCDAUrl('DocumentLibrary', 'getContents')
        },
        sorters : [{
            property : 'cm:name',
            direction : 'ASC'
        }]
    });
    
    var objList = Ext.create('FileExplorer.ObjList', {
        region : 'center',
//        i18nFunc : msg,
//        actionProvider : actionProvider,
//        actionExecutor : actionExecutor,
        listeners : {
            selectionchange : function(recs) {
                //console.log(recs);
            }
        },
        viewConfigs : {
            detailed : {
                columns : [{
                    xtype : 'fethumbnailcolumn'
                }, {
                    xtype : 'fedetailcolumn',
                    flex : 1
                }],
            },
            table : {
                columns : [{
                    xtype : 'feiconcolumn'
                }, {
                    width : 200,
                    xtype : 'fenamecolumn',
                    dataIndex : 'cm:name',
                    i18nkey : 'name'
                }, {
                    width : 200,
                    xtype : 'fedisplaycolumn',
                    dataIndex : 'cm:title',
                    i18nkey : 'title'
                }, {
                    width : 100,
                    xtype : 'feusercolumn',
                    dataIndex : 'cm:creator',
                    i18nkey : 'createdby'
                }, {
                    width : 155,
                    xtype : 'fedatetimecolumn',
                    dataIndex : 'cm:modified',
                    i18nkey : 'datemodified'
                }, {
                    width : 80,
                    xtype : 'fecolumn',
                    dataIndex : 'edm:state',
                    i18nkey : 'status'
                }, {
                    width : 80,
                    xtype : 'fesizecolumn',
                    i18nkey : 'size'
                }, /*{
                    xtype : 'feactioncolumn',
                    i18nkey : 'operation'
                }*/]
            }
        },
        store : store,
    });

    var inited = false;
    return {
        xtype : 'panel',
        border : false,
        layout : 'border',
        bodyBorder : false,
       // autoScroll:true,
        bodyStyle : {
            background : 'transparent'
        },
        listeners : {/*
            viewShown : function(signal) {
                if (!inited) {
                    inited = true;
                    return;
                }

                if (signal.reloadTree) {
                    var node = tree.getCurrentNode();
                    tree.store.reload({
                        node : node,
                        callback : function() {
                            node.expand();
                        }
                    });
                }
                if (signal.reloadGrid) {
                    objectList.getDockedItems()[2].moveFirst();
                }
            }
        */
        	viewShown : function() {
                 store.reload();
             }
        },
       items:[searchPanel, objList],
    };

}
