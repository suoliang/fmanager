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
		return '.recommend-list-container';
	},
	listMask : function() {
		return '.recommend-list-mask';
	},
	pagingContainer : function() {
		return '.recommend-list-paging';
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
	searchPopup : "search-popup",
	columnIdsform : "columnIds-form",
	searchPopupBox : "popup-box",
	more : "board-more",
	sortBtn : 'btn-sort',
	openBriefPickerBtn : 'btn-open-brief-picker',
	
};

var input = {
	keyWordInput : '#keyWordInput',
	createTimeStartInput : '#createTimeStartInput',
	createTimeEndInput : '#createTimeEndInput',
	keyWord : 'input[name=keyWord]',
	createTimeStart : 'input[name=createTimeStart]',
	createTimeEnd : 'input[name=createTimeEnd]',
	timeType : 'input[name=timeType]',
	mediaType : 'input[name=mediaType]',
	topicType : 'input[name=topicType]',
	orderField : 'input[name=orderField]',
	type : 'input[name=type]',
	columnIds : 'input[name=columnIds]',
	columnId : 'input[name=columnId]'
};

//******************** Page Function ********************//

var type;
var page = page || {};
(function(page) {
	var isMsaking = false;

	page.currentParam = '1=1';

	page.initEvent = function() {//初始化事件   
		//搜索
		var topicFun = function() {
			if($('#customCheck').hasClass("active")==true&&$("#check").hasClass("active")==false){
				if($("#createTimeStartInput").val()==""){
					$.msg.warning('请选择起始日期');
				}else{
					if(!$("[scope='option'][tag='time']").find(".active").attr("value")){
						page.currentParam = page.options.serialize();
					}else{
						$(input.keyWord).val($(input.keyWordInput).val());
						page.currentParam = $(selector.topicAdvanced()).serialize();
					}
					page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
			    }
			}else{
				if(!$("[scope='option'][tag='time']").find(".active").attr("value")){
					page.currentParam = page.options.serialize();
				}else{
					$(input.keyWord).val($(input.keyWordInput).val());
					page.currentParam = $(selector.topicAdvanced()).serialize();
				}
				page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
			}
		};
		
		$(input.keyWordInput).keydown(function(e) {
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


		//清空
		$("#condition-empty").click(function() {
			page.options.conditionEmpty();
			page.currentParam = page.options.serialize();
			page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
		});


		
		pageStart();
		
		function pageStart(){
			//专题-时间
				$('#c_timeck01').click(function() {
					//取消勾选“今天,昨日....”
					$(std.findTag("time", '[scope=option]')).find('[type=checkbox]').removeClass('active');
					$(std.findTag("time", '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
					$("#timeType").attr("value","");//不带时间条件
					//即时搜索
//					page.currentParam = page.options.serialize();
//					page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
					if ($('#c_timeck02').is(':hidden')) {
						$('#c_timeck02').fadeIn('slow');
						$('#c_timeck01 .c_choice_icon').addClass("active");
						$('#c_timeck01 .c_choice_tag').addClass("c_tag_active");
					} else {
						$('#c_timeck02').fadeOut('slow');
						$('#c_timeck01 .c_choice_icon').removeClass("active");
						$('#c_timeck01 .c_choice_tag').removeClass("c_tag_active");			
					}
				});
				//下拉
				$('#c_sp1').click(function() {
					if ($('#c_sp2').is(':hidden')) {
						$('#c_sp1_i').html('▲');
						$('#c_sp1').text('隐藏筛选条件');
						$('#c_sp2').slideDown('slow');
					} else {
						$('#c_sp1_i').html('▼');
						$('#c_sp1').text('更多筛选条件');
						$('#c_sp2').slideUp('slow');
					}
				});
			}
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

		recommend.recommendArticle(page.currentParam + "&currentpage=" + currentpage + "&pagesize=" + pagesize, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.reloadStyle();
				page.list.initEvent();

				$(selector.listMask()).hide();
				isMsaking = false;

			}
		});
	};

	page.totopicByPaging = function(currentpage, pagesize) {
		$(selector.listContainer()).empty();
		$(selector.pagingContainer()).hide();
		page.tosearch(currentpage, pagesize);
		page.scorllTo($(input.keyWordInput));
	};
	page.totopicByPagingStart = function(currentpage, pagesize) {
		$(selector.listContainer()).empty();
		$(selector.pagingContainer()).hide();
		page.tosearch(currentpage, pagesize);
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
		columnIds : 'columnIds-form'
	};
	
	//加载勾选“今天”
	$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox][value=1]').addClass('active');
	$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname][value=1]').addClass('c_tag_active');

	//加载勾选“全部”
	$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
	$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	$(std.findTag(oid.time, '[scope=option]')).click(function() {//初始化事件
		options.clickTime(this);
	});
	$(std.findTag(oid.media, '[scope=option]')).click(function() {//初始化事件
		options.clickMedia(this);
	});
	$(std.findTag(oid.mediaall, '[scope=option]')).click(function() {//初始化事件
		options.clickMediaAll(this);
	});
	$("#condition-empty").click(function() {//"清空"事件初始化
		options.conditionEmpty();
	});
	options.serialize = function() {//获取选项数据
		this.sync();
		return $(selector.topicAdvanced()).serialize();
	};
	options.conditionEmpty = function() {
		//清空关键字
		$('#keyWordInput').val("");
		/*$('#type').val("");
		$("#column").hide();
		$("#columnIds").val("");
		//取消时间媒体的选中状态选项
		$(".active").removeClass('active');
		$(".c_tag_active").removeClass('c_tag_active');

		$(std.find(tag.mediaValueform, "media")).empty();//清空媒体选项(form-value)
		$("#timeType").attr("value", "");//清空时间条件(form-value)
		//清空自定义时间
		$("#createTimeStart").attr("value", "");
		$("#createTimeEnd").attr("value", "");
		$(input.createTimeStartInput).val("");
		$(input.createTimeEndInput).val("");*/
		
	}
	
	
	options.sync = function() {//同步选项数据
		$(input.keyWord).val($(input.keyWordInput).val());
		$(input.createTimeStart).val($(input.createTimeStartInput).val());
		$(input.createTimeEnd).val($(input.createTimeEndInput).val());
	};
	options.set = function(view) {//设置选项
		if (view.postTimeType) {//时间类型
			$(input.timeType).val(1);//设置为“今天”
			//即时搜索
			page.currentParam = page.options.serialize();
			page.totopicByPagingStart(1, page.paging.pagesize ? page.paging.pagesize : 10);
		}
	};
	options.clickTime = function(elmt) {
		//隐藏自定义时间
//		debugger
		$('#c_timeck02').fadeOut('slow');
		$('#c_timeck01 .c_choice_icon').removeClass("active");
		$('#c_timeck01 .c_choice_tag').removeClass("c_tag_active");
		//set值
		var value = $(elmt).find('[type=checkbox]').attr('value');
		$(input.timeType).val(value);
		if(value != null){
			$("[name='createTimeStart']").val("");
			$("[name='createTimeEnd']").val("");
		}
		$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
		//即时搜索
//		page.currentParam = page.options.serialize();
		$(input.keyWord).val($(input.keyWordInput).val());

		if(value == null){
			$(input.createTimeStart).val($(input.createTimeStartInput).val());
			$(input.createTimeEnd).val($(input.createTimeEndInput).val());
		}
		page.currentParam = $(selector.topicAdvanced()).serialize();
		page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	};
	options.clickMedia = function(elmt) {
		//设置“全部”的显示状态
		
		var media = $(std.find('option-item', std.oid(elmt), '[scope=option]'));
		$(input.columnIds).val("");
		if(std.oid(elmt)==0){
			$("#column").hide();
		}else{
			$("#column").show();
		}
		
		$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
		options.clickColumnId(media);
		
		//即时搜索
		page.currentParam = page.options.serialize();
		page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	};
	
	//栏目名称事件
	options.clickColumnId = function(elmt) {
		var type=$(elmt).attr("value");
		$("#type").val(type);
		
		recommend.recommendColumn("type="+type, {
			success : function(tasks) {
				$("#types").empty();
				if (util.isBlank(tasks)) {
					return;
				}
				$.each(tasks, function(i, item) {
					 $("#types").append("<div class='c_tab_choice c_fl' scope='option' tag='option-column' oid='"+item.id+"' selct='false'>" +
					 	"<div class='c_sprite c_choice_icon c_fl' type='checkbox' tag='option-hook' oid='"+item.id+"' value='"+item.id+"'></div>" +
					 	"<div class='c_choice_tag c_fl' title='"+item.name+"' tag='option-name' oid='"+item.id+"' type='metaname'>"+item.name+"</div></div>");
					 if(item.name==null){
							$("#column").hide();
						}
				});
				
				var oid = {
						column : 'option-column'
					};
				$(std.findTag(oid.column, '[scope=option]')).click(function() {//初始化事件
					clickColumn(this);
				});
				clickColumn = function(elmt) {
					var columnId = $(std.find('option-column', std.oid(elmt), '[scope=option]'));
					if ($(columnId).attr('selct') == 'true') {
						$(columnId).attr('selct', 'false');
						$(elmt).find('[type=checkbox]').removeClass('active');
						$(elmt).find('[type=metaname]').removeClass('c_tag_active');
					} else {
						$(columnId).attr('selct', 'true');
						$(elmt).find('[type=checkbox]').addClass('active');
						$(elmt).find('[type=metaname]').addClass('c_tag_active');
					}
					//set值
//					var value = $(elmt).find('[type=checkbox]').attr('value');
//					$(input.columnId).val(value);
					
					var columnIds= [];
					$(std.findTag('option-column', '[scope=option][selct=true]')).each(function(i, item) {
						var value = $(item).find('[type=checkbox]').attr('value');
						$(input.columnIds).val(value);
						columnIds.push({
							columnIds : value
						});
					});
					limitOption("columnId", 'columnIds', columnIds);
					function limitOption(oid, inputName, options) {
						var formCtx = $(std.find(tag.columnIdsform, oid));
						formCtx.empty();
						
						$(options).each(function(i, option) {
							formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.columnIds + '" />');
						});
					}
					//即时搜索
					page.currentParam = page.options.serialize();
					page.totopicByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
				};
			}
		});
	};
	
	
	
	var start = {
		    elem: input.createTimeStartInput,
		    format: 'YYYY-MM-DD',
		    max: laydate.now(), //最大日期
		    isclear : false, //是否显示清空
			istoday : false, //是否显示今天
			issure : true, //是否显示确认
		    choose: function(datas){
		         end.min = datas; //开始日选好后，重置结束日的最小日期
		         end.start = datas; //将结束日的初始值设定为开始日
		    }
		};
	var end = {
		    elem: input.createTimeEndInput,
		    format:  'YYYY-MM-DD',
		    min: '1998-01-01',
		    max: laydate.now(),
		    isclear : false, //是否显示清空
			istoday : false, //是否显示今天
			issure : true, //是否显示确认
		    choose: function(datas){
		        start.max = datas; //结束日选好后，重置开始日的最大日期
		    }
		};
		laydate(start);
		laydate(end);
	
})(page.options);

