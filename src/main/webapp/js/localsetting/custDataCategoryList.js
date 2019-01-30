
/**
 * ajax 获取客户数据分类列表 sharefun2068 2014-12-17
 */

var  queryPageList=function(pageNo,pagesize){
    	var json={
	       type:sys_type,
               keywords:'', 
               currentpage:pageNo==null?page_globePath:pageNo,
               pagesize:pagesize
	};
        
     //if (jQuery("#input_2").val().trim() != '' && jQuery("#input_2").val() != '输入查找词')
        
       if (jQuery("#keywords").val().trim() != ''){
           json.keywords=jQuery("#keywords").val().trim();
       } 
	$.ajax({
		url: sys_globePath + "/custdata/listPageByAjax.htm", 
		data:json,
		dataType:"text",
		type: "post",
		success: function(data){
			$("#custCategoryList").html("");
			$("#custCategoryList").html(data);
		}	
	});
}

//删除客户数据分类信息 sharefun2068 2014-12-17
var deleteCustDataCategory=function(obj){
        layer.confirm('确认要删除该条记录吗?',function(index){
        layer.close(index);
            var fun = function(msg){
                    if(msg.result == "success"){
                        layer.msg("删除成功！",2,1);
                        window.setTimeout(function(){queryPageList(1,10);},1500);
                    }else{
                        layer.alert(msg.result);
                    }
                }
         ajaxCommFun(sys_globePath+ "/custdata/delete.htm" , "id="+obj, fun);
    }); 
}

//排序
var indexBlur=function(id,obj,ided){
    
    if($(obj).val()==null||$(obj).val().trim()==""){
       layer.msg("排序序号不能为空！",1,0);
       $(obj).val(ided);
       //$(obj)[0].focus();
    }else{
       var fun = function(msg){
                    if(msg.result == "success"){
                        //layer.msg("删除成功！",2,1);
                        //window.setTimeout(function(){queryPageList(1,10);},1500);
                    }else{
                        layer.alert(msg.result);
                    }
                }
       ajaxCommFun(sys_globePath+ "/custdata/setIndex.htm" , "id="+id+"&index="+$(obj).val(), fun);
    }
}