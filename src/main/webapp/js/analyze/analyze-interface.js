/**
 * 首页异步接口
 * @author GUOQIANG
 */
var analyze = analyze || {};
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
	};
	
	analyze.action = {
		getSituationChart : std.u("/analyze/generalize/situation.htm"),
		getTendencyChart : std.u("/analyze/generalize/tendency.htm"),
		getStatCustTopicList : std.u("/analyze/generalize/statCustTopic.htm"),
		getStatWebsiteList : std.u("/analyze/generalize/statWebsite.htm"),
		listTopic : std.u("/analyze/topic/list.htm"),
		listTopicOptionInfo : std.u("/analyze/topic/listTopicOptionInfo.htm"),
		searchTopic : std.u("/analyze/topic/getAllSearchTopic.htm")
	};

	analyze.listTopic = function(parentId, fun) {
		assert.exists(parentId, "parentId", "analyze.listTopic");
		assert.isFun(fun.success, "success", "analyze.listTopic");

		if (cache(analyze.listTopic, parentId, {}, fun)) {
			return;
		}
		ajaxCommFun(analyze.action.listTopic, "parentId=" + parentId, function(response) {
			if (response.type == dict.action.suc) {
				cache(analyze.listTopic, parentId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};
	
	analyze.listTopicOptionInfo = function(ids, fun) {
		assert.exists(ids, "ids", "analyze.listTopicOptionInfo");
		assert.isFun(fun.success, "success", "analyze.listTopicOptionInfo");

		ajaxCommFun(analyze.action.listTopicOptionInfo, {
			ids : util.joinCollection(ids, ",")
		}, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};
	
	analyze.searchTopic = function(params, fun) {
		assert.exists(params, "params", "analyze.searchTopic");
		assert.isFun(fun.success, "success", "analyze.searchTopic");

		if (cache(analyze.searchTopic, params, null, fun)) {
			return;
		}
		ajaxCommFun(analyze.action.searchTopic, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
				cache(analyze.searchTopic, params, response);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};
	
	analyze.getSituationChart = function(params, fun) {
		assert.exists(params, "params", "analyze.getSituationChart");
		assert.isFun(fun.success, "success", "analyze.getSituationChart");

		ajaxCommFunText(analyze.action.getSituationChart, params, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	analyze.getTendencyChart = function(params, fun) {
		assert.exists(params, "params", "analyze.getTendencyChart");
		assert.isFun(fun.success, "success", "analyze.getTendencyChart");

		ajaxCommFunText(analyze.action.getTendencyChart, params, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	analyze.getStatCustTopicList = function(params, fun) {
		assert.exists(params, "params", "analyze.getStatCustTopicList");
		assert.isFun(fun.success, "success", "analyze.getStatCustTopicList");

		ajaxCommFunText(analyze.action.getStatCustTopicList, params, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	analyze.getStatWebsiteList = function(params, fun) {
		assert.exists(params, "params", "analyze.getStatWebsiteList");
		assert.isFun(fun.success, "success", "analyze.getStatWebsiteList");

		ajaxCommFunText(analyze.action.getStatWebsiteList, params, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

})();