/**
 * 文件弹框异步接口
 * @author LIUJUNWU
 */
var exportIF = exportIF || {};
(function() {

	exportIF.action = {
		queryTask : std.u("/export/searchTask.htm"),
		deleteTaskFile : std.u("/export/deleteTaskFile.htm")
	};

	exportIF.listTaskData = function(params, fun) {
		assert.exists(params, "params", "exportIF.listTaskData");
		assert.isFun(fun.success, "success", "exportIF.listTaskData");

		var index = layer.load('正在加载数据...');

		ajaxCommFun(exportIF.action.queryTask, params, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};
	
	exportIF.deleteTaskFile = function(params) {
		assert.exists(params, "params", "exportIF.deleteTaskFile");
		
		ajaxCommFun(exportIF.action.deleteTaskFile, params);
	};
})();