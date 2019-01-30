//******************** INIT Function ********************//
$(function() {
	pageStart();
	showTopic(0);
	//面板跳转过来页面初始化
	if(fromBoard){
		//模拟专题选中
		$("div[tag='btn-select-topic'][oid='"+topicId+"']").removeClass("c_color_gay").addClass("c_color_red");
		//模拟时间自定义点击
		$("[tag='time'][scope='option']").find(".c_choice_icon").removeClass("active");
		$("[tag='time'][scope='option']").find("[type='metaname']").removeClass("c_tag_active");
		$("#c_timeck01").find(".c_choice_icon").addClass("active");
		$("#c_timeck01").find(".c_choice_tag").addClass("c_tag_active");
		$('#c_timeck02').show();
		$("#startTimeInput").val(startTimeInput);
		$("#endTimeInput").val(endTimeInput);
//		$("#c_timeck01").click();
		////topic-advanced 表单隐藏域赋值
		$("[name='startTime']").val(startTimeInput);
		$("[name='endTime']").val(endTimeInput);
		$("[name='topicId']").val(topicId);
		if(tree != ""){
			$("span[ tag='btn-show-topic'][oid='"+topCustTopicId+"']").removeClass("c_tree_open").addClass("c_tree_close");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").attr("status", "open");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").attr("loaded", "loaded");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").append(tree);
			clickTopic();
			openChild();
		}
	} else {/**不经过首页面板,代码虽一样,分开来写*/
		//模拟专题选中
		$("div[tag='btn-select-topic'][oid='"+topicId+"']").removeClass("c_color_gay").addClass("c_color_red");
		//模拟时间自定义点击
		$("[tag='time'][scope='option']").find(".c_choice_icon").removeClass("active");
		$("[tag='time'][scope='option']").find("[type='metaname']").removeClass("c_tag_active");
		$("#c_timeck01").find(".c_choice_icon").addClass("active");
		$("#c_timeck01").find(".c_choice_tag").addClass("c_tag_active");
		$("#startTimeInput").val(startTimeInput);
		$("#endTimeInput").val(endTimeInput);
//		$("#c_timeck01").click();
		////topic-advanced 表单隐藏域赋值
		$("[name='startTime']").val(startTimeInput);
		$("[name='endTime']").val(endTimeInput);
		$("[name='topicId']").val(topicId);
		if(tree != ""){
			$("span[ tag='btn-show-topic'][oid='"+topCustTopicId+"']").removeClass("c_tree_open").addClass("c_tree_close");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").attr("status", "open");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").attr("loaded", "loaded");
			$("div[level='0'][oid='"+topCustTopicId+"'][tag='list-sub-topic']").append(tree);
			clickTopic();
			openChild();
		}
	}
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

