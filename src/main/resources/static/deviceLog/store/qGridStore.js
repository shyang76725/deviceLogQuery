Ext.define('deviceLog.store.qGridStore', {
	id:'qGridStore',
	extend : 'Ext.data.Store',
    autoLoad: false,
    proxy: {
        type: 'memory',   // 使用 rest 來與後端 server 溝通
        reader: {
            type: 'json'
        }
    },
	model : 'deviceLog.model.qGridModel'
});