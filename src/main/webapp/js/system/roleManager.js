//******************** Business Function ********************//
var dept = dept || {};
dept.generateRow = function(args) {
	var cls=$(args.obj).parent().attr("class");
	var cname="";
	if(typeof(cls)=="undefined"||cls==""){
		cname="c_ml10";
	}else if(cls=="c_ml10"){
		cname="c_ml20";
	}else if(cls=="c_ml20"){
		cname="c_ml30";
	}else if(cls=="c_ml30"){
		cname="c_ml40";
	}else if(cls=="c_ml40"){
		cname="c_ml50";
	}
	else if(cls=="c_ml50"){
		cname="c_ml60";
	}
	else if(cls=="c_ml60"){
		cname="c_ml70";
	}
	else if(cls=="c_ml70"){
		cname="c_ml80";
	}else if(cls=="c_ml80"){
		cname="c_ml90";
	}else if(cls=="c_ml90"){
		cname="c_ml100";
	}
    var ck="";
    if($("#p"+args.pid).is(":checked")){
	ck="checked";
    }
	 $(rolePerms).each(function(p, v) {
		 if(v== args.oid){
			 ck="checked";
		 }
	 }
	 );
    var data = '<ul class="c_tab2 c_tab_top">' + //
    '<li class="c_tab_sys3_add_sub1"><input type="checkbox" name="permId" id="p'+args.oid+'" value="' +//
    args.oid + '" cid="' + args.pid + '" onclick="checkParent(this);"  '+ck+' class="c_check"></div></li>' + //
    '<li class="c_tab_sys3_add_sub2"> <div class="'+cname+'">' + //
    '<div class="c_fl c_sprite c_icon_open c_mt10 c_mr5"  retract="' + args.retract +//
    '"  id="divId' + args.oid + '"  oid="' + args.oid + '" pid="' + args.pid + //
    '" onclick="dept.onSwitchSubDeptList(this);"></div><div class="c_omit c_mr5">' + args.name + '</div> </div></li>' + '</ul>';

    return data;
};
dept.loadSubDepts = function(obj) {
    ajaxCommFun(getContextPath() + "showPerm.htm", "systemCode=fmanager&parentId=" + $(obj).attr('oid'), function(data) {
	if (data.result == "success") {
	    var html = "";
	    var retract = 0;
	    if ($(obj).attr('retract')) {
		retract = parseInt($(obj).attr('retract'));
	    }
	    retract = retract + 20;
	    jQuery.each(data.permList, function(index, item) {
		html += dept.generateRow({
		    pid : $(obj).attr('oid'),
		    oid : item.id,
		    name : item.name,
		    retract : retract,
		    obj:obj
		});
	    });
	    $(html).insertAfter($(obj).parent().parent());
	}
    });
};
dept.hideSubDepts = function(obj) {
    $('div[pid=' + $(obj).attr('oid') + ']').each(function() {
	var tr = $(this).parent().parent().parent();
	tr.hide();
	$(this).removeClass('c_icon_closd');
	$(this).addClass('c_icon_open');
	// 迭代
	dept.hideSubDepts($(this));
    });
};
dept.showSubDepts = function(obj) {
    $('div[pid=' + $(obj).attr('oid') + ']').each(function() {
	var tr = $(this).parent().parent().parent();
	tr.show();
	$(this).removeClass('c_icon_closd');
	$(this).addClass('c_icon_open');
    });
};
dept.onSwitchSubDeptList = function(obj) {
    if ($(obj).hasClass('c_icon_open')) {
	if ($(obj).attr('loaded') == 'loaded') {
	    this.showSubDepts(obj);
	} else {
	    this.loadSubDepts(obj);
	    $(obj).attr('loaded', 'loaded');
	}

	$(obj).removeClass('c_icon_open');
	$(obj).addClass('c_icon_closd');
    } else {
	this.hideSubDepts(obj);

	$(obj).removeClass('c_icon_closd');
	$(obj).addClass('c_icon_open');
    }
};
dept.toUpdateDept = function(deptid) {
    $("#toAddDept").attr('oid', deptid);
    $("#toAddDept").click();
};
dept.deleteDept = function(oid, deleteFlag) {
    if (deleteFlag == 1) {
	alertMsg("被删除的组织必须为已停用组织");
	return;
    }
    confirms({
	msgs : "该组织及所有下级组织将被一并删除，确定要删除吗？",
	success : function() {
	    ajaxCommFun(ctx + "/deptManage/deleteDeptXHR.htm", "deptid=" + oid, function(data) {
		if (data.result == "success") {
		    location.href = location.href;
		} else {
		    alertMsg(data.result);
		}
	    });
	}
    });
};

var checkParent2 = function(parent) {

    if ($(parent).is(":checked") || $(parent).attr("disabled") == 'disabled') {
	$("#divId" + $(parent).val()).attr('onclick', '');
    } else {
	$("#divId" + $(parent).val()).attr('onclick', 'dept.onSwitchSubDeptList(this);');
    }

    $('input[cid=' + $(parent).val() + ']').each(function() {
	if ($(parent).is(":checked") || $(parent).attr("disabled") == 'disabled') {
	    $(this).prop("checked", false);
	    $(this).prop("disabled", "disabled");
	    $("#divId" + $(this).val()).attr('onclick', '');
	    checkParent(this);
	} else {
	    $(this).prop("disabled", "");
	    $("#divId" + $(this).val()).attr('onclick', 'dept.onSwitchSubDeptList(this);');
	    checkParent(this);
	}
    });
}



var checkParent = function(parent) {
    if (!$(parent).is(":checked")) {
	$("#p"+$(parent).attr("cid")).prop("checked", false);
	$("#permIdAll").prop("checked", false);
    }
    $('input[cid=' + $(parent).val() + ']').each(function() {
	if ($(parent).is(":checked")) {
	    $(this).prop("checked", true);
	    checkParent(this);
	} else {
	    $(this).prop("checked", false);
	    checkParent(this);
	}
    });
}

var checkAllPerm=function(ths){
	 if ($(ths).is(":checked")) {
			$('input[name=permId]').prop("checked", true);
	 }else{
		 $('input[name=permId]').prop("checked", false);
	 }
}


$(function() {
});

var getContextPath = function() {
    var path = window.location.pathname;
    return path.substr(0, path.lastIndexOf('/') + 1);
};
