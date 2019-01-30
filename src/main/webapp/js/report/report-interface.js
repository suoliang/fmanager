/**
 * 首页异步接口
 * @author GUOQIANG
 */
var report = report || {};
(function() {

	report.action = {
		queryBrief : std.u("/report/brief/list.htm"),
		listBriefData : std.u("/report/brief/listBrief.htm"),
		saveBrief : std.u("/report/brief/save.htm"),
		deleteBrief : std.u("/report/brief/delete.htm"),
		queryBriefArticle : std.u("/report/brief/article/list.htm"),
		addBriefArticle : std.u("/report/brief/article/add.htm"),
		deleteBriefArticle : std.u("/report/brief/article/delete.htm"),

		queryDaily : std.u("/report/daily/list.htm"),
		listDailyData : std.u("/report/daily/listBrief.htm"),
		saveDaily : std.u("/report/daily/save.htm"),
		deleteDaily : std.u("/report/daily/delete.htm"),
		queryDailyArticle : std.u("/report/daily/article/list.htm"),
		addDailyArticle : std.u("/report/daily/article/add.htm"),
		deleteDailyArticle : std.u("/report/daily/article/delete.htm")
	};

	var useCache = function(obj, key, response, fun) {
		if (util.isNull(fun)) {//建立缓存
			$.data(obj, '' + key, {
				data : response.data,
				message : response.message
			});
		} else {//使用缓存
			var response = $.data(obj, '' + key);
			if (!$.isUndefined(response)) {
				fun.success(response.data, response.message);
				return true;
			}
			return false;
		}
	};

	/*	brief  */

	report.queryBrief = function(params, fun) {
		assert.isFun(fun.success, "success", "report.listBrief");

		ajaxCommFunText(report.action.queryBrief, params, function(response) {
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

	report.listBriefData = function(params, fun) {
		assert.exists(params, "params", "report.listBriefData");
		assert.isFun(fun.success, "success", "report.listBriefData");

		var index = layer.load('正在加载数据...');

		ajaxCommFun(report.action.listBriefData, params, function(response) {

			layer.close(index);

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

	report.saveBrief = function(params, fun) {
		assert.exists(params, "params", "report.saveBrief");
		assert.isFun(fun.success, "success", "report.saveBrief");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.saveBrief, params, function(response) {

			layer.close(index);

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

	report.deleteBrief = function(params, fun) {
		assert.exists(params, "params", "report.deleteBrief");
		assert.isFun(fun.success, "success", "report.deleteBrief");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.deleteBrief, params, function(response) {

			layer.close(index);

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

	report.queryBriefArticle = function(params, fun) {
		assert.exists(params, "params", "report.queryBriefArticle");
		assert.isFun(fun.success, "success", "report.queryBriefArticle");

		ajaxCommFunText(report.action.queryBriefArticle, params, function(response) {
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

	report.addBriefArticle = function(params, fun) {
		assert.exists(params, "params", "report.addBriefArticle");
		assert.isFun(fun.success, "success", "report.addBriefArticle");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.addBriefArticle, params, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alert(4);
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	report.deleteBriefArticle = function(params, fun) {
		assert.exists(params, "params", "report.deleteBriefArticle");
		assert.isFun(fun.success, "success", "report.deleteBriefArticle");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.deleteBriefArticle, params, function(response) {

			layer.close(index);

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

	/*	daily  */

	report.queryDaily = function(params, fun) {
		assert.isFun(fun.success, "success", "report.queryDaily");

		ajaxCommFunText(report.action.queryDaily, params, function(response) {
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

	report.listDailyData = function(params, fun) {
		assert.exists(params, "params", "report.listDailyData");
		assert.isFun(fun.success, "success", "report.listDailyData");

		var index = layer.load('正在加载数据...');

		ajaxCommFun(report.action.listDailyData, params, function(response) {

			layer.close(index);

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

	report.saveDaily = function(params, fun) {
		assert.exists(params, "params", "report.saveDaily");
		assert.isFun(fun.success, "success", "report.saveDaily");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.saveDaily, params, function(response) {

			layer.close(index);

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

	report.deleteDaily = function(params, fun) {
		assert.exists(params, "params", "report.deleteDaily");
		assert.isFun(fun.success, "success", "report.deleteDaily");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.deleteDaily, params, function(response) {

			layer.close(index);

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

	report.queryDailyArticle = function(params, fun) {
		assert.exists(params, "params", "report.queryDailyArticle");
		assert.isFun(fun.success, "success", "report.queryDailyArticle");

		ajaxCommFunText(report.action.queryDailyArticle, params, function(response) {
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

	report.addDailyArticle = function(params, fun) {
		assert.exists(params, "params", "report.addDailyArticle");
		assert.isFun(fun.success, "success", "report.addDailyArticle");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.addDailyArticle, params, function(response) {

			layer.close(index);

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

	report.deleteDailyArticle = function(params, fun) {
		assert.exists(params, "params", "report.deleteDailyArticle");
		assert.isFun(fun.success, "success", "report.deleteDailyArticle");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(report.action.deleteDailyArticle, params, function(response) {

			layer.close(index);

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