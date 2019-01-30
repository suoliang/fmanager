var falt;
$(function() {
	//新增虚拟身份,所属站点
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});
	
	/**
	 * 新增站点时，光标放到虚拟身份上
	 */
	$('#virtualIdentity_id').focus();
});

/**
 * 新增虚拟身份
 */
function addPeople(){
	//新增虚拟身份判断昵称是否为空，为空获得焦点不执行新增。
	if($.trim($('#virtualIdentity_id').val())==''){
		$('#virtualIdentity_id').focus();
		$('#virtualIdentity_id').val("");
		return;
	}else if($('#inputselect7').val()=='1'){
		$('#querySitePrompt_id').css('display','');
	}
	//校验虚拟身份昵称是否唯一 
	var nickname = $('#virtualIdentity_id').val();
	ajaxCommFun(std.u('/people/nicknameCheck.htm?nickname='+nickname),{
	}, function(response) {
		if (response.type == dict.action.suc) {
			if (response.data >= 1) {
				$('#siteNickname_id1').css('display', '');
			}else{
				var virtualIdentity = $("#virtualIdentity_id").val();//虚拟身份
				var virtualIdentity_site= $("#inputselect7").val();//所属站点
				var remarks= $("#remarks_id").val();//备注
				window.location.href = "addVirtualIdentityPeople.htm?virtualIdentity="+virtualIdentity+"&virtualIdentity_site="+virtualIdentity_site+"&remarks="+remarks;
			}
		} 
	});
}
/**
 * 返回虚拟身份
 */
function back(){
	window.location.href = "VirtualIdentity.htm";
}

/**
 * 站点名称
 * 先从失去焦点时开始判断容易理解
 */
var inputFoucus = function(obj) {
	//获取焦点时影藏提示框信息
	if ($(obj).val() != '') {
		$('#siteNickname_id').css('display', 'none');
		$('#siteNickname_id1').css('display', 'none');
	}
};
var inputBlur = function(obj) {
	if ($(obj).val() == '') {
		$('#siteNickname_id').css('display', '');
	}
	if ($(obj).val() != '') {
		$('#siteNickname_id').css('display', 'none');
	}
};