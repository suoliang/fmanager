var isMsaking = false;
var isExcel;
var type;
var page = page || {};
var oid = {
		time : 'time',
		site : 'site-item'
	};
var tag = {
		mediaValueform : "mediaType-form",
		openExportWordBtn : 'all-article-word',
		openExportExcelBtn : 'all-article-excel'
	};
var input = {
		mediaType : 'input[name=mediaType]'
	};
/**
 * 多个回车事件
 */

//最小粒度分页，查询focus数据
var queryFocus = function(currentpage, pagesize) {//分页搜索
//	console.log("currentpage : " + currentpage + " pagesize : " + pagesize);
	$('.focus-list-mask').show();
	isMsaking = true;
	var medias = [];
	$("input[name='mediaType']").each(function() {
		medias.push($(this).val());
	});
	ajaxCommFunText(std.u('/focusing/voicefocusByPage.htm'), {
		warningLevel : $('#warningLevel').val(),
		source : $('#source').val(),
		keyword : $('#input_1').val(),
		startTime : $('#startTimeInput').val(),
		scopeFlag : $('#scopeFlag').val(),
		endTime : $('#endTimeInput').val(),
//		timeType: $(".c_tab_con1").find(".active").attr("value"),
		timeType :$("input[name=timeType]").val(),
		mediaType :util.joinCollection(medias, ","),
		currentpage : currentpage,
		pagesize : pagesize

	}, function(response) {
		if (response.type == dict.action.suc) {
			$('.voice-ctx').append(response.data);//渲染DOM到页面
			
			$(std.findTag('add-briefing-theme')).unbind().click(function() {
				turnhandle(std.oid(this));//转操作下拉事件注册
			});
			$(std.findTag('ctx-btn')).unbind().mouseleave(function() {//鼠标移走后隐藏转操作下拉
				$(this).find('ul').hide();
			});
			
			$(std.findTag("btn-open-brief-picker")).unbind('click').click(function() {//加入简报
				popup.openBriefPicker(std.oid(this));
			});
			
			$('.focus-list-mask').hide();
			isMsaking = false;
			subContent();
		} else {
			alertMsg(response.message);
		}
	});
};

//分页
var queryFocusByPaging = function(pageNo, pagesize) {
	$('.voice-ctx').empty();//清空列表
	$('.focus-list-paging').hide();//隐藏分页DOM
	queryFocus(pageNo, pagesize);//查询分页数据
	$("body,html").animate({
		scrollTop : $('#obj').offset().top - 70
	}, 500);
	
};

//设置分页， 改变分页信息
var pageInfo = {};
var setPageInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
	pageInfo.currentpage = currentpage;
	pageInfo.pagesize = pagesize;
	pageInfo.totalpage = totalpage;
	pageInfo.totalsize = totalsize;

	if (pageInfo.currentpage >= pageInfo.totalpage) {
		$('.focus-list-paging').show();
	}

	currentpage = Math.ceil(currentpage / 4);
	pagesize = pagesize * 4;
	totalpage = Math.ceil(totalpage / 4);

	$('.focus-list-paging').paging("setInfo", currentpage, pagesize, totalpage, totalsize);
};




