/*
 * 初始化方法
 */
$(function() {

	$('[tag=down-action]').click(function() {
		var index = $(this).attr("index");//本行的index
		var oid = $(this).attr("oid");//本行的ID
		var needTrue = $(this).attr("id");//本行是否显示
//		var nextOid =$('[tag=down-action][index='+(parseInt(oid)+1)+']').attr("oid");//下一行的ID
		var menuRow = std.find('menu-row', oid).next();
		if(menuRow.size() > 0) {
			var nextOid = menuRow.attr("oid");//下一行的id
			var nextIndex = menuRow.attr("index");//下一行的index
			var nextNeedTrue=menuRow.attr("id");//下一行是否需要显示
			window.location.href = std.u('/setting/toDown.htm') + "?id=" + oid + "&index=" + index +"&needTrue="+needTrue+ "&nextOid=" + nextOid+"&nextIndex="+nextIndex+"&nextNeedTrue="+nextNeedTrue;
		}
	});

	$('[tag=up-action]').click(function() {
		
		var index = $(this).attr("index");//本行的index
		var oid = $(this).attr("oid");//本行的ID
		var needTrue = $(this).attr("id");//本行是否显示
//		var lastOid = $('[tag=up-action][index=' + (parseInt(oid) - 1) + ']').attr("oid");//上一行的ID
		var menuRow = std.find('menu-row', oid).prev();//上一行
		//在最上面的时候
		if(menuRow.attr("oid")==undefined){
			return false;
		}
		if(menuRow.size() > 0) {
			var lastOid = menuRow.attr("oid");//上一行的id
			var lastIndex = menuRow.attr("index");//上一行的index
			var lastTrue=menuRow.attr("id");//上一行是否需要显示
			window.location.href = std.u('/setting/toUp.htm') + "?id=" + oid + "&index=" + index +"&needTrue="+needTrue+ "&lastOid=" + lastOid+"&lastIndex="+lastIndex+"&lastTrue="+lastTrue;
		}
	});
//	$('[tag=tag_sort_id]').keydown(function(event){
//		if(event.keyCode==13){
//			inputBlur(this)
//		} 
//	});
	
	$('[tag=tag_sort_id]').unbind().blur(function(){
		var tag=this;
		$("#seveOrder").unbind().click(function(){
			inputBlur(tag);
		})
	});
});

$(function() {
	showHidden();
});

var showHidden = function(){
	$("[tag=tag_id]").each(function(prop, value) {
		if ($(value).text() == 'true') {
			$(value).html("显示");
		} else if ($(value).text() == 'false') {
			$(value).html("隐藏");
		}
	});
	//#808080
	$('[tag=up-action]').first().css('color', '#808080');
	$('[tag=down-action]').last().css('color', '#808080');
	
}



//$(function() {
//	$("[tag=tag_sort_id]").each(function(prop, value) {
////		alert(prop);
////		alert($(value).attr("oid"));
//		$(value).attr("oid").keydown(function(e) {
//			if (e && e.keyCode == 13) {
//					alert("asads");
//			}
//			e.stopPropagation();
//		});
//		
//	});
//});
	
//	$("#indexInput_id").keydown(function(e) {
//		if (e && e.keyCode == 13) {
//			var json={
//					oid :$(obj).attr("oid"),//本行的index
//					index : $(obj).val()//本行的ID
//				};
//				
//				$.ajax({
//					url:std.u('/setting/listPageByAjax.htm'), 
//					data:json,
//					dataType:"text",
//					type: "post",
//					success: function(data){
//					}	
//				});
//				refreshSort();
//		}
//		e.stopPropagation();
//	});
	
	
/**
 * 显示隐藏
 */
function needShow(id, needShow, index,name) {
	if(name=='设置'){
//		layer.confirm('"设置"模块模块不能隐藏!',function(index){
//		layer.close(index);
//			return false;
//		});
		layer.msg('"设置"模块模块不能隐藏!',1,0);
	}else{
		window.location.href = std.u('/column/WhetherTF.htm') + "?id=" + id + "&needShow=" + needShow + "&index=" + index;
	}
	//	$('#voicefocus_id').submit();
}

//手动刷新
function refreshSort(){
	var sort_id;
	var oid;
	$("[name=indexInput]").each(function(prop, value) {
		var params={
				index : $(value).val(),
				id : $(this).attr("oid") //本行的ID
		};
		ajaxCommFunText(ctx+'/setting/refreshSort.htm', params, function(response) {
			if (response.type == dict.action.suc) {
				$("#menuList").empty();
				var html=response.data;
				$("#menuList").html(html);
				showHidden();
				$('[tag=tag_sort_id]').unbind().blur(function(){//失去焦点事件注册
					var tag=this;
					$("#seveOrder").unbind().click(function(){
						inputBlur(tag);
					})
				});
			} else {
				alertMsg(response.message);
			}
		});
	});
}

function inputBlur(obj){
	if($(obj).val()==null||$(obj).val().trim()==""){
		layer.msg("排序序号不能为空！",1,0);
		$(obj).val(0);
	}
	var params={
			oid : $(obj).attr("oid"),//本行的index
			index : $(obj).val()//本行的ID
	};
	
	ajaxCommFunText(ctx+'/setting/listPageByAjax.htm', params, function(response) {
		if (response.type == dict.action.suc) {
			refreshSort()
		} else {
			alertMsg(response.message);
		}
	});
}

