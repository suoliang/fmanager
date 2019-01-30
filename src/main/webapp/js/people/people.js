/**
 * 初始化
 */
$(function() {
	//点击页数页面上移
	$(std.findTag('paging-action')).click(function(){
		scorllTo();
	});
	//选择站点查询
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});

});
$(function() {
	//所属状态查询
	$.divselect("#divselect8", "#inputselect8", function(txt, value) {
		falt = value;
	});	
	//来源查询
	$.divselect("#divselect9", "#inputselect9", function(txt, value) {
		falt = value;
	});	
});
/**
 * 新增虚拟人物函数
 */
function addVirtualIdentity(){
	window.location.href = "addVirtualIdentityPeopleUrl.htm";
}
/**
 * 修改虚拟人物函数
 */
function updateVirtualIdentity(id,currentPage){
	window.location.href = "updateVirtualIdentityPeopleUrl.htm?id="+id+"&currentPage="+currentPage;
}
/**
 * 虚拟人物查询函数
 * currentPage:当前页
 */
function queryPeople(currentPage){
	//维护状态
//	var statusid=  $("#status_id").find(".active").prop("id");
	//来源
//	var createId=  $("#create_id").find(".active").prop("id");
	
	
	//维护状态->下拉框
	var statusid=$("#inputselect8").val();
	//来源->下拉框
	var createId=$("#inputselect9").val();
	
	ajaxCommFunText(std.u('/people/VirtualIdentityAjax.htm'),{
		virtualIdentityQuery : $("#virtualIdentity_id").val(),//虚拟人物查询参数
		virtualSelectSiteQuery : $("#inputselect7").val(),//选择站点查询参数
//		status_id:$("#"+statusid).attr("oid"),//状态
//		create_id:$("#"+createId).attr("oid"),//来源
		status_id:statusid,//状态
		create_id:createId,//来源
		currentPage:currentPage//当前页
		
	}, function(response) {
		$('#virtualTable_id').html(response.data);
	});
}
/**
 *维护状态判断样式是否选中
 */
function selectStatus(obj,id,div_id){
	if ($(obj).hasClass("active")) {//点击进行 判断该对象是否具有这个样式
		$("#"+id).removeClass('active');
	} else {
		$("#"+div_id).find(".active").removeClass('active');//先移除所有的样式，后面添加选中的样式。
		$("#"+id).addClass('active');//添加选中的样式
	}
}
scorllTo = function() {//定位到元素
	util.scorllTo('.c_top');
};
/**
 * 维护状态改变
 */
//当前页
function status(id,statusId,currentPage){
	//维护状态
//	var statusQueryid=  $("#status_id").find(".active").attr("oid");
	//来源
//	var createId=  $("#create_id").find(".active").attr("oid");
	
	
	//维护状态->下拉框
	var statusid=$("#inputselect8").val();
	//来源->下拉框
	var createId=$("#inputselect9").val();
	
	
	//准备好需要修改成为的statusId。
	if(statusId==0){
		statusId=1;
	}else{
		statusId=0
	}
	ajaxCommFunText(std.u('/people/updateStatusAjax.htm'),{
		
		virtualIdentityQuery : $("#virtualIdentity_id").val(),//虚拟人物查询参数
		virtualSelectSiteQuery : $("#inputselect7").val(),//选择站点查询参数
		statusQuery_id:statusid,//查询维护状态
		create_id:createId,//来源
		id:id,//需要修改的行
		status_id:statusId,//需要修改成为的最终statusId。
		currentPageStatus:currentPage
	}, function(response) {
		$('#virtualTable_id').html(response.data);
	});
}



