/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var MessageBoard = MessageBoard || {};
(function() {

	MessageBoard.action = {
			similarQueryInfo : std.u("/focusing/queryList.htm")
	};
	
	MessageBoard.similarQueryInfo = function(params, fun) {
		assert.exists(params, "params", "focusing.similarQueryInfo");
		assert.isFun(fun.success, "success", "focusing.similarQueryInfo");
		ajaxCommFunText(MessageBoard.action.similarQueryInfo, params, function(response) {
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