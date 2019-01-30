/**
 * @author LIUJUNWU
 */
var page = page || {};
(function() {
	//******************** Global Constant ********************//
	var selector = {
		searchAdvanced : function() {
			return '#search-advanced';
		},
		listHead : function() {
			return '.search-list-head';
		},
		listContainer : function() {
			return '.search-list-container';
		},
		pagingContainer : function() {
			return '#siteAudit-paging';
		},
		top : function() {
			return '.c_top';
		}
	};

	var tag = {
		pagingAction : "paging-action",
		pagingInfo : "paging-info",
		sortBtn : 'btn-sort'
	};

	var input = {
		reportId : 'input[name=reportId]',
		orderField : 'input[name=orderField]',
		orderType : 'input[name=orderType]'
	};
	
	//******************** Page Function ********************//

	page.currentParam = '1=1';
	
	var status;
	page.initEvent = function() {
		//查询
		$('#searchBtn').click(function(event) {
			var pagesize = $("#siteAudit-paging").paging('option', 'pagesize');
			page.queryCustsite(1, pagesize);
		});
		//取得下拉框的值
		$.divselect("#divselect7", "#status-select-item", function(txt, value) {
			status = value;
		});
	};
	page.queryCustsite = function(pageNo, pagesize) {
		var param;
		param = $.extend(param, {
			pageNo : pageNo,
			pageSize : pagesize,
			procStatus : status,
			url : $('#inputUrl').val(),
			name : $('#inputSiteName').val(),
			customerName : $('#inputCustomerName').val()
			
		});
		ajaxCommFunText(std.u("/setting/siteAudit/queryList.htm"), param, function(response) {
			if (response.type == dict.action.suc) {
				$('#siteAuditTable_id').empty();//清空
				// 刷新页面 response.data -> html
				$('#siteAuditTable_id').append(response.data);
				//事件注册（审核需求）
				$(std.findTag("needAudit")).click(function(){
					page.list.needAudit(this);
				});
				//事件注册（关联FID）
				$(std.findTag("FID-Box")).click(function(){
					page.list.relevanceFID(this);
				});
				
			} else {
				alertMsg(response.message);
			}
		});
	}

	page.querySiteAudit = function(pageNo, pagesize) {
		page.currentParam = page.options.serialize();
		siteAudit.querySiteAudit(page.currentParam + "&pageNo=" + pageNo + "&pageSize=" + pagesize, {
			success : function(html) { 
				$('#siteAuditTable_id').empty();
				$('#siteAuditTable_id').html(html);//把controller返回的页面嵌入到页面
				page.list.initEvent();
			}
		});
	}

	//******************** Option Function ********************//
	var flag=false;
	page.options = page.options || {};
	(function(options) {
		options.serialize = function() {//获取选项数据
			this.sync();
			return $('#siteAudit-form').serialize();
		};
		options.sync = function() {//同步选项数据
			$('#customerName').val($('#inputCustomerName').val());
			$('#name').val($('#inputSiteName').val());
			$('#url').val($('#inputUrl').val());
			$('#procStatus').val(status);
		};
		var spiderSiteId = $("#input-FID").val();
		if (spiderSiteId == '') {
			flag=true;
		}
		$('#input-FID').change(function(){
			if (spiderSiteId == '') {
				$('#Hint-FID1').css('display', 'none');
			}
		})
		$('#input-FID').blur(function(){//校验FID是否存在 
			if ($("#input-FID").val() == '') {
				return;
			}else if(!/^[0-9]*$/.test($("#input-FID").val())){
				layer.msg("FID必须是数字！",1,0);
				return;
			}else if($("#input-FID").val().length>10){
				layer.msg("FID位数不能超出十位！",1,0);
				return;
			}
			if ($("#input-FID").val() != null) {
				var spiderSiteId=$("#input-FID").val();
				var param = {
						spiderSiteId : spiderSiteId
					};
				ajaxCommFun(std.u('/setting/siteAudit/checkFID.htm'), param, function(response) {
					if (response.type == dict.action.suc) {
						if (!response.data) {
							layer.msg("输入的FID不存在！",1,0);
							flag=false;
							return;
						}else{
							flag=true;
						}
					} 
				});
			}
		});
		
	})(page.options);

	//******************** List Function ********************//  

	page.list = page.list || {};
	(function(list) {
		list.initEvent = function() {
			$(std.findTag("needAudit")).click(function(){
				list.needAudit(this);
			});
			$(std.findTag("FID-Box")).click(function(){
					list.relevanceFID(this);
			});
		};
		//审核需求弹出框
		list.needAudit = function(entity){
				$.box('#needAuditBox', {
					onOpen : function() {
						$("#needAuditBox-name").html($("#needAudit-name"+std.oid(entity)).val());
						$("#needAuditBox-url").html($("#needAudit-url"+std.oid(entity)).val());
						$('input[name="radio-result"]').attr("checked",true);
						$("#remark").val("");
					}
				}, {
					submit : {
						close : false,
						dom : [ '#needAuditBox .c_but_ok' ],
						fun : function(index) {
							var procStatus=$("[name='radio-result']:checked").val();
							var remark = $("#remark").val()
							if(procStatus==3){
								if(remark==''){
									layer.msg("请填写备注！",1,0);
									return;
								}
							}else if(procStatus==null){
								layer.msg("请选审核结果！",1,0);
								return;
							}
							if (procStatus==3 && remark == "") {
								$("#remark").focus();
								return false;
							}
							//异步修改对应的站点处理状态
							var custSiteId=std.oid(entity);
							var remark=$("#remark").val();
							var param = {
									remark : remark,//备注
									procStatus : procStatus //审核结果
									}
							param = $.extend(param, {
								id : custSiteId
							});
							outer = this;
							ajaxCommFun(std.u("/setting/siteAudit/needAudit.htm"), param, function(response) {
								if (response.type == dict.action.suc) {
									outer.close(index);//“确定”关闭窗口
									// 刷新页面 
									$("#siteAudit-paging").paging('refresh');
									var pagesize = $("#siteAudit-paging").paging('option', 'pagesize');
									var currentpage = $("#siteAudit-paging").paging('option', 'currentpage');
									page.queryCustsite(currentpage, pagesize);
								} else {
									alertMsg(response.message);
								}
							});
							
						}
					},
					close : {
						fun : function() {
							//新增后去掉先前记录
							$("#trueentity").val("");
							$("#virtualentity").val("");
						},
						dom : [ '#needAuditBox .c_but_no', '#needAuditBox .close' ]
					}
				});
		};
		//关联FID弹出框
		list.relevanceFID = function(entity){
			$.box('#FID-Box', {
				onOpen : function() {
					$("#needAuditBox-name-FID").html($("#needAudit-name"+std.oid(entity)).val());
					$("#needAuditBox-url-FID").html($("#needAudit-url"+std.oid(entity)).val());
					$('#Hint-FID').css('display', 'none');
					$('#Hint-FID1').css('display', 'none');
					$("#input-FID").val("");
				}
			}, {
				submit : {
					fun : function(index) {
						//关联FID  
						if(flag){
							var spiderSiteId = $("#input-FID").val();
							var custSiteId=std.oid(entity);
							if (spiderSiteId == '') {
								$('#Hint-FID1').css('display', '');
								return;
							}else if(!/^[0-9]*$/.test(spiderSiteId)){
								layer.msg("FID必须是数字！",1,0);
								return;
							}else if(spiderSiteId.length>10){
								layer.msg("FID位数不能超出十位！",1,0);
								return;
							}
							$('#Hint-FID').css('display', 'none');
							$('#Hint-FID1').css('display', 'none');
							var param = $.extend(param, {
								spiderSiteId : spiderSiteId,
								id : custSiteId
							});
							outer = this;
							ajaxCommFun(std.u("/setting/siteAudit/boundFid.htm"), param, function(response) {
								if (response.type == dict.action.suc) {
									outer.close(index);//“确定”关闭窗口
									// 刷新页面 
									$("#siteAudit-paging").paging('refresh');
									var pagesize = $("#siteAudit-paging").paging('option', 'pagesize');
									var currentpage = $("#siteAudit-paging").paging('option', 'currentpage');
									page.queryCustsite(currentpage, pagesize);
								} else {
									alertMsg(response.message);
								}
							});
						}
						
					},
					close : false,
					dom : [ '#FID-Box .c_but_ok' ]
				},
				close : {
					fun : function() {
						//新增后去掉先前记录
						$("#trueentity").val("");
						$("#virtualentity").val("");
					},
					dom : [ '#FID-Box .c_but_no', '#FID-Box .close' ]
				}
			});
		};
	})(page.list);

	//******************** Paging Function ********************//

	page.paging = page.paging || {};
	(function(paging) {
		paging.initEvent = function() {
			$(selector.pagingContainer()).paging({
				gotoNoImpl : function(pageNo, pagesize) {
					page.querySiteAudit(pageNo, pagesize);
				}
			});
		};
	})(page.paging);

	$(function() {

		//page init
		page.initEvent();

		//list init
		page.list.initEvent();

		//paging init
		page.paging.initEvent();

	});
})();