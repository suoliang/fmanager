/**
 * 异步接口
 * @author GUOQIANG
 */
var popup = popup || {};
(function() {

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
	}

	popup.action = {
		listOrganization : std.u("/shareuser/listOrganization.htm"),
		listUser : std.u("/shareuser/listUser.htm"),
		searchUser : std.u("/shareuser/searchUser.htm")
	};

	popup.listOrganization = function(params, fun) {
		assert.exists(params, "params", "popup.listOrganization");
		assert.isFun(fun.success, "success", "popup.listOrganization");

		if (useCache(popup.listOrganization, params, null, fun)) {
			return;
		}
		ajaxCommFun(popup.action.listOrganization, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
				useCache(popup.listOrganization, params, response);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	popup.listUser = function(params, fun, sync) {
		assert.exists(params, "params", "popup.listUser");
		assert.isFun(fun.success, "success", "popup.listUser");

		if (useCache(popup.listUser, params, null, fun)) {
			return;
		}
		ajaxCommFun(popup.action.listUser, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
				useCache(popup.listUser, params, response);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, (util.isNotBlank(sync) && sync == false) ? false : true);
	};

	popup.searchUser = function(params, fun) {
		assert.exists(params, "params", "popup.listUser");
		assert.isFun(fun.success, "success", "popup.listUser");

		if (useCache(popup.searchUser, params, null, fun)) {
			return;
		}
		ajaxCommFun(popup.action.searchUser, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
				useCache(popup.searchUser, params, response);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

})();