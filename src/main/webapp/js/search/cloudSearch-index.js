var page = page || {};
(function() {
	//******************** Global Constant ********************//
	var tag = {
		messageLink : "btn-leave-detail",
		articleSearch : 'article-search'
	};

	var input = {
		reportId : 'input[name=reportId]',
		orderField : 'input[name=orderField]',
		orderType : 'input[name=orderType]'
	};

	//******************** Page Function ********************//

	page.currentParam = '1=1'; 

	page.initEvent = function() {
		//加载之后往form里填充值
//		page.options.defaultLoad();
//		$(std.findTag('option-item')).each(function() {
//			page.options.condition(this);
//		});
	};

	//******************** Option Function ********************//

	page.options = page.options || {};
	(function(options) {
		//初始化事件
		$("#keywordInput").keydown(function(e) {
			if (e && e.keyCode == 13) {
				options.searchFun();
			}
			e.stopPropagation();
		});
		$(std.findTag('option-itemAll')).unbind().click(function() {
			options.conditionAll(this);
		});

		$(std.findTag('option-item')).unbind().click(function() {
			options.condition(this);
		});

		$(std.findTag(tag.articleSearch)).unbind('click').click(function() {//搜索
			if($('[tag=option-item][selct=true]').size() == 0 && $('[tag=option-itemAll][selct=true]').size() == 0){
				return layer.msg("请选择搜索引擎！");
				
			}
			options.searchFun();
		});
		options.searchFun=function(){
			//var params = $('#search-advanced').serialize();
			$("#keyword").val($.trim($("#keywordInput").val()));
			
			$(std.findTag('iframe-tag')).each(function(i,item) {
				var url = $(item).attr('title');
				console.log(url);
				var params = url+$("#keyword").val();
				if (url == 'http://search.twcc.com/#web/') {
					params = params + '/1/';
				}
				$("#"+$(item).attr('id')).attr('src', ctx + '/CloudSearch/toFrameList.htm?urlLeft=' +encodeURI(encodeURI(params)));
			});
			
			/**参数中中文乱码问题处理*/
			//params = params + "&keyword=" + encodeURI(encodeURI($("#keyword").val()));
			//window.location.href = std.u("/CloudSearch/Search.htm?" + params);
		};
		options.condition=function(item){
			
			var engine = $(std.find('option-item', std.oid(item), '[scope=option]')); 
			/**全选按钮去除选中样式*/
			$(std.findTag('option-itemAll', '[scope=option]')).attr('selct','false');
			$(std.findTag('option-itemAll', '[scope=option]')).find('[type=checkbox]').removeClass('active');
			$(std.findTag('option-itemAll', '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
			
			/**标签内容对象*/
			var tabContent = $(std.find('tab-content-item', std.oid(item), '[scope=tab-content]'));
			/**标签对象*/
			var tab = $(std.find('tab-item', std.oid(item), '[scope=tab]'));
			if ($(engine).attr('selct') == 'true') {/**选中时去除选中*/
				$(engine).attr('selct', 'false');
				var chooseArray = $(std.findTag('option-item', '[scope=option][selct=true]'));
				/**至少保留一个选中项*/
				if (chooseArray.length < 1) {
					$(engine).attr('selct', 'true');
					$($(std.findTag('tab-item', '[scope=tab][selct=true]'))[0]).find('[name=navi]').addClass('nav_pag_active');
					var obj = $($(std.findTag('tab-item', '[scope=tab][selct=true]'))[0]);
					$($(std.findTag('tab-content-item', std.oid(obj)))).css('display','');
					return;
				}
				$(item).find('[type=checkbox]').removeClass('active');
				$(item).find('[type=metaname]').removeClass('c_tag_active');
				//--------/**标签隐藏，第一个标签样式显示*/
				$(tab).css('display','none');
				$(std.findTag('tab-item', '[scope=tab][selct=true]')).each(function(i, item) {
					$($(item)).find('[name=navi]').removeClass('nav_pag_active');
				});
				$(tab).attr('selct', 'false');
				$($(std.find('tab-item', std.oid($(std.findTag('tab-item', '[scope=tab][selct=true]')[0]))))).find('[name=navi]').addClass('nav_pag_active');
				//--------/**标签内容隐藏，第一个标签内容显示*/
				$(tabContent).attr('selct', 'false');
				$(tabContent).css('display','none');
				$(std.find('tab-content-item', std.oid(item))).attr('selct','false');
				$(std.find('tab-content-item', std.oid($(std.findTag('tab-item', '[scope=tab][selct=true]')[0])))).attr('selct','true');
				$($(std.findTag('tab-content-item', '[scope=tab-content][selct=true]'))[0]).css('display','');
			} else {/**未选中，选中*/
				//-------/**标签未选中隐藏*/
				$(std.findTag('tab-item', '[scope=tab][selct=false]')).each(function(i, item) {
					$(item).css('display','none');
				});
				//-------
				$(engine).attr('selct', 'true');
				$(item).find('[type=checkbox]').addClass('active');
				$(item).find('[type=metaname]').addClass('c_tag_active');
				//---------/**当前选中标签显示，移除所有选中标签样式后，当前所选标签选中*/
				$(tab).css('display','');
				$(std.findTag('tab-item', '[scope=tab][selct=true]')).each(function(i, item) {
					$($(item)).find('[name=navi]').removeClass('nav_pag_active');
				});
				$(tab).attr('selct', 'true');
				$(tab).find('[name=navi]').addClass('nav_pag_active');
				//---------/**标签内容隐藏，当前选中标签内容显示*/
				$(std.findTag('tab-content-item', '[scope=tab-content]')).attr('selct','false');
				$(std.findTag('tab-content-item', '[scope=tab-content]')).css('display','none');
				$(tabContent).attr('selct', 'true');
				$(tabContent).css('display','');
				$(std.find('tab-content-item', std.oid(item), '[scope=tab-content]')).css('display','');
			}
			
			var engines = []; 
			$(std.findTag('option-item', '[scope=option][selct=true]')).each(function(i, item) {
				var value = $(item).find('[type=checkbox]').attr('value');
				engines.push({
					id : value
				});
			});
			limitOption("spiderId", 'spiderId', engines);
			//动态生成input
			function limitOption(oid, inputName, options) {
				var formCtx = $(std.find("engineId-form", oid));
				formCtx.empty();

				$(options).each(function(i, option) {
					formCtx.append('<input type="hidden" name="engineIds[' + i + '].engineId" value="' + option.id + '" />');
				});
			}
		};

		options.conditionAll=function(item){
			if ($(std.findTag('option-itemAll', '[scope=option]')).attr('selct') == 'true') {
				return;
				$(std.findTag('option-itemAll', '[scope=option]')).attr('selct','false');
				$(item).find('[type=checkbox]').removeClass('active');
				$(item).find('[type=metaname]').removeClass('c_tag_active');
				
				$(std.findTag('tab-item', '[scope=tab]')).attr('selct','true');
				$(std.findTag('tab-item', '[scope=tab]')).css('display','none');
				
				$(std.findTag('tab-content-item', '[scope=tab-content]')).attr('selct','true');
				$(std.findTag('tab-content-item', '[scope=tab-content]')).css('display','none');
				
			} else {
				$(std.findTag('option-itemAll', '[scope=option]')).attr('selct','true');
				$(item).find('[type=checkbox]').addClass('active');
				$(item).find('[type=metaname]').addClass('c_tag_active');
				
				//---------/**标签内容隐藏，标签及其样式隐藏，并默认显示标签集合和标签内容集合的第一个元素*/
				$(std.findTag('tab-content-item', '[scope=tab-content]')).attr('selct','false');
				$(std.findTag('tab-content-item', '[scope=tab-content]')).css('display','none');
				
				$(std.findTag('tab-item', '[scope=tab][selct=true]')).each(function(i, item) {
					$($(item)).find('[name=navi]').removeClass('nav_pag_active');
				});
				$(std.findTag('tab-item', '[scope=tab]')).attr('selct','false');
				$(std.findTag('tab-item', '[scope=tab]')).css('display','');
				$($(std.findTag('tab-item', '[scope=tab]')[0])).find('[name=navi]').addClass('nav_pag_active');
				$($(std.findTag('tab-content-item', '[scope=tab-content]'))[0]).css('display','');
				
			}
			
			$(std.findTag('option-item', '[scope=option]')).attr('selct', 'false');
			$(std.findTag('option-item', '[scope=option]')).find('[type=checkbox]').removeClass('active');
			$(std.findTag('option-item', '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
			
			var engines = []; 
			$(std.findTag('option-item', '[scope=option][selct=true]')).each(function(i, item) {
				var value = $(item).find('[type=checkbox]').attr('value');
				engines.push({
					id : value
				});
			});
			limitOption("spiderId", 'spiderId', engines);
			//动态生成input
			function limitOption(oid, inputName, options) {
				var formCtx = $(std.find("engineId-form", oid));
				formCtx.empty();

				$(options).each(function(i, option) {
					formCtx.append('<input type="hidden" name="engineIds[' + i + '].engineId" value="' + option.id + '" />');
				});
			}
			
		};
		
	})(page.options);

	//******************** List Function ********************//

	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			
		};
	})(page.list);

	//******************** Paging Function ********************//

	page.paging = page.paging || {};
	(function(paging) {
		paging.initEvent = function() {
			$(std.findTag("page-num-item")).unbind('click').click(function() {
				$("#keyword").val($.trim($("#keywordInput").val()));
				window.location.href = std.u("/CloudSearch/Search.htm?pageNo=" + std.oid(this) + "&"+ $('#search-advanced').serialize());
			});
			$(std.findTag("page-up-item")).unbind('click').click(function() {
				$("#keyword").val($.trim($("#keywordInput").val()));
				var oid=$("#currentpageNo").val()-1;
				window.location.href = std.u("/CloudSearch/Search.htm?pageNo=" + oid + "&"+ $('#search-advanced').serialize());
			});
			$(std.findTag("page-next-item")).unbind('click').click(function() {
				$("#keyword").val($.trim($("#keywordInput").val()));
				var oid=parseInt($("#currentpageNo").val())+1;
				window.location.href = std.u("/CloudSearch/Search.htm?pageNo=" + oid + "&"+ $('#search-advanced').serialize());
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