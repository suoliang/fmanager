/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var topic = topic || {};
(function() {
	var cache = function(face, id, response, fun) {
		if (util.isNull(fun)) {
			$.data(face, '' + id, {
				data : response.data,
				message : response.message
			});
		} else {
			var response = $.data(face, '' + id);
			if (!$.isUndefined(response)) {
				fun.success(response.data, response.message);
				return true;
			}
			return false;
		}
	}

	topic.action = {
		topicArticle : std.u("/topic/topicArticle.htm"),
	};

	topic.topicArticle = function(params, fun) {
		assert.isFun(fun.success, "success", "topic.topicArticle");
		ajaxCommFunText(topic.action.topicArticle, params, function(response) {
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
	
	topic.topicExportAll = function(params) {
		ajaxCommFun(topic.action.topicArticle2, params);
	};

})();