//加载显示树结构
showTopic = function(parentId) {
	listTopic('parentId=' + (util.isNotBlank(parentId) ? parentId : '0'), {
		success : function(topics) {
			//render
			var currentTopicId = parentId ? parentId : 0;
			var orgListElmt = $(std.find('list-sub-topic', currentTopicId));
			orgListElmt.empty();
			$(topics).each(function(i, item) {
				var level = $(std.find('list-sub-topic', currentTopicId)).attr('level');
				
				level = parseInt(level) + 1;
				
				var ch = new Array;  
				ch = item.id.split(","); 
				var showTopic = "";
				if (util.isNotBlank(item.id)) {
					if (ch.length == 0){
						showTopic = '       <span ' + (item.id!=0?std.flag('btn-show-topic', item.id):'') + ' class="'+(item.subCount == 0 || item.subCount == undefined?"c_tree_close":"c_tree_open")+' c_sprite c_fl c_ml' + level + '0"></span>'; //
					}
					/**前面的加减号是否显示*/
					if (ch.length == 1) {
						showTopic = '       <span ' + (item.id!=0?std.flag('btn-show-topic', item.id):'') + ' class="'+(item.subCount == 0 || item.subCount == undefined?"c_tree_close":"c_tree_open")+' c_sprite c_fl c_ml' + level + '0"></span>'; //
					}
				}
				var selectTopic = '		<div' + std.flag('btn-select-topic', item.id) + ' class="c_color_gay c_omit" type="checkbox" value="'+item.id+'" title="'+item.name+'">' + item.name + '</div>'; //
				/**样式调整- c_pl5*/
				if (util.isBlank(item.id)) {
					selectTopic = '		<div' + std.flag('btn-select-topic', item.id) + ' class="c_color_gay c_omit c_pl5" type="checkbox" value="'+item.id+'" title="'+item.name+'">' + item.name + '</div>'; //
				}
				/**样式调整- c_pl5*/
				if (ch.length > 1) {
					selectTopic = '		<div' + std.flag('btn-select-topic', item.id) + ' class="c_color_gay c_omit c_pl5" type="checkbox" value="'+item.id+'" title="'+item.name+'">' + item.name + '</div>'; //
				}
				orgListElmt.append('<div ' + std.flag('item-topic', item.id) + ' status="close" class="c_sub_speTree">' + //
						'	<div' + std.flag('btn-click-topic', item.id) + ' class="c_tree_list c_border_bot c_f12" scope=option selct="false">' + //
//						'		<i' + std.flag('btn-select-topic', item.id) + ' class="c_tree_no c_sprite c_fl" oid="'+item.id+'" value="'+item.id+'" type="checkbox"></i>' + //
//						'		<div ' + (item.id!=0?std.flag('btn-click-topic1', item.id):'') + '>' + //
						showTopic +
						selectTopic + //
//						'		</div>' + //
						'	</div>' + //
						'	<div class="c_sub"' + std.flag('list-sub-topic', item.id) + ' level="' + level + '"></div>' + //
						'</div>');
			});
			orgListElmt.attr('loaded', 'loaded');

			//初始化事件   TODO    
//			$(std.findTag('btn-select-topic')).unbind().click(function() {
//				//-----------选中字体-----------
//				$(std.findTag('btn-select-topic','[type=checkbox]')).removeClass('c_color_red');
//				$(std.findTag('btn-select-topic','[type=checkbox]')).addClass('c_color_gay');
//				$(std.findTag('btn-click-topic', '[scope=option]')).attr('selct', 'false');
//				
//				$(this).parent().attr('selct', 'true');
//				$(this).removeClass('c_color_gay');
//				$(this).addClass('c_color_red');
//				
//				var topicIds = []; 
//				
//				$(std.findTag('btn-click-topic', '[scope=option][selct=true]')).each(function(i, item) {
//					var value = $(item).find('[type=checkbox]').attr('value');
//					topicIds.push({
//						id : value
//					});
//				});
//				limitOption("topicIds", 'topicId', topicIds);
//				//动态生成input
//				 function limitOption(oid, inputName, options) {
//					var formCtx = $(std.find("topicId-form", oid));
//					formCtx.empty();
//					
//					$(options).each(function(i, option) {
//						formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
//					});
//				}
//				//即时搜索
//				page.currentParam = page.options.serialize();
//				page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
//			});
			clickTopic();
			//展开子节点
//			$(std.findTag('btn-show-topic')).unbind().click(function() {
//				
//				var orgListElmt = $(std.find('list-sub-topic', std.oid(this)));
//				if (orgListElmt.attr('loaded') != 'loaded') {
//					if(std.oid(this)!=0){
//					showTopic(std.oid(this));
//					}
//				}
//
//				if (orgListElmt.attr('status') == 'open') {
//					orgListElmt.hide();
//					orgListElmt.attr('status', 'close');
//					$(std.find('btn-show-topic', std.oid(this))).removeClass('c_tree_close');
//					$(std.find('btn-show-topic', std.oid(this))).addClass('c_tree_open');
//				} else {
//					orgListElmt.show();
//					orgListElmt.attr('status', 'open');
//					$(std.find('btn-show-topic', std.oid(this))).removeClass('c_tree_open');//c_tree_close     c_icon_closd
//					$(std.find('btn-show-topic', std.oid(this))).addClass('c_tree_close');//c_tree_open       c_icon_open
//				}
//			});
			openChild();
		}
	});
};
function clickTopic(){
	//初始化事件   TODO    
	$(std.findTag('btn-select-topic')).unbind().click(function() {
		//-----------选中字体-----------
		$(std.findTag('btn-select-topic','[type=checkbox]')).removeClass('c_color_red');
		$(std.findTag('btn-select-topic','[type=checkbox]')).addClass('c_color_gay');
		$(std.findTag('btn-click-topic', '[scope=option]')).attr('selct', 'false');
		
		$(this).parent().attr('selct', 'true');
		$(this).removeClass('c_color_gay');
		$(this).addClass('c_color_red');
		
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
				var optionId = option.id;
				/**去除专题id的最后一个逗号*/
				if (optionId.lastIndexOf(",") != -1) {
					optionId = optionId.substring(0,optionId.lastIndexOf(","));
				}
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + optionId + '" />');
			});
		}
		//即时搜索
		page.currentParam = page.options.serialize();
		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
	});
}
function openChild(){
	//展开子节点
		$(std.findTag('btn-show-topic')).unbind().click(function() {
			
			var orgListElmt = $(std.find('list-sub-topic', std.oid(this)));
			if (orgListElmt.attr('loaded') != 'loaded') {
				if(std.oid(this)!=0){
					showTopic(std.oid(this));
				}
			}
			
			if (orgListElmt.attr('status') == 'open') {
				orgListElmt.hide();
				orgListElmt.attr('status', 'close');
				$(std.find('btn-show-topic', std.oid(this))).removeClass('c_tree_close');
				$(std.find('btn-show-topic', std.oid(this))).addClass('c_tree_open');
			} else {
				orgListElmt.show();
				orgListElmt.attr('status', 'open');
				$(std.find('btn-show-topic', std.oid(this))).removeClass('c_tree_open');//c_tree_close     c_icon_closd
				$(std.find('btn-show-topic', std.oid(this))).addClass('c_tree_close');//c_tree_open       c_icon_open
			}
		});
	}
