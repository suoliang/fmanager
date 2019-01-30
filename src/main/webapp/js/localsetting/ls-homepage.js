//******************** Global Constant ********************//

var selector = {
	boardContainer : function() {
		return '.board-container';
	},
	pagingContainer : function() {
		return '#homepage-paging';
	},
	boardSearchBtn : function() {
		return '.board-search';
	}
};

var tag = {
	boardShare : 'board-share',
	boardShareInfo : 'board-share-info'
}

//******************** Setting Function ********************//

var page = page || {};
(function(page) {
	page.currentParam = '1=1';

	page.queryBoard = function(pageNo, pagesize) {//分页搜索

		console.log("currentpage : " + pageNo + " pagesize : " + pagesize);

		setting.queryBoard(page.currentParam + "&currentpage=" + pageNo + "&pagesize=" + pagesize, {
			success : function(html) {
				$(selector.boardContainer()).empty();
				$(selector.boardContainer()).append(html);

				$(std.findTag(tag.boardShare)).unbind('click').click(function() {
					page.shareBoard(std.oid(this));
				});
			}
		});
	};

	page.shareBoard = function(boardId) {

		var doShareBoard = function(userIds) {
			homepage.shareBoard(boardId, userIds, {
				success : function(board) {
					var shareNameInfo = util.joinCollection(board.users, ',', 'realName');
					$(std.find(tag.boardShareInfo, board.id)).html(shareNameInfo);
					layer.closeAll();
					layer.msg("共享成功！", 2, 1);
				}
			});
		};

		homepage.findBoard(boardId, {
			success : function(board) {
				popup.openUserPicker(board.userIds, function(userIds) {
					doShareBoard(util.joinCollection(userIds, ','));
				});
			}
		});
	};
})(page);

//******************** Paging Function ********************//

page.paging = page.paging || {};
(function(paging) {
	paging.tosetInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
		$(selector.pagingContainer()).paging("setInfo", currentpage, pagesize, totalpage, totalsize);
	};

	paging.togetInfo = function(type) {//获取分页信息
		return $(selector.pagingContainer()).paging("option", type);
	};
})(page.paging);

//******************** INIT Function ********************//

$(function() {
	$(selector.pagingContainer()).paging({
		gotoNoImpl : function(pageNo, pagesize) {
			page.queryBoard(pageNo, pagesize);
		}
	});

	$(std.findTag(tag.boardShare)).click(function() {
		page.shareBoard(std.oid(this));
	});

	$(selector.boardSearchBtn()).click(function() {
		page.currentParam = $("#condition-form").serialize();
		page.queryBoard(1, page.paging.togetInfo('pagesize'));
	});

	$(std.findTag('board-action-sort')).each(function(i, item) {
		$(item).unbind('click').bind('click', function() {
			$(this).focus().select();
		});
	});
});