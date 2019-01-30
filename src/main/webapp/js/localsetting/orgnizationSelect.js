
// 查询组织
function queryOrgListByOrgId(orgId, level) {
	var json = {
		rootOrgId : 1,
		orgId : orgId,
		level : level,
		searchKey : ''
	};
	if ($("#targetChoosePopupKey").val().trim() != ''
			&& $("#targetChoosePopupKey").val().trim() != '内容搜索') {
		json.searchKey = $("#targetChoosePopupKey").val().trim();
	}
	$.ajax({
		url : std.u("/custtopic/queryNextOrgnization.htm"),
		data : json,
		dataType : "text",
		type : "post",
		success : function(data) {
			json.searchKey = '';
			$("#orgnizationListTr").nextAll().remove();
			$("#orgnizationListTr").after(data);
		}
	});
}

// 查询组织
function queryOrgListSubByOrgId(orgId, trvar, varpadding, level) {
	var json = {
		rootOrgId : 1,
		orgId : orgId,
		level : level,
		trvar : trvar,
		varpadding : varpadding
	};
	$.ajax({
		url : std.u("/custtopic/queryNextOrgnization.htm"),
		data : json,
		dataType : "text",
		type : "post",
		success : function(data) {
			$("tr[var=" + trvar + "]").after(data);
		}
	});
}

function removeThis(obj){

    $("div[vartype=member]").each(function() {
        
        if ($(obj).attr("memberid") ==$(this).attr("var")) {
               $(this).removeClass("c_none");
        }
        
      });
	$(obj).remove();
        
}