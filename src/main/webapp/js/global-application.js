/**
 * 字典表
 * @author GUOQIANG
 */
var dict = dict || {};
dict = {
	action : {
		question : 'question',
		suc : 'success',
		err : 'error'
	},
	homepage : {
		DisplayStatus : {
			SHOW : 1,
			HIDE : 2
		},
		ModuleInterfaceType : {
			INPUT : 0,
			VIEW : 1,
			MORE : 2
		}
	}
};
/**
 * 数字逗号分隔工具
 * @author LIUJUNWU
 */
var formatNum = function(strNum) {
	if (strNum.length <= 3) {
		return strNum;
	}
	if (!/^(\+|-)?(\d+)(\.\d+)?$/.test(strNum)) {
		return strNum;
	}
	var a = RegExp.$1, b = RegExp.$2, c = RegExp.$3;
	var re = new RegExp();
	re.compile("(\\d)(\\d{3})(,|$)");
	while (re.test(b)) {
		b = b.replace(re, "$1,$2$3");
	}
	return a + "" + b + "" + c;
}
/**
 * 工具
 * @author GUOQIANG
 */
var ajaxCommFun = function(url, data, callback, sync) {
	$.ajax({
		type : 'post',
		url : url,
		data : data,
		dataType : 'json',
		async : !sync,
		success : function(response, textStatus, XHR) {
			if (XHR.responseText.indexOf('login.css') != -1) {
				document.location = std.u('/index.htm');
			} else {
				if (callback) {
					callback(response);
				}
			}
		},
		error : function(XHR, textStatus, errorThrown) {
			if (XHR.responseText.indexOf('login.css') != -1) {
				document.location = std.u('/index.htm');
			} else {
				if (callback) {
					callback({
						statuts : 'error',
						message : errorThrown,
						attrs : arguments
					});
				}
			}
		}
	});
};
var ajaxCommFunText = function(url, data, callback, sync) {
	$.ajax({
		type : 'post',
		url : url,
		data : data,
		dataType : 'text',
		async : !sync,
		success : function(response, textStatus, XHR) {
			if (XHR.responseText.indexOf('login.css') != -1) {
				document.location = std.u('/index.htm');
			} else if (response.indexOf('COMMON_EXCEPTION_MESSAGE_PAGE') != -1) {
				var message = $(response).find('#COMMON_EXCEPTION_MESSAGE_CONTENT').html();
				if (callback) {
					callback({
						type : dict.action.err,
						message : message,
						data : ''
					});
				}
			} else {
				if (callback) {
					callback({
						type : dict.action.suc,
						data : response
					});
				}
			}
		},
		error : function(XHR, textStatus, errorThrown) {
			if (XHR.responseText.indexOf('login.css') != -1) {
				document.location = std.u('/index.htm');
			} else {
				if (callback) {
					callback({
						type : dict.action.err,
						message : errorThrown,
						data : arguments
					});
				}
			}
		}
	});
};
/**
 * 分页
 * @author GUOQIANG
 */
