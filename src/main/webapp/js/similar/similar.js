var page = page || {};
(function() {
	//******************** Global Constant ********************//
	var selector = {
			
		pagingContainer : function() {
			return '#similar-message-paging';
		},
		totalSizeLabel : '#count-Show-i'

	};

	var tag = {
		messageLink : "title_tag",
	};


	//******************** Page Function ********************//

	page.currentParam = '1=1';

	page.initEvent = function() {

//		$(std.findTag("btn-back-to-brief")).click(function() {
//			util.post(ctx + '/focusing/similarQueryInfo.htm', {
//				pageNo : std.oid(this)
//			});
//		});

	};
	
	page.similarQueryInfo = function(pageNo, pagesize) {
		var params={
				guidGroup : $("#similarGuidGroup").val(),
				guid : $("#similarGuid").val(),
				pageNo : pageNo,
				pageSize : pagesize
		}
		MessageBoard.similarQueryInfo(params, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.initEvent();
			}
		});
	};

	//******************** List Function ********************//
	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			//查看文章详情
			$(std.findTag(tag.messageLink)).unbind('click').click(function() {
				$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/search/openArticleDetail.htm') + "'></form>");
				$("#param-form").append("<input type='hidden' name='guid' value='" + std.oid(this) + "'/>");
				document.createElement("a").onclick = $("#param-form").submit();
				$("#param-form").remove();
			});
			
			/**返回按钮*/
			$(std.findTag("returnBack")).unbind('click').click(function() {
				var params = window.location.search;
				/**参数中中文乱码问题处理*/
				params = encodeURI(params);
				
				var locationUrl = list.getUrlParam("jumpLocation");
				
				if (locationUrl == "search") {
					//+"&jumpLocation=search";/**由于相似文页面公用一个,此字段控制返回按钮的跳转*/
					location.href = std.u("/search/index.htm")+params;
					return;
				} else if (locationUrl == "topic") {
					location.href = std.u("/topic/index.htm")+params;
					return;
				} else {
					/**浏览器兼容性*/
					location.href = 'javascript:history.go(-1)';
					return;
				}
				
			});
			
		};

		list.getUrlParam = function(name){/**获取浏览器URL参数*/
		    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		    var r = window.location.search.substr(1).match(reg);
		    if(r!=null)return  unescape(r[2]); return null;
		};
		
		list.renderDom = function(html) {//渲染文章数据
			$('.similar-list-container').empty();
			$('.similar-list-container').html(html);
		};
	})(page.list);

	//******************** Paging Function ********************//

	page.paging = page.paging || {};
	(function(paging) {
		paging.initEvent = function() {
			$(selector.pagingContainer()).paging({
				onChange : function(totalpage, totalsize) {
					$(selector.totalSizeLabel).html(formatNum(totalsize));
				},
				gotoNoImpl : function(pageNo, pagesize) {
					page.similarQueryInfo(pageNo, pagesize);
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