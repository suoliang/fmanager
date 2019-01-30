//******************** Global Constant ********************//

var selector = {
	monitorKeywordList : '#monitor-keyword-list',
	pagingContainer : '#monitor-keyword-list-paging'
};

var tag = {
	searchRuleBtn : 'btn-search-rule',
	editRuleBtn : 'btn-edit-rule',
	addRuleBtn : 'btn-add-rule'
};

//******************** Monitor Index Function ********************//

var page = page || {};
(function(page) {
	page.params = '1=1';

	page.queryMonitorRule = function(pageNo, pagesize) {
		monitor.queryMonitorRule(page.params + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) {
				$(selector.monitorKeywordList).empty();
				$(selector.monitorKeywordList).html(html);
				page.initEvent();
			}
		});
	};

	page.initEvent = function() {
		$(std.findTag(tag.searchRuleBtn)).unbind().click(function() {
			$('input[name="word"]').val($("#input-keyword").val());
			page.params = $("#condition-form").serialize();
			page.queryMonitorRule(1, page.paging.togetInfo('pagesize'));
		});

		$('#input-keyword').unbind().keydown(function(e) {
			if (e && e.keyCode == 13) {
				$(std.findTag(tag.searchRuleBtn)).click();
			}
			e.stopPropagation();
		});
		
		//新增
		$(std.findTag(tag.addRuleBtn)).unbind().click(function() {
			page.editMonitoKeyword(null);
		});
		
		//编辑
		$(std.findTag(tag.editRuleBtn)).unbind().click(function() {
			monitor.findMonitorKeyword("kid=" + std.oid(this), {
				success : function(monitorKeyword) {
					page.editMonitoKeyword(monitorKeyword);
				}
			});
		});
		
		//删除
		$(std.findTag("btn-delete-rule")).click(function() {
			page.deleteMonitoKeyword (std.oid(this));
		});
	};

	page.editMonitoKeyword = function(monitorKeyword) {
		popup.openMonitorKeywordInput(monitorKeyword, function(form) {
			monitor.updateRule(form, {
				success : function(data, message) {
					$.msg.success(message);
					page.paging.refresh();
				}
			});
		});
	};
	
	page.deleteMonitoKeyword = function(kid) {
		var index = layer.confirm("确定删除监测规则吗？", function() {//yes
			layer.close(index);

			monitor.deleteRule("kid=" + kid, {
				success : function(data, message) {
					$.msg.success(message);
					$('#monitor-keyword-list-paging').paging('refresh');
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
			page.queryMonitorRule(pageNo, pagesize);
		}
	});

	page.initEvent();
});