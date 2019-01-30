/**
 * 预警异步接口
 * @author GUOQIANG
 */
var custtopic = custtopic || {};
(function() {

	custtopic.action = {
		saveCustTopic : std.u('/custtopic/advanced/saveCustTopic.htm'),
		parseKeyword : std.u('/custtopic/parseKeyword.htm')
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
})();