/**
 * 初始化
 */
$(function() {
	//点击页数页面上移
	$(std.findTag('paging-action')).click(function(){
		scorllTo();
	});
	//初始化
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});

});
/**
 * 初始化
 */
$(function() {
	//来源查询
	$.divselect("#divselect9", "#inputselect9", function(txt, value) {
		falt = value;
	});
	//所属状态查询
	$.divselect("#divselect8", "#inputselect8", function(txt, value) {
		falt = value;
	});
});

/**
 * 新增真实人物的函数
 */
function addTrueIdentity() {
	window.location.href = "addTrueIdentityPeopleUrl.htm";
}
/**
 * 查询方法
 */
function search(currentPage) {
	//分类->下拉框
	var typeId = $("#inputselect7").val();
	//维护状态->下拉框
	var statusid = $("#inputselect8").val();
	//来源->下拉框
	var createId = $("#inputselect9").val();

	ajaxCommFunText(std.u('/people/queryAjax.htm'), {
		status_id : statusid,//状态
		create_id : createId,//来源
		type_id : typeId,//分类
		trueIdentity : $('#trueIdentity_id').val(),//真实身份
		currentPage : currentPage
	//当前页
	//媒体类型查询
	}, function(response) {
		$('#sitectx_id').html(response.data);
	});
}

/**
 *维护状态判断样式是否选中
 */
function selectStatus(obj, id, div_id) {
	if ($(obj).hasClass("active")) {//点击进行 判断该对象是否具有这个样式
		$("#" + id).removeClass('active');
	} else {
		$("#" + div_id).find(".active").removeClass('active');//先移除所有的样式，后面添加选中的样式。
		$("#" + id).addClass('active');//添加选中的样式
	}
}

/**
 *人物分类样式选中
 */
function selectType(obj, id, div_id) {
	if ($("#" + id).hasClass("active")) {//点击进行 判断该对象是否具有这个样式
		$("#" + id).removeClass('active');
	} else {
		$("#" + div_id).find(".active").removeClass('active');//先移除所有的样式，后面添加选中的样式。
		$("#" + id).addClass('active');//添加选中的样式
	}
}

/**
 * 改变维护状态
 */
function changeStatus(obj, pageNo) {

	//分类->下拉框
	var typeId = $("#inputselect7").val();
	//维护状态->下拉框
	var statusid = $("#inputselect8").val();
	//来源->下拉框
	var createId = $("#inputselect9").val();

	var stutusFinal;
	if ($(obj).attr("oid") == 1) {
		stutusFinal = 0;
	} else {
		stutusFinal = 1;
	}
	ajaxCommFunText(std.u('/people/updateStatus.htm'), {
		status_id : statusid,//状态
		create_id : createId,//来源
		type_id : typeId,//分类
		trueIdentity : $('#trueIdentity_id').val(),//真实身份
		status : stutusFinal,//传入判断后的状态参数
		pageNo : pageNo,//当前页
		id : $(obj).attr("id")
	//状态修改的id
	//媒体类型查询
	}, function(response) {
		$('#sitectx_id').html(response.data);
	});
}

function addSiteName(obj) {//obj代表传过来的是对象的前一个jquery对象
	if ($(obj).hasClass("active")) {
		$("[oid=" + $(obj).attr("oid") + "]").removeClass('active');
	} else {
		$("[oid=" + $(obj).attr("oid") + "]").addClass('active');
	}
}

scorllTo = function() {//定位到元素
	util.scorllTo('.c_top');
};
/*
 * scorllTo = function(obj) {//定位到元素
		$("body,html").animate({
			scrollTop : $(obj).offset().top - 50
		}, 500);
	};
 * 
 * 
 * scorllTo($(input.keywordInput));
 * */
/**
 * 详细
 */
function details(id,pageNo) {
	window.location.href = "details.htm?id=" + id+"&pageNo="+pageNo;
}
