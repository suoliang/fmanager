/**
 * ajax 获取客户专题列表 sharefun2068 2014-12-24
 */
var statusSelect = null;
$(function(){
	$.divselect("#statusSelect", "#topicStatus", function(txt, value) {
		statusSelect = value;
	});	
});
var queryPageList = function(pageNo, pagesize, parentId) {
	var json = {
		keywords : '',
		currentpage : pageNo == null ? page_globePath : pageNo,
		pagesize : pagesize
	};

	if (jQuery("#keywords").val().trim() != '') {
		json.keywords = jQuery("#keywords").val().trim();
		parentId = null;
	} else {
		json.parentId = parentId;
	}

	json.topicStatus = $("[name='topicStatus']").val();
	ajaxCommFunText(std.u('/custtopic/listPageByAjax.htm'), json, function(response) {
		$("#custTopicList").html("");
		$("#custTopicList").html(response.data);
	});
}
//开关点击时间
var querySubListByParentId = function(parentId, obj, level, subId) {
	if ($(obj).hasClass("c_icon_open")) { //仅仅对+图标有效  
		//替换父记录图标
		$(obj).removeClass("c_icon_open");
		$(obj).addClass("c_icon_closd");
		var json = {
			parentId : parentId,
			level : level,
			subId : subId
		};
		ajaxCommFunText(std.u('/custtopic/subListByParentId.htm'), json, function(response) {
			$(response.data).insertAfter($("#id_" + level + "_" + parentId));
		});
	} else {
		$(obj).removeClass("c_icon_closd");
		$(obj).addClass("c_icon_open");

		$("div[id^='" + subId+parentId + "']").each(function(index, item) {
			if ($(this).attr('id') != $(obj).attr('id')) {
				$(this).parent().parent().parent().remove();
			}
		});
	}

}
var gotoTopicDetail = function(id) {
	var pageNo = $("#custtopic-paging").paging('option', 'currentpage');
	util.go('/custtopic/detail.htm?id=' + id + '&pageNo=' + pageNo);
}

//删除客户专题信息 sharefun2068 2014-12-24
var deleteCustTopic = function(obj) {
	layer.confirm('如果存在子专题，将全部被删除，是否继续？', function(index) {
		layer.close(index);
		var fun = function(msg) {
			if (msg.result == "success") {
				layer.msg("删除成功！", 2, 1, function() {
					queryPageList(1, 10, 0);
				});
			} else {
				layer.alert(msg.result);
			}
		}
		ajaxCommFun(sys_globePath + "/custtopic/delete.htm", "id=" + obj, fun);
	});
}

/**初始化专题列表*/
var initCustTopic = function(topicId) {
	ajaxCommFun(std.u("/custtopic/checkIsHaveInit.htm"), "", function(response){
		if (response.type == dict.action.err) {
			$.msg.warning(response.message);
		} else {
			popup.openTopicInitInput(topicId, function(form) {
				console.log(form);
				custtopic.custTopicInit('topicId=' + form.topicId + '&topicInitDate=' + form.topicInitDate,{
					success : function(data, message){
						$.msg.success(message);                      
						$(std.find('btn-init-topic', topicId)).text("初始化中，需要一段时间");
						$(std.find('btn-init-topic', topicId)).removeAttr("onclick");
						$(std.find('btn-init-topic', topicId)).attr("class","c_color_red");
					}
				});
			});
		};
	});
};

