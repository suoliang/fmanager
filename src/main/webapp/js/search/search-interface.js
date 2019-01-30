/**
 * 首页异步接口
 * @author LIUJUNWU
 */
var search = search || {};
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

	search.action = {
		saveFilter : std.u("/search/saveCondition.htm"),
		deleteFilter : std.u("/search/deleteCondition.htm"),
		getFilter : std.u("/search/findCondition.htm"),

		searchArticle : std.u("/search/searchArticle.htm"),
		findArticle : std.u("/search/findArticle.htm"),

		listTopic : std.u("/search/topic/list.htm"),
		listTopicOptionInfo : std.u("/search/topic/listTopicOptionInfo.htm"),

		listIndustry : std.u("/search/industry/list.htm"),
		listIndustryOptionInfo : std.u("/search/industry/listIndustryOptionInfo.htm"),

		listArea : std.u("/search/area/list.htm"),
		listAreaOptionInfo : std.u("/search/area/listAreaOptionInfo.htm"),

		listSite : std.u("/search/site/list.htm"),
		listSiteByCategory : std.u("/search/site/listSiteByCategory.htm"),
		listSiteByMediaType : std.u("/search/site/listSiteByMediaType.htm")
	};

	search.saveFilter = function(params, fun) {
		assert.isFun(fun.success, "success", "search.saveFilter");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(search.action.saveFilter, params, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);

			} else if (response.type == dict.action.question) {
				var index2 = layer.confirm("过滤器已存在，是否强制更新？", function() {//yes
					search.saveFilter(util.isNotBlank(params) ? params + "&force=true" : params, fun);
					layer.close(index2);
				}, function() {//no
					fun.success(response.data, response.message);
				});

			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	search.deleteFilter = function(id, fun) {
		assert.exists(id, "id", "search.deleteFilter");
		assert.isFun(fun.success, "success", "search.deleteFilter");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(search.action.deleteFilter, "id=" + id, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(id, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	search.getFilter = function(id, fun) {
		assert.exists(id, "id", "search.getFilter");
		assert.isFun(fun.success, "success", "search.getFilter");

		ajaxCommFun(search.action.getFilter, "id=" + id, function(response) {
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

	search.searchArticle = function(params, fun) {
		assert.isFun(fun.success, "success", "search.searchArticle");
		ajaxCommFunText(search.action.searchArticle, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	search.findArticle = function(guid, fun) {
		assert.exists(guid, "guid", "search.findArticle");
		assert.isFun(fun.success, "success", "search.searchArticle");

		ajaxCommFunText(search.action.findArticle, "guid=" + guid, function(response) {
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

	search.listTopic = function(parentId, fun) {
		assert.exists(parentId, "parentId", "search.listTopic");
		assert.isFun(fun.success, "success", "search.listTopic");

		if (cache(search.listTopic, parentId, {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listTopic, "parentId=" + parentId, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listTopic, parentId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listIndustry = function(parentId, fun) {
		assert.exists(parentId, "parentId", "search.listIndustry");
		assert.isFun(fun.success, "success", "search.listIndustry");

		if (cache(search.listIndustry, parentId, {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listIndustry, "parentId=" + parentId, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listIndustry, parentId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listArea = function(parentId, fun) {
		assert.exists(parentId, "parentId", "search.listArea");
		assert.isFun(fun.success, "success", "search.listArea");

		if (cache(search.listArea, parentId, {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listArea, "parentId=" + parentId, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listArea, parentId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listSite = function(fun) {
		assert.isFun(fun.success, "success", "search.listSiteByMediaCategory");

		if (cache(search.listSite, 'all', {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listSite, {}, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listSite, 'all', response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listSiteByCategory = function(categoryId, fun) {
		assert.exists(categoryId, "categoryId", "search.listSiteByCategory");
		assert.isFun(fun.success, "success", "search.listSiteByMediaCategory");

		if (cache(search.listSiteByCategory, categoryId, {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listSiteByCategory, "categoryId=" + categoryId, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listSiteByCategory, categoryId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listSiteByMediaType = function(typeId, fun) {
		assert.exists(typeId, "typeId", "search.listSiteByMediaType");
		assert.isFun(fun.success, "success", "search.listSiteByMediaType");

		if (cache(search.listSiteByMediaType, typeId, {}, fun)) {
			return;
		}
		ajaxCommFun(search.action.listSiteByMediaType, "typeId=" + typeId, function(response) {
			if (response.type == dict.action.suc) {
				cache(search.listSiteByMediaType, typeId, response);
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	search.listTopicOptionInfo = function(ids, fun) {
		assert.exists(ids, "ids", "search.listTopicOptionInfo");
		assert.isFun(fun.success, "success", "search.listTopicOptionInfo");

		ajaxCommFun(search.action.listTopicOptionInfo, {
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

	search.listIndustryOptionInfo = function(ids, fun) {
		assert.exists(ids, "ids", "search.listIndustryOptionInfo");
		assert.isFun(fun.success, "success", "search.listIndustryOptionInfo");

		ajaxCommFun(search.action.listIndustryOptionInfo, {
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

	search.listAreaOptionInfo = function(ids, fun) {
		assert.exists(ids, "ids", "search.listAreaOptionInfo");
		assert.isFun(fun.success, "success", "search.listAreaOptionInfo");

		ajaxCommFun(search.action.listAreaOptionInfo, {
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
})();