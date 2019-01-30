var falt;
$(function() {
	//新增真实身份，学历
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});
});

/**
 * 新增真实人物身份
 */
function addTruePeople(){
	if($.trim($("#truePeople_id").val())==""){
		$("#truePeople_id").focus();
	}else{
		ajaxCommFunText(std.u('/people/addTrueIdentityPeople.htm'), {
			truePeople:$("#truePeople_id").val(),//真实身份
			trueKeyword:$("#trueKeyword_id").val(),//关键字
			trueDescription:$("#trueDescription_id").val(),//简介
			sex:$("input[name='sex']:checked").val(),//性别
			mobile:$("#mobile_id").val(),//手机
			qq:$("#qq_id").val(),//QQ
			wx:$("#wx_id").val(),//微信
			email:$("#email_id").val(),//email
			Occupation:$("#Occupation_id").val(),//职业
			Education:$("#inputselect7").val(),//学历
			birthday:$("#birthday_id").val()//生日
		}, function(response) {
			window.location.href = "TrueIdentity.htm";
		});	
	}
	
	
}
/**
 * 返回真实身份
 */
function back(){
	window.location.href = "TrueIdentity.htm";
}


