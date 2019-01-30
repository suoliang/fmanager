//******************** Global Constant ********************//

//******************** Warning Index Function ********************//

var page = page || {};
(function(page) {
	var includeFlag;
	var excludeFlag;

	

	page.initEvent = function() {
		page.tidyKeyword();

		includeFlag = $('#custtopic_keyword').text();
		excludeFlag = $('#custtopic_excludedKeyword').text();

		$('#custtopic_name').blur(function() {
			if (util.isBlank($(this).val())) {
				$(std.findTag('error-name')).show();
			} else {
				$(std.findTag('error-name')).hide();
			}
		});

		$(std.findTag('btn-parse-keyword')).click(function() {
			page.parseKeyword();
		});

		$(std.findTag('btn-save-custtopic')).click(function() {
			page.saveCustTopic();
		});
		
		$('#add-keyword').click(function() {
			page.addKeyword();
		});
		page.delKeyword();
		page.initShowKeyword();
		
	};
	
	page.initShowKeyword = function() {
		$(std.findTag('formatKeyword-line-item')).click(function() {
			std.findTag('formatKeyword-line-item').removeClass("active");
			$(this).addClass("active");
			var formatKeyword = $(this).find("[tag=formatKeyword]").text();
			var Keywords = formatKeyword.split("-");
			var includeKeyword = $.trim(Keywords[0].split("(")[0]);
			var arbitrarilyKeyword = $.trim(Keywords[0].split("(")[1]);
			var excludedKeyword = "";
			if (util.isNotBlank(arbitrarilyKeyword)) {
				arbitrarilyKeyword = arbitrarilyKeyword.replace(')', '').replace(/\|\s/g, ';');
			}else{
				arbitrarilyKeyword = "";
			}
			if (util.isNotBlank(Keywords[1])) {
				excludedKeyword = Keywords[1].replace('(', '').replace(')', '');
				excludedKeyword = excludedKeyword.replace(new RegExp('\\|', "gm"), '');
			}
			$('#custtopic_keyword').text(includeKeyword);
			$('#custtopic_arbitrarilyKeyword').text(arbitrarilyKeyword);
			$('#custtopic_excludedKeyword').text(excludedKeyword);
		});
	};
	
	page.delKeyword = function() {
		$(std.findTag('del-keyword')).click(function() {
			var KeywordLines = std.findTag('formatKeyword-line-item');
			if(KeywordLines.length > 1){
				$($(this).parent()).remove();
			}
		});
	};
	
	page.addKeyword = function() {
		std.findTag('formatKeyword-line-item').removeClass("active");
		$('#custtopic_keyword').text("");
		$('#custtopic_arbitrarilyKeyword').text("");
		$('#custtopic_excludedKeyword').text("");
	};

	page.parseKeyword = function() {//TODO
		page.tidyKeyword();

		if (!page.checkForm()) {
			return;
		}

		includeFlag = $('#custtopic_keyword').text();
		excludeFlag = $('#custtopic_excludedKeyword').text();

		custtopic.parseKeyword({
			keyword : page.generateKeyword()
		}, {
			success : function(html) {
				$(std.findTag('list-custtopic-keyword')).html(html);
				
				var includeKeyword = $.trim($('#custtopic_keyword').text());
				var formatKeywordItem;
				std.findTag('formatKeyword').each(function(e, item) {
					if(includeKeyword==$.trim($(item).text().split("-")[0])){
						formatKeywordItem = item;
					}
				})
				$(formatKeywordItem).parent().parent().addClass("active");
				page.delKeyword();
				page.initShowKeyword();
			}
		});
	};

	page.saveCustTopic = function() {
		page.tidyKeyword();

		if (!page.submitCheckForm()) {
			return;
		}

		var KeywordLines = std.findTag('formatKeyword-line-item');
		if(KeywordLines.length == 0){
			$.msg.warning('请生成采集关键词');
			return;
		}

		page.sync();
		
		custtopic.saveCustTopic($('#custTopicForm').serialize(), {
			success : function() {
				util.go('/custtopic/list.htm');
			}
		});
	};

	page.sync = function() {
		$('input[name=keyword]').val($('#custtopic_keyword').text());
		$('input[name=excludedKeyword]').val($('#custtopic_excludedKeyword').text());
		$('input[name=remark]').val($('#custtopic_remark').text());
	};

	page.generateKeyword = function() {
		var result = '';

		page.tidyKeyword();

		var includeKeyword = $.trim($('#custtopic_keyword').text());
		var arbitrarilyKeyword = page.formatArbitrarilyKeyword($.trim($('#custtopic_arbitrarilyKeyword').text()));
		var excludeKeyword = $.trim($('#custtopic_excludedKeyword').text());
		var selectedLine = std.findTag('formatKeyword-line-item').hasClass("active");
		if(selectedLine){
			$($(".active[tag=formatKeyword-line-item]").find("[tag=custtopic_keyword-item]")).val(includeKeyword);
			$($(".active[tag=formatKeyword-line-item]").find("[tag=custtopic_arbitrarilyKeyword-item]")).val(arbitrarilyKeyword);
			$($(".active[tag=formatKeyword-line-item]").find("[tag=custtopic_excludedKeyword-item]")).val(excludeKeyword);
			
			result = page.gatherKeyword(result);
		}else{
			result = page.gatherKeyword(result) + page.formatKeyword(includeKeyword+"" , "  "+arbitrarilyKeyword , excludeKeyword+"");
		}
		
		return result;
	};
	
	page.gatherKeyword = function(result) {
		$(std.findTag("formatKeyword-line-item")).each(function(i, formatKeywordLine) {
			var includeKeyword = $(this).find("[tag=custtopic_keyword-item]")
			var abitrarilyKeyword = $(this).find("[tag=custtopic_arbitrarilyKeyword-item]")
			var excludedKeyword = $(this).find("[tag=custtopic_excludedKeyword-item]")
			if(includeKeyword) {
				result = result + page.formatKeyword($(includeKeyword).val()+" " ,"  "+$(abitrarilyKeyword).val() , $(excludedKeyword).val()+"");
			}
		});
		return result;
	} 

	page.formatKeyword = function(includeKeyword, abitrarilyKeyword, excludeKeyword) {
		var result = '';
		includeKeyword = includeKeyword.replace(/\n+/g, '');
		includeKeyword = includeKeyword.replace(/\s/g, '\+');
		if(util.isNotBlank(excludeKeyword)){
			excludeKeyword = excludeKeyword.replace(/\n+/g, '');
			excludeKeyword = excludeKeyword.replace(/\s/g, '\|');
			excludeKeyword = excludeKeyword.replace(/\;/g, '\|');
			excludeKeyword = excludeKeyword.replace(/\|+/g, '\|');
		}
		if(util.isNotBlank(includeKeyword)){
			var keywords = includeKeyword.split('\;');
			$(keywords).each(function(i, keyword) {
				if (util.isNotBlank(keyword)) {
					result += keyword + abitrarilyKeyword;
					if (util.isNotBlank(excludeKeyword)) {
						result += "-(" + excludeKeyword + ")";
					}
					result += ";";	
				}
			});
		}else if(util.isBlank(includeKeyword) && util.isNotBlank(abitrarilyKeyword)){
			result = abitrarilyKeyword;
			if (util.isNotBlank(excludeKeyword)) {
				result += " -(" + excludeKeyword + ")";
			}
		}else if(util.isBlank(includeKeyword) && util.isBlank(abitrarilyKeyword)){
			result = "-(" + excludeKeyword + ")";
		}
		
		return result;
	};
	
	page.tidyKeyword = function() {
		var includeKeyword = page.trimKeyword($('#custtopic_keyword').text());
		var excludeKeyword = page.trimKeyword($('#custtopic_excludedKeyword').text());
		
		includeKeyword = includeKeyword.replace(/\;/g, '\;<br/>');
		excludeKeyword = excludeKeyword.replace(/\;/g, '\;<br/>');

		$('#custtopic_keyword').html(includeKeyword);
		$('#custtopic_excludedKeyword').html(excludeKeyword);
		

	};
	
	page.trimKeyword = function(str) {
		if (str != '') {
			str = str.replace(new RegExp('；', "gm"), ';');
			str = str.replace(/\s+/g, ' ');
			str = str.replace(/\s\;/g, '\;');
			str = str.replace(/\;\s/g, '\;');
			str = str.replace(/\;+/g, '\;');
		}
		return str;
	};
	
	page.formatArbitrarilyKeyword = function(str) {
		if (str != '') {
			str = page.trimKeyword(str);
			if(str.match(/^.*\;$/)){//匹配最后一个字符“;”
				str=str.substring(0,str.length-1)
			}
			str = "(" + str.replace(/\;/g, '|') + ")";
		}
		return str;
	};
	
	
	
	page.checkForm = function() {
		if (util.isBlank($('#custtopic_name').val())) {
			$(std.findTag('error-name')).show();
			$('#custtopic_name').focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\\;；.\s\u4e00-\u9fa5]{0,}$/.test($('#custtopic_name').val())) {
			$.msg.warning('专题名称只能是中文,字母,数字,下划线,空格,分号,小数点', 2);
			$('#custtopic_name').focus();
			return false;
		} else if (util.isBlank($.trim($("#custtopic_keyword").text())) && util.isBlank($.trim($("#custtopic_arbitrarilyKeyword").text()))) {
			$.msg.warning('包含关键词或包含任意关键词至少填一项');
			$("#custtopic_keyword").focus();
			return false;
		} else if ($("#custtopic_arbitrarilyKeyword").text().length > $("#custtopic_arbitrarilyKeyword").attr("maxlength")) {
			$.msg.warning("包含任意关键词"+$("#custtopic_arbitrarilyKeyword").attr("maxlength")+"个字范围内", 2);
			$("#custtopic_arbitrarilyKeyword").focus();
			return false;
		} else if ($("#custtopic_excludedKeyword").text().length > $("#custtopic_excludedKeyword").attr("maxlength")) {
			$.msg.warning("不包含关键词在"+$("#custtopic_excludedKeyword").attr("maxlength")+"个字范围内", 2);
			$("#custtopic_excludedKeyword").focus();
			return false;
		} else if ($("#custtopic_excludedKeyword").text().length > $("#custtopic_excludedKeyword").attr("maxlength")) {
			$.msg.warning("不包含关键词在"+$("#custtopic_excludedKeyword").attr("maxlength")+"个字范围内", 2);
			$("#custtopic_excludedKeyword").focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\\;；.\s\u4e00-\u9fa5]{0,}$/.test($("#custtopic_keyword").text())) {
			$.msg.warning('关键字只能是中文,字母,数字,下划线,空格,分号,小数点', 2);
			$("#custtopic_keyword").focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\\;；.\s\u4e00-\u9fa5]{0,}$/.test($("#custtopic_arbitrarilyKeyword").text())) {
			$.msg.warning('包含任意关键词只能是中文,字母,数字,下划线,空格,分号,小数点', 2);
			$("#custtopic_arbitrarilyKeyword").focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\s\u4e00-\u9fa5]{0,}$/.test($("#custtopic_excludedKeyword").text())) {
			$.msg.warning('不包含关键字只能是中文,字母,数字,下划线,空格', 2);
			$("#custtopic_excludedKeyword").focus();
			return false;
		} else{
			var jQexcludedKeyword = $("#custtopic_excludedKeyword");
			var excludedKeywords = $.trim(jQexcludedKeyword.text());
			if($.trim(excludedKeywords) != ""){
				var excludedKeywordArr = excludedKeywords.split(" ");
				var custtopic_keywords = $("#custtopic_keyword").text();
				for(var i = 0; i < excludedKeywordArr.length; i ++){
					if(custtopic_keywords.indexOf(excludedKeywordArr[i]) != -1){
						$.msg.warning('包含关键字和不包含关键字不能有相同的词！', 2);
						jQexcludedKeyword.focus();
						return false;
					}
				}
			}
			
			var excludedKeywords = $.trim(jQexcludedKeyword.text());
			if($.trim(excludedKeywords) != ""){
				var excludedKeywordArr = excludedKeywords.split(" ");
				var custtopic_arbitrarilyKeywords = $("#custtopic_arbitrarilyKeyword").text();
				for(var i = 0; i < excludedKeywordArr.length; i ++){
					if(custtopic_arbitrarilyKeywords.indexOf(excludedKeywordArr[i]) != -1){
						$.msg.warning('包含任意关键词和不包含关键字不能有相同的词！', 2);
						jQexcludedKeyword.focus();
						return false;
					}
				}
			}
		}
		
		if($("#custtopic_remark").text().length > $("#custtopic_remark").attr("maxlength")){
			$.msg.warning("专题描述在"+$("#custtopic_remark").attr("maxlength")+"个字范围内", 2);
			return false;
		}
		
		var includeKeywordInputs = $.trim($('#custtopic_keyword').text()).split(";");
		var keywordflag = false;
		$(includeKeywordInputs).each(function(i , includeKeywordInput){
			std.findTag("formatKeyword-line-item").each(function(){
				if(!$(this).hasClass("active")){
					var formatKeyword = $.trim($(this).find("[tag=custtopic_keyword-item]").val());
					if(includeKeywordInput == formatKeyword && includeKeywordInput != ''){
						keywordflag = true;
					}
				}
			})
		})
		if(keywordflag){
			$.msg.warning("包含关键词已存在", 2);
			$("#custtopic_keyword").focus();
			return false;
		}
		  
		var arbitrarilyKeywordInput = $.trim($('#custtopic_arbitrarilyKeyword').text());
		if (util.isNotBlank(arbitrarilyKeywordInput)) {
			arbitrarilyKeywordInput = arbitrarilyKeywordInput.replace(/\；/g, '\;');
		}
		var arbitrarilyKeywordInputs = arbitrarilyKeywordInput.split(";");
		
		var arbitrarilykeywordflag = false;
		var arbitrarilyKeywords;
		$(arbitrarilyKeywordInputs).each(function(i , arbitrarilyKeywordInput){
			std.findTag("formatKeyword-line-item").each(function(i , lineItem){
				var arbitrarilyKeyword = $.trim($(lineItem).find("[tag=custtopic_arbitrarilyKeyword-item]").val());
				var formatKeyword = $.trim($(lineItem).find("[tag=custtopic_keyword-item]").val());
				if (util.isNotBlank(arbitrarilyKeyword) && util.isBlank(formatKeyword) && util.isBlank($('#custtopic_keyword').text()) && util.isNotBlank(arbitrarilyKeywordInput)) {
					console.log("arbitrarilyKeyword==="+arbitrarilyKeyword);
					console.log("formatKeyword==="+formatKeyword);
					arbitrarilyKeywords = arbitrarilyKeyword.replace('(', '').replace(')', '').split("|");
					$(arbitrarilyKeywords).each(function(i , arbitrarilyKeywordItem){
						if($.trim(arbitrarilyKeywordInput) == $.trim(arbitrarilyKeywordItem) && arbitrarilyKeywordInput != ''){
							arbitrarilykeywordflag = true;
						}
					})
				}
			})
		})
		if(arbitrarilykeywordflag){
			$.msg.warning("包含任意关键词已存在", 2);
			$("#custtopic_arbitrarilyKeyword").focus();
			return false;
		}
		
		return true;
	};
	
	
	page.submitCheckForm = function() {
		if (util.isBlank($('#custtopic_name').val())) {
			$(std.findTag('error-name')).show();
			$('#custtopic_name').focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\\;；.\s\u4e00-\u9fa5]{0,}$/.test($('#custtopic_name').val())) {
			$.msg.warning('专题名称只能是中文,字母,数字,下划线,空格,分号,小数点', 2);
			$('#custtopic_name').focus();
			return false;
		} 
		
		return true;
	};
	

})(page);

