/**
 * JS 扩展
 * @author GUOQIANG
 */
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
};
String.prototype.ltrim = function() {
	return this.replace(/(^\s*)/g, "");
};
String.prototype.rtrim = function() {
	return this.replace(/(\s*$)/g, "");
};
Date.prototype.format = function(format) {
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3) / 3),
		"S" : this.getMilliseconds()
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};
Date.prototype.addDays = function(d) {
	this.setDate(this.getDate() + d);
	return this;
};
Date.prototype.addWeeks = function(w) {
	this.addDays(w * 7);
	return this;
};
Date.prototype.addMonths = function(m) {
	var d = this.getDate();
	this.setMonth(this.getMonth() + m);
	if (this.getDate() < d) {
		this.setDate(0);
	}
	return this;
};
Date.prototype.addYears = function(y) {
	var m = this.getMonth();
	this.setFullYear(this.getFullYear() + y);
	if (m < this.getMonth()) {
		this.setDate(0);
	}
	return this;
};
var console = console || {
	log : function() {
	}
};
/**
 * JQuery 扩展
 * @author GUOQIANG
 */
if (jQuery) {
	$.extend({
		isUndefined : function(obj) {
			return (typeof obj == "undefined") || (obj == null);
		},
		isFunction : function(obj) {
			return !this.isUndefined(obj) && (typeof obj == 'function');
		},
		isObject : function(obj) {
			return obj == "object";
		}
	});
	$.extend({
		pollData : {},
		poll : function(name, order, fun, time) {
			if (order == 'start') {
				var data = $.pollData[name];
				if (util.isNotNull(data) && util.isNotNull(data.id)) {
					clearInterval(data.id);
				}
				var id = setInterval(fun, util.isNotNull(time) ? time : 1000);
				$.pollData[name] = {
					id : id
				};
			} else if (order == 'end') {
				var data = $.pollData[name];
				if (util.isNotNull(data) && util.isNotNull(data.id)) {
					clearInterval(data.id);
				}
			}
		},
		/* 
		example $.cookie(’name’, ‘value’);
		example $.cookie(’name’, ‘value’, {expires: 7, path: ‘/’, domain: ‘jquery.com’, secure: true});
		example $.cookie(’name’, null);//删除
		var value = $.cookie('name');
		**/
		cookie : function(name, value, options) {
			if (typeof value != 'undefined') { // name and value given, set cookie
				options = options || {};
				if (value === null) {
					value = '';
					options.expires = -1;
				}
				var expires = '';
				if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
					var date;
					if (typeof options.expires == 'number') {
						date = new Date();
						date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
					} else {
						date = options.expires;
					}
					expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
				}
				var path = options.path ? '; path=' + options.path : '';
				var domain = options.domain ? '; domain=' + options.domain : '';
				var secure = options.secure ? '; secure' : '';
				document.cookie = [ name, '=', encodeURIComponent(value), expires, path, domain, secure ].join('');
			} else { // only name given, get cookie
				var cookieValue = null;
				if (document.cookie && document.cookie != '') {
					var cookies = document.cookie.split(';');
					for (var i = 0; i < cookies.length; i++) {
						var cookie = jQuery.trim(cookies[i]);
						// Does this cookie string begin with the name we want?
						if (cookie.substring(0, name.length + 1) == (name + '=')) {
							cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
							break;
						}
					}
				}
				return cookieValue;
			}
		}

	});
}
/**
 * class 定义
 * @author GUOQIANG
 */
var cyyun = cyyun || {};
(function(cyyun) {
	/**
	 * 基本异常
	 */
	function BaseError() {
		this.BaseError.apply(this, arguments);
	}
	cyyun.BaseError = BaseError;
	Error.prototype = new Error();
	BaseError.prototype.BaseError = function(msg) {
		this.name = "BaseError";
		this.message = msg;
	};
	BaseError.prototype.toString = function() {
		console.log(this);
		// note that name and message are properties of Error
		return this.name + ": " + this.message;
	};

	/**
	 * 参数异常
	 */
	function AssertError() {
		this.AssertError.apply(this, arguments);
	}
	cyyun.AssertError = AssertError;
	AssertError.prototype._super = BaseError.prototype;
	AssertError.prototype.AssertError = function(msg, method, argname) {
		this._super.BaseError.call(this, msg);
		this.name = "AssertError";
		this.method = method;
		this.argname = argname;
	};
	AssertError.prototype.toString = function() {
		// note that name and message are properties of Error
		return this.name + ": " //
				+ ((typeof this.method != "undefined") && (this.method != null) ? this.method + " -> " : "")//
				+ ((typeof this.argname != "undefined") && (this.argname != null) ? this.argname : "") //
				+ " " + this.message;
	};
})(cyyun);
/**
 * 断言
 * @author GUOQIANG
 */
var assert = assert || {};
assert = {//$.extend(assert, {});
	undef : function(obj, argname, method) {
		if (!$.isUndefined(obj)) {
			throw new cyyun.AssertError("已定义", method, argname);
		}
	},
	exists : function(obj, argname, method) {
		if ($.isUndefined(obj)) {
			throw new cyyun.AssertError("不存在", method, argname);
		}
	},
	isFun : function(obj, argname, method) {
		if (!$.isFunction(obj)) {
			throw new cyyun.AssertError("非函数类型", method, argname);
		}
	},
	notFun : function(obj, argname, method) {
		if ($.isFunction(obj)) {
			throw new cyyun.AssertError("为函数类型", method, argname);
		}
	},
	isObj : function(obj, argname, method) {
		if (!$.isObject(obj)) {
			throw new cyyun.AssertError("非对象类型", method, argname);
		}
	},
	notObj : function(obj, argname, method) {
		if ($.isObject(obj)) {
			throw new cyyun.AssertError("为对象类型", method, argname);
		}
	}
};
/**
 * UTILS
 * @author GUOQIANG
 */
