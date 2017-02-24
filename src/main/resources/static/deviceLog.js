Ext.application({
    name: 'deviceLog',
    appFolder: 'deviceLog',
    autoCreateViewport: true,
	models : ['qGridModel'],
	stores : ['qGridStore'],
	views : ['Viewport'],
	controllers : ['deviceLogControl']
});

