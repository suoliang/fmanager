/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var siteAudit = siteAudit || {};
(function() {

	siteAudit.action = {
		querysiteAudit : std.u("/setting/siteAudit/queryList.htm")
	};
	
	siteAudit.querySiteAudit = function(params, fun) {
		ajaxCommFunText(siteAudit.action.querysiteAudit, params, function(response) {
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