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

	};

	page.parseKeyword = function() {
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
			}
		});
	};

	page.saveCustTopic = function() {
		page.tidyKeyword();

		if (!page.checkForm()) {
			return;
		}

		if (includeFlag != $('#custtopic_keyword').text() || excludeFlag != $('#custtopic_excludedKeyword').text()) {
			$.msg.warning('请生成采集关键词');
			return;
		}

//		if ($(std.findTag('status-keyword-disable')).size() > 0) {
//			$.msg.warning('有未通过的采集关键词', 2);
//			$("#custtopic_keyword").focus();
//			return;
//		}

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

		var includeKeyword = $('#custtopic_keyword').text();
		var excludeKeyword = $('#custtopic_excludedKeyword').text();

		includeKeyword = includeKeyword.replace(/\n+/g, '');
		includeKeyword = includeKeyword.replace(/\s/g, '\+');

		excludeKeyword = excludeKeyword.replace(/\n+/g, '');
		excludeKeyword = excludeKeyword.replace(/\s/g, '\|');
		excludeKeyword = excludeKeyword.replace(/\;/g, '\|');
		excludeKeyword = excludeKeyword.replace(/\|+/g, '\|');

		var keywords = includeKeyword.split('\;');
		$(keywords).each(function(i, keyword) {
			if (util.isNotBlank(keyword)) {
				result += keyword;
				if (util.isNotBlank(excludeKeyword)) {
					result += "-(" + excludeKeyword + ")";
				}
				result += ";";
			}
		});

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
			str = str.replace(new RegExp('（', "gm"), '(');
			str = str.replace(new RegExp('）', "gm"), ')');
			
			str = str.replace(/\++/g, '+');
			str = str.replace(/\+\;/g, '\;');
			str = str.replace(/\;\+/g, '\;');
			
			str = str.replace(/\-+/g, '-');
			str = str.replace(/\-\;/g, '\;');
			str = str.replace(/\;\-/g, '\;');
			
			str = str.replace(/\;+/g, '\;');
			
			if(str.startsWith('+') || str.startsWith('-')){
				str=str.substring(1);
			}
			if(str.endsWith('+') || str.endsWith('-')) {
				str=str.substring(0 ,str.length -1)
			}
		}
		return str;
	};

	page.checkForm = function() {
		if (util.isBlank($('#custtopic_name').val())) {
			$(std.findTag('error-name')).show();
			$('#custtopic_name').focus();
			return false;
		} else if (util.isBlank($.trim($("#custtopic_keyword").text()))) {
			$.msg.warning('包含关键词必填');
			$("#custtopic_keyword").focus();
			return false;
		} else if ($("#custtopic_keyword").text().length > 400) {
			$.msg.warning('包含关键词在400个字范围内', 2);
			$("#custtopic_keyword").focus();
			return false;
		} else if ($("#custtopic_excludedKeyword").text().length > 400) {
			$.msg.warning('不包含关键词在400个字范围内', 2);
			$("#custtopic_excludedKeyword").focus();
			return false;
		} else if (!/^[a-zA-Z0-9_\\;；\+\-\(\)\|\（）\u4e00-\u9fa5]{0,}$/.test($("#custtopic_keyword").text())) {
			$.msg.warning('关键字只能是中文,字母,数字,下划线,分号,加号,减号,括号,竖线', 2);
			$("#custtopic_keyword").focus();
			return false;
		}
		return true;
	};

})(page);

//******************** INIT Function ********************//

$(function() {
	page.initEvent();

	//专题检索
	$(std.find('popup-box', 'topic')).optionMagr({
		multiple : false,
		listSubOption : function(id) {
			var options = [];
			search.listTopic(id, {
				success : function(topics) {
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
						}
					})
				}
			});
			return options;
		}
	});

	$('.topic-selector').click(function() {
		var topicbox = std.selector('popup-box', 'topic');

		$.box(topicbox, {
			onOpen : function() {
				$(topicbox).optionMagr('initValue', []);
				$(topicbox).optionMagr('showSubOption', 0);
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
					}
				}
			},
			close : {
				dom : [ topicbox + ' .box-close', topicbox + ' .box-cancel' ]
			}
		});
	});
});