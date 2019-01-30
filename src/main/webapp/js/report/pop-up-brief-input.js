/**
 * @author GUQIANG
 */
var popup = popup || {};
(function() {
	var briefInput = {};
	var briefInputBox = "[tag=popup-box][oid=brief-input]";

	var refreshScrollbar = function() {
		$(briefInputBox).find('.scrollbar-org').tinyscrollbar();
		$(briefInputBox).find('.scrollbar-user').tinyscrollbar();
		$(briefInputBox).find('.scrollbar-selected-user').tinyscrollbar();
	}

	popup.openBriefInput = function(id, title, onSubmit) {
		$.box(briefInputBox, {
			onOpen : function() {
				briefInput.init(id, title);
			}
		}, {
			submit : {
				close : false,
				dom : [ briefInputBox + " .box-submit" ],
				fun : function(index) {
					var form = briefInput.submit();
					if (util.isNull(form)) {
						return;
					}
					if (onSubmit) {
						onSubmit(form);
					}
					this.close(index);
				}
			},
			close : {
				dom : [ briefInputBox + " .box-close", briefInputBox + " .box-cancel" ]
			}
		});
	};

	briefInput.init = function(id, title) {
		//初始化页面
		$(briefInputBox).find('input[name=briefId]').val(id);
		$(briefInputBox).find('input[name=briefTitle]').val(title);

		$(briefInputBox).find('input[name=briefTitle]').unbind().keydown(function(e) {
			if (e && e.keyCode == 13) {
				$(briefInputBox).find(".box-submit").click();
			}
			e.stopPropagation();
		});
	};

	briefInput.submit = function() {
		var id = $(briefInputBox).find('input[name=briefId]').val();
		var title = $(briefInputBox).find('input[name=briefTitle]').val();

		var form = {};
		if (util.isNotNull(id)) {
			form.id = id;
		} else {
			form.id = '';
		}
		if (util.isNotBlank(title)) {
			if (!/^[a-zA-Z0-9_\\;；.\s\u4e00-\u9fa5]{0,}$/.test($.trim(title))) {
				$.msg.warning('简报名称只能是中文,字母,数字,下划线,空格,分号,小数点', 2);
				return null;
			}
			form.title = $.trim(title);
		} else {
			$.msg.warning("简报名称不能为空");
			return null;
		}
		return form;
	};
})();