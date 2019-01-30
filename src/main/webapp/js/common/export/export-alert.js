/**
 * 文件导出
 * @author LIUJUNWU
 */
var exportbox = exportbox || {};
var exportTasks = {};
var taskId;
var exportEvent=true;
(function() {
	var exportPickerBox = "[tag=export-box][oid=export-picker]";

	var tag = {
		taskList : 'task-list',
		exportStart : 'exportStart',
		taskDel : 'task-delete'
	};

	var input = {
		title : 'input[name=brief-picker-title]'
	};

	exportbox.openExportPicker = function(userid, type) {
		$.box(exportPickerBox, {
			onOpen : function() {
				exportTasks.queryTasks(userid, type);//查询任务列表
				
				exportTasks.initEvent();
			}
		}, {
			close : {
				dom : [ exportPickerBox + " .box-close", exportPickerBox + " .box-cancel" ]
			}
		});
	};
	
	exportTasks.renderTaskList = function(tasks) {//展示文件列表
		$(std.findTag(tag.taskList)).empty();
		var status;
		var downloadBtn;
		var deleteBtn;
		$.each(tasks, function(i, item) {
			downloadBtn = item.status == '2' ? true : false;
			deleteBtn = item.status != '1' ? true : false;
			status= item.status == '1' ? '<span class="c_color_red">文件生成中</span>' : item.status == '2' ? '成功' : item.status == '3' ? '失败' : '系统异常';
			$('	<ul class="c_tab2 c_tab_top">' + //
				'	<li class="c_tc col_2"><div class="c_omit">' + status + '</div></li>' + //
				'	<li class="c_tc col_6"><div class="c_omit">' + item.name + '</div></li>' + //
				'	<li class="c_tc col_2">' + //
				(downloadBtn ? '<div class="c_fl c_mr5 c_ml5 c_amore"><a href="/uploads/' + item.name + '"target="_self">下载</a></div>' : '') + //
				(deleteBtn ? '<div class="c_fl c_amore" fileName="'+ item.name + '" tag="task-delete" oid="'+ item.id + ',' + item.type +'"><a>删除</a></div>' : '')  + //
				'   </li>' + //
				'	</ul>' ).appendTo(std.findTag(tag.taskList));
		});
		
		exportTasks.initEvent();
	};
	
	exportTasks.initEvent = function() {
		$(std.findTag(tag.exportStart)).unbind('click').click(function() {//"生成文件"初始化事件
			var totalNumber=$('#count-Show-i').html();
			if(totalNumber.replace(',', '') > 10000){
				$.msg.warning('您当前所选择的导出范围大于1万条数据，请联系客服。', 2);
			}else{
				$(this).next('div').removeClass("c_none");
				page.options.exportAllWordOrExcel(std.oid(this));//导出   注：这里是分别对应‘预警’、‘专题’、‘搜索’模块中的js中的一个方法
			}
		});
		
		$(std.findTag(tag.taskDel)).click(function() {//"删除"初始化事件
			var parameter=std.oid(this).split(',');
			var userId=std.oid(std.findTag(tag.exportStart));
			var taskId=parameter[0];
			var type=parameter[1];
			var status=100;//约定100为“删除”状态
			exportTasks.updateTaskStatus(taskId, userId, type, status);
			
			exportTasks.deleteTaskFile(std.findTag(tag.taskDel).attr('fileName'));//删除任务对应文件
		});
		
		if(exportEvent){
			$(std.findTag(tag.exportStart)).removeClass('c_no_but');
		}else{
			$(std.findTag(tag.exportStart)).addClass('c_no_but');
			$(std.findTag(tag.exportStart)).unbind('click').click(function() {
				$.msg.warning('文件生成中,请稍候再操作。', 1);  
			});
		}
	};
	
	exportTasks.addTask=function(userId, type) {//添加任务
		var url = ctx+"/export/addTask.htm";
		var params={
				createrId : userId,
				type : type
		}
		ajaxCommFun(url, params, function(response) {
 			if (response.type == dict.action.suc) {
 				taskId=response.data.id;
 				exportEvent=false;
 				exportTasks.queryTasks(userId, type);//查询任务列表
 			} else {
 				alertMsg(response.message);
 			}
	 	});
	};
	
	exportTasks.updateTaskStatus=function(taskId, userId, type, status) {//修改任务状态
		var url = ctx+"/export/updateTaskStatus.htm";
		var params={
				id : taskId,
				status : status
		}
		ajaxCommFun(url, params, function(response) {
			if (response.type == dict.action.suc) {
				exportEvent=true;
				$(std.findTag(tag.exportStart)).next('div').addClass("c_none");
				exportTasks.queryTasks(userId, type);//查询任务列表
			} else {
				alertMsg(response.message);
			}
		});
	};
	
	exportTasks.queryTasks=function(userid, type) {//查询任务列表
		var params={
				createrId : userid,
				type : type
		}
		exportIF.listTaskData(params, {
			
			success : function(tasks) {
				
				exportTasks.renderTaskList(tasks);//展示文件列表
				
			}
		});
	};
	
	exportTasks.deleteTaskFile=function(fileName) {//删除任务对应文件
		var params={
				name : fileName
		}
		exportIF.deleteTaskFile(params);
	};
})();