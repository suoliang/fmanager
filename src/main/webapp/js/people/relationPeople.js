//******************** INIT Function ********************//
$(function() {
	$("#relationpage-paging").paging({
		gotoNoImpl : function(pageNo, pagesize) {
			queryRelationPeople(pageNo, pagesize);
		}
	});
	//点击页数页面上移
	$(std.findTag('paging-action')).click(function(){
		//alert(4);
//		util.scorllTo('.c_top');//定位到元素
		scorllTo();
	});
	//初始化渲染页面
	$.divselect('#site-select', '#site-select-input',function() {});

	showrealName();//真实人物绑定(文本框)

	showvirtualName();//虚拟人物绑定(文本框)

	//初始化绑定事件
	$('#openRelationBtn').click(function() {
		addRelation();
	});
	//查询事件
	queryenvet();

	//取消关联事件注册
	$(std.findTag('cancel-relation')).click(function() {
		clossRelation(std.oid(this));
	});
});
scorllTo = function() {//定位到元素
	util.scorllTo('.c_top');
};
/*
 * 查询事件	
 * */
function queryenvet() {
	$('#searchBtn').click(function(event) {
		var pagesize = $("#relationpage-paging").paging('option', 'pagesize');
		queryRelationPeople(1, pagesize);
	});
	$('#trueIdentity_id').keydown(function(event) {
		if (event.keyCode == 13) {
			var pagesize = $("#relationpage-paging").paging('option', 'pagesize');
			queryRelationPeople(1, pagesize);
		}
	});
	$('#virtualIdentity_id').keydown(function(event) {
		if (event.keyCode == 13) {
			var pagesize = $("#relationpage-paging").paging('option', 'pagesize');
			queryRelationPeople(1, pagesize);
		}
	});
}
/**
 * 取消关联
 */
function clossRelation(oid) {
	layer.confirm('确认要删除该条关联记录吗?',function(index){
        layer.close(index);
        var fun = function(response){
                if(response.type == dict.action.suc){
                    layer.msg("删除成功！",2,1);
                    $("#relationpage-paging").paging('refresh');
    				//取消关联事件注册
    				$(std.findTag('cancel-relation')).click(function() {
    					clossRelation(std.oid(this));
    				});
                }else{
                    layer.alert(response.message);
                }
            }
        ajaxCommFun(std.u('/people/removeRelationPeople.htm'), "vid=" + oid, fun);
	});
}

/**
 * 查询关联人物
 */
var queryRelationPeople = function(pageNo, pagesize) {
	var param = getParam();
	param = $.extend(param, {
		pageNo : pageNo,
		pageSize : pagesize
	});
	ajaxCommFunText(std.u("/people/listRelationPeople.htm"), param, function(response) {
		if (response.type == dict.action.suc) {
			// 刷新页面 response.data -> html
			$('#virtualTable_id').html(response.data);
			//刷新页面后重新绑定事件
			$(std.findTag('cancel-relation')).click(function() {
				clossRelation(std.oid(this));
			});

		} else {
			alertMsg(response.message);
		}
	});

}
/**
 * 新增关联页面操作
 */
var addRelation = function() {
	$("#trueNone").attr("style", "display:none");
	$("#trueNone1").attr("style", "display:none");
	$("#zhandianNone").attr("style", "display:none");
	$("#xuninNone").attr("style", "display:none");
	$("#xuninNone1").attr("style", "display:none");
	$("#site-select-input").val("");
	$('#trueentity').focus();// 新增站点时，光标放到真实身份上
	$.box('#peopleIDBox', {
		onOpen : function() {
			$("#def-option").click();
		}
	}, {
		submit : {
			close : false,
			dom : [ '#peopleIDBox .c_but_ok' ],
			fun : function(index) {
				//id
				var trueentityId = $("#peopleId").val()
				var virtualentityId = $("#virtualId").val()
				//input
				var trueentity = $("#trueentity").val()
				var siteselect = $("#site-select-input").val()
				var virtualentity = $("#virtualentity").val()

				if (trueentity == "") {
					$("#trueNone").attr("style", "display:block");
				}
				if (trueentityId == "" && trueentity != "") {
					$("#trueNone").attr("style", "display:none");
					$("#trueNone1").attr("style", "display:block");
				}
				if (siteselect == "") {
					$("#zhandianNone").attr("style", "display:block");
				}
				if (siteselect == 1) {
					$("#zhandianNone").attr("style", "display:block");
				}
				if (virtualentity == "") {
					$("#xuninNone").attr("style", "display:block");
				}
				if (virtualentityId == "" && virtualentity != "") {
					$("#xuninNone").attr("style", "display:none");
					$("#xuninNone1").attr("style", "display:block");
				}
				if (trueentityId != "" && virtualentityId != "" && siteselect != "") {
					this.close(index);
					addRelationSubmit(trueentityId, virtualentityId);
				}
			}
		},
		close : {
			fun : function() {
				//新增后去掉先前记录
				$("#trueentity").val("");
				$("#virtualentity").val("");
			},
			dom : [ '#peopleIDBox .c_but_no', '#peopleIDBox .close' ]
		}
	});
}
function addRelationSubmit(trueentityId, virtualentityId) {
	//新增后去掉先前记录
	$("#trueentity").val("");
	$("#virtualentity").val("");
	var param = getParam();
	param = $.extend(param, {
		id : trueentityId,
		vid : virtualentityId
	});
	ajaxCommFun(std.u("/people/addRelationPeople.htm"), param, function(response) {
		if (response.type == dict.action.suc) {
			// 刷新页面 response.data -> html
			$('#virtualTable_id').html(response.data);
			$("#relationpage-paging").paging('refresh');
			//刷新页面后重新绑定事件
			$(std.findTag('cancel-relation')).click(function() {
				clossRelation(std.oid(this));
			});
		} else {
			alertMsg(response.message);
		}
	});
}
/**
 * 查询方法
 */
