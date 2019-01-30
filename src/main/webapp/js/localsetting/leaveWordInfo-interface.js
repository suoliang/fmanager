/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var MessageBoard = MessageBoard || {};
(function() {

	MessageBoard.action = {
		queryBriefArticle : std.u("/setting/leaveWord/queryList.htm")
	};
	
	MessageBoard.queryBriefArticle = function(params, fun) {
		assert.exists(params, "params", "report.queryBriefArticle");
		assert.isFun(fun.success, "success", "report.queryBriefArticle");

		ajaxCommFunText(MessageBoard.action.queryBriefArticle, params, function(response) {
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