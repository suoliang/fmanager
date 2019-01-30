var cyyun = {
	mainWidth : function() {
		var bw = $("body").width();
		var lh = $(".c_sidebar").height();
		$(".c_main").height(lh - 50).css("overflow-y", "scroll");
		if ($('.c_sidebar').width() == 210) {
			$(".c_main").width(bw - 210);
		} else if ($('.c_sidebar').width() == 75) {
			$(".c_main").width(bw - 75);
		}

	},
	listContentWidth : function() {
		var listWidth = $(".c_list_block").width();
		$(".c_artical").width(listWidth - 270);
	},
	sideBarHide : function() {

	},
	sideBarSlider : function() {
		$(".c_hide").on();
	}
}
/*c_main width end*/
$(function() {
	  function autoHeight(){  
		  var h = $(window).height(); 
		  var h_old = 300;
		  if (h > h_old) { 
			 $(".c_sidebar").css('min-height', h);
			 $(".c_container").css('min-height', $(".c_sidebar").height()+50); 
			 
			 /**专题模板设置的滚动高度*/
			if ($(".c_zhuanti").height() > 650) {
				$(".c_zhuanti").height(650);
			}
			 
		  } 
		  else { 
			 return false; 
	      } 
	  }
	  $(function(){
		autoHeight();
		$(window).resize(autoHeight);
		/**控制首页底部显示*/
		$(".c_footer").show();
		
		/**专题模板滚动条加载*/
		$("#topic_scroll02").niceScroll({
			cursorcolor : "#D2D0D0",
			cursoropacitymax : 1,
			touchbehavior : false,
			cursorwidth : "5px",
			cursorborder : "0",
			cursorborderradius : "5px"
		});
		
	  });
	

	$("#home_scroll01").niceScroll({
		cursorcolor : "#D2D0D0",
		cursoropacitymax : 1,
		touchbehavior : false,
		cursorwidth : "5px",
		cursorborder : "0",
		cursorborderradius : "5px"
	});

	//管理员
	$(document).ready(function() {
		$("#c_hi1").hover(function() {
			$(this).find(".c_downbox").show();
			$('.c_user span').text('▲')
		}, function() {
			$(this).find(".c_downbox").hide();
			$('.c_user span').text('▼')
		});
	});
})

/*以下下拉菜单*/
jQuery.divselect = function(divselectid, inputselectid, callback) {
	var inputselect = $(inputselectid);

	// init
	var text, selectid;
	if ($(divselectid + " ul li a[def=true]").size() != 0) {
		text = $(divselectid + " ul li a[def=true]").text();
		selectid = $(divselectid + " ul li a[def=true]").attr('selectid');

		if (callback) {
			callback(text, selectid);
		}

	} else if ($(divselectid + " ul li a").size() != 0) {
		text = $(divselectid + " ul li a").first().text();
		selectid = $(divselectid + " ul li a").first().attr('selectid');

		if (callback) {
			callback(text, selectid);
		}

	} else {
		text = "没有可选项";
		selectid = "";
	}
	$(divselectid + " div div[class=citeT]").text(text);
	inputselect.val(selectid);

	// show
	$(divselectid + " div").unbind('click').click({
		viewid : divselectid,
		inputid : inputselectid
	}, function(e) {
		var ul = $(e.data.viewid + " ul");
		if (ul.css("display") == "none") {
			ul.slideDown("fast");
		} else {
			ul.slideUp("fast");
		}
		e.stopPropagation();
	});

	// click
	$(divselectid + " ul li a").unbind('click').click({
		viewid : divselectid,
		inputid : inputselectid
	}, function(e) {
		var txt = $(this).text();
		$(e.data.viewid + " div div[class=citeT]").html(txt);
		var value = $(this).attr("selectid");
		inputselect.val(value);
		$(e.data.viewid + " ul").hide();

		if (callback) {
			callback(txt, value);
		}
		e.stopPropagation();
	});

	// hide
	$(document).click({
		viewid : divselectid,
		inputid : inputselectid
	}, function(e) {
		$(e.data.viewid + " ul").hide();
		e.stopPropagation();
	});
};
/*以下子菜单切换*/
function changeTab(oA) {
	var oAName = oA.getAttribute("name");
	var oAs = document.getElementsByName(oAName);
	for (var i = 0; i < oAs.length; i++) {
		if (oAs[i].className == "nav_pag_active" && oAs[i] != oA) {
			oAs[i].className = "";
			var oDiv = document.getElementById(oAs[i].getAttribute("rel"));
			oDiv.style.display = "none";
		}
	}
	oA.className = "nav_pag_active";
	document.getElementById(oA.getAttribute("rel")).style.display = "";
	return false;
}