var falt;
var scopeSelect=0;
var typeSelect;
//on load 事件回调
$(function() {
	$(std.findTag('add-briefing-theme')).click(function() {
		turnhandle(std.oid(this));//转操作下拉事件注册
		$(std.findTag('ctx-btn')).mouseleave(function() {//鼠标移走后隐藏转操作下拉
			$(this).find('ul').hide();
		});
	});
	
	$(std.findTag("btn-open-brief-picker")).unbind('click').click(function() {//加入简报
		popup.openBriefPicker(std.oid(this));
	});
	
	$.divselect("#alertLevelSelect", "#alertLevel", function(txt, value) {
		falt = value;
	});
	
	$.divselect("#scopeSelect", "#alertscope", function(txt, value) {
		scopeSelect = value;
	});
	
	$.divselect("#typeSelect", "#focustype", function(txt, value) {
		typeSelect = value;
	});
	
	
	/*时间选择js开始------------------------------------------------------------*/
	var clickTime = function(elmt) {
		//隐藏自定义时间
//		$('#c_timeck02').fadeOut('slow');
		$('#c_timeck01 .c_choice_icon').removeClass("active");
		$('#c_timeck01 .c_choice_tag').removeClass("c_tag_active");
		if($('#c_timeck02').hasClass("c_none")==false){
			$('#c_timeck02').addClass("c_none")
			
		}
		//复选框样式变灰色
		$('#selectall').removeClass("c_sprite c_choice_icon c_fl active").addClass("c_sprite c_choice_icon c_fl");
		

		//set值
		var value = $(elmt).find('[type=checkbox]').attr('value');
//		$(input.timeType).val(value);
		$("input[name=timeType]").val(value);
		if(value != null){
			$("[name='startTime']").val("");
			$("[name='endTime']").val("");
		}
		$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
//		queryFocusByPaging(pageInfo.currentpage, pageInfo.pagesize);
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
		
	};
	//初始化加载勾选“今天”全部
	var defaultTimeType = $("input[name=timeType]").val();
	$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox][value='+defaultTimeType+']').addClass('active');
	$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname][value='+defaultTimeType+']').addClass('c_tag_active');
	//初始化时间点击事件
	$(std.findTag(oid.time, '[scope=option]')).click(function() {
		clickTime(this);
	});
	/*时间选择js结束------------------------------------------------------------*/
	
	//	$.divselect("#alertSortSelect", "#alertSort");//排序收回
	//	$.divselect("#alertLevelSelect", "#alertLevel");//预警等级收回
	$('.focus-list-paging').hide();
	//绑定分页UI，使工具起效
	$('.focus-list-paging').paging({
		onChange : function(totalpage, totalsize) {
			$("#count-Show-i").html(formatNum(totalsize));
		},
		gotoNoImpl : function(pageNo, pagesize) {
			queryFocusByPaging((pageNo - 1) * 4 + 1, pagesize / 4);
		}
	});
	//滚动刷新222
	$(window).scroll(function() {
		if (isMsaking) {
			return;
		}
		if ($(document).scrollTop() >= $(document).height() - $(window).height() - $('.c_top').height()) {
			if (pageInfo.currentpage % 4 == 0 || pageInfo.currentpage >= pageInfo.totalpage) {
				$('.focus-list-paging').show();
			} else {
				queryFocus(pageInfo.currentpage + 1, pageInfo.pagesize ? pageInfo.pagesize : 10);
			}
		}
	});
	/*
	 * 指定关键字keydown事件，回车时调用查询方法。
	 */
	$('#input_1').keydown(function(e) {
		if (e && e.keyCode == 13) {
			Search();
		}
		e.stopPropagation();
	});
	
	//"导出全部(Word)"事件初始化
	$(std.findTag(tag.openExportWordBtn)).unbind('click').click(function() {
		isExcel = false;
		type = 15;
		exportbox.openExportPicker(std.oid(this), type);
	});
	
	//"导出全部(Excel)"事件初始化
	$(std.findTag(tag.openExportExcelBtn)).unbind('click').click(function() {
		isExcel = true;
		type = 12;
		exportbox.openExportPicker(std.oid(this), type);
	});
});


//转操作
function turnhandle(guid){
	var ul=$("#c_new"+guid);
	if(ul.css("display")=="none"){
		ul.slideDown();
	}else{
		ul.slideUp();
	}
}

$(function(){
	subContent();
});


function subContent(){
	$("[tag=focusContent]").each(function(prop, value){
		var valueText= $(value).text();
		var objLength = $(value).text().length; 
		var num = $(this).attr("limit");
		
		if(objLength > num){ 
//		$(this).attr("title",objString); 
		valueText = $(this).text(valueText.substring(0,num) + "...");
		}
	});
}



//日期选择不能超过当前日期，并且结束日期不能小于开始日期
var myDate = new Date();
var startTimeInput,endTimeInput;
var input = {
	startTimeInput : '#startTimeInput',
	endTimeInput : '#endTimeInput'
};
startTimeInput = {
	elem : input.startTimeInput,
	format : 'YYYY-MM-DD hh:mm:ss',
	min : '1977-01-01',
	max : laydate.now(),
	isclear : true, //是否显示清空
	istoday : false, //是否显示今天
	istime: true,//是否显示时间
	issure : true, //是否显示确认
	choose : function(datas) {
		endTimeInput.min = datas;
		//endTimeInput.start = datas;/**时分秒结束框需要从23:59:59开始*/
	}
};
endTimeInput = {
	elem : input.endTimeInput,
	format : 'YYYY-MM-DD hh:mm:ss',
	start : laydate.now(new Date().getTime()) + ' 23:59:59',
	min : '2015-01-26',
	max : laydate.now(),
	isclear : true, //是否显示清空
	istoday : false, //是否显示今天
	istime: true,//是否显示时间
	issure : true, //是否显示确认
	choose: function(datas){
		startTimeInput.max = datas;
	}
};
$(function() {
	laydate(startTimeInput);
	laydate(endTimeInput);
});


