/**
 * 页面初始化方法
 */
$(function init() {
	$("input[name='checkboxName']").each(function() {//遍历每个元素
		if ($(this).attr("fl") == 'true') {//判断是否为显示列
			$(this).attr("checked", "true");//选中
		} else {

		}
	});
	$('[name=navi]').get(0).click();//子菜单初始化选中第一个
});

function menuSubmit() {
	var fids = [];// 定义一个未选中的id数组
	var findex = [];// 定义一个未选中的索引数组
	var tids = [];// 定义一个选中的id数组
	var tindex = [];// 定义一个选中的索引数组
	var parentId=[];//选中的节点的父id的数组
	var unparentId=[];//未选中的节点的父id的数组
	
	$("input[tag=check-box]:checkbox").not("input:checked").each(function() {//获取未选中的
		fids.push($(this).val());//
		findex.push($(this).attr("index"));
		unparentId.push($(this).attr("id"));
	});
	$("input[name='checkboxName']:checked").each(function() {//获取选中的
		tids.push($(this).val());//
		tindex.push($(this).attr("index"));
		parentId.push($(this).attr("id"));
	});
	window.location.href = std.u('/setting/subMenuShow.htm') + "?fids=" + fids + "&findex=" + findex + "&tindex=" + tindex + "&tids=" + tids+"&parentId="+parentId+"&unparentId="+unparentId;
}

/**
 * 页面中需要显示的模块以及菜单
 */
function menuShow() {
	$('#voicefocus_id').submit();
}

$(function() {
	$("[tag=Children_tag]").each(function(prop, value) {
		if ($(value).text() == 'true') {
			$(value).html("显示");
		} else if ($(value).text() == 'false') {
			$(value).html("隐藏");
		}
	});
	//遍历所有oid为subMenu的元素
	$('[oid=subMenu]').each(function() {
		//点击事件的时候
		$(this).click(function() {
			//所有id中有navi的都隐藏
			$('[id*=navi]').hide();
			//选中的显示
			$('#' + $(this).attr('rel')).show();
			//给选中添加样式
			//			$('[oid=subMenu]').addClass("nav_pag_active");
		});
	});
	//	$('[name=navi]').first().addClass("nav_pag_active");
	$('[name=navi]').first().click();
});

/**
 * 
 */
var tag = {
	searchPopupBox : "popup-box",
	conditionForm : "condition-form"
};

var oid = {
	site : 'subMenu'
};

//******************** Page Function ********************//
var page = page || {};
(function(page) {
	var isMsaking = false;
	page.currentParam = '1=1';
})(page);

//******************** Condition Function ********************//
page.condition = page.condition || {};//options的辅助对象(options为对外开放对象)
(function(condition) {
	condition.getCondition = function(oid) {
		var ids = [];
		$(std.find(tag.conditionForm, oid)).find('input').each(function(i, obj) {
			ids.push($(obj).val());
		});
		return ids;
	};

})(page.condition);

/**
 * 点击子菜单出发事件
 * @param obj
 */
function changeTab(obj) {
	$("[id!=" + $(obj).attr("id") + "]").removeClass('nav_pag_active');//移除没有选中的id的样式
	$("[id=" + $(obj).attr("id") + "]").addClass('nav_pag_active');//添加已经选中的id的样式
	var sitebox = std.selector(tag.searchPopupBox, oid.site);
}
/**
 * 子菜单全选按钮
 */
function selectAll() {
	//	alert($(obj).attr("id"));
	//	alert($(obj).attr("id"));

	//	$("input[type='checkbox']").prop("checked",true);

}

$(function() {
	$(".c_check").each(function(i, obj) {
		$(obj).change(function(event) {
			els = $("input[ctx=" + $(this).val() + "]");
			if (document.getElementById($(this).val()).checked) {
				for (var index = 0; index < els.size(); index++)
					els[index].checked = true;
			} else {
				for (var index = 0; index < els.size(); index++)
					els[index].checked = false;
			}
		});
	});

	$("input[name=checkboxName]:checkbox").click(function() {
		var isAllChecked = true;
		$("input[name=checkboxName][ctx=" + $(this).attr("ctx") + "]:checkbox").each(function(i, obj) {
			// alert(obj.checked+"状态");
			if (!obj.checked) {
				isAllChecked = false;
				return false;
			}
		});
		if (isAllChecked) {
			// alert(document.getElementById("allCheckBox").checked);
			document.getElementById($(this).attr("ctx")).checked = true;
		} else {
			document.getElementById($(this).attr("ctx")).checked = false;
			// $("#allCheckBox").attr("checked", false);
		}
	});
});
