(function() {
	$.widget("ui.optionMagr", {
		options : {
			s : {
				idContainer : '.value-container'
			},
			tag : {
				optionHiddenCtx : 'option-hidden-container',
				optionHidden : 'option-hidden',
				optionCtx : 'option-container',
				optionList : 'option-list',
				optionItem : 'option-item',
				optionHook : 'option-hook',
				optionName : 'option-name',
				value : 'value'
			},
			width : 100,
			multiple : true,
			checkAll : true,
			selectable : true,
			onAddValue : function(id, name) {
			},
			onRemoveValue : function(id, name) {
			},
			listSubOption : function(oid) {
			},
			param : {}
		},
		htmlOptionCtx : function(oid) {
			return '' + //			
			'<div ' + std.flag(this.options.tag.optionCtx, oid) + '></div>';
		},
		htmlOptionFrame : function(oid, name) {
			return '' + //			
			'<div class="c_con_tit c_m10">' + name + '</div>' + //
			'<div class="c_con_list c_cb c_m10 c_f12"' + std.flag(this.options.tag.optionList, oid) + '></div>' + //
			'<div class="c_m10 c_cb c_con_sep"></div>';//
		},
		htmlOption : function(oid, name, selected) {
			var tag = this.options.tag;
			return '' + //
			'<div class="c_tab_choice c_fl"' + std.flag(tag.optionItem, oid) + ' selct="' + (selected ? 'true' : 'false') + '">' + //
			'	<div class="c_sprite c_choice_icon ' + (selected ? 'active' : '') + ' c_fl"' + std.flag(tag.optionHook, oid) + '></div>' + //
			'	<div class="c_fl c_tag_active c_w' + this.options.width + '" title="' + name + '"' + std.flag(tag.optionName, oid) + '>' + name + '</div>' + //
			'	<div class="c_cb"></div>' + //
			'</div>';
		},
		clearSubOption : function(oid) {
			$(this.element).find(std.find(this.options.tag.optionCtx, oid).parent().find(std.findTag(this.options.tag.optionCtx))).empty();
		},
		initValue : function(options) {
			this.clearValue();

			var outer = this;
			var tag = outer.options.tag;

			var setHidden = function(option) {
				if (util.isNull(option)) {
					return;
				}
				var parentId = null;
				if (util.isNotNull(option.parent)) {
					setHidden(option.parent);
					parentId = option.parent.id;
				} else {
					parentId = 0;
				}
				var hiddenCtx = $(outer.element).find(std.findTag(tag.optionHiddenCtx));
				if (hiddenCtx.find(std.find(tag.optionHidden, option.id)).size() <= 0) {
					hiddenCtx.find(std.find(tag.optionHidden, parentId)).append('<div' + std.flag(tag.optionHidden, option.id) + ' name="' + option.name + '"></div>');//添加隐藏结构
				}
			};
			$(options).each(function(i, option) {
				var hiddenCtx = $(outer.element).find(std.findTag(tag.optionHiddenCtx));
				if (hiddenCtx.find(std.find(tag.optionHidden, option.id)).size() <= 0) {
					setHidden(option);
				}
			});
			$(options).each(function(i, option) {
				outer.addValue(option.id);
			});
		},
		clearValue : function() {
			var value = this.getValue();
			var outer = this;
			$(value).each(function(ii, obj) {
				outer.removeValue(obj.id);
			});

			var tag = this.options.tag;
			$(this.element).find(std.findTag(tag.optionHiddenCtx)).find(tag.optionHidden, 0).empty();//清除隐藏值
		},
		getValue : function() {
			var value = [];
			$(this.element).find(this.options.s.idContainer).find('li').each(function() {
				value.push({
					id : std.oid(this),
					name : $(this).attr('name'),
					site: $(this).attr("site")
				});
			});
			return value;
		},
		addValue : function(id) {
			var s = this.options.s;
			var tag = this.options.tag;

			var outer = this;
			$(this.element).find(std.selector(tag.optionHiddenCtx)).find(std.selector(tag.optionHidden, id)).find(std.selector(tag.optionHidden) + "[selct=true]").each(function() {//从隐藏值中找到已经被选中的子项目值
				//outer.removeValue(std.oid(this));//移除已选项
			});

			$(this.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, id)).attr('selct', 'true');//设置隐藏值为选中

			$(this.element).find(std.findTag(tag.optionCtx)).find(std.find(tag.optionItem, id)).attr('selct', "true");//设置为已选
			$(this.element).find(std.findTag(tag.optionCtx)).find(std.find(tag.optionHook, id)).addClass('active');//高亮选项

			var name = $(this.element).find(std.selector(tag.optionHiddenCtx)).find(std.selector(tag.optionHidden, id)).attr('name');//获取选项名称
			$(this.element).find(s.idContainer).append('<li' + std.flag(tag.value, id) + ' name="' + name + '" site="'+this.options.site+'">' + name + '<i class="c_sprite"></i></li>');//添加选项

			//绑定移除事件
			$(this.element).find(s.idContainer).find(std.find(tag.value, id)).click(function() {
				if (!outer.options.selectable) {
					return;
				}
				outer.removeValue(id);
			});
			if (this.options.onAddValue) {
				this.options.onAddValue(id, name);
			}
		},
		removeValue : function(id) {
			var s = this.options.s;
			var tag = this.options.tag;

			$(this.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, id)).attr('selct', 'false');//设置隐藏值为未选中

			$(this.element).find(std.findTag(tag.optionCtx)).find(std.find(tag.optionItem, id)).attr('selct', "false");//设置为未选
			$(this.element).find(std.findTag(tag.optionCtx)).find(std.find(tag.optionHook, id)).removeClass('active');//取消高亮选项

			var name = $(this.element).find(s.idContainer).find(std.find(tag.value, id)).attr('name');
			$(this.element).find(s.idContainer).find(std.find(tag.value, id)).remove();//删除选项

			if (this.options.onRemoveValue) {
				this.options.onRemoveValue(id, name);
			}
		},
		showSubOption : function(oid, skipSelectByNotFoundSubOption) {
			if ($(this.element).find(this.options.s.idContainer).find('li[oid=' + oid + ']').size() > 0) {//已选存在，高亮当前项，不需要子项
				this.selectOption(oid);
				return;
			}

			this.clearSubOption(oid);//清除子选项(该OID的父项下的所有子项容器)

			var options = this.options.listSubOption(oid);//获取子项
			if ($(options).size() <= 0) {
				if (util.isNull(skipSelectByNotFoundSubOption) || !skipSelectByNotFoundSubOption) {
					if(oid != 0) {//0是不存在的顶级选项，不需要选中
						this.selectOption(oid, true);
					}
				}
				return;
			}

			//子项处理
			var outer = this;
			var tag = this.options.tag;
			var value = this.getValue();//获取已选选项
			$(this.element).find(std.find(tag.optionCtx, oid)).show();//显示子选项容器

			var name = $(this.element).find(std.selector(tag.optionHiddenCtx)).find(std.selector(tag.optionHidden, oid)).attr('name');//获取选项名称
			$(this.element).find(std.find(tag.optionCtx, oid)).html(this.htmlOptionFrame(oid, name && name.trim() != "" ? name : "选项"));//渲染框架

			//渲染子选项
			$(options).each(function(i, option) {
				var selected = false;
				$(value).each(function(ii, obj) {
					if (obj.id == option.id)
						selected = true;//渲染已选主题
				});
				$(outer.element).find(std.find(tag.optionList, oid)).append(outer.htmlOption(option.id, option.name, selected));//渲染子选项
				$(outer.element).find(std.find(tag.optionCtx, oid)).append(outer.htmlOptionCtx(option.id));//渲染子选项容器
				if ($(outer.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, oid)).find(std.find(tag.optionHidden, option.id)).size() <= 0) {
					$(outer.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, oid))//
					.append('<div' + std.flag(tag.optionHidden, option.id) + ' name="' + option.name + '"></div>');//添加隐藏结构
				}
			});
			//绑定选择事件
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionHook)).each(function() {
				$(this).click(function(e) {
					outer.selectOption(std.oid(this));
					e.stopPropagation();
				});
			});
			//绑定显示事件
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionName)).each(function() {
				$(this).click(function(e) {
					//outer.showSubOption(std.oid(this));
					e.stopPropagation();
				});
			});

			this.refreshScrollbar();//刷新滚动条
		},
		hideSubOption : function(oid) {
			$(this.element).find(std.find(this.options.tag.optionCtx, oid)).empty();
			this.refreshScrollbar();//刷新滚动条
		},
		selectOption : function(oid, skipShowSubOptionByCancelSelect) {
			if (!this.options.selectable) {
				return;
			}

			var tag = this.options.tag;

			if ($(this.element).find(std.findTag(tag.optionCtx)).find(std.find(tag.optionItem, oid)).attr('selct') == "true") {//是否选中
				this.removeValue(oid);//移除已选项
				if (util.isNull(skipShowSubOptionByCancelSelect) || !skipShowSubOptionByCancelSelect) {
					//this.showSubOption(oid, true);//显示子选项
				}

			} else {
				if (!this.options.multiple) {
					this.clearValue();
				}
				this.addValue(oid);//添加已选项
				//this.hideSubOption(oid);//移除子选项
			}
		},
		selectAllSubOption : function(oid) {
			if (!this.options.selectable) {
				return;
			}

			if (!this.options.multiple) {
				return;
			}
			var outer = this;
			var tag = this.options.tag;
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionItem)).each(function(i, item) {
				if ($(item).attr('selct') == "false") {//是否选中
					outer.addValue(std.oid(item));//添加已选项
					//outer.hideSubOption(std.oid(item));//移除子选项
				}
			});
		},
		cancelAllSubOption : function(oid) {
			if (!this.options.selectable) {
				return;
			}

			var outer = this;
			var tag = this.options.tag;
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionItem)).each(function(i, item) {
				outer.removeValue(std.oid(item));//移除已选项
			});
		},
		submit : function() {
			return this.getValue();
		},
		getParam : function(prop) {
			return this.options[prop];
		},
		setParam : function(prop, value) {
			this.options[prop] = value;
		},
		setSelectable : function(value) {
			this.options.selectable = value;
		},
		refreshScrollbar : function() {
			$(this.element).find('.scrollbar-ctx').tinyscrollbar();
		},
		showSearchOption : function(options) {
			//子项处理
			var oid = 0;
			var outer = this;
			var tag = this.options.tag;
			var value = this.getValue();//获取已选选项
			$(this.element).find(std.find(tag.optionCtx, oid)).show();//显示子选项容器

			var name = $(this.element).find(std.selector(tag.optionHiddenCtx)).find(std.selector(tag.optionHidden, oid)).attr('name');//获取选项名称
			$(this.element).find(std.find(tag.optionCtx, oid)).html(this.htmlOptionFrame(oid, name && name.trim() != "" ? name : "选项"));//渲染框架

			//渲染子选项
			$(options).each(function(i, option) {
				var selected = false;
				$(value).each(function(ii, obj) {
					if (obj.id == option.id)
						selected = true;//渲染已选主题
				});
				$(outer.element).find(std.find(tag.optionList, oid)).append(outer.htmlOption(option.id, option.name, selected));//渲染子选项
				$(outer.element).find(std.find(tag.optionCtx, oid)).append(outer.htmlOptionCtx(option.id));//渲染子选项容器
				if ($(outer.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, oid)).find(std.find(tag.optionHidden, option.id)).size() <= 0) {
					$(outer.element).find(std.findTag(tag.optionHiddenCtx)).find(std.find(tag.optionHidden, oid))//
					.append('<div' + std.flag(tag.optionHidden, option.id) + ' name="' + option.name + '"></div>');//添加隐藏结构
				}
			});
			//绑定选择事件
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionHook)).each(function() {
				$(this).click(function(e) {
					outer.selectOption(std.oid(this));
					e.stopPropagation();
				});
			});
			//绑定显示事件
			$(this.element).find(std.find(tag.optionCtx, oid)).find(std.findTag(tag.optionName)).each(function() {
				$(this).click(function(e) {
					//outer.showSubOption(std.oid(this));
					e.stopPropagation();
				});
			});

			this.refreshScrollbar();//刷新滚动条
		}
	});
	
	searchTopic = function(keyword) {
		analyze.searchTopic('keywords=' + (util.isNotBlank(keyword) ? keyword : ''), {
			success : function(topics) {
				renderTopic(topics);
			}
		});
	};
	renderTopic = function(topics) {
		$(std.findTag("option-container")).empty();
		var popupBox = std.selector("popup-box", "topic");
		$(popupBox).optionMagr('showSearchOption', topics);
	};
	
	$("#searchKeywords").keydown(function(e) {
		if (e && e.keyCode == 13) {
			$("#searchTopic").click();
		}
	});
	
	$("#searchTopic").click(function(){
		var keywords = $.trim($("#searchKeywords").val());
		searchTopic(keywords);
	});
	
})();