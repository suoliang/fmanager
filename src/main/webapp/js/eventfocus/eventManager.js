
var layerWin;

/**
 * ajax 获取客户数据分类列表 
 * sharefun2068 
 * 2014-12-18
 */
var  queryPageList=function(pageNo,pagesize){
    //alert("ok");
    	var json={
             currentpage:pageNo,
             pagesize:pagesize  
        };
        
       if (jQuery("#keywords").val().trim() != ''){
           json.keywords=jQuery("#keywords").val().trim();
       } 
        
	$.ajax({
		url: sys_globePath + "/incident/listPageByAjax.htm", 
		data:json,
		dataType:"text",
		type: "post",
		success: function(data){
			$("#eventList").html("");
			$("#eventList").html(data);
		}	
	});
}

//进入分类数据设置弹窗 sharefun2068 2014-12-19
var  eventSetCategory=function(eventId){
   // alert("okkk"+eventId);
var fun = function(data) {     
    var html="";
     //alert(data.catList);
    jQuery.each(data.catList,function(index, item) {
         // alert(item.name);
        var name=item.name;
        var catId=item.id;
        var checked="";
        
        //alert(item.name);
        jQuery.each(data.custdataList,function(custindex, custitem) {       
              if(custitem.cateId==catId){
                  checked="checked";
              }
         });
        html=html+'<input type="checkbox" name="catId" value="'+catId+'"   '+checked+'  > '+name+"</input>"; 
        
        
    });
    
    html=html+'<input type="hidden" name="eventId" id="eventId"  value='+eventId+'  />';
    
    
    jQuery("#custdatacategoryList").html(html);
    layerWin = jQuery.layer({
		type : 1,
		title : [ '分类设置', true ],
		border : [ 5, 0.5, '#666', true ],
		offset : [ '100px', '' ],
		move : [ '.xubox_title', true ],
		area : [ '300px', 'auto' ],
		shadeClose : true,
		moveType : 1,
		zIndex : 1200,
		page : {
			dom : '#categoryEvent'
		}
	});
        
}
  ajaxCommFun(sys_globePath + "/incident/setCategory.htm","dataId="+eventId+"&type=1" , fun);  
}

//提交分类结果 sharefun2068 2014-12-19
var submitSetCategory=function(){
    var catIds="";
    $('input[name="catId"]:checked').each(function(){
                catIds=catIds+$(this).val()+","
      });
    
    var eventId=$("#eventId").val();
    if(catIds==""){  
        return;
    }else{
        var fun=function(data){
              if(data.result=="success"){
                  layer.closeAll();
                  layer.msg("分类设置成功！",2,10);
                  queryPageList(1,10);
              }
        }

        ajaxCommFun(sys_globePath + "/incident/submitSetCategory.htm",
        "type=1&dataId="+eventId+"&catIds="+catIds.substr(0,catIds.length-1), fun);  
    }
}