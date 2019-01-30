
//sharefun2068 2014-12-8 ajax Common Function
var ajaxCommFun = function(url, data, callback) {
    jQuery.ajax( {
        type: "post",
        url: url,
        data: data,
        dataType: 'json',
        success: callback
    });
};

//sharefun2068 2014-12-8 ajax Common Function With beforeSendFunc
var ajaxCommFunWithBeforeSend = function(url, data, callback,beforeSendFunc) {
    jQuery.ajax( {
        type: "post",
        url: url,
        data: data,
        dataType: 'json',
        success: callback,
        beforeSend: beforeSendFunc
    });
};

//sharefun2068 2014-12-08 获取path
var getContextPath = function() {
    var path = window.location.pathname;
    return path.substr(0, path.lastIndexOf('/') + 1);
};

//去除空字符函数 sharefun2068 2014-12-08 add
String.prototype.trim=function(){
  return this.replace(/(^\s*)|(\s*$)/g, "");
};
//sharefun2068 2014-12-08 去除左空格
String.prototype.ltrim=function(){
  return this.replace(/(^\s*)/g,"");
};
//sharefun2068 2014-12-08 去除右空格
String.prototype.rtrim=function(){
   return this.replace(/(\s*$)/g,"");
 };
//sharefun2068 2014-12-08 获取checkbox参数形式的值
var getCheckboxValueByName = function(dagouclass) {
    var chk_value = [];
    jQuery("." + dagouclass).each(function() {
        if(jQuery(this).find(".ckbx").val() != 0) {
            chk_value.push(jQuery(this).find(".ckbx").val());
        }
    });
    return chk_value.length == 0 ? '0' : chk_value + "";
};
  
//全选插件  
//checkall_name   操作全选的复选框name  
//checkbox_name   被操作的复选框的name  name值可不统一 设置包含值 以XXX开头 自己修改  
(function ($) {  
    $.fn.check = function (options) {  
        var defaults = {  
            checkall_name: "checkall_name", //全选框name  
            checkbox_name: "checkbox_name" //被操作的复选框name  
        },  
        ops = $.extend(defaults, options);  
        e = $("input[name='" + ops.checkall_name + "']"), //全选  
        f = $("input[name='" + ops.checkbox_name + "']"), //单选  
        g = f.length; //被操作的复选框数量  
        f.click(function () {  
            $("input[name ='" + ops.checkbox_name + "']:checked").length == $("input[name='" + ops.checkbox_name + "']").length ? e.attr("checked", !0) : e.attr("checked", !1);  
        }), e.click(function () {  
            for (i = 0; g > i; i++) f[i].checked = this.checked;  
        });  
    };  
})(jQuery);  

