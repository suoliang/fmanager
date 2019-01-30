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
			return '#leaveWord-message-paging';
		},
		top : function() {
			return '.c_top';
		}
	};

	var tag = {
		messageLink : "btn-leave-detail",
		pagingAction : "paging-action",
		pagingInfo : "paging-info",
		sortBtn : 'btn-sort'
	};

	var input = {
		reportId : 'input[name=reportId]',
		orderField : 'input[name=orderField]',
		orderType : 'input[name=orderType]',
		startTimeInput : '#startTimeInput',
		endTimeInput : '#endTimeInput', 
		startTime : '#startTime', 
		endTime : '#endTime'
	};
	
	//******************** Page Function ********************//

	page.currentParam = '1=1';
	
	var status;
	var startTime;
	var endTime;
	page.initEvent = function() {
		//查询
		$('#searchBtn').click(function(event) {
			var pagesize = $("#leaveWord-message-paging").paging('option', 'pagesize');
			page.queryMessageBoard(1, pagesize);
		});
		
		//状态下拉框下拉效果   gengeral.js
		$.divselect('#divselect5', '#status-select-item');

		$(std.findTag("btn-back-to-brief")).click(function() {
			util.post(ctx + '/report/brief/index.htm', {
				pageNo : std.oid(this)
			});
		});
		//取得下拉框的值
		$.divselect("#divselect5", "#status-select-item", function(txt, value) {
			status = value;
		});
	};
	page.queryMessageBoard = function(pageNo, pagesize) {
		var param;
		param = $.extend(param, {
			pageNo : pageNo,
			pageSize : pagesize,
			status : $("[name='status']").val(),
			startTime :$("[name='startTime']").val(),
			endTime : $("[name='endTime']").val()
			
		});
		ajaxCommFunText(std.u("/setting/leaveWordReply/queryList.htm"), param, function(response) {
			if (response.type == dict.action.suc) {
				$('#messageBoardTable_id').empty();//清空
				// 刷新页面 response.data -> html
				$('#messageBoardTable_id').append(response.data);
				//事件注册
				$(std.findTag("reply")).click(function(){
					page.list.reply(this);
				});
				
			} else {
				alertMsg(response.message);
			}
		});
	}
	page.queryBriefArticle = function(pageNo, pagesize) {
		page.currentParam = page.options.serialize();
		MessageBoardReply.queryBriefMessageboard(page.currentParam + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) { 
				$('#messageBoardTable_id').empty();
				$('#messageBoardTable_id').html(html);//把controller返回的页面嵌入到页面
				page.list.reloadStyle();
				page.list.initEvent();
			}
		});
	}

	//******************** Option Function ********************//

	page.options = page.options || {};
	(function(options) {
		options.serialize = function() {//获取选项数据
			this.sync();
			return $('#MessageBoard-form').serialize();
		};
		options.sync = function() {//同步选项数据
			$('#status').val($('#status-select-item').val());
			$(input.startTime).val($(input.startTimeInput).val());
			$(input.endTime).val($(input.endTimeInput).val());
		};
		

		var startTime, endTime;
		var myDate = new Date();
		startTime = {
			elem : input.startTimeInput,
			format : 'YYYY-MM-DD',
			isclear : false, // 是否显示清空
			istoday : false, // 是否显示今天
			issure : true, // 是否显示确认
			max : myDate.toLocaleString(),
			choose : function(datas) {
				endTime.min = datas;
				endTime.start = datas;
//				if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//					startTime = datas;
//				}
			}
		};
		endTime = {
			elem : input.endTimeInput,
			format : 'YYYY-MM-DD',
			isclear : false, // 是否显示清空
			istoday : false, // 是否显示今天
			issure : true, // 是否显示确认
			max : myDate.toLocaleString(),
			choose : function(datas) {
				startTime.max = datas;
//				if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//					endTime = datas;
//				}
			}
		};
		laydate(startTime);
		laydate(endTime);
// laydate({
// elem : input.startTimeInput,
//			format : 'YYYY-MM-DD',
//			isclear : false, //是否显示清空
//			istoday : false, //是否显示今天
//			issure : true, //是否显示确认
//			choose : function(datas) {
//				endTimeInput.min = datas;
//				endTimeInput.start = datas
//				if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//					startTime = datas;
//				}
//			}
//		});
		$(input.startTimeInput).get(0).onkeydown = function(e) {
			console.log(e.keyCode || e.which || e.charCode);
			var key = e.keyCode || e.which || e.charCode;
			if (key == 8) {
				$(input.startTimeInput).val('');
				page.condition.limitTimeScope('', $(input.endTimeInput).val());
			} else {
				return false;
			}
		};
//		laydate({
//			elem : input.endTimeInput,
//			format : 'YYYY-MM-DD',
//			isclear : false, //是否显示清空
//			istoday : false, //是否显示今天
//			issure : true, //是否显示确认
//			choose : function(datas) {
//				if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//					endTime = datas;
//				}
//			}
//		});
		$(input.endTimeInput).get(0).onkeydown = function(e) {
			console.log(e.keyCode || e.which || e.charCode);
			var key = e.keyCode || e.which || e.charCode;
			if (key == 8) {
				$(input.endTimeInput).val('');
				page.condition.limitTimeScope($(input.startTimeInput).val(), '');
			} else {
				return false;
			}
		};
	})(page.options);

	//******************** List Function ********************//  

	
	
	
	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			$(std.findTag(tag.messageLink)).unbind('click').click(function() {//留言标题点击事件注册
				window.location.href = std.u("/setting/leaveWordReply/leaveWordInfoDetail.htm?id="+std.oid(this)+"&pageNo="+$('#messageReplyId').attr("value"));
			});
			$(std.findTag("reply")).click(function(){
				list.reply(this);
			});
		};
		list.reply = function(entity){
				$.box('#replyIDBox', {
					onOpen : function() {
						$("#def-option").click();
					}
				}, {
					submit : {
						close : false,
						dom : [ '#replyIDBox .c_but_ok' ],
						fun : function(index) {
							var replyRontent = $("#MessageBoardReply-content").val()
							if (replyRontent == "") {
								return false;
							}
							//异步添加对应的留言回复信息
							var messageBoardId=std.oid(entity);
							var param = {
									msgId : messageBoardId,// 留言ID
									content : replyRontent//回复内容
									}
							param = $.extend(param, {
								msgId : messageBoardId
							});
							outer = this;
							ajaxCommFun(std.u("/setting/leaveWordReply/addMessageBoardReply.htm"), param, function(response) {
								if (response.type == dict.action.suc) {
									outer.close(index);//“确定”关闭窗口
									// 刷新页面 
//									$("#leaveWord-message-paging").paging('refresh');
									var pagesize = $("#leaveWord-message-paging").paging('option', 'pagesize');
									var currentpage = $("#leaveWord-message-paging").paging('option', 'currentpage');
									page.queryMessageBoard(currentpage, pagesize);
								} else {
									alertMsg(response.message);
								}
							});
							
						}
					},
					close : {
						fun : function() {
							//新增后去掉先前记录
							$("#trueentity").val("");
							$("#virtualentity").val("");
						},
						dom : [ '#replyIDBox .c_but_no', '#replyIDBox .close' ]
					}
				});
				
				//异步查询对应的留言信息及填充到页面上
				var messageBoardId=std.oid(entity);
				var param = {id : messageBoardId}// 留言ID
				param = $.extend(param, {
					id : messageBoardId
				});
				ajaxCommFun(std.u("/setting/leaveWordReply/findMessageBoard.htm"), param, function(response) {
					if (response.type == dict.action.suc) {
						$("#MessageBoard-title").text("");
						$("#MessageBoard-content").text("");
						$("#MessageBoard-title").append(response.data.title);
						$("#MessageBoard-content").append(response.data.content);
					} else {
						alertMsg(response.message);
					}
				});
		};
		list.reloadStyle = function() {
			$(selector.listContainer()).children('div').each(function(prop) {
				$(this).removeClass('c_bg_ye');
				if (prop % 2 != 0) {
					$(this).addClass('c_bg_ye');
				}
			});
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
function forwardMessageLink(Obj){
	window.location.href = std.u("/setting/leaveWordReply/leaveWordInfoDetail.htm?id="+std.oid(Obj)+"&pageNo="+$('#messageReplyId').attr("value"));
	$(std.findTag("reply")).click(function(){
		page.list.reply(Obj);
	});
}
