/**
 * 首页：index.htm
 * @author GUQIANG
 */

var validate_board_input = function() {
};

var config = config || {};
(function(page) {

	//******************** Closure Variable ********************//

	var selector = {
		boardContainer : function() {
			return '.board-container';
		},
		boardHeader : function() {
			return '.board-header';
		},
		config : function() {
			return '.board-icon.config';
		},
		close : function() {
			return '.board-icon.close';
		}
	}

	var tag = {
		board : "board-frame",
		boardMask : "board-mask",
		header : "board-header",
		title : "board-title",
		body : "board-body",
		config : "board-config",
		input : "board-input",
		refreshBtn : "board-refresh-btn",
		configBtn : "board-config-btn",
		moreBtn : "board-more-btn",
		closeBtn : "board-close-btn",
		submitBtn : "board-config-submit-btn",
		cancelBtn : "board-config-cancel-btn",
		icon : "board-icon"
	}

	//******************** Page Variable ********************//

	page.boards = [];

	//******************** Page Function ********************//

	page.initHtml = function() {
		$(page.boards).each(function(key, item) {
			//生成框架
			page.renderBoardFrame(item);
			//生成页面
			page.renderBoardView(item);
		});
		page.addBoardNew();//生成新建板块
	};

	page.initData = function() {
	};

	page.initEvent = function() {
		//可拖拽
		$(selector.boardContainer()).sortable({
			items : '.board-frame:not([oid=new])',
			handle : selector.boardHeader(),
			delay : 200,
			stop : function(event, ui) {
				page.onmove(std.oid(ui.item), ui.item.index() + 1);
			}
		});

		//加载板块事件
		$(std.findTag(tag.board)).each(function() {
			page.loadBoardEvent(std.oid(this));
		});
	};

	page.addBoardNew = function() {
		$(selector.boardContainer()).append('' + //
		'<div class="c_block c_mr_pre07 c_bg_add c_fl board-new-config"' + std.flag(tag.configBtn, 'new') + '>' + //
		'	<div class="c_sprite c_add"></div>' + //
		'</div>');

		page.loadBoardEvent('new');
	}

	page.addBoardNewConfig = function() {
		//renderBoardFrame 但是不添加视图页面
		$(selector.boardContainer()).append('' + //
		'<div class="c_block c_mr_pre07 c_bg_block c_border c_fl board-new-config"' + std.flag(tag.board, 'new') + '>' + //
		'	<div class="c_bg_titbg c_h30 c_block_title board-header"' + std.flag(tag.header, 'new') + '>' + //
		'		<i class="c_icon_tool c_sprite"></i>' + //
		'		<span' + std.flag(tag.title, 'new') + '>板块设置</span>' + //
		'		<ul class="c_fr">' + //
		'			<li><a href="javascript:void(0);" class="c_sprite c_close"' + std.flag(tag.closeBtn, 'new') + '></a></li>' + //
		'		</ul>' + //
		'	</div>' + //
		'	<div class="c_block_con">' + //
		'		<div class="c_block_loading c_none"' + std.flag(tag.boardMask, 'new') + '></div>' + //
		'		<div class="board-config"' + std.flag(tag.config, 'new') + '></div>' + //
		'	</div>' + //
		'</div>');

		page.loadBoardEvent('new');
	}

	page.loadBoardEvent = function(oid) {
		//刷新
		$(std.find(tag.refreshBtn, oid)).unbind('click').click(function() {
			page.onrefresh(std.oid(this));
		});
		//配置
		$(std.find(tag.configBtn, oid)).unbind('click').click(function() {
			page.onconfig(std.oid(this));
		});
		//更多
		$(std.find(tag.moreBtn, oid)).unbind('click').click(function() {
			page.ongotoMore(std.oid(this));
		});
		//关闭
		$(std.find(tag.closeBtn, oid)).unbind('click').click(function() {
			page.onclose(std.oid(this));
		});
	};

	page.loadConfigEvent = function(oid) {
		// 确定
		$(std.find(tag.submitBtn, oid)).unbind('click').click(function() {
			page.onsubmit(oid);
		});
		//取消
		$(std.find(tag.cancelBtn, oid)).unbind('click').click(function() {
			page.oncancel(oid);
		});
	};

	page.renderBoardFrame = function(board) {
		var moreLink = homepage.getModuleInterface(board.module, dict.homepage.ModuleInterfaceType.MORE);

		var confBtn = !board.isDefault;
		var moreBtn = moreLink ? true : false;

		$(selector.boardContainer()).append('' + //
		'<div class="c_block c_mr_pre07 c_bg_block c_border c_fl board-frame"' + std.flag(tag.board, board.id) + '>' + //
		'	<div class="c_bg_titbg c_h30 c_block_title board-header"' + std.flag(tag.header, board.id) + '>' + //
		'		<i class="' + board.module.icon + ' c_sprite"></i>' + //
		'		<span' + std.flag(tag.title, board.id) + '>' + board.name + '</span>' + //
		'		<ul class="c_fr">' + //
		'			<li' + std.flag(tag.refreshBtn, board.id) + '><a href="javascript:void(0);" class="c_sprite c_refresh"></a></li>' + //
		(confBtn ? '<li' + std.flag(tag.configBtn, board.id) + '><a href="javascript:void(0);" class="c_sprite c_setup"></a></li>' : '') + //
		(moreBtn ? '<li' + std.flag(tag.moreBtn, board.id) + '><a href="' + ctx + moreLink + '?' + board.params + '" class="c_sprite c_more"></a></li>' : '') + //
		'			<li' + std.flag(tag.closeBtn, board.id) + '><a href="javascript:void(0);" class="c_sprite c_close"></a></li>' + //
		'		</ul>' + //
		'	</div>' + //
		'	<div class="c_block_con">' + //
		'		<div class="c_block_loading c_none"' + std.flag(tag.boardMask, board.id) + '></div>' + //
		'		<div class="board-body"' + std.flag(tag.body, board.id) + '></div>' + //
		'		<div class="board-config"' + std.flag(tag.config, board.id) + '></div>' + //
		'	</div>' + //
		'</div>');
	};

	page.renderBoardView = function(board) {
		$(std.find(tag.boardMask, board.id)).show();
		homepage.captureView(board, {
			success : function(html) {
				$(std.find(tag.body, board.id)).html(html);
				$(std.find(tag.boardMask, board.id)).hide();
			}
		});
	};

	page.renderBoardConfig = function(oid) {
		$(std.find(tag.boardMask, oid)).show();
		homepage.captureConfig(oid, {
			success : function(html) {
				$(std.find(tag.config, oid)).html(html);
				page.loadConfigEvent(oid);
				$(std.find(tag.boardMask, oid)).hide();
			}
		});
	};

	page.onmove = function(id, location) {
		homepage.updateBoardIndex(id, location, {
			error : function() {
				$(selector.boardContainer()).sortable("cancel");
			}
		});
	};

	page.onclose = function(id) {
		if (id == 'new') {
			$(std.find(tag.board, id)).remove();
		} else {
			if ($(std.findTag(tag.input)).size() > 0) {
				alert("有未保存的配置请先保存。");
				var oid = std.oid($(std.findTag(tag.input)).first());
				page.onscorll($(std.find(tag.board, oid)));
			} else {
				homepage.deleteBoard(id, {
					success : function(msg, attrs) {
						$(std.find(tag.board, id)).remove();
					}
				});
			}
		}
	};

	page.onconfig = function(id) {
		if ($(std.findTag(tag.input)).size() > 0) {
			alert("有未保存的配置请先保存。");
			var oid = std.oid($(std.findTag(tag.input)).first());
			page.onscorll($(std.find(tag.board, oid)));
		} else {
			if (id == 'new') {
				$(std.find(tag.configBtn, 'new')).remove();//移除新建按钮
				page.addBoardNewConfig();//生成新建board板块
				page.addBoardNew();//生成新建按钮
			}
			page.renderBoardConfig(id);//渲染配置页面
			page.showConfig(id);//显示配置页
		}
	};

	page.onrefresh = function(id) {
		if (id == 'new') {
			return;
		} else {
			homepage.findBoard(id, {
				success : function(board) {
					page.renderBoardView(board);
				}
			});
		}
	};

	page.ongotoMore = function(id) {
	};

	page.onsubmit = function(boardId) {
		var message = validate_board_input();
		if (message) {
			alert(message);
			return;
		}

		var form = $(std.find(tag.config, boardId)).find('[tag=module-form][selected=selected]');
		var boardName = $(form).find('input[name=boardName]').val()
		if (boardName == null || boardName == "") {
			alert("板块名称不能为空");
			return;
		}

		var param = $(form).serialize();

		console.log(param);

		$(std.find(tag.boardMask, boardId)).show();
		if (boardId == 'new') {
			homepage.createBoard($(form).attr('oid'), param, boardName, {
				success : function(board) {
					$(std.find(tag.board, 'new')).remove();//移除新建板块
					$(std.find(tag.configBtn, 'new')).remove();//移除新建按钮

					page.renderBoardFrame(board);//生成新板块框架
					page.renderBoardView(board);//渲染新板块页面
					page.loadBoardEvent(board.id);//加载新板块事件

					page.addBoardNew();//生成新建按钮

					$(std.find(tag.boardMask, boardId)).hide();
				}
			});
		} else {
			homepage.updateBoard(boardId, $(form).attr('oid'), param, $(form).find('input[name=boardName]').val(), {
				success : function(board) {
					//page.renderBoardFrame(board);只要该标题和更多链接
					var title = $(std.find(tag.title, board.id));
					var more = homepage.getModuleInterface(board.module, dict.homepage.ModuleInterfaceType.MORE);
					$(title).text(board.name);
					$(title).attr('href', ctx + more);

					page.showView(board.id);//显示视图页
					page.renderBoardView(board);//渲染更新后的板块页面

					$(std.find(tag.boardMask, boardId)).hide();
				}
			});
		}
	};

	page.oncancel = function(id) {
		if (id == 'new') {
			page.onclose(id);
		}
		page.showView(id);
	};

	page.onscorll = function(obj) {
		$("body,html").animate({
			scrollTop : $(obj).offset().top - 50
		}, 500);
	};

	page.showView = function(id) {
		$(std.find(tag.config, id)).empty();
		$(std.find(tag.config, id)).hide();
		$(std.find(tag.body, id)).show();

		$(std.find(tag.moreBtn, id)).show();
		$(std.find(tag.refreshBtn, id)).show();
		$(std.find(tag.configBtn, id)).show();
		$(std.find(tag.closeBtn, id)).show();
	};

	page.showConfig = function(id) {
		$(std.find(tag.config, id)).show();
		$(std.find(tag.body, id)).hide();

		$(std.find(tag.moreBtn, id)).hide();
		$(std.find(tag.refreshBtn, id)).hide();
		if (id != 'new') {
			$(std.find(tag.configBtn, id)).hide();//特殊化，新建板块配置按钮tag与普通板块配置按钮tag相同，但是新建板块无需隐藏
			$(std.find(tag.closeBtn, id)).hide();
		}
	};

	//******************** INIT Function ********************//

	$(function() {
		homepage.listUserViewBoard({
			success : function(boards) {
				if (boards != null) {
					page.boards = boards;
				}

				page.initHtml();//渲染页面
				page.initData();//初始化数据
				page.initEvent();//初始化事件
			}
		});

		//		window.onbeforeunload = function(e) {
		//			if ($(std.findTag(tag.input)).size() > 0) {
		//				return "有未保存的板块配置";
		//			}
		//		};
	});

})(config);