//共享用户 sharefun2068 2014-12-25
var shareUser = function(userIds, shareFun) {
	$("#targetChoosePopupKey").val("内容搜索");
	$.box("#targetChooseDiv", {}, {});
	// 查询所有的第一级组织
	//queryOrgListByOrgId(1, 1);

	// 查询所有的成员
	queryMemberListByOrgId(null, null, null, userIds);

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

		userIdsUPD = userIdsUPD.substring(0, userIdsUPD.length - 1);
		if (util.isNotNull(shareFun)) {
			shareFun(userIdsUPD);
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

function queryMemberListByOrgId(orgId, orgName, flag, userids) {
	var json = {
		orgId : orgId,
		orgName : orgName,
		searchKey : ''
	};
	if (flag != 'false') {
		if ($("#targetChoosePopupKey").val().trim() != '' && $("#targetChoosePopupKey").val().trim() != '内容搜索') {
			json.searchKey = $("#targetChoosePopupKey").val().trim();
		}
	}
	$.ajax({
		url : std.u("/custtopic/queryUserListByOrgId.htm"),
		data : json,
		dataType : "text",
		type : "post",
		success : function(data) {
			json.searchKey = '';
			$("#memberTdList").nextAll().remove();
			$("#memberTdList").after(data);
			if (userids != null && userids != "") {
				//alert(userids);
				userids = userids.substring(0, userids.length - 1);
				var selectedhtml = "";
				for (var k = 0; k < userids.split(",").length; k++) {
					selectedhtml = selectedhtml + '<div class="c_org_msg c_cb c_pl20" onclick="removeThis(this)" memberid="' + userids.split(",")[k] + '"  >' + $("#user_" + userids.split(",")[k]).attr("varname") + '</div>';
					$("#user_" + userids.split(",")[k]).addClass("c_none");
				}
			}
			$("#targetList").html(selectedhtml);

			$("div[class=c_pl20]").each(function() {
				var _this = $(this);
				_this.unbind();
				_this.click(function() {
					$("div[vartype=member]").each(function() {
						if ($(this).attr("var") == _this.attr("memberid")) {
							$(this).removeClass("c_none");
						}
					});
					$(this).remove();
				})
			});

			$('#scrollbar_002').tinyscrollbar();
		}
	});
}