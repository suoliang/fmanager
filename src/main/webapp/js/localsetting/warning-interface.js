/**
 * 预警异步接口
 * @author GUOQIANG
 */
var warning = warning || {};
(function() {

	warning.action = {
		queryRule : std.u('/setting/warning/rule/list.htm'),
		saveRule : std.u('/setting/warning/rule/save.htm'),
		batchSaveRule : std.u('/setting/warning/rule/batchsave.htm'),
		findRule : std.u('/setting/warning/rule/find.htm'),
		changeRuleStatus : std.u('/setting/warning/rule/changeRuleStatus.htm')
	};

	warning.queryRule = function(params, fun) {
		assert.isFun(fun.success, "success", "warning.queryRule");

		ajaxCommFunText(warning.action.queryRule, params, function(response) {
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

	warning.saveRule = function(params, fun) {
		assert.isFun(fun.success, "success", "warning.saveRule");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(warning.action.saveRule, params, function(response) {

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

	warning.batchSaveRule = function(params, fun) {
		assert.isFun(fun.success, "success", "warning.batchSaveRule");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(warning.action.batchSaveRule, params, function(response) {

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

	warning.findRule = function(params, fun) {
		assert.isFun(fun.success, "success", "warning.findRule");

		ajaxCommFun(warning.action.findRule, params, function(response) {
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

	warning.changeRuleStatus = function(params, fun) {
		assert.isFun(fun.success, "success", "warning.changeRuleStatus");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(warning.action.changeRuleStatus, params, function(response) {

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

})();