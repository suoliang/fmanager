var brief = brief || {};
(function() {
	brief.param = "title=";

	brief.initEvent = function() {

		$(std.findTag("btn-goto-brief-article")).unbind().click(function() {
			util.post(ctx + '/report/brief/article/index.htm', {
				reportId : std.oid(this),
				pageNo : $('#report-brief-paging').paging('option', 'currentpage')
			});
		});
		var doSearch = function() {
			$('input[name="title"]').val($("#title").val());
			brief.param = $("#condition-form").serialize();
			brief.queryBrief(1, $('#report-brief-paging').paging('option', 'pagesize'));
		}
		$(std.findTag("btn-search-brief")).unbind().click(function() {
			doSearch();
		});
		$('.c_search_box input[name="title"]').unbind().keydown(function(e) {
			if (e && e.keyCode == 13) {
				doSearch();
			}
			e.stopPropagation();
		});
		
		$("#title").keydown(function(e){
			if (e && e.keyCode == 13) {
				doSearch();
			}
			e.stopPropagation();
		});

		$(std.findTag("btn-export-brief")).click(function() {

		});

		$(std.findTag("btn-edit-brief")).click(function() {
			brief.saveBrief(std.oid(this), $(this).attr('title'));
		});

		$(std.findTag("btn-delete-brief")).click(function() {
			brief.deleteBrief(std.oid(this));
		});
	};

	brief.queryBrief = function(pageNo, pagesize) {
		report.queryBrief(brief.param + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) {
				$('.report-container').html(html);
				brief.initEvent();
			}
		});
	};

	brief.saveBrief = function(id, title) {
		popup.openBriefInput(id, title, function(form) {/**encodeURIComponent转义*/
			report.saveBrief('reportId=' + form.id + '&title=' + encodeURIComponent(form.title), {
				success : function(data, message) {
					$.msg.success(message);
					$('#report-brief-paging').paging('refresh');
				}
			});
		});
	};

	brief.deleteBrief = function(id) {
		var index = layer.confirm("确定删除简报吗？", function() {//yes
			layer.close(index);

			report.deleteBrief("reportId=" + id, {
				success : function(data, message) {
					$.msg.success(message);
					$('#report-brief-paging').paging('refresh');
				}
			});
		});
	};

	$(function() {

		$('#report-brief-paging').paging({
			gotoNoImpl : function(pageNo, pagesize) {
				brief.queryBrief(pageNo, pagesize);
			}
		});

		$(std.findTag("btn-add-brief")).click(function() {
			brief.saveBrief(null, null);
		});

		brief.initEvent();

	});
})();
