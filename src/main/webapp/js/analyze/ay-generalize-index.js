/**
 * 首页：index.htm
 * @author GUQIANG
 */
var page = page || {};
(function() {

	//******************** Closure Variable ********************//

	var selector = {
		situationChart : '#generalize-situation-chart',
		situationChartMask : '#generalize-situation-chart-mask',
		tendencyChart : '#generalize-tendency-chart',
		tendencyChartMask : '#generalize-tendency-chart-mask',
		statCustTopicList : '#generalize-statCustTopic-list',
		statCustTopicListMask : '#generalize-statCustTopic-list-mask',
		statWebsiteList : '#generalize-statWebsite-list',
		statWebsiteListMask : '#generalize-statWebsite-list-mask',
		searchSituation : '#search',
		searchTendency : '#searchTendency',
		searchKeywords : '#searchKeywords'
	};

	var tag = {
		showStatWebsiteListBtn : 'btn-show-statWebsite-list',
		statWebsiteList : 'list-statWebsite',
		conditionForm : 'condition-form',
		popupBox : 'popup-box',
		situationInfo : 'situation-info',
		tendencyInfo : 'tendency-info',
		situationAllTopic : 'situation-allTopic',
		tendencyAllTopic : 'tendency-allTopic'
	};

	var oid = {
		situationTopics : 'situation-topics',
		tendencyTopics : 'tendency-topics',
		topic : 'topic'
	};
	
	var input = {
		startDate : 'input[name=startDate]',
		endDate : 'input[name=endDate]'
	};

	var start = {
		elem : '#startDate',
		format : 'YYYY-MM-DD',
		min : laydate.now(-31),
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			end.min = datas;
			end.start = datas;
			if (new Date(datas) > new Date($(input.endDate).val())) {
				$(input.endDate).val(datas);
			}
			$(std.find('btn-today', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'situation')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'situation')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'situation')).find('.name').removeClass('c_tag_active');
		}
	};

	var end = {
		elem : '#endDate',
		format : 'YYYY-MM-DD',
		min : '1900-01-01',
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			start.max = datas;
			$(std.find('btn-today', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'situation')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'situation')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'situation')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'situation')).find('.name').removeClass('c_tag_active');
		}
	};
	//媒体趋势
	var tendencystart = {
		elem : '#tendencystartDate',
		format : 'YYYY-MM-DD',
		min : laydate.now(-31),
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			tendencyend.min = datas;
			tendencyend.start = datas;
			if (new Date(datas) > new Date($(input.tendencyend).val())) {
				$(input.tendencyend).val(datas);
			}
			$(std.find('btn-today', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'tendency')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'tendency')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'tendency')).find('.name').removeClass('c_tag_active');
		}
	};

	var tendencyend = {
		elem : '#tendencyendDate',
		format : 'YYYY-MM-DD',
		min : '1900-01-01',
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			tendencystart.max = datas;
			$(std.find('btn-today', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'tendency')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'tendency')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'tendency')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'tendency')).find('.name').removeClass('c_tag_active');
		}
	};
	//专题分布
	var custTopicstart = {
		elem : '#custTopicstartDate',
		format : 'YYYY-MM-DD',
		min : laydate.now(-31),
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			custTopicend.min = datas;
			custTopicend.start = datas;
			if (new Date(datas) > new Date($(input.custTopicend).val())) {
				$(input.custTopicend).val(datas);
			}
			$(std.find('btn-today', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'custTopic')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'custTopic')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'custTopic')).find('.name').removeClass('c_tag_active');
		}
	};

	var custTopicend = {
		elem : '#custTopicendDate',
		format : 'YYYY-MM-DD',
		min : '1900-01-01',
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			custTopicstart.max = datas;
			$(std.find('btn-today', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'custTopic')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'custTopic')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'custTopic')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'custTopic')).find('.name').removeClass('c_tag_active');
		}
	};
	//站点统计
	var websitestart = {
		elem : '#websitestartDate',
		format : 'YYYY-MM-DD',
		min : laydate.now(-31),
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			websiteend.min = datas;
			websiteend.start = datas;
			if (new Date(datas) > new Date($(input.websiteend).val())) {
				$(input.websiteend).val(datas);
			}
			$(std.find('btn-today', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'website')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'website')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'website')).find('.name').removeClass('c_tag_active');
		}
	};

	var websiteend = {
		elem : '#websiteendDate',
		format : 'YYYY-MM-DD',
		min : '1900-01-01',
		max : '2099-06-16',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			websitestart.max = datas;
			$(std.find('btn-today', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-today', 'website')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-week', 'website')).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', 'website')).find('.checkbox').removeClass('active');
			$(std.find('btn-month', 'website')).find('.name').removeClass('c_tag_active');
		}
	};

	//******************** Page Function ********************//

	page.initEvent = function() {
		var getmindate = function(){
			var now = laydate.now();
			var lastYear = parseInt(now.split("-")[0]) - 1;
			return lastYear+"-01-01";
		}
		start.max = laydate.now();
		start.min = getmindate();
		end.max = laydate.now();
		end.min = $(input.startDate).val();
		tendencystart.max = laydate.now();
		tendencystart.min = getmindate();
		tendencyend.max = laydate.now();
		tendencyend.min = $(input.tendencystart).val();
		custTopicstart.max = laydate.now();
		custTopicstart.min = getmindate();
		custTopicend.max = laydate.now();
		custTopicend.min = $(input.custTopicstart).val();
		websitestart.max = laydate.now();
		websitestart.min = getmindate();
		websiteend.max = laydate.now();
		websiteend.min = $(input.websitestart).val();
		laydate(start);
		laydate(end);
		laydate(tendencystart);
		laydate(tendencyend);
		laydate(custTopicstart);
		laydate(custTopicend);
		laydate(websitestart);
		laydate(websiteend);

		$(std.findTag(tag.situationAllTopic)).unbind('click').click(function() {
			page.clickTopicAll(this);
		});
		
		//专题选项管理
		$(std.selector(tag.popupBox, oid.topic)).optionMagr({
			width : 100,
			listSubOption : function(id) {
				var options = [];
				analyze.listTopic(id, {
					success : function(topics) {
						options = topics;
					}
				});
				return options;
			}
		});
		
		$(selector.searchSituation).click(function(){
			page.clickPopTopic("situation",oid.situationTopics);
		});
		
		$(std.findTag(tag.tendencyAllTopic)).unbind('click').click(function() {
			page.clickTopicTendencyAll(this);
		});

		$(selector.searchTendency).click(function(){
			page.clickPopTopic("tendency",oid.tendencyTopics);
		});
		
		$(std.findTag('btn-today')).click(function() {
			$(input.startDate+'[oid='+std.oid(this)+']').val(laydate.now());
			$(input.endDate+'[oid='+std.oid(this)+']').val(laydate.now());
			end.min = laydate.now();

			$(std.find('btn-today', std.oid(this))).find('.checkbox').addClass('active');
			$(std.find('btn-today', std.oid(this))).find('.name').addClass('c_tag_active');
			$(std.find('btn-week', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-week', std.oid(this))).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-month', std.oid(this))).find('.name').removeClass('c_tag_active');
			switch(std.oid(this)){
			case 'situation':
				page.loadSituation($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'tendency':
				page.loadTendency($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'custTopic':
				page.loadStatCustTopic($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'website':
				page.loadStatWebsite($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			}
		});
		
		$(std.findTag('btn-week')).click(function() {
			$(input.startDate+'[oid='+std.oid(this)+']').val(laydate.now(-6));
			$(input.endDate+'[oid='+std.oid(this)+']').val(laydate.now());
			end.min = laydate.now(-6);

			$(std.find('btn-today', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-today', std.oid(this))).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', std.oid(this))).find('.checkbox').addClass('active');
			$(std.find('btn-week', std.oid(this))).find('.name').addClass('c_tag_active');
			$(std.find('btn-month', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-month', std.oid(this))).find('.name').removeClass('c_tag_active');
			switch(std.oid(this)){
			case 'situation':
				page.loadSituation($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'tendency':
				page.loadTendency($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'custTopic':
				page.loadStatCustTopic($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'website':
				page.loadStatWebsite($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			}
		});

		$(std.findTag('btn-month')).click(function() {
			$(input.startDate+'[oid='+std.oid(this)+']').val(forwardOneMonth);
			$(input.endDate+'[oid='+std.oid(this)+']').val(endDate);
//			end.min = laydate.now(-30);

			$(std.find('btn-today', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-today', std.oid(this))).find('.name').removeClass('c_tag_active');
			$(std.find('btn-week', std.oid(this))).find('.checkbox').removeClass('active');
			$(std.find('btn-week', std.oid(this))).find('.name').removeClass('c_tag_active');
			$(std.find('btn-month', std.oid(this))).find('.checkbox').addClass('active');
			$(std.find('btn-month', std.oid(this))).find('.name').addClass('c_tag_active');
			switch(std.oid(this)){
			case 'situation':
				page.loadSituation($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'tendency':
				page.loadTendency($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'custTopic':
				page.loadStatCustTopic($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'website':
				page.loadStatWebsite($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			}
		});
		
		$(std.findTag('btn-analyze')).click(function() {
			switch(std.oid(this)){
			case 'situation':
				page.loadSituation($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'tendency':
				page.loadTendency($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'custTopic':
				page.loadStatCustTopic($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			case 'website':
				page.loadStatWebsite($(input.startDate+'[oid='+std.oid(this)+']').val(), $(input.endDate+'[oid='+std.oid(this)+']').val());
				break;
			}
		});
		
		$('#exportSituation').click(function() {
			page.exportSituation();
		});
		
		$('#exportTendency').click(function() {
			page.exportTendency();
		});
		
		//默认勾选状态-默认选中今日
		$(std.findTag('btn-today')).find('.checkbox').addClass('active');
		$(std.findTag('btn-today')).find('.name').addClass('c_tag_active');
	};

	page.reloadChart = function() {
		page.loadSituation($(input.startDate).val(), $(input.endDate).val());
		page.loadTendency($(input.startDate).val(), $(input.endDate).val());
		page.loadStatCustTopic($(input.startDate).val(), $(input.endDate).val());
		page.loadStatWebsite($(input.startDate).val(), $(input.endDate).val());
		
	};
	page.custTopicGotoSearth = function(envet) {
		var topicId = std.oid(envet);
		$(envet).append("<form id='redirect' action='"+ctx+"/search/index.htm' method='post' style='display:none;'></form>");
//		$("#redirect").append("<input name='siteName' value='"+siteName+"'/>");
		$("#redirect").append("<input name='topicType' value='"+topicId+"'/>");
		$("#redirect").append("<input name='startTime' value='"+$("#custTopicstartDate").val()+" 00:00:00"+"'/>");	
		$("#redirect").append("<input name='endTime' value='"+$("#custTopicendDate").val()+" 23:59:59"+"'/>");
		$("#redirect").submit();
//		window.location.href = std.u("/search/index.htm?topicType=" + topicId + "&startTime=" + $(input.startDate).val() + "&endTime=" + $(input.endDate).val());
	};
	page.statWebsiteGotoSearth = function(envet) {
		var websiteId = std.oid(envet);
		$(envet).append("<form id='redirect' action='"+ctx+"/search/index.htm' method='post' style='display:none;'></form>");
		$("#redirect").append("<input name='spiderSiteId' value='"+websiteId+"'/>");
		$("#redirect").append("<input name='startTime' value='"+$("#websitestartDate").val()+" 00:00:00"+"'/>");
		$("#redirect").append("<input name='endTime' value='"+$("#websiteendDate").val()+" 23:59:59"+"'/>");
		$("#redirect").append("<input name='mediaType' value='"+$(envet).parent().parent().parent().attr("oid")+"'/>");
		$("#redirect").submit();
//		window.location.href = std.u("/search/index.htm?siteName=" + siteName + "&spiderSiteId=" + websiteId + "&startTime=" + $(input.startDate).val() + "&endTime=" + $(input.endDate).val());
	};
	page.loadSituation = function(startDate, endDate) {
		$(selector.situationChart).hide();
		$(selector.situationChart).html('');
		$(selector.situationChartMask).show();
		
		var situationParam = $("#situation-condition").serialize();
		
		analyze.getSituationChart("startDate=" + startDate + "&endDate=" + endDate + "&" + situationParam, {
			success : function(data) {
				$(selector.situationChart).show();
				$(selector.situationChart).html(data);
				$(selector.situationChartMask).hide();
				
				page.loadSituationCount(startDate, endDate);
				
			},
			error : function() {
				$(selector.situationChart).show();
				$(selector.situationChart).html('<div class="c_nodata" style="line-height: 250px;">加载失败</div>');
				$(selector.situationChartMask).hide();
			}
		});
	};
	
	page.loadSituationCount = function(startDate, endDate) {
		var situationParam = $("#situation-condition").serialize();
		
		ajaxCommFun(std.u("/analyze/generalize/situationCount.htm"), "startDate=" + startDate + "&endDate=" + endDate + "&" + situationParam, function(response){
			if (response.type == dict.action.suc) {
				/**后台Map无序,传到前台JSONArray是有序的*/
				var readInMediaMap = response.data.readInMedia;
				
				//计算总阅读数
				var readNos = $(std.findTag('readNo'));
				var readNo = new Number;
				var arrayRead = new Array();
				var countReadNum = 0;
				for (var i in readInMediaMap) { 
					arrayRead[countReadNum] = readInMediaMap[i];
					countReadNum++;
				} 
				readNos.each(function(i , item){
					$(item).text(arrayRead[i]);
					readNo = readNo + parseInt($(item).text());
				});
				$("#article-totalReadNos").html(readNo);
				
				/**后台Map无序,传到前台JSONArray是有序的*/
				var replyInMediaMap = response.data.replyInMedia;
				//计算总回复数
				var replyNos = $(std.findTag('replyNo'));
				var replyNo = new Number;
				var arrayReply = new Array();
				var countReplyNum = 0;
				for (var i in replyInMediaMap) {
					arrayReply[countReplyNum] = replyInMediaMap[i];
					countReplyNum++;
				}
				replyNos.each(function(i , item){
					$(item).text(arrayReply[i]);
					replyNo = replyNo + parseInt($(item).text());
				});
				$("#article-totalReplyNos").html(replyNo);						
				
			} else {
				$.msg.warning(response.message);
				//alertMsg(response.message);
			};
		});
	};

	page.loadTendency = function(startDate, endDate) {
		$(selector.tendencyChart).hide();
		$(selector.tendencyChart).html('');
		$(selector.tendencyChartMask).show();
		
		var tendencyParam = $("#tendency-condition").serialize();
		
		analyze.getTendencyChart("startDate=" + startDate + "&endDate=" + endDate + "&" + tendencyParam, {
			success : function(data) {
				$(selector.tendencyChart).show();
				$(selector.tendencyChart).html(data);
				$(selector.tendencyChartMask).hide();
			},
			error : function() {
				$(selector.tendencyChart).show();
				$(selector.tendencyChart).html('<div class="c_nodata" style="line-height: 250px;">加载失败</div>');
				$(selector.tendencyChartMask).hide();
			}
		});
	};

	page.loadStatCustTopic = function(startDate, endDate) {
		$(selector.statCustTopicList).hide();
		$(selector.statCustTopicList).html('');
		$(selector.statCustTopicListMask).show();
		analyze.getStatCustTopicList("startDate=" + startDate + "&endDate=" + endDate, {
			success : function(data) {
				$(selector.statCustTopicList).show();
				$(selector.statCustTopicList).html(data);
				$(selector.statCustTopicListMask).hide();
				
				$(std.findTag('custTopic-goto-searth')).click(function() {//专题数量跳转事件注册
					page.custTopicGotoSearth(this);
				});
				
			},
			error : function() {
				$(selector.statCustTopicList).show();
				$(selector.statCustTopicList).html('<div class="c_nodata" style="line-height: 250px;">加载失败</div>');
				$(selector.statCustTopicListMask).hide();
			}
		});
	};
	page.loadStatWebsite = function(startDate, endDate) {
		$(selector.statWebsiteList).hide();
		$(selector.statWebsiteList).html('');
		$(selector.statWebsiteListMask).show();
		analyze.getStatWebsiteList("startDate=" + startDate + "&endDate=" + endDate, {
			success : function(data) {
				$(selector.statWebsiteList).show();
				$(selector.statWebsiteList).html(data);
				$(selector.statWebsiteListMask).hide();
				
				$(std.findTag(tag.showStatWebsiteListBtn)).click(function() {
					if ($(this).attr('status') == 'open') {
						$(this).attr('status', 'close');
						$(this).text('展开列表 ▼');
						$(std.find(tag.statWebsiteList, std.oid(this))).slideUp();
					} else {
						$(this).attr('status', 'open');
						$(this).text('隐藏列表 ▲');
						$(std.find(tag.statWebsiteList, std.oid(this))).slideDown();
					}
				});
				$(std.findTag('statWebsite-goto-searth')).click(function() {//站点数量跳转事件注册
					page.statWebsiteGotoSearth(this);
				});
			},
			error : function() {
				$(selector.statWebsiteList).show();
				$(selector.statWebsiteList).html('<div class="c_nodata" style="line-height: 250px;">加载失败</div>');
				$(selector.statWebsiteListMask).hide();
			}
		});
	};
	
	page.exportSituation = function() {
		var situationParam = $("#situation-condition").serialize();
		window.location.href = std.u("/analyze/exportSituation.htm?startDate=" + $('#startDate').val() + "&endDate=" + $('#endDate').val() + "&" + situationParam);
	};
	
	page.exportTendency = function() {
		var tendencyParam = $("#tendency-condition").serialize();
		window.location.href = std.u("/analyze/exportTendency.htm?startDate=" + $('#tendencystartDate').val() + "&endDate=" + $('#tendencyendDate').val() + "&" + tendencyParam);
	};

	page.clickTopicAll = function(elmt) {
		if ($(std.findTag(tag.situationAllTopic)).attr('selct') == 'true') {
			$(std.findTag(tag.situationAllTopic)).attr('selct', 'false');
			$(elmt).find('.checkbox').removeClass('active');
			$(elmt).find('.name').removeClass('c_tag_active');
		} else {
			page.clearShowTopic(tag.situationInfo);
			$(std.findTag(tag.situationAllTopic)).attr('selct', 'true');
			$(elmt).find('.checkbox').addClass('active');
			$(elmt).find('.name').addClass('c_tag_active');
		}
		
		var topics = [];
		if($(std.findTag(tag.situationAllTopic)).attr('selct') == 'true'){
			topics.push({
				id : [],
				name : '全部'
			});
		}
		
		page.limitOption(oid.situationTopics, 'topic', topics);
	};
	
	page.clickTopicTendencyAll = function(elmt) {
		if ($(std.findTag(tag.tendencyAllTopic)).attr('selct') == 'true') {
			$(std.findTag(tag.tendencyAllTopic)).attr('selct', 'false');
			$(elmt).find('.checkbox').removeClass('active');
			$(elmt).find('.name').removeClass('c_tag_active');
		} else {
			page.clearShowTopic(tag.tendencyInfo);
			$(std.findTag(tag.tendencyAllTopic)).attr('selct', 'true');
			$(elmt).find('.checkbox').addClass('active');
			$(elmt).find('.name').addClass('c_tag_active');
		}

		var topics = [];
		if($(std.findTag(tag.tendencyAllTopic)).attr('selct') == 'true'){
			topics.push({
				id : [],
				name : '全部'
			});
		}
		
		page.limitOption(oid.tendencyTopics, 'topic', topics);
	};
	
	page.limitOption = function(oid, inputName, options) {
		var html = "";

		var formCtx = $(std.find(tag.conditionForm, oid));
		formCtx.empty();

		$(options).each(function(i, option) {
			html += ", " + option.name;
			formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
		});
		html = html.substring(2, html.length);

		return html;
	};
	
	page.getSelectTopicCondition = function(oid) {
		var ids = [];
		$(std.find(tag.conditionForm, oid)).find('input').each(function(i, obj) {
			ids.push($(obj).val());
		});
		return ids;
	};

	page.setQueryTopicCondition = function(topics,formCtx) {
		var html = "";
		$(topics).each(function(i, option) {
			html += ", " + option.name;
			formCtx.append('<input type="hidden" name="topic" value="' + option.id + '" />');
		});
		html = html.substring(2, html.length);
		return html;
	};
	
	page.showTopic = function(html,type) {
		if (type == "situation") {
			if (util.isNotBlank(html)) {
				$(std.findTag(tag.situationAllTopic)).attr('selct', 'false');
				$(std.findTag(tag.situationAllTopic)).find('.checkbox').removeClass('active');
				$(std.findTag(tag.situationAllTopic)).find('.name').removeClass('c_tag_active');
				$(std.find(tag.situationInfo, oid.topic)).attr("title",html);
				$(std.find(tag.situationInfo, oid.topic)).text(html);
			} else {
				page.clearShowTopic(tag.situationInfo);
			}
		}
		if (type == "tendency") {
			if (util.isNotBlank(html)) {
				$(std.findTag(tag.tendencyAllTopic)).attr('selct', 'false');
				$(std.findTag(tag.tendencyAllTopic)).find('.checkbox').removeClass('active');
				$(std.findTag(tag.tendencyAllTopic)).find('.name').removeClass('c_tag_active');
				$(std.find(tag.tendencyInfo, oid.topic)).attr("title",html);
				$(std.find(tag.tendencyInfo, oid.topic)).text(html);
			} else {
				page.clearShowTopic(tag.tendencyInfo);
			}
		}
	};
	
	page.clearShowTopic = function(tag) {
		$(std.find(tag, oid.topic)).attr("title","");
		$(std.find(tag, oid.topic)).text("");
	};
	
	page.clickPopTopic = function(type,oidTopic) {
		var topicbox = std.selector(tag.popupBox, oid.topic);

		$.box(topicbox, {
			onOpen : function() {
				var ids = page.getSelectTopicCondition(oidTopic);
				var options = [];
				if (!util.isEmpty(ids)) {
					analyze.listTopicOptionInfo(ids, {
						success : function(topics) {
							options = topics;
						}
					});
				}
				$(topicbox).optionMagr('initValue', options);
				$(topicbox).optionMagr('showSubOption', 0);
				$(selector.searchKeywords).val("");
			}
		}, {
			submit : {
				dom : [ topicbox + " .box-submit" ],
				fun : function() {
					var formCtx = $(std.find(tag.conditionForm, oidTopic));
					formCtx.empty();
					var topics = $(topicbox).optionMagr('submit');
					var html = page.setQueryTopicCondition(topics,formCtx);
					
					page.showTopic(html,type);
					
				}
			},
			close : {
				dom : [ topicbox + " .box-close", topicbox + " .box-cancel" ]
			}
		});
	};
	
	//******************** INIT Function ********************//

	$(function() {
		page.initEvent();
		page.reloadChart();
	});

})();