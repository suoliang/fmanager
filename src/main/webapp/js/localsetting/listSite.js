/**
 * ajax 删除
 */
var deleteAjax = function(id, name) {

	layer.confirm('确认要删除该条记录吗?', function(index) {
		layer.close(index);

		$.ajax({
			url : sys_globePath + "/setting/delete.htm?id=" + id,
			async : true,
			dataType : "text",
			type : "post",
			success : function(data) {
				asyncSubmit('addeventForm', name);//异步刷新列表
			}
		});
	});
}
var falt;
var falt8;
$(function() {
	//把来源下拉框的值赋值给隐藏域
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});
	//把媒体类型下拉框的值赋值给隐藏域
	$.divselect("#divselect8", "#inputselect8", function(txt, value) {
		falt8 = value;
	});
});



$(function() {
	$('#site_name_id').keydown(function(e) {
		if (e && e.keyCode == 13) {
			search();
		}
		e.stopPropagation();
	});

});

//$(function() {
//	$("#site_Classification_id").autocomplete({
//		source : function(request, response) {
//			var json = {
//				keywords : ''
//			};
//			if (jQuery("#site_Classification_id").val().trim() != '') {
//				json.keywords = jQuery("#site_Classification_id").val().trim();
//			}
//			$.ajax({
//				url : "siteAutoComplete.htm",
//				data : json,
//				dataType : "json",
//				type : "post",
//				success : function(resp) {
//					response($.map(resp.data, function(item) {
//						return {
//							label : item.site_Classification + "(" + item.site_Classification + ")",
//							value : item
//						};
//					}));
//				}
//			});
//		},
//		select : function(event, ui) {
//			$("#site_Classification_id").val(ui.item.value.site_Classification_id);
//			$("#site_Classification_id").attr("label", ui.item.value.site_Classification);
//			ui.item.value = ui.item.value.site_Classification;
//		}
//	});
//});

function search() {
	asyncSubmit("addeventForm");
}

function addSiteId(obj) {
	if ($(obj).hasClass("active")) {//点击进行 判断该对象是否具有这个样式
		$("[oid=" + $(obj).attr("oid") + "]").removeClass('active');
	} else {
		$("[oid=" + $(obj).attr("oid") + "]").addClass('active');
	}
}

function addSiteName(obj) {//obj代表传过来的是对象的前一个jquery对象
	if ($(obj).hasClass("active")) {
		$("[oid=" + $(obj).attr("oid") + "]").removeClass('active');
	} else {
		$("[oid=" + $(obj).attr("oid") + "]").addClass('active');
	}
}

/**
 * 站点名称
 * 先从失去焦点时开始判断容易理解
 */
var inputFoucus = function(obj) {
	if ($(obj).val() == '') {
		//           $('#site_name').css('display','');
	}
}
var inputBlur = function(obj) {
	if ($(obj).val() == '') {
		$('#site_name').css('display', '');
	}
	if ($(obj).val() != '') {
		$('#site_name').css('display', 'none');
	}
};
/**
 * 站点地址
 * 先从失去焦点时开始判断容易理解
 */
var inputFoucusAddr = function(obj) {
	if ($(obj).val() == '') {
		//        $('#site_addr').css('display','');
	}

}
var inputBlurAddr = function(obj) {
	if ($(obj).val() == '') {
		$('#site_addr').css('display', '');
	}
	if ($(obj).val() != '') {
		$('#site_addr').css('display', 'none');
	}

	//    var json={
	//			siteaPlace : "http://"+$('#site_addr').val()//站点地址
	//	};
	//    $.ajax({
	//		url:std.u('/setting/queryRepeatSite.htm'), 
	//		data:json,
	//		dataType:"text",
	//		type: "post",
	//		success: function(data){
	//			if(data=='"2"'){
	//				 $('#site_addr_same').css('display','');
	////				layer.msg('站点地址已经存在,请重新填写！',1,0);
	//			}
	//		}	
	//	});
}

/**
 * url格式化
 * @param url
 * @returns {String}
 */
function urlFormat(url) {
	url = url.substr(0, 7).toLowerCase() == "http://" ? url : "http://" + url;

	return url;
}



//初始站点全选事件 
var initSelectSiteCKB = function(){
	
	$("#siteAllSelect").change(function(event) {
		var elsDW = document.getElementsByName("siteCheck");
		if (document.getElementById("siteAllSelect").checked) {
			for (var index = 0; index < elsDW.length; index++)
				elsDW[index].checked = true;
		} else {
			for (var index = 0; index < elsDW.length; index++)
				elsDW[index].checked = false;
		}
	});
	
	// select record checkbox
	$("#sitectx input[name=siteCheck]:checkbox").click(function() {
		var isAllChecked = true;
		$("#sitectx input[name=siteCheck]:checkbox").each(function(i, obj) {
			// alert(obj.checked+"状态");
			if (!obj.checked) {
				isAllChecked = false;
				return false;
			}
		});
		if (isAllChecked) {
			// alert(document.getElementById("allCheckBox").checked);
			document.getElementById("siteAllSelect").checked = true;
		} else {
			document.getElementById("siteAllSelect").checked = false;
			// $("#allCheckBox").attr("checked", false);
		}
	});
	
};

var categorys = function(){
	//判断是否选择好站点
	var sites = getSelectSites();
	if(sites==''){
		win.msg.short("请选择需要分类的站点！", "W01");
		return false;
	}else{
		var fun = function(resp) {     
			var html="";
			jQuery.each(resp.data.catList,function(index, item) {
				var name=item.name;
				var catId=item.id;
				var checked="";
				html=html+'' + //
				'<div class="c_tab_choice c_fl">' + //
				'	<div class="c_sprite c_choice_icon c_fl" onclick="addSiteId(this);" type="check_div" name="catId" oid="'+item.id+'"></div>' + //
				'	<div class="c_fl c_tag_active c_w90" onclick="addSiteName($(this).prev());" value="'+catId+'">'+name+'</div>' + //
				'	<div class="c_cb"></div>' + //
				'</div>';
			});
			html=html+'<input type="hidden" name="dataIds" id="dataIds"  value='+sites+'  />';
			
			jQuery("#custdatacategoryLists").html(html);
			
			var index = $.box('#categoryEvents', {}, {});
		}
		ajaxCommFun(sys_globePath + "/setting/setCategorys.htm","type=3" , fun);
	}
};


var getSelectSites = function(){
	var sitesCheck = "";
	$("#sitectxDiv input[name=siteCheck]:checkbox").each(function(i, obj) {
		// alert(obj.checked+"状态");
		if (obj.checked) {
			sitesCheck = sitesCheck+ $(obj).val()+",";
		}
	});
	if(sitesCheck.length>0){
		sitesCheck = sitesCheck.substring(0, sitesCheck.length-1);
	}
	return sitesCheck;
};