listTopic = function(params, fun) {
	ajaxCommFun(std.u("/topic/showCusttopic.htm"), params, function(response) {
		if (response.type == dict.action.suc) {
			fun.success(response.data, response.message);
		} else {
			alertMsg(response.message);
			if ($.isFunction(fun.error)) {
				fun.error(response.message, response.attrs);
			}
		}
	}, true);
};
//******************** 静态页面js效果 ********************//
function pageStart(){
//专题-时间
	$('#c_timeck01').click(function() {
		//取消勾选“今天,昨日....”
		$(std.findTag("time", '[scope=option]')).find('[type=checkbox]').removeClass('active');
		$(std.findTag("time", '[scope=option]')).find('[type=metaname]').removeClass('c_tag_active');
		$("#timeType").attr("value","");//不带时间条件
		//即时搜索
//		page.currentParam = page.options.serialize();
//		page.tosearchByPaging(1, page.paging.pagesize ? page.paging.pagesize : 10);
		if ($('#c_timeck02').is(':hidden')) {
			$('#c_timeck02').fadeIn('slow');
			$('#c_timeck01 .c_choice_icon').addClass("active");
			$('#c_timeck01 .c_choice_tag').addClass("c_tag_active");
		} /*else {
			$('#c_timeck02').fadeOut('slow');
			$('#c_timeck01 .c_choice_icon').removeClass("active");
			$('#c_timeck01 .c_choice_tag').removeClass("c_tag_active");			
		}*/
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