var search = search || {};
search.action = {
	url : std.u("/focusing/QueryInfo.htm")
};
//搜索
function Search() {
	if($('#customCheck').hasClass("active")==true&&$("#check").hasClass("active")==false){
		if($("#startTimeInput").val()==""){
			$.msg.warning('请选择起始日期');
		}else if($("#endTimeInput").val()==""){
			$.msg.warning('请选择结束日期');
		}else{
	    	$('#scopeFlag').val(scopeSelect);
	    	$('#source').val(typeSelect);
	    	queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	    }
	}else{
		$('#scopeFlag').val(scopeSelect);
		$('#source').val(typeSelect);
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	}
}

//显示文章详情 
function voicefocusInfo(guid) {
	var keyword = $.trim($("[name='keyword']").val());
	$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/focusing/openArticleDetail.htm') + "'></form>");
	$("#param-form").append("<input type='hidden' name='guid' value='" + guid + "'/>");
	$("#param-form").append("<input type='hidden' name='keyword' value='" + keyword + "'/>");
	document.createElement("a").onclick = $("#param-form").submit();
	$("#param-form").remove();
}

/**
 * 按时间排序
 */
function sortingTime() {
	//	window.location.href=std.u('/setting/toDown.htm')+"?id="+oid+"&index="+index+"&nextOid="+nextOid;
}

//自定义时间和清空
$("[type='custom']").bind("click",function(){
	Show();
	//复选框样式变灰色
	$('#selectall').removeClass("c_sprite c_choice_icon c_fl active").addClass("c_sprite c_choice_icon c_fl");
	});

$("[type='customCheck']").bind("click",function(){
	Show();
	//复选框样式变灰色
	$('#selectall').removeClass("c_sprite c_choice_icon c_fl active").addClass("c_sprite c_choice_icon c_fl");
	$("div[type='son']").removeClass("c_sprite c_choice_icon c_fl active").addClass("c_sprite c_choice_icon c_fl");
	});

$("#clean").bind("click",function(){
	//清空搜索关键词
	$("#input_1").val("");
});

function Show(){
	 if($('#c_timeck02').hasClass('c_none')==true){
		 $('#c_timeck02').removeClass("c_none");
		 $('#customCheck').removeClass("c_sprite c_choice_icon  c_fl").addClass("c_sprite c_choice_icon  c_fl active");
		 $('#custom').removeClass("c_choice_tag c_fl").addClass("c_choice_tag c_fl c_tag_active");
		 //清空时间(复选框样式,文字样式,复选框值)
		 $(std.findTag("time")).find('[type=checkbox]').removeClass('active');
		 $(std.findTag("time")).find('[type=metaname]').removeClass('c_tag_active');
		 
		 var value=$("input[name=timeType]").attr("value");
		 $(std.findTag("time")).find('[type=checkbox]').val(value);
		 $(std.findTag("time")).find('[type=metaname]').val(value);
	 }/*else{
		 $('#c_timeck02').addClass("c_none");
		 $('#customCheck').removeClass("c_sprite c_choice_icon  c_fl active").addClass("c_sprite c_choice_icon  c_fl");
		 $('#custom').removeClass("c_choice_tag c_fl c_tag_active").addClass("c_choice_tag c_fl");
		 
		 var value=$("input[name=timeType]").attr("value");
		 $(std.findTag("time")).find('[type=checkbox]').val(value);
		 $(std.findTag("time")).find('[type=metaname]').val(value);
	 }*/
}

