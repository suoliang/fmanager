
var asyncSubmit = function(formId,currentPageVal){
	var formObj = $('#'+formId);
	var asyncFlag = formObj.find("input[name='asyncFlag']").val();
	var prefix = formObj.find("input[name='alias']").val();
	var replaceId = formObj.find("input[name='replaceId']").val();
	if(currentPageVal!=null){
		$('#currentPageHid_'+prefix).val(currentPageVal);
	}else{
		$('#currentPageHid_'+prefix).val(1);
	}
	
	if(asyncFlag=='true'){
		var url = getContextPath() + formObj.attr('action');
		PageInfoUtil.ajaxSubmitReplaceByElement(url, formObj.serialize(),replaceId,replaceId);
	}else{
		formObj.submit();
	}
};

var PageInfoUtil = PageInfoUtil || {};

PageInfoUtil.ajaxCommFunPage = function(url, data, callback) {
	jQuery.ajax({
		type : "post",
		async: false,
		//dataType:'html',
		url : url,
		timeout: 30000,
		data : data,
		success : callback
	});
};

/**
 * 请求提交抓取附加节点
 */
PageInfoUtil.ajaxSubmitAppendByElement = function(url,data,catchId,appendId){
	PageInfoUtil.ajaxCommFunPage(url, data, function(str){
		var ss = $(str).find('#'+catchId).html();
		$('#'+appendId).append(ss);
	});
};

/**
 * 请求提交抓取替换节点
 */
PageInfoUtil.ajaxSubmitReplaceByElement = function(url,data,catchId,replaceId){
	PageInfoUtil.ajaxCommFunPage(url, data, function(str){
		var ss = $(str).find('#'+catchId).html();
		$('#'+replaceId).html(ss);
	});
};



var initPageTab =  function(json){
	PageInfoFactory.createPageInfoFm(json);
};

var PageInfoFactory = PageInfoFactory || {};

/**
 * fm 项目分页模板
 */
PageInfoFactory.createPageInfoFm=function (json){
	//var formId = json["formId"];//form表单的Id
	var divId = json["divId"];//form表单内分页标签的DIVId
	var asyncFlag  = json["asyncFlag"];//刷新，无刷新标志 
	var prefix = json["prefix"];//分页标签的标志
	var currentPage = json["currentPage"];//当前页
	var totalPage = json["totalPage"];//一共多少页
	var count = json["count"];//总数
	var pageSize = json["pageSize"];//每页多少条
	var replaceId = json["replaceId"];//需要替换的ID
	if(replaceId == null){
		replaceId = json["formId"];
	}
	
	    
	//初始化页面
    var str = $('#'+divId).html();
    str = str.replace(/currentPageAlias/g,currentPage);
    str = str.replace(/totalPageAlias/g,totalPage);
    str = str.replace(/countAlias/g,count);
    str = str.replace(/pageSizeAlias/g,pageSize);
    str = str.replace(/asyncFlagAlias/g,asyncFlag);
    str = str.replace(/aliasAlias/g,prefix);
    str = str.replace(/replaceIdAlias/g,replaceId);
    str = str.replace(/prefix/g,prefix);
    
    var pageDiv = $('#'+divId);
    pageDiv.html(str);
	var pageStr = PageInfoFactory.createPageInfoFm.getPageSrc(currentPage,pageSize,totalPage,count);
	
	
	//设置页面
	$('#pagingitem_'+prefix).html(pageStr);
	//设置总数 
	$('#totalpage_'+prefix).html(count);
	
	pageDiv.find('[tag=paging-action]').click(function() {
		PageInfo.search(json,std.oid(this),pageSize);
	});
	
	pageDiv.find('#gotoNo_'+prefix).delegate(this,'click',function() {
		
		PageInfo.goToCurrPageBtn(json,pageSize,totalPage,'pageNo');
	});
	
	pageDiv.find('#pageNo_'+prefix).delegate(this,'keypress',function(e) {
		if (e && e.keyCode == 13) {
			PageInfo.goToCurrPageBtn(json,pageSize,totalPage,'pageNo');
		}
	});
	
};

