/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var recommend = recommend || {};
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

	recommend.action = {
			recommendArticle : std.u("/recommend/recommendArticle.htm"),
			recommendColumn : std.u("/recommend/recommendColumn.htm")
	};

	recommend.recommendArticle = function(params, fun) {
		assert.isFun(fun.success, "success", "recommend.recommendArticle");
		ajaxCommFunText(recommend.action.recommendArticle, params, function(response) {
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
	

	
	recommend.recommendColumn = function(params, fun) {
		assert.isFun(fun.success, "success", "recommend.recommendColumn");
		ajaxCommFun(recommend.action.recommendColumn, params, function(response) {
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
