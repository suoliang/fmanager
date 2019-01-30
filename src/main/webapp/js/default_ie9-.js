var cyyun;
cyyun={
    placeholderChange:{
        init:function(){
            supportPlaceholder='placeholder'in document.createElement('input'),
                placeholder=function(input){
                    var type=$(this).attr("type");

                    var text = input.attr('placeholder'),
                        defaultValue = input.defaultValue;
                    if(!defaultValue){

                        input.val(text).addClass("dk_phcolor");


                    }
                    input.focus(function(){
                        if(input.val() == text){

                            $(this).val("");
                        }
                    });
                    input.blur(function(){
                        if(input.val() == ""){

                            $(this).val(text).addClass("dk_phcolor");

                        }
                    });
                    
                    input.keydown(function(){
                        $(this).removeClass("dk_phcolor");
                    });
                };
           
            if(!supportPlaceholder){
                $('input').each(function(){
                    text = $(this).attr("placeholder");
                    if($(this).attr("type") == "text"||$(this).attr("type") == "password"){

                        placeholder($(this));
                    }
                });
            }
        }
        },
};

$(function(){
    cyyun.placeholderChange.init();
});