function getParam() {
	return {
		realName : $('#trueIdentity_id').val(),// 真实身份
		virtualName : $('#virtualIdentity_id').val()// 虚拟身份
	};
}
/*
 * 新增关联 真实身份名字绑定
 */
function showrealName() {
	$("#trueentity").autocomplete({
		source : function(request, response) {
			ajaxCommFun(ctx + "/people/boundTruePeople.htm", {
				realName : request.term
			}, function(resp) {
				if (resp.type == dict.action.suc) {
					response($.map(resp.data, function(item) {
						return {
							value : item.realName,
							label : item.realName,
							id : item.id
						};
					}));
					$('.ui-autocomplete.ui-menu').css("z-index", '198910150');
				}
			});
		},
		select : function(event, ui) {
			$("#peopleId").val(ui.item.id);
			ui.item.value = ui.item.label;
		},
		autoFill : true,
		selectFirst : true,
		autoFocus : true
	})

	//验证输入的真实身份是否存在
	$("#trueentity").blur(function() {
		//id
//		var trueentityId = $("#peopleId").val()
		//input
		var trueentity = $("#trueentity").val()
		
		$("#trueNone").attr("style", "display:none");
		$("#trueNone1").attr("style", "display:none");
		
		var param = getParam();
		param = $.extend(param, {
			realName : $('#trueentity').val()
		});
		ajaxCommFun(std.u("/people/boundTruePeopleSupport.htm"), param, function(response) {
			if (response.type == dict.action.suc) {
				$('#peopleId').val(response.data);
				$("#trueNone1").attr("style", "display:none");
			} else {
				if (trueentity != "") $("#trueNone1").attr("style", "display:block");
			}
		});
	});

}

/*
 * 新增关联 虚拟身份名字绑定
 */
function showvirtualName() {
	$("#virtualentity").autocomplete({
		source : function(request, response) {
			ajaxCommFun(ctx + "/people/boundVirtualPeople.htm", {
				siteId : $('#site-select-input').val(),
				nickname : request.term
			}, function(resp) {
				if (resp.type == dict.action.suc) {
					response($.map(resp.data, function(item) {
						return {
							value : item.nickname,
							label : item.nickname,
							id : item.id
						};
					}));
					$('.ui-autocomplete.ui-menu').css("z-index", '198910150');
				} else {
					$.msg.error(resp.message);
				}
			});
		},
		select : function(event, ui) {
			$("#virtualId").val(ui.item.id);
			ui.item.value = ui.item.label;
		},
		autoFill : true,
		selectFirst : true,
		autoFocus : true
	});
	//验证输入的虚拟身份是否存在
	$("#virtualentity").blur(function() {
		//id
//		var virtualentityId = $("#virtualId").val()
		//input
		var virtualentity = $("#virtualentity").val()
		$("#xuninNone").attr("style", "display:none");
		$("#xuninNone1").attr("style", "display:none");
		
		var param = getParam();
		param = $.extend(param, {
			nickname : virtualentity,
			siteId : $("#site-select-input").val()

		});
		ajaxCommFun(std.u("/people/boundVirtualPeopleSupport.htm"), param, function(response) {
			if (response.type == dict.action.suc) {
				$('#virtualId').val(response.data);
				$("#xuninNone1").attr("style", "display:none");
			} else {
				if (virtualentity != "") $("#xuninNone1").attr("style", "display:block");
			}
		});
	});
}
