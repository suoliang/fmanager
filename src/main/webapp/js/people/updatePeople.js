var falt;
$(function() {
	//新增虚拟身份,所属站点
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});
	
});

/**
 * 修改虚拟身份
 * currentPage:当前页
 */
function updatePeople(currentPage){
	if($("#virtualIdentity_id").val()==""){
		$("#virtualIdentity_id").focus();
	}else{
		var id = $("#updates_id").val();// 需要修改的id
		var virtualIdentity = $("#virtualIdentity_id").val();//虚拟身份
		var virtualIdentity_site= $("#inputselect7").val();//修改所属站点
		var remarks= $("#remarks_id").val();//备注
		window.location.href = "updateVirtualIdentityPeople.htm?virtualIdentity="+virtualIdentity+"&virtualIdentity_site="+virtualIdentity_site+"&remarks="+remarks+"&id="+id+"&currentPage="+currentPage;
	}
}
/**
 * 返回虚拟身份
 */
function back(){
	window.location.href = "VirtualIdentity.htm";
}