$.widget('ui.paging', {
	options : {
		currentpage : null,
		pagesize : null,
		totalpage : null,
		totalsize : null,
		onChange : function(totalpage, totalsize, pageNo, pagesize) {
		},
		gotoNoImpl : function() {
			$.msg('未绑定分页实现', 'warning');
		}
	},

	renderDom : function(middle, active, showMore1, showMore2) {
		var html = '<div class="c_page_num"' + std.flag('paging-action', (active - 1)) + '>&lt;</div>';//(active > 1 ? active - 1 : 1))
		if (showMore1) {
			html += '<div class="c_page_num"' + std.flag('paging-action', 1) + '>1</div><div class="c_page_more">...</div>';
		}
		$(middle).each(function(key, item) {
			html += '<div class="c_page_num ' + (active == item ? 'active' : '') + '"' + std.flag('paging-action', item) + '>' + item + '</div>';
		});
		if (showMore2) {
			html += '<div class="c_page_more">...</div><div class="c_page_num"' + std.flag('paging-action', this.options.totalpage) + '>' + this.options.totalpage + '</div>';
		}
		html += '<div class="c_page_num"' + std.flag('paging-action', (active + 1)) + '>&gt;</div>';//active < this.options.totalpage ? active + 1 : this.options.totalpage

		this.element.find('[tag=paging-item]').empty();
		this.element.find('[tag=paging-item]').append(html);
	},

	bindEvent : function() {//绑定事件
		var outer = this;
		this.element.find('[tag=paging-action]').click(function() {
			outer.gotoNo(std.oid(this));
		});
		this.element.find('[tag=paging-button][oid=gotoNo]').unbind('click').click(function() {
			outer.gotoNo();
		});
		this.element.find('[tag=paging-input][oid=pageNo]').unbind('keydown').keydown(function(e) {
			if (e && e.keyCode == 13) {
				outer.gotoNo();
				e.stopPropagation();
			}
		});
	},

	toshow : function() {//显示分页数据
		var pg = {};
		pg.currentpage = parseInt(this.options.currentpage);
		pg.pagesize = parseInt(this.options.pagesize);
		pg.totalpage = parseInt(this.options.totalpage);
		pg.totalsize = parseInt(this.options.totalsize);

		if (pg.totalpage <= 5) {//五页内
			var middle = [];
			for (var num = 1; num <= pg.totalpage; num++) {
				middle.push(num);
			}
			this.renderDom(middle, pg.currentpage, false, false);

		} else if (pg.currentpage <= 3) {//当前页为3页内
			var middle = [ 1, 2 ];
			if (pg.currentpage >= 2) {
				middle.push(3);
			}
			if (pg.currentpage >= 3) {
				middle.push(4);
			}
			this.renderDom(middle, pg.currentpage, false, true);

		} else if (pg.totalpage - pg.currentpage <= 2) {//当前页为倒数三页内
			var middle = [];
			if (pg.currentpage <= pg.totalpage - 2) {
				middle.push(pg.totalpage - 3);
			}
			if (pg.currentpage <= pg.totalpage - 1) {
				middle.push(pg.totalpage - 2);
			}
			middle.push(pg.totalpage - 1);
			middle.push(pg.totalpage);

			this.renderDom(middle, pg.currentpage, true, false);

		} else {//当前页在中间
			this.renderDom([ pg.currentpage - 1, pg.currentpage, pg.currentpage + 1 ], pg.currentpage, true, true);
		}

		this.bindEvent();
	},

	gotoNo : function(number) {//跳转
		var pageNo = -1;
		if (number) {
			pageNo = number;
		} else {
			pageNo = this.element.find('[tag=paging-input][oid=pageNo]').val();
		}
		if (/^\d+$/.test($.trim(pageNo))) {
			pageNo = parseInt(pageNo);
			if (pageNo >= 1 && pageNo <= this.options.totalpage) {
				this.options.gotoNoImpl(pageNo, this.options.pagesize);
				this.toshow();
			} else {
				layer.msg('页号超出范围', 1, 3);
			}
		} else {
			layer.msg('请输入正整数', 1, 3);
		}
	},

	setInfo : function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
		this.options.currentpage = parseInt(currentpage);
		this.options.pagesize = parseInt(pagesize);
		this.options.totalpage = parseInt(totalpage);
		this.options.totalsize = parseInt(totalsize);

		this.toshow();

		this.element.find('[tag=paging-info][oid=currentpage]').html(this.options.currentpage);
		this.element.find('[tag=paging-info][oid=pagesize]').html(this.options.pagesize);
		this.element.find('[tag=paging-info][oid=totalpage]').html(this.options.totalpage);
		this.element.find('[tag=paging-info][oid=totalsize]').html(this.options.totalsize);
		this.element.find('[tag=paging-input][oid=pageNo]').val(this.options.currentpage);

		this.options.onChange(this.options.totalpage, this.options.totalsize, this.options.currentpage, this.options.pagesize);
	},

	refresh : function() {
		this.options.gotoNoImpl(this.options.currentpage, this.options.pagesize);
		this.toshow();
	}
});