//******************** INIT Function ********************//
function elementRemove(obj){
	$(obj).remove();
}
$(function() {
	page.initEvent();
	
	var topicFlag=true;
	//专题检索
	$(std.find('popup-box', 'topic')).optionMagr({
		multiple : false,
		listSubOption : function(id) {
			var options = [];
			custtopic.listTopic(id, {
				success : function(topics,a) {
					topicFlag=a=='当前用户存在顶级专题' ? false : true; 
					
					options = $.map(topics, function(topic) {
						if (topic.createrId != sys.getLoginUserId()) {
							return;
						}
						if (topic.id == $('input[name=id]').val()) {
							return;
						}
						return {
							id : topic.id,
							name : topic.name
						};
					});
				}
			});
			return options;
		}
	});

	$('.topic-selector').click(function() {
		var topicbox = std.selector('popup-box', 'topic');
		$(topicbox).optionMagr('initValue', []);
		$(topicbox).optionMagr('showSubOption', 0);
		if(topicFlag){
			$.msg.warning('您当前的帐号未创建顶级专题', 2);  
		}else{
			$.box(topicbox, {
				onOpen : function() {
					if($('input[name=parentId]').val() != 0){
						$(".value-container").append("<li name='"+$("#custtopic_parentName").text()+"' oid='"+$('input[name=parentId]').val()+"' tag='value' onclick='elementRemove(this);'>"+$("#custtopic_parentName").text()+"<i class='c_sprite'></i></li>");
					}
				}
			}, {
				submit : {
					dom : [ topicbox + ' .box-submit' ],
					fun : function(index) {
						outer = this;
						var obbj = $(topicbox).optionMagr('submit');
						if (obbj != null && obbj.length > 0) {
							$('input[name=parentId]').val(obbj[0].id);
							$('#custtopic_parentName').html(obbj[0].name);
						}else{
							$('input[name=parentId]').val(0);
							$('#custtopic_parentName').html("顶级专题");
						}
					}
				},
				close : {
					dom : [ topicbox + ' .box-close', topicbox + ' .box-cancel' ]
				}
			});
		}
	});
});