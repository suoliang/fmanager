/**
 * 监测设置异步接口
 * @author LIUJUNWU
 */
var monitor = monitor || {};
(function() {

	monitor.action = {
		queryRule : std.u('/setting/monitor/keyword/list.htm'),
		updateRule : std.u('/setting/monitor/keyword/update.htm'),
		deleteRule : std.u('/setting/monitor/keyword/delete.htm'),
		findMonitorKeyword : std.u('/setting/monitor/keyword/find.htm'),
	};

	monitor.queryMonitorRule = function(params, fun) {
		assert.isFun(fun.success, "success", "monitor.queryMonitorRule");

		ajaxCommFunText(monitor.action.queryRule, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	monitor.updateRule = function(params, fun) {
		assert.isFun(fun.success, "success", "monitor.updateRule");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(monitor.action.updateRule, params, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	monitor.deleteRule = function(params, fun) {
		assert.isFun(fun.success, "success", "monitor.deleteRule");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(monitor.action.deleteRule, params, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};
	
	monitor.findMonitorKeyword = function(params, fun) {
		assert.isFun(fun.success, "success", "monitor.findMonitorKeyword");

		ajaxCommFun(monitor.action.findMonitorKeyword, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};
})();