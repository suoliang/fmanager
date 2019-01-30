/**
 * 首页：index.htm
 * @author LIUJUNWU
 */

//******************** Global Constant ********************//
var selector = {
	topicAdvanced : function() {
		return '#topic-advanced';
	},
	listContainer : function() {
		return '.topic-list-container';
	},
	listMask : function() {
		return '.topic-list-mask';
	},
	pagingContainer : function() {
		return '.topic-list-paging';
	},
	top : function() {
		return '.c_top';
	},
	totalSizeLabel : '#count-Show-i'
};

var tag = {
	
	articleSearch : "article-search",
	articleLink : "article-link",
	pagingAction : "paging-action",
	pagingInfo : "paging-info",
	mediaValueform : "mediaType-form",
	topicValueform : "topicId-form",
	filterSimilarValueform : "filterSimilar-form",
	searchPopup : "search-popup",
	searchPopupBox : "popup-box",
	more : "board-more",
	sortBtn : 'btn-sort',
	openBriefPickerBtn : 'btn-open-brief-picker',
	openExportWordBtn : 'all-article-word',
	openExportExcelBtn : 'all-article-excel',
};

var input = {
	keywordInput : '#keywordInput',
	startTimeInput : '#startTimeInput',
	endTimeInput : '#endTimeInput',
	keyword : 'input[name=keyword]',
	startTime : 'input[name=startTime]',
	endTime : 'input[name=endTime]',
	timeType : 'input[name=timeType]',
	mediaType : 'input[name=mediaType]',
	topicType : 'input[name=topicType]',
	orderField : 'input[name=orderField]',
	orderType : 'input[name=orderType]'
};

//******************** Page Function ********************//

var isExcel;
var type;
var page = page || {};
(function(page) {
	var isMsaking = false;
	
	page.currentParam = '1=1';

	page.initEvent = function() {//初始化事件   
		//搜索
		var topicFun = function() {
			if($('#customCheck').hasClass("active")==true&&$("#check").hasClass("active")==false){
				if($("#startTimeInput").val()==""){
					$.msg.warning('请选择起始日期');
				}else if($("#endTimeInput").val()=="") {
					$.msg.warning('请选择结束日期');
				}else{
					if(!$("[scope='option'][tag='time']").find(".active").attr("value")){
						page.currentParam = page.options.serialize();
					}else{
						$(input.keyword).val($(input.keywordInput).val());
						page.currentParam = $(selector.topicAdvanced()).serialize();
					}
					page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
			    }
			}else{
				if(!$("[scope='option'][tag='time']").find(".active").attr("value")){
					page.currentParam = page.options.serialize();
				}else{
					$(input.keyword).val($(input.keywordInput).val());
					page.currentParam = $(selector.topicAdvanced()).serialize();
				}
				page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
			}
		};
		
		$(input.keywordInput).keydown(function(e) {
			if (e && e.keyCode == 13) {
				topicFun();
			}
			e.stopPropagation();
		});
		
		$(std.find(tag.articleSearch, tag.articleSearch)).click(function() {
			topicFun();
		});

		//滚动
		$(window).scroll(function() {
			if (isMsaking) {
				return;
			}
			if ($(document).scrollTop() >= $(document).height() - $(window).height() - $(selector.top()).height()) {
				if (page.paging.currentpage % 4 == 0 || page.paging.currentpage >= page.paging.totalpage) {
					$(selector.pagingContainer()).show();
				} else {
					page.tosearch(page.paging.currentpage + 1, page.paging.pagesize ? page.paging.pagesize : 10);
				}
			}
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
			page.currentParam = page.options.serialize();
			page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
		}
		$(std.findTag(tag.sortBtn)).click(function() {
			$(input.orderField).val(std.oid(this));//阅读数
			doSort(this);
		});

		//清空
		$("#condition-empty").click(function() {
			//清空关键字
			$('#keywordInput').val("");
		});

		//"其它"事件初始化
		$(std.findTag('btn-click-otherTopic')).click(function() {
			page.options.otherTopic();
			page.currentParam = page.options.serialize();
			page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
		});
		
		//"导出全部(Word)"事件初始化
		$(std.findTag(tag.openExportWordBtn)).unbind('click').click(function() {
			isExcel = false;
			type = 16;
			exportbox.openExportPicker(std.oid(this), type);
		});
		
		//"导出全部(Excel)"事件初始化
		$(std.findTag(tag.openExportExcelBtn)).unbind('click').click(function() {
			isExcel = true;
			type = 13;
			exportbox.openExportPicker(std.oid(this), type);
		});
	};

	page.scorllTo = function(obj) {//定位到元素
		$("body,html").animate({
			scrollTop : $(obj).offset().top - 50
		}, 500);
	};

	page.tosearch = function(currentpage, pagesize) {//分页搜索

//		console.log("currentpage : " + currentpage + " pagesize : " + pagesize);
		$(selector.listMask()).show();
		isMsaking = true;
		topic.topicArticle(page.currentParam + "&currentpage=" + currentpage + "&pagesize=" + pagesize, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.reloadStyle();
				
				page.list.showMediaCount();
				/**显示完成后删除隐藏域的span及子元素*/
				$("#countMediaHidden").remove();
				
				page.list.initEvent();
				
				$(selector.listMask()).hide();
				isMsaking = false;
				
			}
		});
	};

	page.tosearchByPaging = function(currentpage, pagesize) {
		$(selector.listContainer()).empty();
		$(selector.pagingContainer()).hide();
		page.tosearch(currentpage, pagesize);
		page.scorllTo($(input.keywordInput));
	};
	
})(page);