/**
 * 弹出框
 * @author GUOQIANG
 */
$.extend({
	msg : function(msg, status, time) {
		var i = status == 'success' ? 1 : (status == 'warning' ? 0 : (status == 'error' ? 3 : -1));
		var t = util.isNotNull(time) ? time : 1;
		layer.msg(msg, t, i);
	},
	alert : function(msg, status, fun) {
		var i = status == 'success' ? 1 : (status == 'warning' ? 0 : (status == 'error' ? 3 : -1));
		layer.msg(msg, i, fun);
	},
	box : function(boxSelector, options, actions) {
		var layerProxy = {
			close : function(index) {
				layer.close(index);
			}
		};
		var layerOptions = $.extend({
			type : 1,
			shade : [ 0.5, '#000' ],
			area : [ 'auto', 'auto' ],
			border : [ 5, 0, '#000' ],// 5, 0.3, '#000'
			title : false,
			closeBtn : false,
			shadeClose : true,
			move : '.c_alt_titTag',
			moveType : 1
		}, options, {
			page : {
				dom : boxSelector
			},
			success : function(layero) {
				if ($.isFunction(options.onOpen)) {
					options.onOpen(layero);
				}
			}
		});
		var index = $.layer(layerOptions);
		if (actions) {
			$.each(actions, function(prop, option) {
				$.each(option.dom, function(i, value) {
					$(value).unbind('click').click(function(e) {
						if (option.fun) {
							option.fun.call(layerProxy, index);
						}
						if (util.isNull(option.close) || option.close == true) {
							layer.close(index);
						}
						e.stopPropagation();
					});
				});
			});
		}
		return index;
	}
});

$.msg.success = function(msg, time) {
	var t = util.isNotNull(time) ? time : 1;
	layer.msg(msg, t, 1);
};
$.msg.warning = function(msg, time) {
	var t = util.isNotNull(time) ? time : 1;
	layer.msg(msg, t, 0);
};
$.msg.error = function(msg, time) {
	var t = util.isNotNull(time) ? time : 1;
	layer.msg(msg, t, 3);
};

$.alert.success = function(msg, fun) {
	layer.alert(msg, 1, fun);
};
$.alert.warning = function(msg, fun) {
	layer.alert(msg, 0, fun);
};
$.alert.error = function(msg, fun) {
	layer.alert(msg, 3, fun);
};

$.mask = function(msg, cover, time) {
	var index = layer.msg(msg, util.isNotNull(time) ? time : 0, {
		type : 16,
		shade : util.isNull(cover) || cover ? [ 0.5, '#000' ] : [ 0 ]
	});
	return index;
};

//******************** Application ********************//

/**
 * 系统
 * @author GUOQIANG
 */
var sys = sys || {};
sys.logout = function(fun) {//登出
	$.ajax({
		type : 'POST',
		url : std.u('/logout.htm'),
		dataType : 'json',
		cache : false,
		async : false,
		success : function(result) {
			if (result.type == 'error') {
				if (fun && fun.error) {
					fun.error(result.message);
				}
			} else {
				if (fun && fun.success) {
					fun.success(result.message);
				} else {
					document.location = std.u('/index.htm');
				}
			}
		}
	});
};
sys.setUEConfig = function(key, value, fun) {//用户配置
	ajaxCommFun(std.u('/userconfig/ue/set.htm'), 'key=' + key + '&value=' + value, function(result) {
		if (result.type == 'error') {
			if (fun && fun.error) {
				fun.error(result.message);
			}
		} else {
			if (fun && fun.success) {
				fun.success(result.message);
			}
		}
	}, true);
};

//******************** INIT Function ********************//

/**
 * 切换主题
 * @author GUOQIANG
 */