/**
 * 获取fm的分页字符串
 */
PageInfoFactory.createPageInfoFm.getPageSrc = function(currentpage,pagesize,totalpage,totalsize){
	
		var pg = {};
		pg.currentpage = parseInt(currentpage);
		pg.pagesize = parseInt(pagesize);
		pg.totalpage = parseInt(totalpage);
		pg.totalsize = parseInt(totalsize);

		if (pg.totalpage <= 5) {//五页内
			var middle = [];
			for (var num = 1; num <= pg.totalpage; num++) {
				middle.push(num);
			}
			return PageInfoFactory.createPageInfoFm.renderDom(middle, pg.currentpage, false, false,pg);

		} else if (pg.currentpage <= 3) {//当前页为3页内
			var middle = [ 1, 2 ];
			if (pg.currentpage >= 2) {
				middle.push(3);
			}
			if (pg.currentpage >= 3) {
				middle.push(4);
			}
			return PageInfoFactory.createPageInfoFm.renderDom(middle, pg.currentpage, false, true,pg);

		} else if (pg.totalpage - pg.currentpage <= 2) {//当前页为倒数三页内
			var middle = [];
			if (pg.currentpage <= pg.totalpage - 2) {
				middle.push(pg.totalpage - 3);
			}
			if (pg.currentpage <= pg.totalpage - 1) {
				middle.push(pg.totalpage - 2);
			}
			middle.push(pg.totalpage - 1);
			middle.push(pg.totalpage);

			return PageInfoFactory.createPageInfoFm.renderDom(middle, pg.currentpage, true, false,pg);

		} else {//当前页在中间
			return PageInfoFactory.createPageInfoFm.renderDom([ pg.currentpage - 1, pg.currentpage, pg.currentpage + 1 ], pg.currentpage, true, true,pg);
		}
};

PageInfoFactory.createPageInfoFm.renderDom = function(middle, active, showMore1, showMore2,pg) {
	var html = '<div class="c_page_num"' + std.flag('paging-action', (active > 1 ? active - 1 : 1)) + '>&lt;</div>';
	if (showMore1) {
		html += '<div class="c_page_num"' + std.flag('paging-action', 1) + '>1</div><div class="c_page_more">...</div>';
	}
	$(middle).each(function(key, item) {
		html += '<div class="c_page_num ' + (active == item ? 'active' : '') + '"' + std.flag('paging-action', item) + '>' + item + '</div>';
	});
	if (showMore2) {
		html += '<div class="c_page_more">...</div><div class="c_page_num"' + std.flag('paging-action',pg.totalpage) + '>' +pg.totalpage + '</div>';
	}
	html += '<div class="c_page_num"' + std.flag('paging-action', (active <pg.totalpage ? active + 1 :pg.totalpage)) + '>&gt;</div>';

	return html;
};



/**
 * 上一页 下一页 基础分页模板
 */
