/**
 * 首页异步接口
 * @author GUOQIANG
 */
var analyze = analyze || {};
(function() {

	analyze.action = {
		getSituationChart : std.u("/analyze/offline/generalize/situation.htm"),
		getTendencyChart : std.u("/analyze/offline/generalize/tendency.htm"),
		getStatCustTopicList : std.u("/analyze/offline/generalize/statCustTopic.htm"),
		getStatWebsiteList : std.u("/analyze/offline/generalize/statWebsite.htm")
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