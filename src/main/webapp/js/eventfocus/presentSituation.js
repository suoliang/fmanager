

//获取某事件基本属性 sharefun2068 2014-12-18
var  queryEventDetailById=function(eventid){  
	$.ajax({
		url: sys_globePath + "/incident/detail.htm?id="+eventid, 
		//data:json,
		dataType:"text",
		type: "post",
		success: function(data){
			$("#eventDetail").html("");
			$("#eventDetail").html(data);
		}	
	});
}

//
