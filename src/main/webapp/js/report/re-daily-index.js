var daily = daily || {};
(function() {

	var dateOption = {
		elem : '#daily-date',
		format : 'YYYY-MM-DD',
		isclear : false, //是否显示清空
		istoday : false, //是否显示今天
		issure : true,
		choose : function(datas) {
			daily.createDaily();
		}
	}
	daily.createDaily = function() {
		var index = layer.load('正在生成日报...');

		var date = $('input[name=date]').val();
		var html = '<iframe marginheight="0" marginwidth="0" frameborder="0" scrolling="no" width="100%" height="100%" id="iframepage" name="iframepage"></iframe>';
		html = $(html);
		html.attr('src', std.u('/report/daily/html.htm?date=' + date));
		html.load(function() {
			var ifm = document.getElementById("iframepage");
			var subWeb = document.frames ? document.frames["iframepage"].document : ifm.contentDocument;
			if (ifm != null && subWeb != null) {
				ifm.height = subWeb.body.scrollHeight;
			}

			if ($(subWeb).find('html').html().indexOf('login.css') != -1) {
				parent.window.location.href = std.u('/index.htm');
			}

			var error = $(subWeb).find('#COMMON_EXCEPTION_MESSAGE_CONTENT').size();
			if (error > 0) {
				$(std.findTag("page-daily-html")).empty();

				var message = $(subWeb).find('#COMMON_EXCEPTION_MESSAGE_CONTENT').html();
				$.msg.error(message);
			}

			layer.close(index);
		});
		$(std.findTag("page-daily-html")).empty();
		$(std.findTag("page-daily-html")).append(html);
	}
	
	daily.initEvent = function() {

		dateOption.max = laydate.now();
		laydate(dateOption);

		//生成日报 
		daily.createDaily();

		$(std.findTag("btn-export-daily")).unbind().click(function() {
			var date = $('input[name=date]').val();
			util.go('/report/daily/doc.htm?date=' + date);
		});
	};

	$(function() {
		daily.initEvent();

		$(std.findTag("btn-generate-daily")).click();
	});
})();