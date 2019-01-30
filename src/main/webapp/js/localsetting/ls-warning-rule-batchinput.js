//******************** Global Constant ********************//

var selector = {
	ruleContainer : '#warning-rule-input-list'
};

var tag = {
	backToRuleBtn : 'btn-back-to-rule',
	submitRuleBtn : 'btn-submit-rule'
}

//******************** Warning Index Function ********************//

var page = page || {};
(function(page) {
	page.rowNo = 0;
	page.addRuleRow = function() {
		page.rowNo = page.rowNo + 1;
		var html = '' + //
		'<ul class="c_tab2 c_tab_top">' + //
		'	<li class="c_tab_add1"><div>' + //
		'			<a href="javascript:void(0);" class="c_tab_time c_w80 c_mt5 c_bc  c_tc add-rule-row">向下新增</a>' + //
		'		</div></li>' + //
		'	<li class="c_tab_add2"><div class="c_mr15">' + //
		'			<input type="text" class="input c_w" tag="rulename" name="rules[' + page.rowNo + '].name">' + //
		'		</div></li>' + //
		'	<li class="c_tab_add3"><div class="c_mr15">' + //
		'			<input type="text" class="input c_w" tag="keyword" name="rules[' + page.rowNo + '].keyword">' + //
		'		</div></li>' + //
		'	<li class="c_tab_add4"><div>' + //
		'			<a href="javascript:void(0);" class="c_mr10 c_amore remove-rule-row">删除行</a>' + //
		'		</div></li>' + //
		'</ul>';
		html = $(html);

		$(html).find('.add-rule-row').click(function() {
			$(this).hide();
			page.addRuleRow();
		});
		$(html).find('.remove-rule-row').click(function() {
			$(this).parent().parent().parent().remove();
			$('.add-rule-row').last().show();
		});

		$(selector.ruleContainer).append(html);
	}
})(page);

//******************** INIT Function ********************//

$(function() {
	function replaceAllSpaceAndquanjiao(str) {
		if (str != '') {
			str = str.replace(/\s+/g, ' ');
			str = str.replace(new RegExp(' ', "gm"), ';');
			str = str.replace(new RegExp('；', "gm"), ';');
		}
		return str;
	}

	$(std.findTag(tag.submitRuleBtn)).click(function() {
		var valid = true;
		var validKeyword = true;
		$('input[tag=rulename]').each(function(i, item) {
			if (util.isBlank($(item).val())) {
				valid = false;
			}
		});
		$('input[tag=keyword]').each(function(i, item) {
			if (util.isBlank($(item).val())) {
				valid = false;
			}
			if (!/^[a-zA-Z0-9_\\;；\s\u4e00-\u9fa5]{0,}$/.test($(item).val())) {
				validKeyword = false;
			}
		});
		if (!valid) {
			$.msg.warning('参数不能为空');
			return;
		}
		if (!validKeyword) {
			$.msg.warning('关键字格式不对');
			return;
		}
		$('input[tag=keyword]').each(function() {
			$(this).val(replaceAllSpaceAndquanjiao($(this).val()));
		});
		var params = $('#rule-form').serialize();
		warning.batchSaveRule(params, {
			success : function() {
				util.go('/setting/warning/index.htm');
			}
		});
	});

	$(std.findTag(tag.backToRuleBtn)).click(function() {
		util.post(std.u('/setting/warning/index.htm'), {
			pageNo : std.oid(this)
		});
	});

	$('.add-rule-row').click(function() {
		$(this).hide();
		page.addRuleRow();
	});
});