var util = util || {};
/*verify*/
util.isNull = function(target) {
	return $.isUndefined(target);
};
util.isNotNull = function(target) {
	return !$.isUndefined(target);
};
util.isBlank = function(target) {
	if ($.isUndefined(target) || $.trim(target) == "") {
		return true;
	}
	return false;
};
util.isNotBlank = function(target) {
	return !util.isBlank(target);
};
util.isEmpty = function(target) {
	return $.isUndefined(target) || $(target).size() == 0;
};
/*process*/
util.appendArgToURL = function(url, args) {
	if (args != null)
		url = url.indexOf("?") == -1 ? url + "?" + args : url + "&" + args;
	return url;
};
util.joinCollection = function(items, separator, key) {
	var result = "";
	if (util.isNotNull(items)) {
		$(items).each(function(i, item) {
			result += separator + (util.isNotNull(key) ? item[key] : item);
		});
		result = result.substring(separator.length);
	}
	return result;
};
/*redirect*/
util.go = function(url) {
	if (util.isNotNull(url)) {
		window.location.href = ctx + url;
	}
};
util.openPage = function(url) {
	if (util.isNotNull(url)) {
		window.open(url);
	}
};
util.post = function(url, params) {
	if (util.isNotNull(url)) {
		$('body').append('<form id="global-standard-util-post" action="' + url + '" method="post"></form>');
		$.each(params, function(prop, value) {
			$('#global-standard-util-post').append('<input type="hidden" name="' + prop + '" value="' + value + '" />');
		});
		$('#global-standard-util-post').submit();
	}
};
/*DOM*/
util.scorllTo = function(obj, time) {
	$("body,html").animate({
		scrollTop : $(obj).offset().top - 50
	}, time ? time : 500);
};
util.setInputPrompt = function(selector, defPrompt, bindEvents) {
	if ($(selector).size() == 0) {
		return;
	}
	//init
	if ($(selector).val().trim() == "" || $(selector).val() == defPrompt) {
		$(selector).val(defPrompt);
		$(selector).css("color", "#999");
	}
	$(selector).unbind().bind($.extend({}, bindEvents, {
		focus : function() {
			if (this.value == defPrompt) {
				this.value = "";
			}
			$(this).css("color", "#666");

			if (bindEvents && bindEvents.focus) {
				bindEvents.focus.apply(this);
			}
		},
		blur : function() {
			if (this.value == "") {
				this.value = defPrompt;
				$(this).css("color", "#999");
			}

			if (bindEvents && bindEvents.blur) {
				bindEvents.blur.apply(this);
			}
		}
	}));
};
util.htmlEncode = function(value) {
	return $('<div/>').text(value).html();
};
util.htmlDecode = function(value) {
	return $('<div/>').html(value).text();
};
/**
 * 规范化实用方法
 * @author GUOQIANG
 */
var std = std || {};
std = {
	selector : function(tag, oid, tagname) {
		if (tag != undefined && tag != null) {
			tag = '[tag=' + tag + ']';
		} else {
			tag = '';
		}
		if (oid != undefined && oid != null) {
			if (oid === '') {
				oid = '[oid]';
			} else {
				oid = '[oid=' + oid + ']';
			}
		} else {
			oid = '';
		}
		if (tagname == undefined) {
			tagname = '';
		}
		return tagname + tag + oid;
	},
	oid : function(obj, tag, oid) {//设置标志/获取标志oid
		if (!$.isUndefined(oid) && !$.isUndefined(tag)) {
			$(obj).attr('tag', tag);
			$(obj).attr('oid', oid);
		} else {
			return $(obj).attr('oid');
		}
	},
	find : function(tag, oid, tagname) {//根据标志查找元素
		assert.exists(oid, "oid", "std.find");
		assert.exists(tag, "tag", "std.find");
		return $(std.selector(tag, oid, tagname));
	},
	findTag : function(tag, tagname) {//根据标志查找元素
		assert.exists(tag, "tag", "std.find");
		return $(std.selector(tag, null, tagname));
	},
	findOid : function(oid, tagname) {//根据标志查找元素
		assert.exists(oid, "oid", "std.find");
		return $(std.selector(null, oid, tagname));
	},
	each : function(tag, fun) {//根据标志tag遍历元素列表
		assert.exists(tag, "tag", "std.find");
		$(std.selector(tag)).each(function(key, item) {
			if (fun) {
				fun(std.oid(item), item, key);
			}
		});
	},
	flag : function(tag, oid) {//生成标志字符串
		assert.exists(oid, "oid", "std.flag");
		assert.exists(tag, "tag", "std.flag");
		return ' tag="' + tag + '" oid="' + oid + '" ';
	},
	flagObj : function(obj, tag, oid) {//设置标志
		assert.exists(oid, "oid", "std.flagObj");
		assert.exists(tag, "tag", "std.flagObj");
		$(obj).attr("tag", tag);
		$(obj).attr("oid", oid);
	},
	val : function(tag, oid, tagname) {//获取标志元素的值
		var obj = std.find(tag, oid, tagname);
		return ($(obj).val() != null && $(obj).val().trim() != "") ? $(obj).val() : undefined;
	},
	u : function(url) {//附加basePath到指定地址
		assert.exists(ctx, "ctx", "std.u");
		assert.exists(url, "url", "std.u");
		return ctx + url;
	}
};