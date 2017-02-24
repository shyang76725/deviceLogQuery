Ext.define('deviceLog.view.Viewport', {
	extend : 'Ext.container.Viewport',
	requires : [ 'Ext.form.Panel' ],
	layout : 'absolute',
	items : [ {
		// height:120,
		// width:250,
		// x:100,
		// y:50,
		title : '歡迎使用',
		xtype : 'form',
		labelWidth : 50,
		frame : true,
		bodyStyle : 'padding: 10px 10px 10px 10px;',
		labelWidth : 50,
		defaults : {
			anchor : '95%',
			allowBlank : false,
			msgTarget : 'side'
		},
		items : [ {
			xtype : 'fieldset',
			title : '查詢條件',
			autoHeight : true,
			items : [ {
				xtype : 'datefield',
				submitFormat:'ymd', 
				fieldLabel : '查詢條件-日期',
				name : 'qDate',
				id : 'qDate',
				width:200,
				value : new Date()
			}, {
				fieldLabel : '查詢條件-帳號',
				name : 'qId',
				id : 'qId',
				xtype : 'textfield',
				anchor : '60%'
			} ]
		}, {
			xtype : 'grid',
			id : 'fGrid',
			title : '檔案清單',
			height : 200,
			selType : "rowmodel",
			plugins : [ Ext.create("Ext.grid.plugin.CellEditing", {
				clicksToEdit : 1,
				id : 'fGridCE',
				listeners : {
					beforeedit : function(editor, e, eOpts) {
							return false;
					}
				}
			}) ],
			columns : [ 
				{header : '檔案路徑',	dataIndex : 'Q001',	width : 200,hidden:true}, 
				{header : '年',dataIndex : 'Q002',width : 50}, 
				{header : '月',dataIndex : 'Q003',width : 50}, 
				{header : '日',dataIndex : 'Q004',width : 50}, 
				{header : '檔名',dataIndex : 'Q005',width : 100}, 
				{header : 'MAC',dataIndex : 'Q006',width : 100}, 
				{header : 'USERID',dataIndex : 'Q007',width : 100} 
			],
			store : 'qGridStore'
		} ],
		dockedItems : [ {
			xtype : 'toolbar',
			dock : 'top',
			layout : {
				pack : 'left'
			},
			items : [ {
				xtype : 'button',
				id : 'doQuery',
				text : '查詢',
				border : 1,
				iconCls : 'icon-search'
			} ]
		} ]
	} ]
});
