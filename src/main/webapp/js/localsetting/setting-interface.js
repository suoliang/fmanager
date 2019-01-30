/**
 * 首页异步接口
 * @author GUOQIANG
 */
var setting = setting || {};
(function() {

	setting.action = {
		queryBoard : std.u("/setting/homepage/query.htm"),
		findCustTopic : std.u("/custtopic/findCustTopic.htm")
	};

	setting.queryBoard = function(params, fun) {
		assert.isFun(fun.success, "success", "setting.queryBoard");

		ajaxCommFunText(setting.action.queryBoard, params, function(response) {
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

	setting.findCustTopic = function(params, fun) {
		assert.isFun(fun.success, "success", "setting.findCustTopic");

		ajaxCommFun(setting.action.findCustTopic, params, function(response) {
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