page.options = page.options || {};
(function(options) {
		options.exportAllWordOrExcel=function(userId) {
			var url=ctx+"/focusing/exportWordThread.htm";
			if(isExcel){
				url=ctx+"/focusing/exportExcelThread.htm";
			}
			exportTasks.addTask(userId, type);//添加任务
			ajaxCommFun(url, $("#voicefocus_id").serialize(), function(response) {//导出全部Word、Excel
				if (response.type == dict.action.suc) {
					exportTasks.updateTaskStatus(taskId, userId, type, 2);//修改任务状态并刷新任务列表 
				} else {
					exportTasks.updateTaskStatus(taskId, userId, type, 3);//修改任务状态并刷新任务列表
				}
			});
		};
})(page.options);

page.condition = page.condition || {};//options的辅助对象(options为对外开放对象)
(function(condition) {
	var oid = {
		media : 'media',
	};

	condition.limitMedia = function(medias) {
		return condition.limitOption(oid.media, 'mediaType', medias);
	};
	
	condition.limitOption = function(oid, inputName, options) {
		var formCtx = $(std.find(tag.mediaValueform, oid));
		formCtx.empty();

		$(options).each(function(i, option) {
			formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
		});
	};
})(page.condition);

page.options = page.options || {};
(function(options, condition) {
	var oid = {
		media : 'option-item',
		mediaall : 'site-item',
		warningLevel : 'warningLevel-item',
		warningLevelall : 'warningLevel-all'
	};
	
	//加载勾选“全部”
	$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
	$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	
	$(std.findTag(oid.media, '[scope=option]')).click(function() {//初始化事件
		options.clickMedia(this);
	});
	
	$(std.findTag(oid.mediaall, '[scope=option]')).click(function() {//初始化事件
		options.clickMediaAll(this);
	});
	
	$(std.findTag(oid.warningLevel, '[scope=option]')).click(function() {//初始化事件
		options.clickWarningLevel(this);
	});
	
	$(std.findTag(oid.warningLevelall, '[scope=option]')).click(function() {//初始化事件
		options.clickWarningLevelAll(this);
	});
	
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
			var media = $(item).find('[type=metaname]').text();

			medias.push({
				id : value,
				name : media
			});
		});
		page.condition.limitMedia(medias);
		
		//即时搜索
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	};
	options.clickMediaAll = function(elmt) {
		var mediaAll = $(std.find(oid.mediaall, std.oid(elmt), '[scope=option]'));
		var formCtx = $(std.find(tag.mediaValueform, "media"));
		if ($(mediaAll).attr('selct') == 'true') {
			$(mediaAll).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
			formCtx.empty();
		} else {
			$(mediaAll).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
			formCtx.empty();
			formCtx.append('<input type="hidden" name=mediaType value=1 />');
			formCtx.append('<input type="hidden" name=mediaType value=2 />');
			formCtx.append('<input type="hidden" name=mediaType value=3 />');
			formCtx.append('<input type="hidden" name=mediaType value=4 />');
			formCtx.append('<input type="hidden" name=mediaType value=5 />');
			formCtx.append('<input type="hidden" name=mediaType value=6 />');
			formCtx.append('<input type="hidden" name=mediaType value=7 />');
			formCtx.append('<input type="hidden" name=mediaType value=8 />');
			formCtx.append('<input type="hidden" name=mediaType value=11 />');
			//其它选项为取消状态
			for (var i = 1; i <= 7; i++) {
				var media = $(std.find('option-item', i, '[scope=option]'));
				$(media).attr('selct', 'false');
				$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
				$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
			}
		}
		//即时搜索
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	};
	
	options.clickWarningLevel = function(elmt) {
		$("#warningLevel").val("");
		$(std.findTag(oid.warningLevelall, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.warningLevelall, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.warningLevelall, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		var value = $(elmt).find('[type=checkbox]').attr('value');

		$(std.find('condition-form', 'warningLevel')).empty().append('<input type="hidden" id="warningLevel" name="warningLevel" value="' + value + '" />');

		$(std.findTag(oid.warningLevel, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.warningLevel, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
		
		//即时搜索
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	};
	
	options.clickWarningLevelAll = function(elmt) {
		if ($(std.findTag(oid.warningLevelall, '[scope=option]')).attr('selct') == 'true') {
			$(std.findTag(oid.warningLevelall, '[scope=option]')).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$("#warningLevel").val("");
			$(std.findTag(oid.warningLevelall, '[scope=option]')).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		$(std.findTag(oid.warningLevel, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.warningLevel, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.warningLevel, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		
		//即时搜索
		queryFocusByPaging((1 - 1) * 4 + 1, 40 / 4);
	};
	
})(page.options);