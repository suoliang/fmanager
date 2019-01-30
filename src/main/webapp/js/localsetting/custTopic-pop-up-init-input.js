/**
 * @author SUOLIANG
 */
var popup = popup || {};
(function(popup) {
	var topicInitInputBox = "[tag=popup-box][oid=topicInit-input]";

	popup.openTopicInitInput = function(topicId, onSubmit) {
		$.box(topicInitInputBox, {
			onOpen : function() {
				popup.init(topicId);
			}
		}, {
			submit : {
				close : false,
				dom : [ topicInitInputBox + " .box-submit" ],
				fun : function(index) {
					var form = popup.submit();
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
				dom : [ topicInitInputBox + " .box-close", topicInitInputBox + " .box-cancel" ]
			}
		});
	};

	popup.init = function(topicId) {
		//初始化页面
		$(topicInitInputBox).find('input[name=topicInitId]').val(topicId);
		$(topicInitInputBox).find('input[name=topicInitDate]').val("");//清空时间
		
	};

	popup.submit = function() {
		var topicId = $(topicInitInputBox).find('input[name=topicInitId]').val();
		var topicInitDate = $(topicInitInputBox).find('input[name=topicInitDate]').val();

		var form = {};
		if (util.isNotNull(topicId)) {
			form.topicId = topicId;
		} else {
			form.topicId = '';
		}
		if (util.isNotBlank(topicInitDate)) {
			form.topicInitDate = $.trim(topicInitDate);
		} else {
			$.msg.warning("初始日期不能为空");
			return null;
		}
		return form;
	};
})(popup);

popup.condition = popup.condition || {};
(function(condition){
	condition.initEvent = function() {//初始化事件
		var startDateOption;
		startDateOption = {
			elem : '#topicInitDate',
			format : 'YYYY-MM-DD hh:mm:ss',
			isclear : true, //是否显示清空
			istoday : false, //是否显示今天
			istime: true,//是否显示时间
			issure : true, //是否显示确认
			choose : function(datas) {
				//$("input[name=topicInitDate]").val(datas);
			}
		};
		
		startDateOption.max = laydate.now();
		startDateOption.min = laydate.now(-7);
		laydate(startDateOption);
	};
})(popup.condition);

$(function() {
	
	popup.condition.initEvent();
	
});