//******************** List Function ********************//

page.list = page.list || {};
(function(list) {
	list.initEvent = function() {
		//查看文章详情
		$(std.findTag(tag.articleLink)).unbind('click').click(function() {
			$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/recommend/openArticleDetail.htm') + "'></form>");
			$("#param-form").append("<input type='hidden' name='rid' value='" + std.oid(this) + "'/>");
			document.createElement("a").onclick = $("#param-form").submit();
			$("#param-form").remove();
		});
		
	};
	list.renderDom = function(html) {//渲染文章数据
		$(selector.listContainer()).append(html);
		//查询后事件注册
		$(std.findTag('add-briefing-theme')).click(function() {
			turnhandle(std.oid(this));//转操作下拉事件注册

			$(std.findTag('ctx-btn')).mouseleave(function() {//鼠标移走后隐藏转操作下拉
				$(this).find('ul').hide();
			});
		});
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
	
	var findArticle = function(id, fun) {
		ajaxCommFunText(std.u("/recommend/detail.htm"), "id=" + id, function(response) {
			if (response.type == dict.action.suc) {
				fun.success(response.data, response.message);
			} else {
				alertMsg(response.message);
				if ($.isFunction(fun.error)) {
					fun.error(response.message, response.attrs);
				}
			}
		});
	};
	
	showArticleDetail2 = function(id) {
		var detailbox = std.selector('popup-box', 'article-detail');
		$(detailbox).data('id', id);
		$(detailbox).find('.article-content').empty();
		$.box(detailbox, {
			onOpen : function() {
				findArticle(id, {
					success : function(html) {
						if ($(detailbox).data('id') == id) {
							$(detailbox).find('.article-content').html(html);
							$(detailbox).find('.scrollbar-ctx').tinyscrollbar();
						}
					}
				});
			}
		}, {
			close : {
				dom : [ detailbox + " .box-close" ]
			}
		});
	}
})(page.list);

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

	page.tosearchByPaging = function(currentpage, pagesize) {
		$(selector.listContainer()).empty();
		$(selector.pagingContainer()).hide();
		page.tosearch(currentpage, pagesize);
		page.scorllTo($(input.keyWordInput));
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
	$(std.findTag('option-item', '[scope=option]')).get(0).click();
});
