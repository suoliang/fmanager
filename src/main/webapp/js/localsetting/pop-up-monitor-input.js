/**
 * @author LIUJUNWU
 */
var popup = popup || {};
(function() {
	var monitorInput = {};
	var monitorInputBox = "[tag=popup-box][oid=monitor-input]";

	popup.openMonitorKeywordInput = function(monitorKeyword, onSubmit) {
		$.box(monitorInputBox, {
			onOpen : function() {
				$("[name='keyword']").val("");
				$("[name='arbitrarilyKeyword']").val("");
				$("[name='excludedKeyword']").val("");
				$("[name='kid']").val("");
				if(monitorKeyword != null){
					var keyword = $.trim(monitorKeyword.word.split("-")[0].split("(")[0]);
					var arbitrarilyKeyword = keywordShowFormat($.trim(monitorKeyword.word.split("-")[0].split("(")[1]));
					var excludedKeyword = keywordShowFormat($.trim(monitorKeyword.word.split("-")[1]));
					
					$("[name='keyword']").val(keyword);
					$("[name='arbitrarilyKeyword']").val(arbitrarilyKeyword);
					$("[name='excludedKeyword']").val(excludedKeyword);
					$("[name='kid']").val(monitorKeyword.kid);
				}
			}
		}, {
			submit : {
				close : false,
				dom : [ monitorInputBox + " .box-submit" ],
				fun : function(index) {
					if (!monitorInput.checkForm()) {
						return;
					}
//					if (!monitorInput.checkMonitorKeywordIsExist(monitorInput.submit()) && monitorKeyword == null) {//新增时检查是否重复
					if (!monitorInput.checkMonitorKeywordIsExist(monitorInput.submit())) {//新增时检查是否重复
						return;
					}
					if (onSubmit) {
						onSubmit(monitorInput.submit());
					}
					this.close(index);
				}
			},
			close : {
				dom : [ monitorInputBox + " .box-close", monitorInputBox + " .box-cancel" ]
			}
		});
	};
	
	monitorInput.checkMonitorKeywordIsExist = function(monitorKeyword) {
		var result;
		ajaxCommFun(std.u('/setting/monitor/keyword/checkMonitorKeywordIsExist.htm'), monitorKeyword, function(response) {
			if (response.type == dict.action.suc) {
				result = true;
			} else if(response.type == dict.action.question){
				$.msg.warning(response.message, 3);
				result = false;
			} else {
				$.msg.warning("检查监测关键词是否存在失败", 3);
				result = false;
			}
		}, true);
		return result;
	}
	
	monitorInput.checkForm = function() {
		var keyword = $("#keyword");
		var arbitrarilyKeyword = $("#arbitrarilyKeyword");
		var excludedKeyword = $("#excludedKeyword");
		keyword.val(replaceAllFormatCharacter(keyword.val()));
		arbitrarilyKeyword.val(replaceAllFormatCharacter(arbitrarilyKeyword.val()));
		excludedKeyword.val(replaceAllFormatCharacter(excludedKeyword.val()));
		if (util.isBlank($.trim(keyword.val())) && util.isBlank($.trim(arbitrarilyKeyword.val()))) {
			$.msg.warning('包含关键词或包含任意关键词至少填一项!',3);
			keyword.focus();
			return false;
		} else if (util.isNotBlank($.trim(keyword.val())) && 2 > keyword.val().length) {
			$.msg.warning("包含关键词长度最少2个字符", 3);
			keyword.focus();
			return false;
		} else if (util.isNotBlank($.trim(arbitrarilyKeyword.val())) && 2 > arbitrarilyKeyword.val().length) {
			$.msg.warning("包含任意关键词长度最少2个字符", 3);
			arbitrarilyKeyword.focus();
			return false;
		} else if (/[;[()|-]/.test(keyword.val()) && util.isNotBlank($.trim(keyword.val()))) {
			$.msg.warning('包含关键字不支持“(”、 “)”、 “|”、“-”、“;”符号', 3);
			keyword.focus();
			return false;
		} else if (/[()|-]/.test(arbitrarilyKeyword.val()) && util.isNotBlank($.trim(arbitrarilyKeyword.val()))) {
			$.msg.warning('包含任意关键词不支持“(”、 “)”、 “|”、“-”符号', 3);
			arbitrarilyKeyword.focus();
			return false;
		} else if (/[()|-]/.test(excludedKeyword.val()) && util.isNotBlank($.trim(excludedKeyword.val()))) {
			$.msg.warning('不包含关键词不支持“(”、 “)”、 “|”、“-”符号', 3);
			excludedKeyword.focus();
			return false;
		} else if (util.isNotBlank($.trim(keyword.val())) && /^[0-9]*$/.test(keyword.val()) || util.isNotBlank($.trim(arbitrarilyKeyword.val())) && /^[0-9]*$/.test(arbitrarilyKeyword.val()) || util.isNotBlank($.trim(excludedKeyword.val())) && /^[0-9]*$/.test(excludedKeyword.val())) {
			$.msg.warning('不支持纯数字格式的关键词', 3);
			return false;
		} else{
			if(util.isNotBlank($.trim(excludedKeyword.val())) && util.isNotBlank($.trim(keyword.val()))){
				var excludedKeywordArr = excludedKeyword.val().split(";");
				for(var i = 0; i < excludedKeywordArr.length; i ++){
					if(keyword.val().indexOf(excludedKeywordArr[i]) != -1){
						$.msg.warning('包含关键字和不包含关键字不能有相同的词！', 3);
						keyword.focus();
						return false;
					}
				}
			}
			
			if(util.isNotBlank($.trim(excludedKeyword.val())) && util.isNotBlank($.trim(arbitrarilyKeyword.val()))){
				var excludedKeywordArr = excludedKeyword.val().split(";");
				for(var i = 0; i < excludedKeywordArr.length; i ++){
					if(arbitrarilyKeyword.val().indexOf(excludedKeywordArr[i]) != -1){
						$.msg.warning('包含任意关键词和不包含关键字不能有相同的词！', 3);
						arbitrarilyKeyword.focus();
						return false;
					}
				}
			}
		}
		return true;
	};

	monitorInput.submit = function() {
		var paramArr = $("form").serializeArray();
		var paramJson = {};
		for(var i = 0;i < paramArr.length; i ++){
			var jsonObj = paramArr[i];
			var key = jsonObj.name;
			paramJson[key] = jsonObj.value;
		}
		paramJson["keyword"] = replaceAllFormatCharacter(paramJson["keyword"]);
		paramJson["arbitrarilyKeyword"] = replaceAllFormatCharacter(paramJson["arbitrarilyKeyword"]);
		paramJson["excludedKeyword"] = replaceAllFormatCharacter(paramJson["excludedKeyword"]);
		paramJson["word"] = monitorInput.formatKeyword(paramJson["keyword"], paramJson["arbitrarilyKeyword"], paramJson["excludedKeyword"]);
		return paramJson;
	};

	monitorInput.formatKeyword = function(includeKeyword, abitrarilyKeyword, excludeKeyword) {
		var result = '';
		if(util.isNotBlank(includeKeyword)){
			includeKeyword = includeKeyword.trim().replace(/\n+/g, '');
			result += includeKeyword;
		}
		if(util.isNotBlank(abitrarilyKeyword)){
			abitrarilyKeyword = abitrarilyKeyword.replace(/\n+/g, '');
			abitrarilyKeyword = abitrarilyKeyword.replace(/\;/g, '\ | ');//词与词之间用“空格”“|”“空格”格式隔开
			abitrarilyKeyword = abitrarilyKeyword.replace(/\|+/g, '\ | ');//词与词之间用“空格”“|”“空格”格式隔开
			result += " (" + abitrarilyKeyword + ")";
		}
		if(util.isNotBlank(excludeKeyword)){
			excludeKeyword = excludeKeyword.replace(/\n+/g, '');
			excludeKeyword = excludeKeyword.replace(/\;/g, '\ | ');
			excludeKeyword = excludeKeyword.replace(/\|+/g, '\ | '); 	
			result += " -(" + excludeKeyword + ")";
		}
		return result;
	};
	
	function replaceAllFormatCharacter(str) {
		if (str != '') {
			str = str.replace(new RegExp('；', "gm"), ';');
			str = str.replace(/\s+/g, ' ');
			str = str.replace(/\s\;/g, '\;');
			str = str.replace(/\;\s/g, '\;');
			str = str.replace(/\;+/g, '\;');
			
			str = str.replace(new RegExp('（', "gm"), '(').replace(new RegExp('）', "gm"), ')');
		}
		return str.trim();
	}
	function keywordShowFormat(str) {
		if (str != '') {
			str = str.replace(new RegExp('；', "gm"), ';');
			str = str.replace(/\s+/g, ' ');
//			str = str.replace(/\ | /g, ' ');
			str = str.replace(/\s\;/g, '\;');
			str = str.replace(/\;\s/g, '\;');
			str = str.replace(/\;+/g, '\;');
			
			str = str.replace(new RegExp('（', "gm"), '(').replace(/\(\s/g, '').replace(/\(/g, '');
			str = str.replace(new RegExp('）', "gm"), ')').replace(/\)\s/g, '').replace(/\)/g, '');
			str = str.replace(new RegExp(' [|] '), ';');//匹配“空格”“|”“空格”
			str = str.replace(/\-\s/g, '').replace(/\-/g, '');
		}
		return str.trim();
	}
	
})();