//******************** Options Function ********************//
page.condition = page.condition || {};//options的辅助对象(options为对外开放对象)
//(function() {
page.options = page.options || {};
(function(options, condition) {
	var oid = {
		time : 'time',
		media : 'option-item',
		mediaall : 'site-item',
		filterSimilar : 'filterSimilar'
	};

	$(std.findTag(oid.time, '[scope=option]')).click(function() {//初始化事件
		options.clickTime(this);
		options.searchNow();
		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	});
	$(std.findTag(oid.media, '[scope=option]')).click(function() {//初始化事件
		options.clickMedia(this);
		options.searchNow();
		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	});
	$(std.findTag(oid.mediaall, '[scope=option]')).click(function() {//初始化事件
		options.clickMediaAll(this);
		options.searchNow();
		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	});
	$(std.findTag(oid.filterSimilar, '[scope=option]')).click(function() {//初始化事件
		options.clickfilterSimilar(this);
		options.searchNow();
		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	});
	$("#condition-empty").click(function() {//"清空"事件初始化
		//清空关键字
		$('#keywordInput').val("");
	});
	$(std.findTag('btn-click-otherTopic')).click(function() {//"其它"事件初始化
		options.otherTopic();
	});
	options.serialize = function() {//获取选项数据
		this.sync();
		return $(selector.topicAdvanced()).serialize();
	};
	options.conditionEmpty = function() {
		//清空关键字
		$('#keywordInput').val("");
		//取消时间媒体的选中状态选项
		$(".active").removeClass('active');
		$(".c_tag_active").removeClass('c_tag_active');

		$(std.find(tag.mediaValueform, "media")).empty();//清空媒体选项(form-value)
		$("#timeType").attr("value", "");//清空时间条件(form-value)
		//清空自定义时间
		$("#startTime").attr("value", "");
		$("#endTime").attr("value", "");
		$(input.startTimeInput).val("");
		$(input.endTimeInput).val("");
		if($('#c_timeck02').hasClass("c_none")==true){
			$('#c_timeck02').fadeOut('slow');
		}
		//清空专题选项
		$(std.findTag('btn-select-topic','[type=checkbox]')).removeClass('c_color_red');
		$(std.findTag('btn-select-topic','[type=checkbox]')).addClass('c_color_gay');
		$(std.find(tag.topicValueform, "topicIds")).empty();//清空专题选项(form-value)
		//------------带上所有专题--------------------------
		/*var topicIds = []; 
		$(std.findTag('btn-click-topic', '[scope=option]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			topicIds.push({
				id : value
			});
		});
		limitOption("topicIds", 'topicId', topicIds);
		//动态生成input
		 function limitOption(oid, inputName, options) {
			var formCtx = $(std.find("topicId-form", oid));
			formCtx.empty();
			
			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}*/
	}
	options.otherTopic = function() {
		if ($(this).parent().attr('selct') == 'false') {
			$(this).parent().attr('selct', 'true');
			$(this).removeClass('c_tree_no');
			$(this).addClass('c_tree_ok');
		} else {
			$(this).parent().attr('selct', 'false');
			$(this).removeClass('c_tree_ok');
			$(this).addClass('c_tree_no');
		}
		var topicIds = [];
		topicIds.push({
			id : 0
		});
		limitOption("topicIds", 'topicId', topicIds);
		//动态生成input
		function limitOption(oid, inputName, options) {
			var formCtx = $(std.find("topicId-form", oid));
			formCtx.empty();

			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
	};
	options.sync = function() {//同步选项数据
		$(input.keyword).val($(input.keywordInput).val());
		$(input.startTime).val($(input.startTimeInput).val());
		$(input.endTime).val($(input.endTimeInput).val());
	};
	options.set = function(view) {//设置选项
//		this.conditionEmpty();
		$(input.keywordInput).val(view.keywords);
		
		if (view.postStartTime) {//开始时间
			$(input.startTimeInput).val(view.postStartTime);
			$(input.startTime).val($(input.startTimeInput).val());
		}

		if (view.postEndTime) {//结束时间
			$(input.endTimeInput).val(view.postEndTime);
			$(input.endTime).val($(input.endTimeInput).val());
		}
		
//		if(view.postStartTime || view.postStartTime){
//			/**自定义时间选中处理*/
//			$('#c_timeck01').click();
//			$(input.startTime).val($(input.startTimeInput).val());
//			$(input.endTime).val($(input.endTimeInput).val());
//			
//		}
		
		if (view.postTimeType) {//时间类型
			options.clickTime($(std.find(oid.time, view.postTimeType, '[scope=option]')));
		}
		
		if (view.medias) {//媒体类型
			var text = "";
			var mediaOptions = [];
			$(view.medias).each(function(i, media) {
				if (media != null) {
					text += ", " + media.name;
					mediaOptions.push({
						id : media.value,
						name : media.name
					});
					/**媒体类型选中处理*/
					options.clickMedia($(std.find(oid.media, media.value, '[scope=option]')));
				}
			});
		}
		
		if (view.filterSimilar != null) {//相似文
			options.clickfilterSimilar($(std.find(oid.filterSimilar, view.filterSimilar, '[scope=option]')));
		}
		
//		if (view.topics) {
//			var text = "";
//			var topicOptions = [];
//			$(view.topics).each(function(i, topic) {
//				if (topic != null) {
//					text += ", " + topic.name;
//					topicOptions.push({
//						id : topic.id,
//						name : topic.name
//					});
//					/**专题选中处理*/
//					//options.clickTopic($(std.findTag('btn-select-topic')));
//				}
//			});
//		}
		
		if (view.orderField) {//排序
			$(input.orderField).val(view.orderField);
		} else {
			$(input.orderField).val(1);
		}
		if (view.orderType) {//排序
			$(input.orderType).val(view.orderType);
		} else {
			$(input.orderType).val(0);
		}
		
		options.searchNow();
	};
	
	options.searchNow = function() {
		page.currentParam = $(selector.topicAdvanced()).serialize();
//		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	};
	
	options.clickTopic = function(elmt) {
		$(std.findTag('btn-select-topic','[type=checkbox]')).removeClass('c_color_red');
		$(std.findTag('btn-select-topic','[type=checkbox]')).addClass('c_color_gay');
		$(std.findTag('btn-click-topic', '[scope=option]')).attr('selct', 'false');
		
		$(elmt).parent().attr('selct', 'true');
		$(elmt).removeClass('c_color_gay');
		$(elmt).addClass('c_color_red');
		
		var topicIds = []; 
		
		$(std.findTag('btn-click-topic', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			topicIds.push({
				id : value
			});
		});
		limitOption("topicIds", 'topicId', topicIds);
		//动态生成input
		 function limitOption(oid, inputName, options) {
			var formCtx = $(std.find("topicId-form", oid));
			formCtx.empty();
			
			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
	};
	
	
	options.clickTime = function(elmt) {
		//隐藏自定义时间
		$('#c_timeck02').fadeOut('slow');
		$('#c_timeck01 .c_choice_icon').removeClass("active");
		$('#c_timeck01 .c_choice_tag').removeClass("c_tag_active");
		//set值
		var value = $(elmt).find('[type=checkbox]').attr('value');
		$(input.timeType).val(value);
		if(value != null){
			$("[name='startTime']").val("");
			$("[name='endTime']").val("");
		}
		$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
		//即时搜索
//		page.currentParam = page.options.serialize();
		$(input.keyword).val($(input.keywordInput).val());

		if(value == null){
			$(input.startTime).val($(input.startTimeInput).val());
			$(input.endTime).val($(input.endTimeInput).val());
		}
	};
	options.clickMedia = function(elmt) {
		//设置“全部”的显示状态
		var mediaAll = $(std.find(oid.mediaall, "all", '[scope=option]'));
		$(mediaAll).attr('selct', 'false');
		$(std.findTag(oid.mediaall, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.mediaall, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		var media = $(std.find('option-item', std.oid(elmt), '[scope=option]'));
		if ($(media).attr('selct') == 'true') {
			$(media).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$(media).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		var medias = [];
		$(std.findTag('option-item', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			medias.push({
				id : value
			});
		});
		limitOption("media", 'mediaType', medias);

		//动态生成input
		function limitOption(oid, inputName, options) {
			var formCtx = $(std.find(tag.mediaValueform, oid));
			formCtx.empty();

			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
		
	};
	options.clickMediaAll = function(elmt) {
		if ($(std.findTag(oid.mediaall, '[scope=option]')).attr('selct') == 'true') {
			$(std.findTag(oid.mediaall, '[scope=option]')).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$(std.findTag(oid.mediaall, '[scope=option]')).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		$(std.findTag(oid.media, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');

		var medias = [];
		if($(std.findTag(oid.mediaall, '[scope=option]')).attr('selct') == 'true'){
			medias.push({
				id : [],
				name : '全部'
			});
		}
		
		limitOption("media", 'mediaType', medias);

		//动态生成input
		function limitOption(oid, inputName, options) {
			var formCtx = $(std.find(tag.mediaValueform, oid));
			formCtx.empty();

			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
	};
	
	options.clickfilterSimilar = function(elmt) {
		var filterSimilar = $(std.find(oid.filterSimilar, std.oid(elmt), '[scope=option]'));
		if ($(filterSimilar).attr('selct') == 'false') {
			selctTrueItme = $(std.findTag(oid.filterSimilar, '[scope=option][selct=true]'));
			selctTrueItme.each(function(i, item) {
				$(item).attr('selct', 'false');
				$(item).find('[type=checkbox]').removeClass('active');
				$(item).find('[type=metaname]').removeClass('c_tag_active');
			});
			$(filterSimilar).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}
		
		var filterSimilars = [];
		$(std.findTag(oid.filterSimilar, '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			filterSimilars.push({
				id : value
			});
		});
		limitOption("filterSimilar", 'filterSimilar', filterSimilars);

		//动态生成input
		function limitOption(oid, inputName, options) {
			var formCtx = $(std.find(tag.filterSimilarValueform, oid));
			formCtx.empty();

			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
		
	};
	
	options.exportAllWordOrExcel=function(userId) {
		var url=ctx+"/topic/exportWordThread.htm";
		if(isExcel){
			url=ctx+"/topic/exportExcelThread.htm";
		}
		exportTasks.addTask(userId, type);//添加任务
		ajaxCommFun(url, $("#topic-advanced").serialize(), function(response) {//导出全部Word、Excel
			if (response.type == dict.action.suc) {
				exportTasks.updateTaskStatus(taskId, userId, type, 2);//修改任务状态并刷新任务列表 
			} else {
				exportTasks.updateTaskStatus(taskId, userId, type, 3);//修改任务状态并刷新任务列表
			}
		});
	};
	
//	laydate({
//		elem : input.startTimeInput,
//		format : 'YYYY-MM-DD',
//		max : laydate.now(),
//		isclear : false, //是否显示清空
//		istoday : false, //是否显示今天
//		issure : true, //是否显示确认
//		choose : function(datas) {
//			page.condition.cancelCondition(oid.time);
//			if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//				page.condition.limitTimeScope(datas, $(input.endTimeInput).val());
//			}
//		}
//	});
//	$(input.startTimeInput).get(0).onkeydown = function(e) {
//		var key = e.keyCode || e.which || e.charCode;
//		if (key == 8) {
//			$(input.startTimeInput).val('');
//			page.condition.limitTimeScope('', $(input.endTimeInput).val());
//		} else {
//			return false;
//		}
//	};
	
	var start = {
		    elem: input.startTimeInput,
		    format: 'YYYY-MM-DD hh:mm:ss',
		    max: laydate.now(), //最大日期
		    isclear : true, //是否显示清空
			istoday : false, //是否显示今天
			istime: true,//是否显示时间
			issure : true, //是否显示确认
		    choose: function(datas){
		         end.min = datas; //开始日选好后，重置结束日的最小日期
		         //end.start = datas; //将结束日的初始值设定为开始日/**时分秒结束框需要从23:59:59开始*/
		    }
		};
	var end = {
		    elem: input.endTimeInput,
		    format:  'YYYY-MM-DD hh:mm:ss',
		    start : laydate.now(new Date().getTime()) + ' 23:59:59',
		    min: '1998-01-01',
		    max: laydate.now(),
		    isclear : true, //是否显示清空
			istoday : false, //是否显示今天
			istime: true,//是否显示时间
			issure : true, //是否显示确认
		    choose: function(datas){
		        start.max = datas; //结束日选好后，重置开始日的最大日期
		    }
		};
		laydate(start);
		laydate(end);
	
//	laydate({
//		elem : input.endTimeInput,
//		format : 'YYYY-MM-DD',
//		min : '1998-01-01',
//		max : laydate.now(),
//		isclear : false, //是否显示清空
//		istoday : false, //是否显示今天
//		issure : true, //是否显示确认
//		choose : function(datas) {
//			page.condition.cancelCondition(oid.time);
//			if (util.isNotNull(datas) && util.isNotBlank(datas)) {
//				page.condition.limitTimeScope($(input.startTimeInput).val(), datas);
//			}
//		}
//	});
//	$(input.endTimeInput).get(0).onkeydown = function(e) {
//		var key = e.keyCode || e.which || e.charCode;
//		if (key == 8) {
//			$(input.endTimeInput).val('');
//			page.condition.limitTimeScope($(input.startTimeInput).val(), '');
//		} else {
//			return false;
//		}
//	};
})(page.options);

//******************** List Function ********************//

page.list = page.list || {};
(function(list) {
	list.initEvent = function() {
		//查看文章详情
		$(std.findTag(tag.articleLink)).unbind('click').click(function() {
			$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/search/openArticleDetail.htm') + "'></form>");
			$("#param-form").append("<input type='hidden' name='guid' value='" + std.oid(this) + "'/>");
			$("#param-form").append("<input type='hidden' name='keyword' value='" + $("#keywordInput").val() + "'/>");
			document.createElement("a").onclick = $("#param-form").submit();
			$("#param-form").remove();
		});
		$(std.findTag(tag.openBriefPickerBtn)).unbind('click').click(function() {//加入简报
			popup.openBriefPicker(std.oid(this));
		});

		/**相似文点击*/
		$(std.findTag("to-similar")).unbind('click').click(function() {
			list.toSimilar($(this).attr("guidGroup"), $(this).attr("guid"));
		});
		
		//查询后事件注册
		$(std.findTag('add-briefing-theme')).unbind().click(function() {
			turnhandle(std.oid(this));//转操作下拉事件注册

			$(std.findTag('ctx-btn')).mouseleave(function() {//鼠标移走后隐藏转操作下拉
				$(this).find('ul').hide();
			});
		});

	};
	
	list.toSimilar = function(guidGroup, guid){
		var pagesize = page.paging.pagesize ? page.paging.pagesize : 10;
		/**全部专题处理*/
		var parameters = page.options.serialize().split("&topicId=");
		var topicId = parameters[1];
		console.log(topicId);
		var topicArray= topicId.split("%");
		if (topicArray.length > 1) {
			topicId = "";
			console.log(topicArray);
		}
		window.location.href = ctx+"/focusing/similarQueryInfo.htm?guid="+guid+"&guidGroup="+guidGroup+"&"+parameters[0]+"&topicId="+topicId+
		"&currentpage=1&pagesize="+pagesize/**此处返回不需要返回当前页*/
		+"&jumpLocation=topic";/**由于相似文页面公用一个,此字段控制返回按钮的跳转*/
	};

	list.renderDom = function(html) {//渲染文章数据
		$(selector.listContainer()).append(html);
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

	/**显示媒体类型的数量*/
	list.showMediaCount = function() {
		var allCountNum = 0;
		var countNum = $(std.findTag("countNum"));
		$(countNum).each(function(i,item){
			var numObj= $(std.find("countNum", $(item).attr("oid")));
			var num = numObj.val();
			allCountNum += Number(num);
			$(std.find("count-num", $(item).attr("oid"))).text("("+formatNum(num)+")");
		});
		$(std.findTag("count-all")).text("("+formatNum(allCountNum)+")");
	};
	
})(page.list);

//转操作
function turnhandle(guid) {
	var ul = $("#c_new" + guid);
	if (ul.css("display") == "none") {
		ul.slideDown();
	} else {
		ul.slideUp();
	}
}

//******************** Paging Function ********************//

page.paging = page.paging || {};
(function(paging) {
	paging.initEvent = function() {
		$(selector.pagingContainer()).hide();
		$(selector.pagingContainer()).paging({
			onChange : function(totalpage, totalsize) {
				$(selector.totalSizeLabel).html(formatNum(totalsize));
			},
			gotoNoImpl : function(pageNo, pagesize) {
				page.tosearchByPaging((pageNo - 1) * 4 + 1, pagesize / 4);
			}
		});
	};

	paging.tosetInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
		paging.currentpage = currentpage;
		paging.pagesize = pagesize;
		paging.totalsize = totalsize;
		paging.totalpage = totalpage;

		if (paging.currentpage >= paging.totalpage) {
			$(selector.pagingContainer()).show();
		}

		currentpage = Math.ceil(currentpage / 4);
		pagesize = pagesize * 4;
		totalpage = Math.ceil(totalpage / 4);
		totalsize = totalsize;
		$(selector.pagingContainer()).paging("setInfo", currentpage, pagesize, totalpage, totalsize);
	};


	paging.togetInfo = function(type) {//设置分页信息
		return $(selector.pagingContainer()).paging("option", type);
	};
})(page.paging);

//******************** INIT Function ********************//
$(function() {
	//page init
	page.initEvent();

	//list init
	page.list.initEvent();

	//paging init
	page.paging.initEvent();

	//******************** 业务需求初始化 ********************//

	$(selector.listMask()).hide();
	//$(std.findTag('time', '[scope=option]')).get(0).click();
});