Ext.define('deviceLog.controller.deviceLogControl', {
    extend: 'Ext.app.Controller',
    views:[],
    stores:[],
    models:[],
    init: function (application) {
        this.control({
            '#doQuery': {
                click: this.doQuery
            },
            '#fGrid': {
            	itemdblclick: this.doDownload
            }
        });
    },
    doQuery: function(){ 
    	waitMsg();
    	var obj = new Object();
    	obj.ID=Ext.getCmp('qId').getValue();
    	obj.Year=Ext.Date.format(new Date(Ext.getCmp('qDate').getValue()),'Y');
    	obj.Month=Ext.Date.format(new Date(Ext.getCmp('qDate').getValue()),'m');
    	obj.Date=Ext.Date.format(new Date(Ext.getCmp('qDate').getValue()),'d');
		Ext.Ajax.request({
        	waitMsg: 'Please wait...',
        	url: "/getList",
        	method:"POST",
        	params:{
        		data : Ext.encode(obj)
        	},
        	error : function(xhr) {
        		Ext.MessageBox.alert('Ajax error', 'Ajax request 發生錯誤' + xhr);
        	},
        	success : function(response){
        		msgHide();
        		response=Ext.decode(response.responseText);
        		if (response.result){
        			Ext.getCmp('fGrid').getStore().loadData(response.data);
        		}
        	}
        });
    },
    doDownload: function(dv, record, item, index, e) {
    	//
    	var path = record.data.Q001;
    	waitMsg();
		Ext.Ajax.request({
        	waitMsg: 'Please wait...',
        	url: "/downLoadFilePrepare/"+path,
        	method:"GET",
        	params:{
        	},
        	error : function(xhr) {
        		Ext.MessageBox.alert('Ajax error', 'Ajax request 發生錯誤' + xhr);
        	},
        	success : function(response){
        		msgHide();
            	var link = document.createElement("a");
                link.download = "";
                link.href = "/downLoadFile/"+path;
                link.click();
        	}
        });
    	
    }
});
function waitMsg(){
	Ext.MessageBox.wait("請耐心等候!", "等待訊息",{text: "處理中 ..."}); 
}
function msgHide(){
	Ext.MessageBox.hide(); 
}