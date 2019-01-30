/**
 * 首页异步接口
 * @author SUOLIANG
 */
var manualReport = manualReport || {};
(function() {

	manualReport.action = {
		queryManual : std.u("/report/manual/queryList.htm")
	};
	
	manualReport.queryManual = function(params, fun) {
		ajaxCommFunText(manualReport.action.queryManual, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

})();