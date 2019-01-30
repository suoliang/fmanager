/**
 * 首页异步接口
 * @author GUOQIANG
 */
var homepage = homepage || {};
(function() {

	homepage.action = {
		listModule : std.u("/home/listModule.htm"),
		listUserViewBoard : std.u("/home/listViewBoard.htm"),
		updateBoardIndex : std.u("/home/updateBoardIndex.htm"),
		input : std.u("/home/input.htm"),
		createBoard : std.u("/home/saveBoard.htm"),
		updateBoard : std.u("/home/saveBoard.htm"),
		deleteBoard : std.u("/home/deleteBoard.htm"),
		findBoard : std.u("/home/findBoard.htm"),
		shareBoard : std.u("/setting/homepage/share.htm"),
		situation:"/home/situation.htm"
	};

	homepage.fillParamToLink = function(link, params) {
		assert.exists(link, "viewLink", "homepage.fillParamToLink");
		return util.appendArgToURL(ctx + link, params);
	};

	homepage.getModuleInterface = function(module, interfaceType) {
		assert.exists(module, "board.module", "homepage.getModuleInterface");
		var viewLink = undefined;
		$(module.interfaces).each(function(key, item) {
			if (item.type == interfaceType) {
				viewLink = item.url;
			}
		});
		return viewLink;
	};

	//检索配置组件
	homepage.listModule = function(fun) {
		assert.isFun(fun.success, "success", "homepage.listUserViewBoard");

		ajaxCommFun(homepage.action.listModule, {}, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	//检索配置组件
	homepage.listUserViewBoard = function(fun) {
		assert.isFun(fun.success, "success", "homepage.listUserViewBoard");

		var index = $.mask('正在加载配置...', false);

		ajaxCommFun(homepage.action.listUserViewBoard, {}, function(data) {

			layer.close(index);

			if (data.type == dict.action.suc) {
				fun.success(data.data);
			} else {
				alertMsg(data.message);
				if ($.isFunction(fun.error)) {
					fun.error(data.message, data.attrs);
				}
			}
		});
	};

	//检索配置组件
	homepage.listUserAllBoard = function(fun) {
		assert.isFun(fun.success, "success", "homepage.listUserAllBoard");

		ajaxCommFun(homepage.action.listUserAllBoard, {}, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.captureView = function(board, fun) {
		assert.exists(board, "board", "homepage.captureView");
		assert.isFun(fun.success, "success", "homepage.captureView");

		var viewLink = this.getModuleInterface(board.module, dict.homepage.ModuleInterfaceType.VIEW);
//		console.info(viewLink);
		ajaxCommFunText(ctx + viewLink, board.params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};
	
	homepage.captureBigView = function(board, fun) {
		assert.exists(board, "board", "homepage.captureBigView");
		assert.isFun(fun.success, "success", "homepage.captureBigView");
		var viewLink = this.getModuleInterface(board.module, dict.homepage.ModuleInterfaceType.VIEW);
		if(viewLink==homepage.action.situation){			
			ajaxCommFunText(ctx + viewLink, board.params+"&viewName=big", function(response) {
				if (response.type == dict.action.suc) {
					fun.success(response.data);
				} else {
					alertMsg(response.message);
					if ($.isFunction(fun.error)) {
						fun.error(response.message, response.attrs);
					}
				}
			});
		}
	};

	homepage.captureConfig = function(id, fun) {
		assert.isFun(fun.success, "success", "homepage.captureConfig");

		viewLink = $.isNumeric(id) ? util.appendArgToURL(homepage.action.input, "boardId=" + id) : homepage.action.input;

		ajaxCommFunText(viewLink, {}, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.captureModuleHtml = function(module, type, params, fun) {
		assert.exists(module, "module", "homepage.captureInput");
		assert.isFun(fun.success, "success", "homepage.captureInput");

		var link = this.getModuleInterface(module, type);

		ajaxCommFunText(ctx + link, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.captureModuleInput = function(inputLink, params, fun) {
		assert.exists(inputLink, "module", "homepage.captureInput");
		assert.isFun(fun.success, "success", "homepage.captureInput");

		ajaxCommFunText(ctx + inputLink, params, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	//更改用户组件位置
	homepage.updateBoardIndex = function(id, location, fun) {
		assert.exists(id, "id", "homepage.updateBoardIndex");
		assert.exists(location, "location", "homepage.updateBoardIndex");

		ajaxCommFun(homepage.action.updateBoardIndex, {
			boardId : id,
			index : location
		}, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.message, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.createBoard = function(moduleId, params, boardName, fun) {
		assert.exists(moduleId, "moduleId", "homepage.createBoard");
		assert.exists(params, "params", "homepage.createBoard");
		assert.isFun(fun.success, "success", "homepage.createBoard");

		ajaxCommFun(homepage.action.createBoard, {
			moduleId : moduleId,
			params : params,
			name : boardName
		}, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.updateBoard = function(boardId, moduleId, params, boardName, fun) {
		assert.exists(moduleId, "moduleId", "homepage.updateBoard");
		assert.exists(params, "params", "homepage.updateBoard");
		assert.isFun(fun.success, "success", "homepage.updateBoard");

		ajaxCommFun(homepage.action.updateBoard, {
			id : boardId,
			moduleId : moduleId,
			params : params,
			name : boardName
		}, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.deleteBoard = function(id, fun) {
		assert.exists(id, "id", "homepage.deleteBoard");
		assert.isFun(fun.success, "success", "homepage.deleteBoard");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(homepage.action.deleteBoard, "boardId=" + id, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.findBoard = function(id, fun) {
		assert.exists(id, "id", "homepage.findBoard");
		assert.isFun(fun.success, "success", "homepage.findBoard");

		ajaxCommFun(homepage.action.findBoard, {
			id : id
		}, function(response) {
			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

	homepage.shareBoard = function(boardId, userIds, fun) {
		assert.exists(boardId, "id", "homepage.findBoard");
		assert.isFun(fun.success, "success", "homepage.findBoard");

		var index = layer.load('正在提交请求...');

		ajaxCommFun(homepage.action.shareBoard, {
			boardId : boardId,
			userIds : userIds
		}, function(response) {

			layer.close(index);

			if (response.type == dict.action.suc) {
				if ($.isFunction(fun.success)) {
					fun.success(response.data, response.attrs);
				}
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};

})();