//共享用户 GUOQIANG 2015-01-30
var shareCustTopic = function(topicId) {
	var fun = function(data) {
		popup.openUserPicker(util.isNotBlank(data.userIds) ? data.userIds : [], function(userIds) {

			var index = layer.load('正在提交请求...');

			ajaxCommFun(sys_globePath + "/custtopic/shareUsers.htm", "id=" + topicId + "&userIds=" + util.joinCollection(userIds, ','), function(msg) {

				layer.close(index);

				if (msg.result == "success") {
					layer.closeAll();
					$.msg.success(util.isEmpty(userIds) ? "操作成功！" : "共享成功！");
				} else {
					$.alert.error(msg.result);
				}
			});
		});
	}
	ajaxCommFun(sys_globePath + "/custtopic/getShareUsers.htm", "id=" + topicId, fun);
}
//共享用户 sharefun2068 2014-12-25 备份用 同上方法
var shareCustTopic2 = function(topicId) {
	//alert(userIds);
	var fun = function(data) {
		$("#targetChoosePopupKey").val("内容搜索");
		$.box("#targetChooseDiv", {
			onOpen : function() {
			}
		}, {});
		// 查询所有的第一级组织
		//queryOrgListByOrgId(1, 1);

		// 查询所有的成员
		queryMemberListByOrgId(null, null, null, data.userIds);

		$("#targetList").html("");//清空所选 
		//$("#targetList").html($("#receivelist").html());
		$("li[class=orgStyle]").each(function() {
			var _this = $(this);
			_this.unbind();
			_this.click(function() {
				var _liOrg = $(this).attr("orgid");
				$("input[type=checkbox]").each(function() {
					if ($(this).attr("var") == _this.attr("orgId")) {
						$(this).removeAttr("checked");
					}
				});
				$(this).remove();
			})
		})

		$("#targetChooseSave").unbind();
		$("#targetChooseSave").click(function() {
			var userIdsUPD = "";
			$("#targetList .c_pl20").each(function(index) {
				userIdsUPD = userIdsUPD + $(this).attr("memberid") + ",";
			});

			if (userIdsUPD != "") {
				userIdsUPD = userIdsUPD.substring(0, userIdsUPD.length - 1);
				var fun1 = function(msg) {
					if (msg.result == "success") {
						layer.closeAll();
						layer.msg("共享成功！", 2, 1);
						//window.setTimeout(function(){queryPageList(1,10);},500);

					} else {
						layer.alert(msg.result);
					}
				}
				ajaxCommFun(sys_globePath + "/custtopic/shareUsers.htm", "id=" + topicId + "&userIds=" + userIdsUPD, fun1);

			} else {
				layer.msg("共享对象不能为空！", 2, 9);
			}

		});

		// 搜索按钮 start
		$("#targetChoosePopupBtn").unbind();
		$("#targetChoosePopupBtn").click(function() {

			// 查询所有的第一级组织
			//queryOrgListByOrgId(1, 1);
			// 查询所有的成员
			queryMemberListByOrgId();
		});
		// 搜索按钮 end 
	}

	ajaxCommFun(sys_globePath + "/custtopic/getShareUsers.htm", "id=" + topicId, fun);
}

//切换启用 停用 状态 sharefun2068 2014-12-25
var changeCustTopicStatus = function(topicId, status) {
	if (status == 0) {
		var index = layer.confirm('如果存在子专题，将全部被停用，是否继续？', function() {
			doChangeCustTopicStatus(topicId, status, function() {
				layer.close(index);
			});
		});
	} else {
		doChangeCustTopicStatus(topicId, status);
	}
}
//权限不够提示信息
var showMessage = function(){
	layer.msg("您没有操作此专题的权限！");
}
//异步接口重构 GUOQIANG
var doChangeCustTopicStatus = function(topicId, status, onFinish) {
	var index = layer.load('正在提交请求...');
	ajaxCommFun(sys_globePath + "/custtopic/changeStatus.htm", "id=" + topicId + "&status=" + status, function(msg) {
		layer.close(index);

		if (msg.result == "success") {
			$.msg.success("操作成功！", 2, 1);
			if (status == 1) {
//				$(std.find('info-topic-status', topicId)).html('<div class="c_tab_state" onclick="changeCustTopicStatus(' + topicId + ',0);">可用</div>');
//				$(std.find('btn-share-topic', topicId)).show();
				//父专题可用时，子专题全部为可用
				disableCustTopic2(topicId, $(std.find('item-topic', topicId)).attr('level'));
			} else {
				disableCustTopic(topicId, $(std.find('item-topic', topicId)).attr('level'));
			}
		} else {
			$.alert.error(msg.result);
//			$.alert.error("服务器异常，请联系管理员！");
		}

		if (onFinish) {
			onFinish();
		}
	});
}

var disableCustTopic = function(oid, level) {
	$(std.find('info-topic-status', oid)).html('<div class="c_tab_state" style="color: #808080;border-color: #808080;" onclick="changeCustTopicStatus(' + oid + ',1);">不可用</div>');
	$(std.find('btn-delete-topic', oid)).hide();
	$(std.find('btn-share-topic', oid)).hide();
	$(std.find('btn-init-topic', oid)).hide();
	var next = $(std.find('item-topic', oid)).next();
	if ($(next).size() > 0 && $(next).attr('level') > level) {
		disableCustTopic(std.oid(next), level);
	}
}
//父专题可用时，子专题全部为可用
var disableCustTopic2 = function(oid, level) {
	$(std.find('info-topic-status', oid)).html('<div class="c_tab_state" onclick="changeCustTopicStatus(' + oid + ',0);">可用</div>');
	$(std.find('btn-delete-topic', oid)).show();
	$(std.find('btn-share-topic', oid)).show();
	$(std.find('btn-init-topic', oid)).show();
	var next = $(std.find('item-topic', oid)).next();
	if ($(next).size() > 0 && $(next).attr('level') > level) {
		disableCustTopic2(std.oid(next), level);
	}
}


//排序
var indexBlur = function(id, obj) {

	if ($(obj).val() == null || $(obj).val().trim() === "") {
		layer.msg("排序序号不能为空！", 1, 0);
		$(obj).val(0)
		//$(obj)[0].focus();
	} else {
		var fun = function(msg) {
			if (msg.result == "success") {

			} else {
				layer.alert(msg.result);
			}
		}
		ajaxCommFun(sys_globePath + "/custtopic/setIndex.htm", "id=" + id + "&index=" + $(obj).val(), fun);
	}
}
