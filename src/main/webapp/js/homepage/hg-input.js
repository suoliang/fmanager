/**
 * 首页：index.htm
 * @author GUQIANG
 */
var input = input || {};
(function(page) {

	//******************** Closure Variable ********************//

	var tag = {
		tab : "module-tab",
		card : "module-card",
		board : "board-frame",
		header : "board-header",
		body : "board-body",
		config : "board-config",
		configBtn : "board-config-btn",
		closeBtn : "board-close-btn",
		submitBtn : "board-config-submit-btn",
		cancelBtn : "board-config-cancel-btn",
		icon : "board-icon"
	}

	//******************** Page Function ********************//

	page.initEvent = function() {
		//tab 切换
		std.each(tag.tab, function(oid, item) {
			$(item).click(function() {
				$(std.findTag(tag.card)).hide();

				$(std.findTag('module-form')).removeAttr('selected');
				$(std.find('module-form', std.oid(this))).attr('selected', 'selected');

				var card = $(std.find(tag.card, std.oid(this)));
				page.selectTab($(this).attr('num'));
				card.show();

				var params = card.attr('params');
				params = util.isNotBlank(params) ? card.attr('params') + "&" + card.attr('userConfigInfo') : card.attr('userConfigInfo');
//				console.info(params);
				homepage.captureModuleInput(card.attr('link'), params, {
					success : function(html) {
						$(std.findTag(tag.card)).empty();
						card.html(html);//FIXME 首页 - 频繁切换板块input片段
					}
				});
			});
		});

		//选择默认tab
		if ($("[tag=module-tab][def=true]").size() > 0) {
			$("[tag=module-tab][def=true]").click();
		} else {
			$("[tag=module-tab]").first().click();
		}
	};

	page.selectTab = function(num) {
		std.each(tag.tab, function(oid, item) {
			$(item).removeClass('nav_pag_active');
			if (num == $(item).attr('num')) {
				$(item).addClass('nav_pag_active');
			}
		});
	}

	//******************** INIT Function ********************//

	$(function() {
		page.initEvent();
	});

})(input);