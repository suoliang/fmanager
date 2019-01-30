var page = page || {};
(function() {
	//******************** Global Constant ********************//
	var selector = {
		listContainer : function() {
			return '#leaveWord-list-container';
		},
		pagingContainer : function() {
			return '#leaveWord-message-paging';
		}
	};

	var tag = {
		messageLink : "btn-leave-detail",
		pagingInfo : "paging-info",
		sortBtn : 'btn-sort'
	};

//	var input = {
//		reportId : 'input[name=reportId]',
//		orderField : 'input[name=orderField]',
//		orderType : 'input[name=orderType]'
//	};

	//******************** Page Function ********************//

	page.currentParam = '1=1';

	page.initEvent = function() {

		$(std.findTag("btn-back-to-brief")).click(function() {
			util.post(ctx + '/report/brief/index.htm', {
				pageNo : std.oid(this)
			});
		});

	};

	page.queryBriefArticle = function(pageNo, pagesize) {
		MessageBoard.queryBriefArticle("&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.initEvent();
			}
		});
	};

	//******************** Option Function ********************//

	page.options = page.options || {};
	(function(options) {
		$("#send-leave").click(function() {
			var contacter = $("#contacterInput").val();
			var mobile = $("#mobileInput").val();
			var title = $("#titleInput").val();
			var content = $("#contentInput").val();
			var flag = true;
			if ($.trim(contacter) == '') {
				$('#contacterInput').focus();
				$('#contacterInput').val("");
				$('#contacterHint').append("联系人不能为空");
				flag = false;
			} else if ($.trim(title) == '') {
				$('#titleInput').focus();
				$('#titleInput').val("");
				$('#titleHint').append("标题不能为空");
				flag = false;
			}
			if (flag) {
				window.location.href = std.u("/setting/leaveWord/leaveWordAdd.htm?contacter=" + contacter + "&mobile=" + mobile + "&title=" + title + "&content=" + content);
			}
		});
	})(page.options);

	//******************** List Function ********************//
	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			$(std.findTag(tag.messageLink)).unbind('click').click(function() {//留言标题点击事件注册
				window.location.href = std.u("/setting/leaveWord/leaveWordInfoDetail.htm?id=" + std.oid(this)+"&pageNo="+$('#messageId').attr("value"));
			});
		};

		list.renderDom = function(html) {//渲染文章数据
			$('.leaveWord-list-container').empty();
			$('.leaveWord-list-container').html(html);
		};

		list.toshowDetail = function(guid) {//显示文章详情
			showArticleDetail(guid);
		};
	})(page.list);

	//******************** Paging Function ********************//

	page.paging = page.paging || {};
	(function(paging) {
		paging.initEvent = function() {
			$(selector.pagingContainer()).paging({
				gotoNoImpl : function(pageNo, pagesize) {
					page.queryBriefArticle(pageNo, pagesize);
				}
			});
		};
	})(page.paging);

	$(function() {

		//page init
		page.initEvent();

		//list init
		page.list.initEvent();

		//paging init
		page.paging.initEvent();

	});
})();