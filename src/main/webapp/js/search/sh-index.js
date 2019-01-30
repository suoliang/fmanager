/**
 * 首页：index.htm
 * @author GUQIANG
 */
//******************** Global Constant ********************//
var selector = {
	clearSearchAdvanced : '#clear-search-advanced',
	saveSearchAdvanced : '#save-search-advanced',
	showSearchAdvanced : '#show-search-advanced',
	searchOption : '#search-option',
	searchCondition : '#search-condition',
	totalSizeInfo : '#count-Show',
	totalSizeLabel : '#count-Show-i',
	searchAdvanced : function() {
		return '#search-advanced';
	},
	filterContainer : function() {
		return '.filter-container';
	},
	conditionContainer : function() {
		return '.condition-container';
	},
	listHead : function() {
		return '.search-list-head';
	},
	listContainer : function() {
		return '.search-list-container';
	},
	listMask : function() {
		return '.search-list-mask';
	},
	pagingContainer : function() {
		return '.search-list-paging';
	},
	top : function() {
		return '.c_top';
	}
};

var tag = {
	filter : "filter-item",
	filterShow : "filter-show",
	filterDelete : "filter-delete",
	filterSave : "filter-save",
	articleSearch : "article-search",
	articleLink : "article-link",
	pagingAction : "paging-action",
	pagingInfo : "paging-info",
	condition : "condition-item",
	conditionInfo : "condition-info",
	conditionCancel : "condition-cancel",
	conditionForm : "condition-form",
	searchPopup : "search-popup",
	searchPopupBox : "popup-box",
	more : "board-more",
	sortBtn : 'btn-sort',
	openBriefPickerBtn : 'btn-open-brief-picker',
	openExportWordBtn : 'all-article-word',
	openExportExcelBtn : 'all-article-excel'
};

var separator = {
	separator: 'separator'
};

var input = {
	keywordInput : '#keywordInput',
	startTimeInput : '#startTimeInput',
	endTimeInput : '#endTimeInput',
	authorInput : '#authorInput',
	siteNameInput : '#siteNameInput',
	filterName : '#filterName',
	keyword : 'input[name=keyword]',
	conditionId : 'input[name=conditionId]',
	conditionName : 'input[name=conditionName]',
	startTime : 'input[name=startTime]',
	endTime : 'input[name=endTime]',
	timeType : 'input[name=timeType]',
	topicType : 'input[name=topicType]',
	industryType : 'input[name=industryType]',
	areaType : 'input[name=areaType]',
	siteType : 'input[name=siteType]',
	siteName : 'input[name=siteName]',
	sentiment : 'input[name=sentiment]',
	media :	'input[name=media]',
	author : 'input[name=author]',
	orderField : 'input[name=orderField]',
	orderType : 'input[name=orderType]',
	action : '#action',
	websiteTagId : 'input[name=websiteTagId]',
	hot:'input[name=hot]'
};

