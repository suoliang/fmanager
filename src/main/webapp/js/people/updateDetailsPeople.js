function updatePeople(id){
	window.location.href = "updatePeople.htm?id="+id;
}
$(function(){
	//选择站点查询
	$.divselect("#divselect7", "#inputselect7", function(txt, value) {
		falt = value;
	});
	//判断新增数据的时候，有没有填写学历。
	if($("#selected_id").attr("oid")!=""){
		$("#inputselect7").val($("#selected_id").attr("oid"));
	}
	//性别
	if($("#nan_id").attr("oid")==2){
		$("#nv_id").attr("checked","checked");
	}else{
		$("#nan_id").attr("checked","checked");
	}
	
	if($("#selected_id").attr("oid")==1){
		$("#selected_id").text("请选择");
	}else if($("#selected_id").attr("oid")==2){
		$("#selected_id").text("小学及以下");
	}else if($("#selected_id").attr("oid")==3){
		$("#selected_id").text("初中");
	}else if($("#selected_id").attr("oid")==4){
		$("#selected_id").text("高中");
	}else if($("#selected_id").attr("oid")==5){
		$("#selected_id").text("中专");
	}else if($("#selected_id").attr("oid")==6){
		$("#selected_id").text("大专");
	}else if($("#selected_id").attr("oid")==7){
		$("#selected_id").text("本科");
	}else if($("#selected_id").attr("oid")==8){
		$("#selected_id").text("研究生");
	}else if($("#selected_id").attr("oid")==9){
		$("#selected_id").text("博士及以上");
	}
	
});
/**
 * 保存编辑内容
 */
function Edit(){
	ajaxCommFunText(std.u('/people/Edit.htm'), {
		id:$("#edit_id").val(),//id
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