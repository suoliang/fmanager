  
//登录图片切换  
  var interval;
    var switchCount = 2; // 切换的页码总数
    var currentSwitch = 0; // 当前切换页码索引
   $(function() {
        jQuery("#dot1").click(switchA);
        jQuery("#dot1").mouseover(switchA);

        jQuery("#dot2").click(switchB);
        jQuery("#dot2").mouseover(switchB);

        startTimer(); // 初始化并启动定时器
            
    });
    
        function switchA(){
            jQuery(this).css("background-position", "-148px -440px");
            jQuery("#dot2").css("background-position", "-160px -440px");
            jQuery("#first").animate({marginLeft:"0px"}, "fast");
            jQuery("#dot1").removeClass().addClass("block c_fl");
            jQuery("#dot2").removeClass().addClass("block_on c_fl");
            
        }
        function switchB(){
            jQuery("#dot1").css("background-position", "-160px -440px");
            jQuery(this).css("background-position", "-148px -440px");
            jQuery("#first").animate({marginLeft:"-478px"}, "fast");
            jQuery("#dot1").removeClass().addClass("block_on c_fl");
            jQuery("#dot2").removeClass().addClass("block c_fl");
        }

        
        function startTimer(){
            // 初始化并启动定时器
            interval = setInterval(autoAwitch, 5000);
        }
        
        function stopTimer(){
            clearTimeout(interval);
        }
        
        
        // 自动切换手机客户端和舆情助手页面
        function autoAwitch() {
            switch(currentSwitch % switchCount){
                case 0:
                     switchB();
                     currentSwitch++;
                     break;
                case 1:
                     switchA();
                     currentSwitch++;
                     break;
            }
        }
        
        
        function showView(id){
            jQuery("#" + id).show(100);
        }
        function hideView(id){
            jQuery("#" + id).hide(100);
        }
        
        
      //以下登陆框兼容ie9以下Placeholder
        (function ($) {
            $.fn.extend({
                "iePlaceholder":function (options) {
                    options = $.extend({
                        placeholderColor:'#7F7F7F',
                        isUseSpan:true,
                        onInput:true
                    }, options);
        			
                    $(this).each(function () {
                        var _this = this;
                        var supportPlaceholder = 'placeholder' in document.createElement('input');
                        if (!supportPlaceholder) {
                            var defaultValue = $(_this).attr('placeholder');
                            var defaultColor = $(_this).css('color');
                            if (options.isUseSpan == false) {
                                $(_this).focus(function () {
                                    var pattern = new RegExp("^" + defaultValue + "$|^$");
                                    pattern.test($(_this).val()) && $(_this).val('').css('color', defaultColor);
                                }).blur(function () {
                                        if ($(_this).val() == defaultValue) {
                                            $(_this).css('color', defaultColor);
                                        } else if ($(_this).val().length == 0) {
                                            $(_this).val(defaultValue).css('color', options.placeholderColor)
                                        }
                                    }).trigger('blur');
                            } else {
                                var $imitate = $('<span class="wrap-placeholder" style="position:absolute;display:inline-block; overflow:hidden; color:'+options.placeholderColor+'; width:'+$(_this).width()+'px; height:'+$(_this).height()+'px;">' + (defaultValue==undefined?"":defaultValue) + '</span>');

                                $imitate.css({
        							'text-indent':$(_this).css('text-indent'),
                                    'margin-left':$(_this).css('margin-left'),
                                    'margin-top':$(_this).css('margin-top'),
        							'text-align':'left',
                                    'font-size':$(_this).css('font-size'),
                                    'font-family':$(_this).css('font-family'),
                                    'font-weight':$(_this).css('font-weight'),
                                    'padding-left':parseInt($(_this).css('padding-left')) + 2 + 'px',
                                    'line-height':_this.nodeName.toLowerCase() == 'textarea' ? $(_this).css('line-weight') : $(_this).outerHeight() + 'px',
                                    'padding-top':_this.nodeName.toLowerCase() == 'textarea' ? parseInt($(_this).css('padding-top')) + 2 : 0
                                });
                                $(_this).before($imitate.click(function () {
                                    $(_this).trigger('focus');
                                }));

                                $(_this).val().length != 0 && $imitate.hide();

                                if (options.onInput) {
                                    var inputChangeEvent = typeof(_this.oninput) == 'object' ? 'input' : 'propertychange';
                                    $(_this).bind(inputChangeEvent, function () {
                                        $imitate[0].style.display = $(_this).val().length != 0 ? 'none' : 'inline-block';
                                    });
                                } else {
                                    $(_this).focus(function () {
                                        $imitate.hide();
                                    }).blur(function () {
                                            /^$/.test($(_this).val()) && $imitate.show();
                                        });
                                }
                            }
                        }
                    });
                    return this;
                }
            });
        })(jQuery);
