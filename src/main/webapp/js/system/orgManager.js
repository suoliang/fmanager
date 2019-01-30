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
	
	var countUser="0";
	$.each(args.countUserMap,function(key,values){
		if(key==args.oid){
			countUser=values;
		}
	});
	
	var data = '<ul class="c_tab2 c_tab_top">'
		+ '<li class="c_tab_sys2_sub1"></li>'
		+ '<li class="c_tab_sys2_sub2"><div class="'+cname+'">'+
		'<div class="c_fl c_sprite c_icon_open c_mt10 c_mr5" retract="' + args.retract + '"  oname="'+args.name+'" oid="' + args.oid + '" pid="' + args.pid + '" onclick="dept.onSwitchSubDeptList(this);"></div><div class="c_omit c_mr5">'
		+ args.name
		+ '</div> </div></li>'
			+ '<li class="c_tab_sys2_sub3">'
			+ countUser
			+ '</li>'
			+ '<li class="c_tab_sys2_sub4"><a href="javaScript:void(0);"  orgId="'+args.oid+'"  orgName="'+args.name+'" parentOrgId="'+args.pid+'"  onclick="orgMagre(this,'+args.pid +',\''+$(args.obj).attr("oname")+'\');"  class="c_mr10 c_color_deeporange c_search_sys2">编辑</a><a href="javaScript:void(0);" onclick="deleteOrg('+args.oid+');" class="c_amore">删除</a>'
			+ '</li>'
			+ '</ul>';
	
	return data;
};
dept.loadSubDepts = function(obj) {
	ajaxCommFun(getContextPath() + "getSubOrgList.htm", "orgid=" + $(obj).attr('oid'), function(data) {
		if (data.result == "success") {
			var html = "";
			var retract = 0;
			if ($(obj).attr('retract')) {
				retract = parseInt($(obj).attr('retract'));
			}
			retract = retract + 20;
			jQuery.each(data.subList, function(index, item) {
				html += dept.generateRow({
					pid : $(obj).attr('oid'),
					oid : item.id,
					name : item.name,
					retract : retract,
					obj:obj,
					countUserMap:data.countUserMap
				});
			});
			$(html).insertAfter($(obj).parent().parent().parent());
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




$(function() {
});

var getContextPath = function() {
	var path = window.location.pathname;
	return path.substr(0, path.lastIndexOf('/') + 1);
};
