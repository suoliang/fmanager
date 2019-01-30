/**
 * @author GUQIANG
 */
var popup = popup || {};
(function() {
	var warningRuleInput = {};
	var warningRuleInputBox = "[tag=popup-box][oid=warning-rule-input]";

	popup.openWarningRuleInput = function(warningRule, onSubmit) {
		$.box(warningRuleInputBox, {
			onOpen : function() {
				$("[name='name']").val("");
				$("[name='keyword']").val("");
				$("[name='excludedKeyword']").val("");
				$("[name='remark']").val("");
				$("[tag='option-hook']").removeClass("active");
				$("[name='id']").val("");
				$(std.findTag('error-name')).hide();
				if(warningRule != null){
					$("[name='name']").val(warningRule.name);
					$("[name='keyword']").val(warningRule.keyword);
					$("[name='excludedKeyword']").val(warningRule.excludedKeyword);
					$("[name='remark']").val(warningRule.remark);
					$("[name='id']").val(warningRule.id);
					var mediaTypes = warningRule.mediaType;
					if(mediaTypes){
						for(var i = 0; i < mediaTypes.length; i ++){
							$("[tag='option-hook'][oid='"+mediaTypes[i]+"']").addClass("active");
						}
					}
				}
			}
		}, {
			submit : {
				close : false,
				dom : [ warningRuleInputBox + " .box-submit" ],
				fun : function(index) {
					if (!warningRuleInput.checkForm()) {
						return;
					}
					if (onSubmit) {
						onSubmit(warningRuleInput.submit());
					}
					this.close(index);
				}
			},
			close : {
				dom : [ warningRuleInputBox + " .box-close", warningRuleInputBox + " .box-cancel" ]
			}
		});
	};

	//初始化页面
	warningRuleInput.init = function(warningRule) {
		$(std.findTag('error-warningRuleName')).hide();

		if (util.isNotNull(warningRule)) {
			$(warningRuleInputBox).find('input[name=warningRuleId]').val(warningRule.id);
			$(warningRuleInputBox).find('input[name=warningRuleName]').val(warningRule.name);
			$(warningRuleInputBox).find('textarea[name=warningRuleKeyword]').val(util.joinCollection(warningRule.keywords, " ", "word"));
		}

		$(warningRuleInputBox).find('input[name=warningRuleName]').unbind().keyup(function() {
			if (util.isNotBlank($(this).val())) {
				$(std.findTag('error-warningRuleName')).hide();
			} else {
				$(std.findTag('error-warningRuleName')).show();
			}
		});
	};

	warningRuleInput.checkForm = function() {
		var name = $(warningRuleInputBox).find("input[name='name']").val();
		var includeKeywords = $(warningRuleInputBox).find("[name='keyword']").val();
		var exclusiveKeywords = $("[name='excludedKeyword']").val();
		var reg = /^[a-zA-Z0-9_\\;；\s\u4e00-\u9fa5]{0,}$/;
		if (util.isBlank(name)) {
			$(std.findTag('error-name')).show();
			return false;
		}
		if (util.isBlank(includeKeywords)) {
			$.msg.warning("预警关键字不能为空");
			return false;
		} else if (!reg.test(includeKeywords)) {
			$.msg.warning('关键字只能是中文字母数字');
			$("[name='keyword']").focus();
			return false;
		}
		if(!reg.test(exclusiveKeywords)){
			$.msg.warning('不包含关键字只能是中文字母数字');
			$("[name='excludedKeyword']").focus();
			return false;
		}
		replaceAllSpaceAndquanjiao(includeKeywords);
		replaceAllSpaceAndquanjiao(exclusiveKeywords);
		if(exclusiveKeywords.length > 0){
			var exclusiveKeywordArr = exclusiveKeywords.split(" ");
			for(var i = 0; i < exclusiveKeywordArr.length; i ++){
				if(exclusiveKeywordArr[i]!=""){
					if(includeKeywords.indexOf(exclusiveKeywordArr[i]) != -1){
						$.msg.warning('关键字，不包含关键字不能重复');
						return false;
					}
				}
			}
		}
		return true;
	};

	warningRuleInput.submit = function() {
		var paramArr = $("form").serializeArray();
		var paramJson = {};
		for(var i = 0;i < paramArr.length; i ++){
			var jsonObj = paramArr[i];
			var key = jsonObj.name;
			paramJson[key] = jsonObj.value;
		}
		var option = $("[tag='option-hook'].active");
		var optionIds = [];
		for(var i = 0; i < option.length; i ++){
			optionIds.push($(option[i]).attr("oid"));
		}
		paramJson["mediaType"] = optionIds;
		
		paramJson["keyword"] = replaceAllSpaceAndquanjiao(paramJson["keyword"]);
		paramJson["excludedKeyword"] = replaceAllSpaceAndquanjiao(paramJson["excludedKeyword"]);
		return paramJson;
//		var form = {};
//		form.id = id;
//		form.name = $.trim(name);
//		form.keyword = replaceAllSpaceAndquanjiao($.trim(keyword));
//		return form;
	};

	function replaceAllSpaceAndquanjiao(str) {
		if (str != '') {
			str = str.replace(/\s+/g, ' ');
//			str = str.replace(new RegExp(' ', "gm"), ';');
			str = str.replace(new RegExp('；', "gm"), ';');
		}
		return str.trim();
	}
	
})();
function changeState(obj){
	if($(obj).attr("tag") != "option-hook"){
		$(obj).find("[tag='option-hook']").toggleClass("active");
	}else{
		$(obj).toggleClass("active");
	}
}