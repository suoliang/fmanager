var manualPage = manualPage || {};
(function(){
	
	manualPage.currentParam = "";

	manualPage.initEvent = function() {
		
		$(".c_button").click(function(event){
			var pagesize = $("#report-manual-paging").paging('option', 'pagesize');
			manualPage.queryManual(1,pagesize);
		});
		
		//状态下拉框下拉效果   gengeral.js
		$.divselect('#divselect1', '#inputselect1');
		//状态下拉框下拉效果   gengeral.js
		$.divselect('#divselect2', '#inputselect2');
		
	};
	
	manualPage.queryManual = function(pageNo, pagesize) {
		var param = "";
		param = $.extend(param, {
			pageNo : pageNo,
			pageSize : pagesize,
			title : $.trim($(".c_keywds").val()),
			category : $('#inputselect1').val(),
			status : $('#inputselect2').val(),
			startTime :$('#startTimeInput').val(),
			endTime : $('#endTimeInput').val()
		});
		
		ajaxCommFunText(std.u("/report/manual/queryList.htm"), param, function(response) {
			if (response.type == dict.action.suc) {
				$('#manual-container').empty();//清空
				// 刷新页面 response.data -> html
				$('#manual-container').append(response.data);
				
				/**事件注册*/
				$(std.findTag("download")).click(function(){
					manualPage.list.downLoad(this);
				});
				
				/**事件注册*/
				$(std.findTag("onlineRead")).click(function(){
					manualPage.list.onlineRead(this);
				});
				
			} else {
				alertMsg(response.message);
			}
		});
		
	};
	
	manualPage.queryCustReport = function(pageNo, pagesize) {
		manualPage.currentParam = manualPage.options.serialize();
		
		manualReport.queryManual(manualPage.currentParam + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) { 
				$('#manual-container').empty();
				$('#manual-container').html(html);//把controller返回的页面嵌入到页面
				/**加载时执行注册里的方法*/
				manualPage.list.initEvent();
			}
		});
	};
	
	/**  options  */
	manualPage.options = manualPage.options || {};
	(function(options) {
		options.serialize = function() {//获取选项数据
			this.sync();
			return $('#manual-form').serialize();
		};
		options.sync = function() {//同步选项数据
			$("#title").val($.trim($(".c_keywds").val()));
			$('#category').val($('#inputselect1').val());
			$('#status').val($('#inputselect2').val());
			$('#startTime').val($('#startTimeInput').val());
			$('#endTime').val($('#endTimeInput').val());
		};
		

		var startTime, endTime;
		startTime = {
			elem : '#startTimeInput',
			format : 'YYYY-MM-DD hh:mm:ss',
			isclear : true, // 是否显示清空
			istoday : false, // 是否显示今天
			istime: true,//是否显示时间
			issure : true, // 是否显示确认
			//max : myDate.toLocaleString(),
			choose : function(datas) {
				endTime.min = datas;
				//endTime.start = datas;/**时分秒结束框需要从23:59:59开始*/
				if (new Date(datas) > new Date($('#endTimeInput').val())) {
					$('#endTimeInput').val(datas);
				}
			}
		};
		endTime = {
			elem : '#endTimeInput',
			format : 'YYYY-MM-DD hh:mm:ss',
			start : laydate.now(new Date().getTime()) + ' 23:59:59',
			isclear : true, // 是否显示清空
			istoday : false, // 是否显示今天
			istime: true,//是否显示时间
			issure : true, // 是否显示确认
			//max : myDate.toLocaleString(),
			choose : function(datas) {
				startTime.max = datas;
			}
		};
		
		options.initEvent = function() {//初始化事件
			startTime.max = laydate.now();
			endTime.max = laydate.now();
			laydate(startTime);
			laydate(endTime);
		};

	})(manualPage.options);
	
	
	/**   list  */
	manualPage.list = manualPage.list || {};
	(function(list) {
		list.initEvent = function(){
			$(std.findTag("download")).click(function(){
				list.downLoad(this);
			});
			
			$(std.findTag("onlineRead")).click(function(){
				list.onlineRead(this);
			});
		};
		
		list.downLoad = function(entity){
			var reportId = std.oid(entity);/**报告id*/
			location.href = std.u("/report/manual/downLoad.htm") + "?id="+reportId;
		};
		
		list.onlineRead = function(entity){
			var reportId = std.oid(entity);
			var param = {id : reportId};/**报告id*/
			param = $.extend(param, {id : reportId});
			
			ajaxCommFun(std.u("/report/manual/onlineRead.htm"), param, function(response){
				if (response.type == dict.action.suc) {
					list.post(std.u("/report/manual/toOnlineRead.htm"),{onlineReadUrl:response.code});
				} else {
					$.msg.warning(response.message);
					//alertMsg(response.message);
				};
			});
		};
		
		/**设置成post提交*/
		list.post = function (url, params) {        
	        var temp = document.createElement("form");        
	        temp.action = url;   
	        temp.target = "_blank";
	        temp.method = "post";        
	        temp.style.display = "none";        
	        for (var x in params) {        
	            var opt = document.createElement("textarea");        
	            opt.name = x;        
	            opt.value = params[x];        
	            temp.appendChild(opt);        
	        }        
	        document.body.appendChild(temp);        
	        temp.submit();        
	        return temp;        
	    };  
		
		
	})(manualPage.list);
	
	
	manualPage.paging = manualPage.paging || {};
	(function(paging) {
		paging.initEvent = function() {
			$('#report-manual-paging').paging({
				gotoNoImpl : function(pageNo, pagesize) {
					manualPage.queryCustReport(pageNo, pagesize);
				}
			});
		};
	})(manualPage.paging);

	$(function() {

		//page init
		manualPage.initEvent();
		
		manualPage.options.initEvent();

		manualPage.list.initEvent();
		
		//paging init
		manualPage.paging.initEvent();

	});
	
})();