var switchTheme = function(themeName) {
	$(std.findTag('theme')).each(function(i, item) {
		$(item).attr('active', 'false');
		$(item).get(0).disabled = true;
	});
	if ($(std.find('theme', themeName)).size() != 0) {
		$(std.find('theme', themeName)).attr('active', 'true');
		$(std.find('theme', themeName)).get(0).disabled = false;
	}
};
var theme = theme || null;
if (theme) {
	switchTheme(theme);
}

$(function() {
	var hours = new Date().getHours();
	if (hours <= 6) {
		$(std.findTag('welcome-desc')).text('早点休息');

	} else if (hours <= 9) {
		$(std.findTag('welcome-desc')).text('早上好');

	} else if (hours <= 12) {
		$(std.findTag('welcome-desc')).text('上午好');

	} else if (hours <= 14) {
		$(std.findTag('welcome-desc')).text('中午好');

	} else if (hours <= 18) {
		$(std.findTag('welcome-desc')).text('下午好');

	} else if (hours <= 21) {
		$(std.findTag('welcome-desc')).text('时候不早了');

	} else {
		$(std.findTag('welcome-desc')).text('我们与您同在');

	}

	/**
	 * 切换主题事件
	 * @author GUOQIANG
	 */
	$('#theme').click(function() {
		if ($(std.find('theme', 'open', '[active=true]')).size() != 0) {
			switchTheme('shrinkage');
			sys.setUEConfig('theme', 'shrinkage');

		} else {
			switchTheme('open');
			sys.setUEConfig('theme', 'open');
		}

		$('.c_menu li .c_sub_menu').hide();
		$('.c_menu li ul li .c_sub2_menu').hide();
	});

	/**
	 * 菜单
	 * @author GUOQIANG
	 */
	$('.c_menu li[level=1]').bind({
		mouseover : function(e) {
			if ($(std.find('theme', 'open', '[active=true]')).size() != 0) {
				$('.c_menu li .c_sub_menu').hide();
				$(this).find('ul li .c_sub2_menu').show();
				//$.cookie('current-opened-menu', std.oid(this));
			}
			$(this).find('.c_sub_menu').show();

			if ($(std.find('theme', 'open', '[active=true]')).size() == 0) {
				var submenu = $(this).find('.c_sub_menu');
				submenu.show();
				if ($(this).offset().top + submenu.outerHeight() > $(window).height()) {
					var upLoc = submenu.outerHeight() * -1;
					submenu.css('margin-top', upLoc);
				} else {
					submenu.css('margin-top', -66);
				}
			}

			e.stopPropagation();
		},
		mouseout : function() {
			if ($(std.find('theme', 'open', '[active=true]')).size() == 0) {
				$(this).find('.c_sub_menu').hide();
			}
		}
	});
	$('.c_menu li ul li').bind({
		mouseover : function() {
			if ($(std.find('theme', 'open', '[active=true]')).size() == 0) {
				$(this).parent().show();
				$(this).find('.c_sub2_menu').show();

				var submenu = $(this).find('.c_sub2_menu');
				submenu.show();
				if ($(this).offset().top + submenu.outerHeight() > $(window).height()) {
					var upLoc = submenu.outerHeight() * -1 + 40;
					submenu.css('margin-top', upLoc);
				}
			}
		},
		mouseout : function() {
			if ($(std.find('theme', 'open', '[active=true]')).size() == 0) {
				$(this).find('.c_sub2_menu').hide();
			}
		}
	});

	if ($(std.find('theme', 'open', '[active=true]')).size() != 0) {
		//var oid = $.cookie('current-opened-menu');
		$('.c_menu li .c_sub_menu').hide();
		$(std.findTag('menu-item', '[open=true]')).find('.c_sub_menu').show();
		$(std.findTag('menu-item', '[open=true]')).find('ul li .c_sub2_menu').show();
	}
});

var alertMsg = function(msg) {
	$.msg.error(msg);
}