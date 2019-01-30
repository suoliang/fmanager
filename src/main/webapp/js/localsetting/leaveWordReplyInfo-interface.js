/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var MessageBoardReply = MessageBoardReply || {};
(function() {

	MessageBoardReply.action = {
		queryBriefMessageboard : std.u("/setting/leaveWordReply/queryList.htm")
	};
	
	MessageBoardReply.queryBriefMessageboard = function(params, fun) {
		ajaxCommFunText(MessageBoardReply.action.queryBriefMessageboard, params, function(response) {
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