PageInfoFactory.createPageInfoBaic=function (json){
	//var formId = json["formId"];//form表单的Id
	var divId = json["divId"];//form表单内分页标签的DIVId
	var asyncFlag  = json["asyncFlag"];//刷新，无刷新标志 
	var prefix = json["prefix"];//分页标签的标志
	var currentPage = json["currentPage"];//当前页
	var totalPage = json["totalPage"];//一共多少页
	var count = json["count"];//总数
	var pageSize = json["pageSize"];//每页多少条
	var replaceId = json["replaceId"];//需要替换的ID
	if(replaceId == null){
		replaceId = json["formId"];
	}
	
	    
	//初始化页面
    var str = $('#'+divId).html();
    str = str.replace(/currentPageAlias/g,currentPage);
    str = str.replace(/totalPageAlias/g,totalPage);
    str = str.replace(/countAlias/g,count);
    str = str.replace(/pageSizeAlias/g,pageSize);
    str = str.replace(/asyncFlagAlias/g,asyncFlag);
    str = str.replace(/aliasAlias/g,prefix);
    str = str.replace(/replaceIdAlias/g,replaceId);
    str = str.replace(/prefix/g,prefix);
    
	$('#'+divId).html(str);
		
  //绑定事件		
  $('#firstPage_'+prefix).delegate(this,'click', function() {
	  PageInfo.firstPage(json,1,pageSize);
    });	
   $('#prePage_'+prefix).delegate(this,'click', function() {
	   PageInfo.prePage(json,currentPage,totalPage,pageSize);
    });
    
    $('#nextPage_'+prefix).delegate(this,'click', function() {
    	PageInfo.nextPage(json,currentPage,totalPage,pageSize);
    });
    
    $('#finalPage_'+prefix).delegate(this,'click', function() {
    	PageInfo.finalPage(json,totalPage,pageSize);
    }); 
    
    $('#finalPage_'+prefix).delegate(this,'click', function() {
    	PageInfo.finalPage(json,totalPage,pageSize);
    }); 
    
    $('#goToCurrPageBtn_'+prefix).delegate(this,'click', function() {
    	PageInfo.goToCurrPageBtn(json,pageSize,totalPage);
    }); 
    
    $('#currPageIpt_'+prefix).delegate(this,'keypress', function(event) {
    	if(event.keyCode == "13")    
        {
    		PageInfo.goToCurrPageBtn(json,pageSize,totalPage);
        }
    }); 
	
};

var PageInfo = PageInfo || {};
PageInfo.search = function(json,currentPage, pageSize) {
	var formId = json["formId"];
	var asyncFlag  = json["asyncFlag"];
	var prefix  = json["prefix"];
	var replaceId = json["replaceId"];
	if(replaceId == null){
		replaceId = json["formId"];
	}
	
	$('#currentPageHid_'+prefix).val(currentPage);
	$('#pageSizeHid_'+prefix).val(pageSize);
	
	var  formObj = $("#"+formId);
	
	var url = getContextPath() + formObj.attr('action');
	if(asyncFlag){
		PageInfoUtil.ajaxSubmitReplaceByElement(url, formObj.serialize(),replaceId,replaceId);
	}else{
		formObj.submit();
	}
};

PageInfo.prePage = function(json,currentPage, totalPage, pageSize) {
	if (currentPage <= 1) {
		currentPage = 1;
	} else {
		currentPage = currentPage - 1;
	}
	PageInfo.search(json,currentPage, pageSize);
};
PageInfo.nextPage = function(json,currentPage, totalPage, pageSize) {
	if (currentPage >= totalPage) {
		currentPage = totalPage;
	} else {
		currentPage = currentPage + 1;
	}
	PageInfo.search(json,currentPage, pageSize);
};

PageInfo.firstPage = function(json,currentPage, pageSize) {
	PageInfo.search(json,currentPage, pageSize);
};

PageInfo.finalPage = function(json,currentPage, pageSize) {
	PageInfo.search(json,currentPage, pageSize);
};

PageInfo.goToCurrPageBtn = function(json,pageSize, totalPage,keyId) {
	var prefix = json["prefix"];
	var currPageIptObj = null;
	if(keyId==null){
		currPageIptObj = $("#currPageIpt_"+prefix);
	}else{
		currPageIptObj = $("#"+keyId+"_"+prefix);
	}
	var currPageIpt = currPageIptObj.val();
	if (currPageIpt == "") {
		currPageIptObj.val("");
		win.msg.short("请输入跳转页数!", "W01");
	} else if (parseInt(currPageIpt) > totalPage) {
		currPageIptObj.val("");
		win.msg.short("你输入的页数超出总页数，请输入正确的页数!", "W01");
	} else if (parseInt(currPageIpt) <= 0) {
		currPageIptObj.val("");
		win.msg.short("你输入的页数小于1，请输入正确的页数！", "W01");
	} else {
		PageInfo.search(json,parseInt(currPageIpt), pageSize);
	}
};

