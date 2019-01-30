//******************** Global Constant ********************//

var selector = {
	warningRuleList : '#warning-rule-list',
	pagingContainer : '#warning-rule-list-paging'
};

var tag = {
	changeRuleStatusBtn : 'btn-change-rule-status',
	searchRuleBtn : 'btn-search-rule',
	editRuleBtn : 'btn-edit-rule',
	addRuleBtn : 'btn-add-rule'
};

//******************** Warning Index Function ********************//

var page = page || {};
(function(page) {
	page.params = '1=1';

	page.queryRule = function(pageNo, pagesize) {
		warning.queryRule(page.params + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) {
				$(selector.warningRuleList).empty();
				$(selector.warningRuleList).html(html);
				page.initEvent();
			}
		});
	};

	page.initEvent = function() {

		$(std.findTag(tag.searchRuleBtn)).unbind().click(function() {
			$('input[name="name"]').val($("#rule-name").val());
			page.params = $("#condition-form").serialize();
			page.queryRule(1, page.paging.togetInfo('pagesize'));
		});

		$('input[name=rule-name]').unbind().keydown(function(e) {
			if (e && e.keyCode == 13) {
				$(std.findTag(tag.searchRuleBtn)).click();
			}
			e.stopPropagation();
		});

		/**
		 * 新增预警
		 */
		$(std.findTag(tag.addRuleBtn)).unbind().click(function() {
//			var pageNo = page.paging.togetInfo('currentpage');
//			util.post(std.u('/setting/warning/rule/batchinput.htm'), {
//				pageNo : pageNo
//			});
			page.editWarningRule(null);
		});
		/**
		 * 编辑预警
		 */
		$(std.findTag(tag.editRuleBtn)).unbind().click(function() {
			warning.findRule("ruleId=" + std.oid(this), {
				success : function(warningRule) {
					page.editWarningRule(warningRule);
				}
			});
		});

		$(std.findTag(tag.changeRuleStatusBtn)).unbind().click(function() {
			var status = $(this).attr('status');
			if (status == 1) {
				status = 0;
			} else {
				status = 1;
			}
			warning.changeRuleStatus('ruleId=' + std.oid(this) + '&stauts=' + status, {
				success : function() {
					$.msg.success('状态更改成功');
					page.paging.refresh();
				}
			});
		});
	};

	page.editWarningRule = function(warningRule) {
		popup.openWarningRuleInput(warningRule, function(form) {
			warning.saveRule(form, {
				success : function(data, message) {
					$.msg.success(message);
					page.paging.refresh();
				}
			});
		});
	};
})(page);

//******************** Paging Function ********************//

page.paging = page.paging || {};
(function(paging) {
	paging.tosetInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
		$(selector.pagingContainer).paging("setInfo", currentpage, pagesize, totalpage, totalsize);
	};

	paging.togetInfo = function(type) {//获取分页信息
		return $(selector.pagingContainer).paging("option", type);
	};

	paging.refresh = function() {
		$(selector.pagingContainer).paging("refresh");
	};
})(page.paging);

//******************** INIT Function ********************//

$(function() {
	$(selector.pagingContainer).paging({
		gotoNoImpl : function(pageNo, pagesize) {
			page.queryRule(pageNo, pagesize);
		}
	});

	page.initEvent();
});