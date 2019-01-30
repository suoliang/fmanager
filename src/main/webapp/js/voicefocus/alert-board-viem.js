//初始化
$(function() {
	$("[tag=alert_tag_color]").each(function(prop, value) {
		if ($(value).text() == 1) {
			$(value).html("人工");
			$(this).attr("class", "c_emergency c_fl c_mt10");
		} else if ($(value).text() == 2) {
			$(value).html("自动");
			$(this).attr("class","c_general c_fl c_mt10");
		}
	});
});
