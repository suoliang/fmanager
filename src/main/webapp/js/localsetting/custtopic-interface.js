/**
 * 预警异步接口
 * @author GUOQIANG
 */




var custtopic = custtopic || {};
(function() {
	custtopic.action = {
		saveCustTopic : std.u('/custtopic/saveCustTopic.htm'),
		parseKeyword : std.u('/custtopic/parseKeyword.htm'),
		listTopic : std.u("/custtopic/topic/list.htm"),
		custTopicInit : std.u("/custtopic/custTopicInit.htm"),
	};

	custtopic.saveCustTopic = function(params, fun) {
		assert.isFun(fun.success, "success", "topic.saveCustTopic");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(custtopic.action.saveCustTopic, params, function(response) {

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
	
	custtopic.listTopic = function(parentId, fun) {
		assert.exists(parentId, "parentId", "custtopic.listTopic");
		assert.isFun(fun.success, "success", "custtopic.listTopic");

		ajaxCommFun(custtopic.action.listTopic, "parentId=" + parentId, function(response) {
			if (response.type == dict.action.suc || response.type == '当前用户存在顶级专题') {
				fun.success(response.data, response.type, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		}, true);
	};

	custtopic.parseKeyword = function(params, fun) {
		assert.isFun(fun.success, "success", "topic.parseKeyword");

		var index = layer.load('正在提交请求...');

		ajaxCommFunText(custtopic.action.parseKeyword, params, function(response) {

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
	
	custtopic.custTopicInit = function(params, fun){
		assert.exists(params, "params", "custtopic.custTopicInit");
		assert.isFun(fun.success, "success", "custtopic.custTopicInit");
		
		var index = layer.load('正在提交请求...');

		ajaxCommFun(custtopic.action.custTopicInit, params, function(response) {

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
