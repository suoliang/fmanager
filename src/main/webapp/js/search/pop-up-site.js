$(function() {
	var sitetag = {
		siteList : 'site-list',
		siteItem : 'site-item',
		siteHook : 'site-hook',
		siteName : 'site-name'
	}
	$(std.findTag('option-tab')).click(function() {
		var outer = this;
		var ctx = null;
		$(std.findTag(tag.searchPopupBox)).each(function(i, item) {
			if ($(item).has(outer).size() > 0) {
				ctx = $(item);//获取容器div层
			}
		});
		if (ctx != null) {
			//切换tab
			$(ctx).find(std.findTag('option-tab')).removeClass('nav_pag_active');
			$(ctx).find(std.find('option-tab', std.oid(this))).addClass('nav_pag_active');
			$(ctx).find(std.findTag('option-card')).hide();
			$(ctx).find(std.find('option-card', std.oid(this))).show();

			//设置参数
			$(ctx).optionMagr('setParam', 'site', std.oid(this));

			//清理数据
//			$(ctx).optionMagr('clearValue');
//			$(ctx).find(std.findTag(sitetag.siteHook)).each(function() {
//				$(this).attr('selct', 'false');
//				$(this).removeClass('active');
//			});

			//绑定特殊事件
			if (std.oid(this) == 'site') {
				$(ctx).optionMagr('option', {
					onAddValue : function(id, name) {
						var parent = std.findTag('option-hidden').has(std.find('option-hidden', id));
						$(parent).each(function(i, item) {
							if ($(item).find('[selct=false],:not([selct])').size() <= 0) {
								$(std.find(sitetag.siteHook, std.oid(item))).attr('selct', 'true');
								$(std.find(sitetag.siteHook, std.oid(item))).addClass('active');//高亮选项
							}
						});
					},
					onRemoveValue : function(id, name) {
						var parent = std.findTag('option-hidden').has(std.find('option-hidden', id));
						$(parent).each(function(i, item) {
							$(std.find(sitetag.siteHook, std.oid(item))).attr('selct', 'false');
							$(std.find(sitetag.siteHook, std.oid(item))).removeClass('active');//高亮选项
						});
					}
				});
			} else {
				$(ctx).optionMagr('option', {
					onAddValue : function(id, name) {
					},
					onRemoveValue : function(id, name) {
					}
				});
			}

			search.listSite({
				success : function(sites) {
					if (util.isEmpty(sites)) {
						$(ctx).find(std.find('option-tab', 'site')).hide();
					}
				}
			});
		}
		//绑定选择事件
		$(ctx).find(std.findTag(sitetag.siteHook)).each(function() {
			$(this).unbind('click').click(function() {
				$(ctx).optionMagr('showSubOption', std.oid(this), true);

				if ($(this).attr('selct') == 'true') {
					$(this).attr('selct', 'false');
					$(this).removeClass('active');
					$(ctx).optionMagr('cancelAllSubOption', std.oid(this), true);

				} else {
					$(this).attr('selct', 'true');
					$(this).addClass('active');//高亮选项
					$(ctx).optionMagr('selectAllSubOption', std.oid(this));
				}
			});
		});
		//绑定显示事件
		$(ctx).find(std.findTag(sitetag.siteName)).each(function() {
			$(this).unbind('click').click(function() {
				$(ctx).optionMagr('showSubOption', std.oid(this), true);
			});
		});
		//绑定选择事件
		$(ctx).find(std.findTag('option-item')).each(function() {
			$(this).unbind('click').click(function() {
				$(ctx).optionMagr('selectOption', std.oid(this), true);
			});
		});
	});
});