//******************** Page Function ********************//
var isExcel;
var type;
var page = page || {};
(function(page) {
	var isMsaking = false;

	page.currentParam = '1=1';

	page.initEvent = function() {//初始化事件
		//下拉
		$(selector.showSearchAdvanced).click(function() {
			if ($(selector.searchOption).is(':hidden')) {
				$(selector.showSearchAdvanced + ' i').text('▲');
				$("#filterCondition").text("隐藏筛选条件");
				$(selector.searchOption).slideDown('slow');
				//$(selector.searchCondition).slideUp();
			} else {
				$(selector.showSearchAdvanced + ' i').text('▼');
				$("#filterCondition").text("展开筛选条件");
				$(selector.searchOption).slideUp('slow');
				//$(selector.searchCondition).slideDown();
			}
		});
		
		$(input.siteNameInput).focus(function(){
			if($(input.siteNameInput).val()=="多个站点用空格分开"){
				$(input.siteNameInput).val('');
			}
		}).focusout(function(){
			if($(input.siteNameInput).val()==""){
				$(input.siteNameInput).val('多个站点用空格分开');
			}
		});

		//保存
		$(selector.saveSearchAdvanced).click(function() {
			$(selector.showSearchAdvanced + ' i').text('▲');
			$("#filterCondition").text("隐藏筛选条件");
			$(selector.searchOption).slideDown('slow');
			//$(selector.searchCondition).slideUp();
			$(input.filterName).focus();
			if(util.isBlank($(input.filterName).val())){
				setTimeout(function(){
					$('#filterName').poshytip('show');
				}, 500);
				setTimeout(function(){
					$('#filterName').poshytip('hide');
				}, 2000);
			}
		});

		//清除
		$(selector.clearSearchAdvanced).click(function() {
			$(selector.conditionContainer()).find(std.selector(tag.conditionCancel)).click();
		});

		//搜索
		var searchFun = function() {
			page.currentParam = page.options.serialize();
			page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
			/**搜索时搜索条件显示*/
			/*$(selector.showSearchAdvanced + ' i').text('▼');
			$(selector.searchOption).slideUp('slow');*/
		};
		$(input.keywordInput).keydown(function(e) {
			if (e && e.keyCode == 13) {
				searchFun();
			}
			e.stopPropagation();
		});
		$(std.find(tag.articleSearch, tag.articleSearch)).click(function() {
			searchFun();
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
		
		//"导出全部(Word)"事件初始化
		$(std.findTag(tag.openExportWordBtn)).unbind('click').click(function() {
			isExcel = false;
			type = 14;
			exportbox.openExportPicker(std.oid(this), type);
		});
		
		//"导出全部(Excel)"事件初始化
		$(std.findTag(tag.openExportExcelBtn)).unbind('click').click(function() {
			isExcel = true;
			type = 11;
			exportbox.openExportPicker(std.oid(this), type);
		});
	};

	page.scorllTo = function(obj) {//定位到元素
		$("body,html").animate({
			scrollTop : $(obj).offset().top - 50
		}, 500);
	};

	page.tosearch = function(currentpage, pagesize) {//分页搜索
		$(selector.listMask()).show();
		isMsaking = true;
		search.searchArticle(page.currentParam + "&currentpage=" + currentpage + "&pagesize=" + pagesize, {
			success : function(html) {
				page.list.renderDom(html);
				page.list.reloadStyle();
				page.list.initEvent();
				$(selector.listMask()).hide();
				isMsaking = false;
			},
			error : function(message) {
				$.msg.warning(message, 3);
				$(selector.listMask()).hide();
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

//******************** Filter Function ********************//

page.filter = page.filter || {};
(function(filter) {
	filter.htmlFilter = function(filter) {
		return '' + //
		'<div class="c_his_list" name="' + filter.name + '"' + std.flag(tag.filter, filter.id) + '>' + //
		'	<div class="c_fl c_list_con c_omit" title="' + filter.name + '"' + std.flag(tag.filterShow, filter.id) + '>' + filter.name + '</div>' + //
		'	<i class="c_clearfix c_sprite c_fr"' + std.flag(tag.filterDelete, filter.id) + '></i>' + //
		'</div>' + //
		'<div class="c_cb"></div>';
	}

	filter.initEvent = function() {//初始化事件
		//scroll
		$(selector.filterContainer()).niceScroll({
			cursorcolor : "#D2D0D0",
			cursoropacitymax : 1,
			touchbehavior : false,
			cursorwidth : "5px",
			cursorborder : "0",
			cursorborderradius : "5px"
		});

		//save filter
		$(input.filterName).keydown(function(e) {
			if (e && e.keyCode == 13) {
				page.filter.tosave();
			}
			e.stopPropagation();
		});
		$(std.find(tag.filterSave, tag.filterSave)).click(function() {
			page.filter.tosave();
		});

		//filter list event
		std.each(tag.filter, function(oid) {
			page.filter.bindEvent(oid);
		});
	};

	filter.renderDom = function(filter) {//渲染过滤项元素
		var elmt = $(selector.filterContainer()).children().get(0);
		if (util.isNull(elmt)) {
			$(selector.filterContainer()).append(this.htmlFilter(filter));
		} else {
			$(elmt).before(this.htmlFilter(filter));
		}
	};

	filter.bindEvent = function(oid) {//绑定过滤项元素事件
		//show filter
		$(std.find(tag.filterShow, oid)).click(function() {
			filter.toshow(oid);
		});

		//delete filter
		$(std.find(tag.filterDelete, oid)).click(function() {
			filter.todelete(oid);
		});
	};
	
	filter.tosave = function() {//保存过滤项
		if (!page.options.hasCondition()) {
			layer.alert('过滤器条件不能为空', 3);
			return;
		}
		if (util.isBlank($(input.filterName).val())) {
			layer.alert('过滤器名称不能为空', 3);
			return;
		}
		var selectTimeItem = $(std.findTag(tag.condition, "[oid='time']:not(:hidden)")).size() == 0;
		if(selectTimeItem && util.isBlank($(input.startTimeInput).val())){
			layer.alert('开始时间条件不能为空', 3);
			return;
		}
		if(selectTimeItem && util.isBlank($(input.endTimeInput).val())){
			layer.alert('结束时间条件不能为空', 3);
			return;
		}
		if ($(input.filterName).val().length > 20) {
			layer.alert('过滤器名称太长（小于20个字符）', 3);
			return;
		}

		outer = this;

		search.saveFilter(page.options.serialize(), {
			success : function(filter) {
				$(input.filterName).val('');
				/**此处用oid,用name属性如果有特殊字符不能动态显示*/
				if ($('[tag=filter-item][oid=' + filter.id + ']').size() == 0) {
					outer.renderDom(filter);
					outer.bindEvent(filter.id);
					$(std.findTag(tag.filter)).removeClass('c_his_active');
					$(std.find(tag.filter, filter.id)).addClass('c_his_active');
				}
			}
		});
	};

	filter.todelete = function(oid) {//删除过滤项
		var index = layer.confirm('删除过滤器，将影响关联的首页板块，确定删除吗？', function() {
			search.deleteFilter(oid, {
				success : function(oid) {
					$(std.find(tag.filter, oid)).remove();
					layer.close(index);
				}
			});
		});
	};

	filter.toshow = function(oid) {//显示过滤项
		search.getFilter(oid, {
			success : function(view) {
				page.options.set(view);
				$(std.findTag(tag.filter)).removeClass('c_his_active');
				$(std.find(tag.filter, view.id)).addClass('c_his_active');
			}
		})
	};
})(page.filter);

//******************** Options Function ********************//

//options = options || {};
//(function() {
page.options = page.options || {};
(function(options) {

	var oid = {
		timeScope : 'time-scope',
		startTime : 'start-time',
		endTime : 'end-time',
		time : 'time',
		topic : 'topic',
		industry : 'industry',
		area : 'area',
		site : 'site',
		siteName : 'siteName',
		siteScope : 'siteScope',
		siteScopeAll : 'siteScopeAll',
		media : 'media',
		sentiment : 'sentiment',
		sentimentAll : 'sentimentAll',
		author : 'author',
		mediaAll: 'site-item',
		filterSimilar : 'filterSimilar'
	};
	
	var changeStartDate = function(date) {
		page.condition.cancelCondition(oid.time);
		if (util.isNotNull(date) && util.isNotBlank(date)) {
			page.condition.limitTimeScope(date, $(input.endTimeInput).val());
		}
	};

	var changeEndDate = function(date) {
		page.condition.cancelCondition(oid.time);
		if (util.isNotNull(date) && util.isNotBlank(date)) {
			page.condition.limitTimeScope($(input.startTimeInput).val(), date);
		}
	};

	var startDateOption, endDateOption;
	startDateOption = {
		elem : input.startTimeInput,
		format : 'YYYY-MM-DD hh:mm:ss',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		istime: true,//是否显示时间
		issure : true, //是否显示确认
		choose : function(datas) {
			changeStartDate(datas);
			endDateOption.min = datas;
			//endDateOption.start = datas;/**时分秒结束框需要从23:59:59开始*/
			if (new Date(datas) > new Date($(input.endTimeInput).val())) {
				$(input.endTimeInput).val(datas);
				changeEndDate(datas);
			}
		}
	};
	endDateOption = {
		elem : input.endTimeInput,
		format : 'YYYY-MM-DD hh:mm:ss',
		start : laydate.now(new Date().getTime()) + ' 23:59:59',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		istime: true,//是否显示时间
		issure : true, //是否显示确认
		choose : function(datas) {
			changeEndDate(datas);
			startDateOption.max = datas;
		}
	};

	options.initEvent = function() {//初始化事件
		//click time type
		$(std.findTag(oid.time, '[scope=option]')).click(function() {
			options.clickTime(this);
		});
		$(std.findTag('btn-time-custom')).click(function() {
			options.clickTimeCustom(this);
		});

		//click sentiment type
		$(std.findTag(oid.sentiment, '[scope=option]')).click(function() {
			options.clickSentiment(this);
		});
		
		//click sentimentAll type
		$(std.findTag(oid.sentimentAll, '[scope=option]')).click(function() {
			options.clickSentimentAll(this);
		});
		
		//click media type
		$(std.findTag(oid.media, '[scope=option]')).click(function() {
			options.clickMedia(this);
		});
		
		//click mediaAll type
		$(std.findTag(oid.mediaAll, '[scope=option]')).click(function() {
			options.clickMediaAll(this);
		});
		

		//input author
		$(input.authorInput).keyup(function() {
			page.condition.limitAuthor($(this).val());
		});

		//click topic pop-up
		$(std.find(tag.searchPopup, oid.topic)).click(function() {
			options.clickTopic();
		});

		//click area pop-up
		$(std.find(tag.searchPopup, oid.area)).click(function() {
			options.clickArea();
		});

		//click site pop-up
		$(std.find(tag.searchPopup, oid.site)).click(function() {
			options.clickSite();
		});
		
		//click siteScope 
		$(std.findTag(oid.siteScope, '[scope=option]')).click(function() {
			options.clickSiteScope(this);
		});
		
		//click siteScopeAll 
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).click(function() {
			options.clickSiteScopeAll(this);
		});
		
		//input site
		$(input.siteNameInput).keyup(function() {
//			page.condition.cancelCondition(oid.media);
//			page.condition.cancelCondition(oid.site);
			page.condition.limitSiteName($(this).val());
		});

		//click industry pop-up
		$(std.find(tag.searchPopup, oid.industry)).click(function() {
			options.clickIndustry();
		});
		
		//click similarArticle
		$(std.findTag(oid.filterSimilar, '[scope=option]')).click(function() {
			options.clickfilterSimilar(this);
		});

		startDateOption.max = laydate.now();
		endDateOption.max = laydate.now();
		laydate(startDateOption);
		laydate(endDateOption);

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
	};

	options.clearTime = function() {
		$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	};

	options.clearTimeScope = function() {
		$(input.startTimeInput).val('');
		$(input.endTimeInput).val('');
	};

	options.clearSentiment = function() {
		$(std.findTag(oid.sentiment, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.sentiment, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.sentiment, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.sentimentAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.sentimentAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	};
	
	options.clearmedia = function() {
		$(std.findTag(oid.media, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.mediaAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.mediaAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	};
	
	options.clearSiteScope = function() {
		$(std.findTag(oid.siteScope, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.siteScope, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.siteScope, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	};
	
	options.clearFilterSimilar = function() {
		$(std.findTag(oid.filterSimilar, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.filterSimilar, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.filterSimilar, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
	};
	
	options.clearAuthor = function() {
		$(input.authorInput).val('');
	}

	options.clearSiteName = function() {
		$(input.siteNameInput).val('');
	}

	options.clear = function() {//清除选项
		page.condition.cancelCondition(oid.time);
		page.condition.cancelCondition(oid.timeScope);
		page.condition.cancelCondition(oid.topic);
		page.condition.cancelCondition(oid.industry);
		page.condition.cancelCondition(oid.area);
		page.condition.cancelCondition(oid.site);
		page.condition.cancelCondition(oid.siteScope);
		page.condition.cancelCondition(oid.media);
		page.condition.cancelCondition(oid.mediaAll);
		page.condition.cancelCondition(oid.sentiment);
		page.condition.cancelCondition(oid.sentimentAll);
		page.condition.cancelCondition(oid.filterSimilar);
		page.condition.cancelCondition(oid.author);
		page.condition.cancelCondition(oid.siteName);
	};

	options.setOptionInfo = function(tag, html) {
		var type = tag;
//		if (tag == oid.media) {
//			tag = oid.site;
//		}
//		$(std.findTag(tag, '[scope=option][type=info]')).attr('title', html);
		if(html == "" && (tag == oid.site)){
			$(std.findTag(tag, '[scope=option][type=info]')).find("[type='"+type+"']").remove();
			$(std.findTag(tag, '[scope=option][type=info]')).find("[type='"+separator.separator+"']").remove();
		}else{
			$(std.findTag(tag, '[scope=option][type=info]')).html(html);
		}
		
		if (html == '') {
			if (tag == oid.time) {
				options.clearTime();
			}
			if (tag == oid.sentiment) {
				options.clearSentiment();
			}
			if (tag == oid.media) {
				options.clearmedia();
			}
			if (tag == oid.author) {
				options.clearAuthor();
			}
			if (tag == oid.siteName) {
				options.clearSiteName();
			}
			if (tag == oid.siteScope) {
				options.clearSiteScope();
			}
			if (tag == oid.filterSimilar) {
				options.clearFilterSimilar();
			}
			if (tag == oid.timeScope) {
				options.clearTimeScope();
			}
		}
	};

	options.set = function(view) {//设置选项
		this.clear();
		$(input.filterName).val(view.name);
		$(input.keywordInput).val(view.keywords);
		if (view.postStartTime) {//开始时间
			$(input.startTimeInput).val(view.postStartTime);
			page.condition.limitTimeScope(view.postStartTime, $(input.endTimeInput).val());
		}

		if (view.postEndTime) {//结束时间
			$(input.endTimeInput).val(view.postEndTime);
			page.condition.limitTimeScope($(input.startTimeInput).val(), view.postEndTime);
		}
		
		if(view.postStartTime || view.postStartTime){
			/**自定义时间选中处理*/
			$(std.findTag('btn-time-custom')).click();
		}

		if (view.postTimeType) {//时间类型
			$(std.find(oid.time, view.postTimeType, '[scope=option]')).click();
		}
		if (view.topics) {//专题
			var text = "";
			$(view.topics).each(function(i, topic) {
				if (topic != null) {
					text += ", " + topic.name;
				}
			});
			text = text.substring(2, text.length);
			options.setOptionInfo(oid.topic, text);
			page.condition.limitTopic(view.topics);
		}
		if (view.industrys) {//行业
			var text = "";
			$(view.industrys).each(function(i, industry) {
				if (industry != null) {
					text += ", " + industry.name;
				}
			});
			text = text.substring(2, text.length);
			options.setOptionInfo(oid.industry, text);
			page.condition.limitIndustry(view.industrys);
		}
		if (view.areas) {//地域
			var text = "";
			$(view.areas).each(function(i, area) {
				if (area != null) {
					text += ", " + area.name;
				}
			});
			text = text.substring(2, text.length);
			options.setOptionInfo(oid.area, text);
			page.condition.limitArea(view.areas);
		}
		var htmlArr = [];
		if (view.sites) {//站点
			var text = "";
			$(view.sites).each(function(i, site) {
				if (site != null) {
					text += ", " + site.name;
				}
			});
			text = text.substring(2, text.length);
			var html = page.condition.limitSite(view.sites);
			htmlArr.push(html);
		}
//		if (view.sentiment) {//站点类型
//			$(view.sentiment).each(function(i, sentiment) {
//				if (sentiment != null) {
//					$(std.find(oid.sentiment, sentiment, '[scope=option]')).click();
//				}
//			});
//		}
		if(view.siteName) {//站点名称
			$(input.siteNameInput).val(view.siteName);
			page.condition.cancelCondition(oid.media);
			page.condition.cancelCondition(oid.site);
			page.condition.limitSiteName(view.siteName);
		}
		
		if(view.siteScope) {//站点范围
			if (view.siteScope != null) {
				$(std.find(oid.siteScope, view.siteScope, '[scope=option]')).click();
			}
		} else {
			/**全部站点选中*/
			$(std.find(oid.siteScopeAll, '0', '[scope=option]')).click();
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
					$(std.find(oid.media, media.value, '[scope=option]')).click();
				}
			});
			text = text.substring(2, text.length);
			var html = page.condition.limitMedia(mediaOptions);
			htmlArr.push(html);
		} else {
			/**媒体类型全部选中处理*/
			$(std.find(oid.mediaAll, '0', '[scope=option]')).click();
		}
		options.setOptionInfo(oid.site, htmlArr.join("<label type='"+separator.separator+"'>,</label>"));
		
		if (view.sentiment) {//褒贬
			$(view.sentiment).each(function(i, sentiment) {
				if (sentiment != null) {
					$(std.find(oid.sentiment, sentiment, '[scope=option]')).click();
				}
			});
		} else {
			$(std.find(oid.sentimentAll, '-1', '[scope=option]')).click();
		}
		
		if (view.filterSimilar != null) {//相似文
			$(std.find(oid.filterSimilar, view.filterSimilar, '[scope=option]')).click();
		} 
		
		if (view.author) {//作者
			$(input.authorInput).val(view.author);
			page.condition.limitAuthor(view.author);
		}
		
		if (view.orderField) {//作者
			$(input.orderField).val(view.orderField);
		} else {
			$(input.orderField).val(1);
		}
		if (view.orderType) {//作者
			$(input.orderType).val(view.orderType);
		} else {
			$(input.orderType).val(0);
		}
		if (view.websiteTagId) {//站点标签ID
			$(input.websiteTagId).val(view.websiteTagId);
		}
		if (view.hot) {//热点
			$(input.hot).val(view.hot);
		}
	};

	options.clickTime = function(elmt) {
		page.condition.cancelCondition(oid.timeScope);

		var value = $(elmt).find('[type=checkbox]').attr('value');

		var time = $(std.find(oid.time, std.oid(elmt), '[scope=option]')).find('[type=metaname]').text();
		page.condition.limitTime(value, time);

		$(std.findTag(oid.time, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.time, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$(elmt).find('[type=checkbox]').addClass('active');
		$(elmt).find('[type=metaname]').addClass('c_tag_active');
	};

	options.clickTimeCustom = function(elmt) {
		if ($(elmt).attr('status') == 'show') {
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
			$(elmt).attr('status', 'hide');
			$(std.findTag('ctx-time-custom')).hide();
		} else {
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
			$(elmt).attr('status', 'show');
			$(std.findTag('ctx-time-custom')).show();
		}
	};

	options.clickSentiment = function(elmt) {
		var sentiment = $(std.find('sentiment', std.oid(elmt), '[scope=option]'));
		$(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.sentimentAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.sentimentAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		
		if ($(sentiment).attr('selct') == 'true') {
			$(sentiment).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$(sentiment).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		var sentiments = [];
		$(std.findTag('sentiment', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			var sentiment = $(item).find('[type=metaname]').text();

			sentiments.push({
				id : value,
				name : sentiment
			});
		});

		page.condition.limitSentiment(sentiments);
	};
	
	options.clickSentimentAll = function(elmt) {
		if ($(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct') == 'true') {
			$(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}
		
		$(std.findTag(oid.sentiment, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.sentiment, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.sentiment, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');

		var sentiments = [];
		if($(std.findTag(oid.sentimentAll, '[scope=option]')).attr('selct') == 'true'){
			sentiments.push({
				id : [],
				name : '全部'
			});
		}

		page.condition.limitSentiment(sentiments);
	};
	
	options.clickMedia = function(elmt) {
		var media = $(std.find('media', std.oid(elmt), '[scope=option]'));
		$(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.mediaAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.mediaAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
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
		$(std.findTag('media', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			var media = $(item).find('[type=metaname]').text();

			medias.push({
				id : value,
				name : media
			});
		});

		page.condition.limitMedia(medias);
	};
	
	options.clickMediaAll = function(elmt) {
		if ($(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct') == 'true') {
			$(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct', 'false');
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active');
		} else {
			$(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		$(std.findTag(oid.media, '[scope=option]')).attr('selct', 'false');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.media, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		var medias = [];
		if($(std.findTag(oid.mediaAll, '[scope=option]')).attr('selct') == 'true'){
			medias.push({
				id : [],
				name : '全部'
			});
		}
		page.condition.limitMedia(medias);
	};
	
	

	options.clickTopic = function() {
		var topicbox = std.selector(tag.searchPopupBox, oid.topic);

		$.box(topicbox, {
			onOpen : function() {
				var ids = page.condition.getCondition(oid.topic);
				var options = [];
				if (!util.isEmpty(ids)) {
					search.listTopicOptionInfo(ids, {
						success : function(topics) {
							options = topics;
						}
					});
				}
				$(topicbox).optionMagr('initValue', options);
				$(topicbox).optionMagr('showSubOption', 0);
			}
		}, {
			submit : {
				dom : [ topicbox + " .box-submit" ],
				fun : function() {
					var html = page.condition.limitTopic($(topicbox).optionMagr('submit'));
					options.setOptionInfo(oid.topic, html);
				}
			},
			close : {
				dom : [ topicbox + " .box-close", topicbox + " .box-cancel" ]
			}
		});
	};

	options.clickIndustry = function() {
		var industrybox = std.selector(tag.searchPopupBox, oid.industry);

		$.box(industrybox, {
			onOpen : function() {
				var ids = page.condition.getCondition(oid.industry);
				var options = [];
				if (!util.isEmpty(ids)) {
					search.listIndustryOptionInfo(ids, {
						success : function(topics) {
							options = topics;
						}
					});
				}
				$(industrybox).optionMagr('initValue', options);
				$(industrybox).optionMagr('showSubOption', 0);
			}
		}, {
			submit : {
				dom : [ industrybox + " .box-submit" ],
				fun : function() {
					var html = page.condition.limitIndustry($(industrybox).optionMagr('submit'));
					options.setOptionInfo(oid.industry, html);
				}
			},
			close : {
				dom : [ industrybox + " .box-close", industrybox + " .box-cancel" ]
			}
		});
	};

	options.clickArea = function() {
		var areabox = std.selector(tag.searchPopupBox, oid.area);

		$.box(areabox, {
			onOpen : function() {
				var ids = page.condition.getCondition(oid.area);
				var options = [];
				if (!util.isEmpty(ids)) {
					search.listAreaOptionInfo(ids, {
						success : function(topics) {
							options = topics;
						}
					});
				}
				$(areabox).optionMagr('initValue', options);
				$(areabox).optionMagr('showSubOption', 0);
			}
		}, {
			submit : {
				dom : [ areabox + " .box-submit" ],
				fun : function() {
					var html = page.condition.limitArea($(areabox).optionMagr('submit'));
					options.setOptionInfo(oid.area, html);
				}
			},
			close : {
				dom : [ areabox + " .box-close", areabox + " .box-cancel" ]
			}
		});
	};
	
	options.clickSiteScope = function(elmt) {
		var siteScope = $(std.find('siteScope', std.oid(elmt), '[scope=option]'));
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		var selctTrueItme;
		if ($(siteScope).attr('selct') == 'false') {
			selctTrueItme = $(std.findTag('siteScope', '[scope=option][selct=true]'));
			selctTrueItme.each(function(i, item) {
				$(item).attr('selct', 'false');
				$(item).find('[type=checkbox]').removeClass('active');
				$(item).find('[type=metaname]').removeClass('c_tag_active');
			});
			$(siteScope).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active');
		}

		var siteScopes = [];
		$(std.findTag('siteScope', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			var siteScope = $(item).find('[type=metaname]').text();
			siteScopes.push({
				id : value,
				name : siteScope
			});
		});

		page.condition.limitSiteScope(siteScopes);
	}
	
	options.clickSiteScopeAll = function(elmt) {
		var siteScope = $(std.find('siteScope', std.oid(elmt), '[scope=option]'));
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=checkbox]').addClass('active');
		$(std.findTag(oid.siteScopeAll, '[scope=option]')).find('[type=metaname]').addClass('c_tag_active');
		var selctTrueItme = $(std.findTag('siteScope', '[scope=option][selct=true]'));
		selctTrueItme.each(function(i, item) {
			$(item).attr('selct', 'false');
			$(item).find('[type=checkbox]').removeClass('active');
			$(item).find('[type=metaname]').removeClass('c_tag_active');
		});
		
		var siteScopes = [];
		siteScopes.push({
			id : 0,
			name : '全部'
		});
		
		page.condition.limitSiteScope(siteScopes);
	}
	
	options.clickSite = function() {
		var sitebox = std.selector(tag.searchPopupBox, oid.site);

		$.box(sitebox, {
			onOpen : function() {
//				var ids = page.condition.getCondition(oid.site);
				$(sitebox).optionMagr('initValue', []);
				$(sitebox).find(std.find('option-tab', oid.media)).click();
			}
		}, {
			submit : {
				dom : [ sitebox + " .box-submit" ],
				fun : function() {
//					var site = $(sitebox).optionMagr('getParam', 'site');
					page.condition.cancelCondition(oid.site);
					page.condition.cancelCondition(oid.media);
					page.condition.cancelCondition(oid.siteName);
					var result = $(sitebox).optionMagr('submit');
					var siteOptions = [];
					var mediaOptions = [];
					for(var i = 0; i < result.length; i ++){
						if(result[i].site == oid.site){
							siteOptions.push(result[i]);
						}else if(result[i].site == oid.media){
							mediaOptions.push(result[i]);
						}
					}
					var htmlArr = [];
					if(siteOptions.length > 0){
						var siteHtml = page.condition.limitSite(siteOptions);
						htmlArr.push(siteHtml);
					}
					if(mediaOptions.length > 0){
						var mediaHtml = page.condition.limitMedia(mediaOptions);
						htmlArr.push(mediaHtml);
					}
					options.setOptionInfo(oid.site, htmlArr.join("<label type="+separator.separator+">,</label>"));
					
					
//					var html = mediaHtml+","+siteHtml;
//					if(html.substring(0,1) == ","){
//						options.setOptionInfo(oid.site, html.substring(1,html.length));
//					}else if(html.substring(html.length-1,html.length) == ","){
//						options.setOptionInfo(oid.site, html.substring(0,html.length-1));
//					}else{
//						options.setOptionInfo(oid.site, html);
//					}
//					if (site == oid.site) {
//						var html = page.condition.limitSite($(sitebox).optionMagr('submit'));
//						page.condition.cancelCondition(oid.media);
//						page.condition.cancelCondition(oid.siteName);
//						options.setOptionInfo(oid.site, html);
//					} else if (site == oid.media) {
//						page.condition.cancelCondition(oid.site);
//						page.condition.cancelCondition(oid.siteName);
//						var html = page.condition.limitMedia($(sitebox).optionMagr('submit'));
//						options.setOptionInfo(oid.site, html);
//					}
				}
			},
			close : {
				dom : [ sitebox + " .box-close", sitebox + " .box-cancel" ]
			}
		});
	};
	
	options.clickfilterSimilar = function(elmt) {
		var filterSimilar = $(std.find('filterSimilar', std.oid(elmt), '[scope=option]'));
		var selctTrueItme;
		if ($(filterSimilar).attr('selct') == 'false') {
			selctTrueItme = $(std.findTag('filterSimilar', '[scope=option][selct=true]'));
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
		$(std.findTag('filterSimilar', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			var filterSimilar = $(item).find('[type=metaname]').text();
			filterSimilars.push({
				id : value,
				name : filterSimilar
			});
		});

		page.condition.limitFilterSimilar(filterSimilars);
	}

	options.hasCondition = function() {//获取选项数据
		return page.condition.hasCondition() || util.isNotBlank($(input.keywordInput).val());
	}
	options.serialize = function() {//获取选项数据
		this.sync();
		return $(selector.searchAdvanced()).serialize();
	};

	options.serializeArray = function() {//获取选项数据
		this.sync();
		return $(selector.searchAdvanced()).serializeArray();
	};

	options.sync = function() {//同步选项数据
		$(input.author).val($.trim($(input.authorInput).val()));
		$(input.siteName).val($.trim($(input.siteNameInput).val()));
		$(input.keyword).val($.trim($(input.keywordInput).val()));
		$(input.startTime).val($(input.startTimeInput).val());
		$(input.endTime).val($(input.endTimeInput).val());
		$(input.conditionName).val($(input.filterName).val());
	};
	
	options.exportAllWordOrExcel=function(userId) {
		var url=ctx+"/search/exportWordThread.htm";
		if(isExcel){
			url=ctx+"/search/exportExcelThread.htm";
		}
		exportTasks.addTask(userId, type);//添加任务
		ajaxCommFun(url, $("#search-advanced").serialize(), function(response) {//导出全部Word、Excel
			if (response.type == dict.action.suc) {
				exportTasks.updateTaskStatus(taskId, userId, type, 2);//修改任务状态并刷新任务列表 
			} else {
				exportTasks.updateTaskStatus(taskId, userId, type, 3);//修改任务状态并刷新任务列表
			}
		});
	};
})(page.options);

//******************** Condition Function ********************//

page.condition = page.condition || {};//options的辅助对象(options为对外开放对象)
(function(condition) {
	var oid = {
		timeScope : 'time-scope',
		startTime : 'start-time',
		endTime : 'end-time',
		time : 'time',
		topic : 'topic',
		industry : 'industry',
		area : 'area',
		site : 'site',
		siteName : 'siteName',
		siteScope : 'siteScope',
		media : 'media',
		sentiment : 'sentiment',
		author : 'author',
		filterSimilar : 'filterSimilar'
	};

	condition.initEvent = function() {
		$(std.findTag(tag.conditionCancel)).click(function() {
			condition.cancelCondition(std.oid(this));
		});
	};

	condition.limitTimeScope = function(startTime, endTime) {
		if (util.isNotNull(startTime)) {
			$(std.find(tag.conditionInfo, oid.startTime)).html(startTime);
		}
		if (util.isNotNull(endTime)) {
			$(std.find(tag.conditionInfo, oid.endTime)).html(endTime);
		}
		if (util.isNotBlank(startTime) || util.isNotBlank(endTime)) {
			$(std.find(tag.condition, oid.timeScope)).show();
		} else {
			$(std.find(tag.condition, oid.timeScope)).hide();
		}
	};
	condition.limitTime = function(id, time) {
		$(std.find(tag.conditionInfo, oid.time)).html(time);
		$(std.find(tag.conditionForm, oid.time)).empty().append('<input type="hidden" name="timeType" value="' + id + '" />');
		$(std.find(tag.condition, oid.time)).show();
	};
	condition.limitTopic = function(topics) {
		return condition.limitOption(oid.topic, 'topicType', topics);
	};
	condition.limitIndustry = function(industrys) {
		return condition.limitOption(oid.industry, 'industryType', industrys);
	};
	condition.limitArea = function(areas) {
		return condition.limitOption(oid.area, 'areaType', areas);
	};
	condition.limitSite = function(sites) {
		return condition.limitOption(oid.site, 'siteType', sites);
	};
	
	condition.limitSiteScope = function(siteScopes) {
		return condition.limitOption(oid.siteScope, 'siteScope', siteScopes);
	};
	condition.limitMedia = function(medias) {
		return condition.limitOption(oid.media, 'mediaType', medias);
	};
	condition.limitSentiment = function(sentiments) {
		return condition.limitOption(oid.sentiment, 'sentiment', sentiments);
	};
	condition.limitOption = function(oid, inputName, options) {
		if ($(options).size() <= 0) {
			condition.cancelCondition(oid);
			return;
		}
		
		var html = "";

		var formCtx = $(std.find(tag.conditionForm, oid));
		formCtx.empty();

		$(options).each(function(i, option) {
			html += ", " + option.name;
			formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
		});
		html = html.substring(2, html.length);

		$(std.find(tag.conditionInfo, oid)).attr('title', html);
		$(std.find(tag.conditionInfo, oid)).html(html);
		if(options.length=1){
//			alert(1);
		}
		$(std.find(tag.condition, oid)).show();
		if(oid == "site" || oid == "media" || oid == "siteScope"){
			return "<label type='"+oid+"'>"+html+"</label>";
		}
		return html;
		
	};
	condition.limitAuthor = function(name) {
		$(std.find(tag.conditionInfo, oid.author)).html(name);
		
		var formCtx = $(std.find(tag.conditionForm, oid.author));
		
		if (util.isNotBlank(name)) {
			var elmt = formCtx.find('[name=author]');
			if (elmt.size() <= 0) {
				formCtx.append('<input type="hidden" name="author" />');
			}
			elmt.val(name);
			$(std.find(tag.condition, oid.author)).show();
			
		} else {
			formCtx.empty();
			$(std.find(tag.condition, oid.author)).hide();
		}
	};
	condition.limitSiteName = function(name) {
		$(std.find(tag.conditionInfo, oid.siteName)).html(name);
		var formCtx = $(std.find(tag.conditionForm, oid.siteName));

		if (util.isNotBlank(name)) {
			var elmt = formCtx.find('[name=siteName]');
			if (elmt.size() <= 0) {
				formCtx.append('<input type="hidden" name="siteName" />');
			}
			elmt.val(name);
			$(std.find(tag.condition, oid.siteName)).show();

		} else {
			formCtx.empty();
			$(std.find(tag.condition, oid.siteName)).hide();
		}
	};
	condition.limitFilterSimilar = function(filterSimilars) {
		return condition.limitOption(oid.filterSimilar, 'filterSimilar', filterSimilars);
	};
	
	condition.cancelCondition = function(id) {
		$(std.find(tag.conditionForm, id)).empty();
		$(std.find(tag.condition, id)).hide();
		page.options.setOptionInfo(id, '');
	};
	condition.getCondition = function(oid) {
		var ids = [];
		$(std.find(tag.conditionForm, oid)).find('input').each(function(i, obj) {
			ids.push($(obj).val());
		});
		return ids;
	};
	condition.hasCondition = function() {
		return $(std.findTag(tag.condition, ":not(:hidden)")).size() > 0;
	}

})(page.condition);
//******************** List Function ********************//

page.list = page.list || {};
(function(list) {
	list.initEvent = function() {
		//查看文章详情
		$(std.findTag(tag.articleLink)).unbind('click').click(function() {
			$(".c_main").append("<form id='param-form' target='_blank' method='post' action='" + std.u('/search/openArticleDetail.htm') + "'></form>");
			$("#param-form").append("<input type='hidden' name='guid' value='" + std.oid(this) + "'/>");
			$("#param-form").append("<input type='hidden' name='keyword' value='" + $(input.keyword).val() + "'/>");
			document.createElement("a").onclick = $("#param-form").submit();
			$("#param-form").remove();
		});

		/**相似文点击*/
		$(std.findTag("to-similar")).unbind('click').click(function() {
			list.toSimilar($(this).attr("guidGroup"), $(this).attr("guid"));
		});
		
		$(std.findTag(tag.openBriefPickerBtn)).unbind('click').click(function() {
			popup.openBriefPicker(std.oid(this));
		});

		$(std.findTag('btn-show-button')).unbind().click(function() {
			if ($(this).hasClass('c_select')) {
				$(this).removeClass('c_select');
				$(this).parent().find('ul').hide();
			} else {
				$(this).addClass('c_select');
				$(this).parent().find('ul').show();
			}
		});

		$(std.findTag('ctx-button')).unbind().mouseleave(function() {
			$(this).find('div').removeClass('c_select');
			$(this).find('ul').hide();
		});
	};

	list.toSimilar = function(guidGroup, guid){
		var pagesize = page.paging.pagesize ? page.paging.pagesize : 10;
		window.location.href = ctx+"/focusing/similarQueryInfo.htm?guid="+guid+"&guidGroup="+guidGroup+"&"+page.options.serialize()+
		"&currentpage=1&pagesize="+pagesize/**此处返回不需要返回当前页*/
		+"&jumpLocation=search";/**由于相似文页面公用一个,此字段控制返回按钮的跳转*/
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

})(page.list);

//******************** Paging Function ********************//

page.paging = page.paging || {};
(function(paging) {
	paging.initEvent = function() {
		$(selector.pagingContainer()).hide();
		$(selector.pagingContainer()).paging({
			onChange : function(totalpage, totalsize) {
				$(selector.totalSizeLabel).html(formatNum(totalsize));
				$(selector.totalSizeInfo).show();
			},
			gotoNoImpl : function(pageNo, pagesize) {
				page.tosearchByPaging((pageNo - 1) * 4 + 1, pagesize / 4);
			}
		});
	};

	paging.tosetInfo = function(currentpage, pagesize, totalpage, totalsize) {//设置分页信息
		paging.currentpage = currentpage;
		paging.pagesize = pagesize;
		paging.totalpage = totalpage;
		paging.totalsize = totalsize;

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

	//filter init
	page.filter.initEvent();

	//option init
	page.options.initEvent();

	//list init
	page.list.initEvent();

	//paging init
	page.paging.initEvent();

	//condition init
	page.condition.initEvent();

	
	
	//专题选项管理
	$(std.selector(tag.searchPopupBox, 'topic')).optionMagr({
		width : 100,
		listSubOption : function(id) {
			var options = [];
			search.listTopic(id, {
				success : function(topics) {
					options = topics;
				}
			});
			return options;
		}
	});

	//行业选项管理
	$(std.selector(tag.searchPopupBox, 'industry')).optionMagr({
		width : 50,
		listSubOption : function(id) {
			var options = [];
			search.listIndustry(id, {
				success : function(industrys) {
					options = industrys;
				}
			});
			return options;
		}
	});

	//地域选项管理
	$(std.selector(tag.searchPopupBox, 'area')).optionMagr({
		width : 40,
		listSubOption : function(id) {
			var options = [];
			search.listArea(id, {
				success : function(areas) {
					options = areas;
				}
			});
			return options;
		}
	});

	//站点选项管理
	$(std.selector(tag.searchPopupBox, 'site')).optionMagr({
		width : 50,
		listSubOption : function(id) {
			var options = [];
			if ($.isNumeric(id) && id < 0) {
				search.listSiteByCategory(id * -1, {
					success : function(sites) {
						$(sites).each(function(i, item) {
							if (item.spiderSiteId != 0) {
								options.push(item);
							}
						});
					}
				});
			} else if (id == 'all') {
				search.listSite({
					success : function(sites) {
						$(sites).each(function(i, item) {
							if (item.spiderSiteId != 0) {
								options.push(item);
							}
						});
					}
				});
			}
			return options;
		}
	});

	//******************** 业务需求初始化 ********************//
	$(selector.listMask()).hide();
	//$(std.findTag('time', '[scope=option]')).get(0).click();

	var action = $(input.action).val();

	if ("focusFilterName" == action) {
		$(selector.saveSearchAdvanced).click();
	}
	/**
	 * 小气泡提示
	 */
	$('#filterName').poshytip({
		content: '请填写过滤器名称',
		showOn: 'none',
		alignTo: 'target',
		alignX: 'inner-left',
		offsetX: 0,
		offsetY: 5
	});
	
});