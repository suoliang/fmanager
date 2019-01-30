/**
 * @author GUQIANG
 */
var popup = popup || {};
(function() {
	var shareUser = {};
	var shareUserBox = "[tag=popup-box][oid=user-picker]";

	var refreshScrollbar = function() {
		$(shareUserBox).find('.scrollbar-org').tinyscrollbar();
		$(shareUserBox).find('.scrollbar-user').tinyscrollbar();
		$(shareUserBox).find('.scrollbar-selected-user').tinyscrollbar();
	}

	popup.openUserPicker = function(userIds, onSubmit) {
		$.box(shareUserBox, {
			onOpen : function() {
				shareUser.init(userIds);
			}
		}, {
			submit : {
				dom : [ shareUserBox + " .box-submit" ],
				fun : function() {
					if (onSubmit) {
						onSubmit(shareUser.submit());
					}
				}
			},
			close : {
				dom : [ shareUserBox + " .box-close", shareUserBox + " .box-cancel" ]
			}
		});
	};

	shareUser.init = function(userIds) {
		//初始化页面
		$(shareUserBox).find('.list-selected-user').empty();
		$(shareUserBox).find('input[name=keyword]').val('用户搜索');
		shareUser.showSubOrg(null);
		shareUser.showUser(null, userIds);

		refreshCheckbox();

		//初始化事件
		util.setInputPrompt('input[name=keyword]', '用户搜索', {
			keydown : function(e) {
				if (e && e.keyCode == 13) {
					$(shareUserBox).find('.btn-search-user').click();
				}
				e.stopPropagation();
			}
		});
		$(shareUserBox).find('.btn-search-user').unbind().click(function() {
			var keyword = $(shareUserBox).find('input[name=keyword]').val();
			if(keyword == '用户搜索'){
				keyword='';
			};
			shareUser.searchUser(keyword, userIds);
		});
		
		$('.c_scrol_viewport').scrollUnique();
	}

	//选择组织
	shareUser.clickOrg = function(id, checked) {
		popup.listUser('orgId=' + id, {
			success : function(users) {
				if (checked) {
					$(users).each(function(i, item) {
						shareUser.addSelectedUser(item.id, item.realName);
					});
					refreshCheckbox();
				} else {
					$(users).each(function(i, item) {
						shareUser.delSelectedUser(item.id);
					});
					refreshCheckbox();
				}
			}
		});
	};

	//选择用户
	shareUser.clickUser = function(id, name) {
		shareUser.addSelectedUser(id, name);
		refreshCheckbox();
	};

	//取消用户
	shareUser.clickSelectedUser = function(id) {
	};

	shareUser.submit = function() {
		var userIds = [];
		$(shareUserBox).find(std.findTag('item-selected-user')).each(function(i, item) {
			userIds.push(std.oid(item));
		});
		return userIds;
	};

	shareUser.showSubOrg = function(orgId) {
		popup.listOrganization('orgId=' + (util.isNotBlank(orgId) ? orgId : ''), {
			success : function(orgs) {
				//render
				var currentOrgId = orgId ? orgId : 0;
				var orgListElmt = $(shareUserBox).find(std.find('list-sub-org', currentOrgId));
				orgListElmt.empty();
				$(orgs).each(function(i, item) {
					var level = $(shareUserBox).find(std.find('list-sub-org', currentOrgId)).attr('level');
					level = parseInt(level) + 1;
					orgListElmt.append('<div' + std.flag('item-org', item.id) + ' status="close" class="c_org c_f12 c_cb">' + //
					'	<div class="c_fl c_ml5 c_org_check "><input' + std.flag('btn-select-org', item.id) + ' type="checkbox"></div>' + //
					'	<div class="c_org_select">' + //
					'		<div class="c_ml' + level + '0">' + //
					'			<div' + std.flag('btn-show-org', item.id) + ' class="c_fl c_sprite c_icon_open c_mt5 c_ml5"></div>' + //
					'			<div class="c_fl c_sprite c_icon_org c_mt5"></div>' + //
					'			<div' + std.flag('btn-show-user', item.id) + ' class=" c_ml45 c_omit" title="' + item.name + '">' + item.name + '</div>' + //
					'		</div>' + //
					'	</div>' + //
					'	<div' + std.flag('list-sub-org', item.id) + ' level="' + level + '"></div>' + //
					'</div>');
				});
				orgListElmt.attr('loaded', 'loaded');
				refreshScrollbar();

				//初始化事件
				$(shareUserBox).find(std.findTag('btn-show-user')).unbind().click(function() {
					shareUser.showUser(std.oid(this), shareUser.submit());
				});
				$(shareUserBox).find(std.findTag('btn-show-org')).unbind().click(function() {
					var orgListElmt = $(shareUserBox).find(std.find('list-sub-org', std.oid(this)));
					if (orgListElmt.attr('loaded') != 'loaded') {
						shareUser.showSubOrg(std.oid(this));
					}
					refreshCheckbox();

					if (orgListElmt.attr('status') == 'open') {
						orgListElmt.hide();
						orgListElmt.attr('status', 'close');
						$(this).removeClass('c_icon_closd');
						$(this).addClass('c_icon_open');

					} else {
						orgListElmt.show();
						orgListElmt.attr('status', 'open');
						$(this).removeClass('c_icon_open');
						$(this).addClass('c_icon_closd');
					}
				});
				$(shareUserBox).find(std.findTag('btn-select-org')).unbind().click(function() {
					shareUser.clickOrg(std.oid(this), $(this).prop('checked'));
				});
			}
		});
	};

	shareUser.showUser = function(orgId, userIds) {
		popup.listUser('orgId=' + (util.isNotBlank(orgId) ? orgId : ''), {
			success : function(users) {
				shareUser.renderUser(users, userIds);
			}
		});
	};

	shareUser.searchUser = function(keyword, userIds) {
		popup.searchUser('keyword=' + (util.isNotBlank(keyword) ? keyword : ''), {
			success : function(users) {
				shareUser.renderUser(users, userIds);
			}
		});
	};

	shareUser.renderUser = function(users, userIds) {
		//render
		$(shareUserBox).find('.list-user').empty();
		$(users).each(function(i, item) {
			var className = '';
			if (item.id == sys.getLoginUserId()) {
				className = 'c_none';
			}
			if ($(shareUserBox).find(std.find('item-selected-user', item.id)).size() > 0) {
				className = 'c_none';
			}
			$(shareUserBox).find('.list-user').append('<div' + std.flag('item-user', item.id) + ' name="' + item.realName + '" class="c_org_msg c_pl20 c_tl ' + className + '">' + item.realName + '</div>');
		});
		refreshScrollbar();
		//add selected user
		$(userIds).each(function(i, userId) {
			var name = $(shareUserBox).find(std.find('item-user', userId)).attr('name');
			if (util.isNotBlank(name)) {
				shareUser.addSelectedUser(userId, name);
			}
		});
		//初始化事件
		$(shareUserBox).find(std.findTag('item-user')).unbind().click(function() {
			shareUser.clickUser(std.oid(this), $(this).attr('name'));
		});
	}

	shareUser.addSelectedUser = function(id, name) {
		if (id == sys.getLoginUserId()) {
			return;
		}
		//render
		if ($(shareUserBox).find(std.find('item-selected-user', id)).size() == 0) {
			$(shareUserBox).find(std.find('item-user', id)).hide();
			$(shareUserBox).find('.list-selected-user').append('<div' + std.flag('item-selected-user', id) + ' class="c_org_msg c_cb c_pl20">' + name + '</div>');
		}
		refreshScrollbar();
		//初始化事件
		$(shareUserBox).find(std.find('item-selected-user', id)).unbind().click(function() {
			shareUser.delSelectedUser(std.oid(this));
			refreshCheckbox();
		});
	};

	shareUser.delSelectedUser = function(userId) {
		if (userId == sys.getLoginUserId()) {
			return;
		}
		//render
		$(shareUserBox).find(std.find('item-user', userId)).show();
		$(shareUserBox).find(std.find('item-selected-user', userId)).remove();
		refreshScrollbar();
	};

	var refreshCheckbox = function() {
		$(std.findTag('item-org')).each(function() {
			var orgId = std.oid(this) != 0 ? std.oid(this) : '';
			popup.listUser('orgId=' + orgId, {
				success : function(users) {
					if ($(users).size() == 0)
						return;

					var checkAll = true;
					$(users).each(function(i, item) {
						if ($(shareUserBox).find(std.find('item-selected-user', item.id)).size() == 0 && item.id != sys.getLoginUserId()) {
							checkAll = false;
						}
					});
					if (checkAll) {
						std.find('btn-select-org', orgId).prop('checked', true);
					} else {
						std.find('btn-select-org', orgId).prop('checked', false);
					}
				}
			}, false);
		});
	};
	//子元素scroll完父元素容器不跟随滚动
	$.fn.scrollUnique = function() {
	    return $(this).each(function() {
	        var eventType = 'mousewheel';
	        if (document.mozHidden !== undefined) {
	            eventType = 'DOMMouseScroll';
	        }
	        $(this).on(eventType, function(event) {
	            // 一些数据
	            var scrollTop = this.scrollTop,
	                scrollHeight = this.scrollHeight,
	                height = this.clientHeight;

	            var delta = (event.originalEvent.wheelDelta) ? event.originalEvent.wheelDelta : -(event.originalEvent.detail || 0);        

	            if ((delta > 0 && scrollTop <= delta) || (delta < 0 && scrollHeight - height - scrollTop <= -1 * delta)) {
	                // IE浏览器下滚动会跨越边界直接影响父级滚动，因此，临界时候手动边界滚动定位
	                this.scrollTop = delta > 0? 0: scrollHeight;
	                // 向上滚 || 向下滚
	                event.preventDefault();
	            }        
	        });
	    });	
	};
	
})();