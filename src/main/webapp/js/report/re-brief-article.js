var page = page || {};
(function() {
	//******************** Global Constant ********************//
	var selector = {
		searchAdvanced : function() {
			return '#search-advanced';
		},
		listHead : function() {
			return '.search-list-head';
		},
		listContainer : function() {
			return '.search-list-container';
		},
		pagingContainer : function() {
			return '#report-brief-article-paging';
		},
		top : function() {
			return '.c_top';
		}
	};

	var tag = {
		articleLink : "article-link",
		pagingAction : "paging-action",
		pagingInfo : "paging-info",
		sortBtn : 'btn-sort'
	};

	var input = {
		reportId : 'input[name=reportId]',
		orderField : 'input[name=orderField]',
		orderType : 'input[name=orderType]'
	};

	//******************** Page Function ********************//

	page.currentParam = '1=1';

	page.initEvent = function() {

		$(std.findTag("btn-export-brief")).click(function() {
			//TODO
		});

		$(std.findTag("btn-back-to-brief")).click(function() {
			util.post(ctx + '/report/brief/index.htm', {
				pageNo : std.oid(this)
			});
		});

		//排序
		var doSort = function(item) {
			var sortIcon = $(item).find(std.findTag('icon-sort'));
			//当前图标排序动作
			if (sortIcon.attr('type') == 'desc') {
				$(input.orderType).val('1');//升序
			} else {
				$(input.orderType).val('0');//降序
			}
			//搜索文章
			page.queryBriefArticle(1, page.paging.pagesize ? page.paging.pagesize : 10);
		}
		$(std.findTag(tag.sortBtn)).click(function() {
			$(input.orderField).val(std.oid(this));//阅读数
			doSort(this);
		});
	};

	page.queryBriefArticle = function(pageNo, pagesize) {
		page.currentParam = page.options.serialize();
		report.queryBriefArticle(page.currentParam + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.reloadStyle();
				page.list.initEvent();
			}
		});
	}

	page.deleteBriefArticle = function(guid) {
		var index = layer.confirm("确定删除简报文章吗？", function() {//yes
			layer.close(index);

			report.deleteBriefArticle('reportId=' + $(input.reportId).val() + '&guid=' + guid, {
				success : function(data, message) {
					$.msg.success(message);
					$(selector.pagingContainer()).paging('refresh');
				}
			});
		});
	}

	//******************** Option Function ********************//

	page.options = page.options || {};
	(function(options) {
		options.serialize = function() {//获取选项数据
			this.sync();
			return $(selector.searchAdvanced()).serialize();
		};

		options.serializeArray = function() {//获取选项数据
			this.sync();
			return $(selector.searchAdvanced()).serializeArray();
		};

		options.sync = function() {//同步选项数据
		};
	})(page.options);

	//******************** List Function ********************//

	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			//查看文章详情
			$(std.findTag(tag.articleLink)).click(function() {
				$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/report/openArticleDetail.htm') + "'></form>");
				$("#param-form").append("<input type='hidden' name='guid' value='" + std.oid(this) + "'/>");
				document.createElement("a").onclick = $("#param-form").submit();
				$("#param-form").remove();
			});

			$(std.findTag('btn-delete-brief-article')).click(function() {
				page.deleteBriefArticle(std.oid(this));
			});

			$(std.findTag('btn-option')).click(function() {
				if ($(this).hasClass('c_select')) {
					$(this).removeClass('c_select');
					$(this).parent().find('ul').hide();
				} else {
					$(this).addClass('c_select');
					$(this).parent().find('ul').show();
				}
			});

			$(std.findTag('ctx-btn')).mouseleave(function() {
				$(this).find('div').removeClass('c_select');
				$(this).find('ul').hide();
			});
		};

		list.renderDom = function(html) {//渲染文章数据
			$(selector.listContainer()).empty();
			$(selector.listContainer()).append(html);
			util.scorllTo($(selector.listContainer()));
		};

		list.reloadStyle = function() {
			$(selector.listContainer()).children('div').each(function(prop) {
				$(this).removeClass('c_bg_ye');
				if (prop % 2 != 0) {
					$(this).addClass('c_bg_ye');
				}
			});

			//重置排序图标及状态
			var item = $(std.find(tag.sortBtn, $(input.orderField).val()));
			var sortIcon = $(item).find(std.findTag('icon-sort'));
			$(std.findTag('icon-sort')).removeClass('c_sort_asc');
			$(std.findTag('icon-sort')).removeClass('c_sort_desc');
			$(std.findTag('icon-sort')).addClass('c_sort');
			$(std.findTag('icon-sort')).not(sortIcon).attr('type', null);
			if ($(input.orderType).val() == '1') {
				sortIcon.attr('type', 'asc');
				sortIcon.removeClass('c_sort');
				sortIcon.removeClass('c_sort_desc');
				sortIcon.addClass('c_sort_asc');
			} else {
				sortIcon.attr('type', 'desc');
				sortIcon.removeClass('c_sort');
				sortIcon.removeClass('c_sort_asc');
				sortIcon.addClass('c_sort_desc');
			}
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

		paging.tosetInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
			paging.currentpage = currentpage;
			paging.pagesize = pagesize;
			paging.totalpage = totalpage;
			paging.totalsize = totalsize;

			$(selector.pagingContainer()).paging("setInfo", currentpage, pagesize, totalpage, totalsize);
		};

		paging.togetInfo = function(type) {//设置分页信息
			return $(selector.pagingContainer()).paging("option", type);
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