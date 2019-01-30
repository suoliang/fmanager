/**
 * @author GUQIANG
 */
var popup = popup || {};
(function() {
	var briefPicker = {};
	var briefPickerBox = "[tag=popup-box][oid=brief-picker]";

	var tag = {
		searchBriefBtn : 'brief-picker-btn-search-brief',
		briefList : 'brief-picker-brief-list',
		briefItem : 'brief-picker-brief-item',
		briefItemCheckbox : 'brief-picker-brief-item-checkbox',
		briefItemTitle : 'brief-picker-brief-item-title',
		briefNmaeform : 'briefNmae-form'
	};

	var input = {
//		id : 'input[name=brief-picker-id]',
		title : 'input[name=brief-picker-title]'
	};

	var refreshScrollbar = function() {
		$(briefPickerBox).find('.brief-picker-scrollbar').tinyscrollbar();
	};
	
	$(std.findTag("btn-add-brief")).click(function() {
		popup.saveBrief(null, null);
	});
	
	popup.saveBrief = function(id, title) {
		popup.openBriefInput(id, title, function(form) {/**encodeURIComponent转义*/
			report.saveBrief('reportId=' + form.id + '&title=' + encodeURIComponent(form.title), {
				success : function(data, message) {
					$.msg.success(message);
					briefPicker.searchBrief('');
				}
			});
		});
	};

	popup.openBriefPicker = function(guid, onSubmit) {
		$.box(briefPickerBox, {
			onOpen : function() {
				briefPicker.init(guid);
			}
		}, {
			submit : {
				close : false,
				dom : [ briefPickerBox + " .box-submit" ],
				fun : function(index) {
					outer = this;

					briefPicker.submit(guid, function() {

						outer.close(index);

						if (onSubmit) {
							onSubmit();
						}
					});
				}
			},
			close : {
				dom : [ briefPickerBox + " .box-close", briefPickerBox + " .box-cancel" ]
			}
		});
	};

	briefPicker.init = function(guid) {
		briefPicker.searchBrief('');

		$(briefPickerBox).find(std.findTag(tag.searchBriefBtn)).unbind().click(function() {
			var keyword = $(briefPickerBox).find(input.title).val();
			briefPicker.searchBrief(keyword);
		});

		$(briefPickerBox).find(input.title).unbind().keydown(function(e) {
			if (e && e.keyCode == 13) {
				$(briefPickerBox).find(std.findTag(tag.searchBriefBtn)).click();
			}
			e.stopPropagation();
		});
	};
	briefPicker.initEvent = function() {
		$(briefPickerBox).find(std.findTag(tag.briefItem)).click(function() {
			briefPicker.clickBrief(this);
		});
	};
	
	briefPicker.clickBrief = function(elmt) {
		var formCtx = $('#briefNmae-form');
		formCtx.empty();
		//TODO   改成多选框
		var briefNmae = $(std.find('brief-picker-brief-item', std.oid(elmt), '[scope=option]'));
		if ($(briefNmae).attr('selct') == 'true') {
			$(briefNmae).attr('selct', 'false'); 
			$(elmt).find('[type=checkbox]').removeClass('active');
			$(elmt).find('[type=metaname]').removeClass('c_tag_active_on');
		} else {
			$(briefNmae).attr('selct', 'true');
			$(elmt).find('[type=checkbox]').addClass('active');
			$(elmt).find('[type=metaname]').addClass('c_tag_active_on');
		}
	};

	briefPicker.searchBrief = function(title) {
		report.listBriefData("title=" + encodeURIComponent(title), {
			
			success : function(briefs) {
				briefPicker.renderBriefList(briefs);
			}
		});
	};

	briefPicker.renderBriefList = function(briefs) {
		$(std.findTag(tag.briefList)).empty();
		$(briefs).each(function(i, item) {
			$(std.findTag(tag.briefList)).append('' + //
			'<div ' + std.flag(tag.briefItem, item.id) + ' class="c_tab_choice c_fl" scope="option" selct="false">' + //
			'	<div ' + std.flag(tag.briefItemCheckbox, item.id) + ' class="c_sprite c_choice_icon  c_fl" type="checkbox" value="'+ item.id + '"></div>' + //
			'	<div ' + std.flag(tag.briefItemTitle, item.id) + ' class="c_fl c_tag_active c_w100" type="metaname">' + item.title + '</div>' + //
			'	<div class="c_cb"></div>' + //
			'</div>');
		});
		refreshScrollbar();
		briefPicker.initEvent();
	};

	briefPicker.serialize = function() {//获取选项数据
		return $('#brief-form').serialize();
	};
	var briefNmaes = [];
	briefPicker.submit = function(guid, onSubmit) {
		briefNmaes = [];
		$(std.findTag('brief-picker-brief-item', '[scope=option][selct=true]')).each(function(i, item) {
			var value = $(item).find('[type=checkbox]').attr('value');
			briefNmaes.push({
				id : value
			});
		});
		limitOption("briefNmae", 'reportIds', briefNmaes);

		//动态生成input
		function limitOption(oid, inputName, options) {
			var formCtx = $('#briefNmae-form');
			formCtx.empty();

			$(options).each(function(i, option) {
				formCtx.append('<input type="hidden" name="' + inputName + '" value="' + option.id + '" />');
			});
		}
		
		popup.currentParam = '1=1';
		popup.currentParam = briefPicker.serialize();
		
		if (util.isBlank(briefNmaes)) {
			$.msg.warning('请选择简报');
			return;
		}
		report.addBriefArticle(popup.currentParam+'&guid=' + guid, {
			success : function() {
				if (onSubmit) {
					onSubmit();
					layer.confirm('是否马上进入简报查看?',function(index){
						 util.go("/report/brief/article/index.htm");
					 });
				}
			}
		});
	};

})();