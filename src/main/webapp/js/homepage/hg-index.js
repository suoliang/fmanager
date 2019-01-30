/**
 * 首页：index.htm
 * @author GUQIANG
 */
var index = index || {};
(function(page) {

	//******************** Closure Variable ********************//

	var selector = {
		boardContainer : function() {
			return '.board-container';
		}
	};

	var tag = {
		board : "board-frame",
		header : "board-header",
		body : "board-body",
		more : "board-more"
	};

	//******************** Page Function ********************//

	page.initData = function() {
	};

	page.initEvent = function() {
	};

	page.renderHtml = function(boards) {
		if (boards == null) {
			return;
		}
		$(boards).each(function(key, item) {
			//生成框架
			page.renderHtmlFrame(item);

			//生成页面
			page.renderHtmlView(item);
		});
	};

	page.renderHtmlFrame = function(board) {
		var moreLink = homepage.getModuleInterface(board, dict.homepage.ModuleInterfaceType.MORE);
		$(selector.boardContainer()).append('' + //
		'<div class="board-frame"' + std.flag(tag.board, board.id) + '>' + //
		'	<div class="board-header"' + std.flag(tag.header, board.id) + '>' + board.name + //
		'		<a class="board-more" target="_blank" ' + std.flag(tag.more, board.id) + ' href="' + ctx + moreLink + '">更多</a>' + //
		'	</div>' + //
		'	<div class="board-body"' + std.flag(tag.body, board.id) + '></div>' + //
		'</div>');
	};

	page.renderHtmlView = function(board) {
		homepage.captureView(board, {
			success : function(data) {
				$(std.find(tag.body, board.id)).html(data);
			}
		});
	};

	//******************** INIT Function ********************//

	$(function() {
		homepage.listUserViewBoard({
			success : function(boards) {
				//渲染页面
				page.renderHtml(boards);
				//初始化数据
				page.initData(boards);
				//初始化事件
				page.initEvent(boards);
			}
		});
	});

})(index);