package com.cyyun.fm.setting.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.service.RoleService;
import com.cyyun.authim.service.UserGroupService;
import com.cyyun.authim.service.bean.RoleBean;
import com.cyyun.authim.service.bean.UserGroupBean;
import com.cyyun.authim.service.bean.UserGroupQueryBean;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;

/**
 * @author baifan 2015-1-15
 */
@Controller(value = "userGroupController")
@RequestMapping(value = { "userGroup" }, method = {
		RequestMethod.POST,
		RequestMethod.GET })
public class UserGroupManagerController extends BaseController {
	
	@Autowired
	private UserGroupService userGroupService;
	
	@Value("${system.code}")
	private String systemCode;
	
	@Autowired
	private RoleService roleService;
	
	
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserGroupManagerController.class.getName());


	@RequestMapping(value = { "list" })
	@ResponseBody
	public MessageBean queryUserGroupList(HttpServletRequest request, HttpServletResponse response, ModelMap out, UserGroupQueryBean userGroupBean) {
		try {
			if (userGroupBean.getName() != null&&!"".equals(userGroupBean.getName()))
			userGroupBean.setName(CyyunSqlUtil.dealSql(userGroupBean.getName()));
			PageInfoBean<UserGroupBean> page = this.userGroupService.queryUserGroupByPage(userGroupBean);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询用户组成功", page);
		} catch (Exception e) {
			e.printStackTrace();
			return buildMessage(MESSAGE_TYPE_ERROR, "查询用户组失败");
		}
	}
	
	@RequestMapping(value = { "index" })
	public String queryUserGroup(HttpServletRequest request, HttpServletResponse response, ModelMap out) {

		return "system/userGroupManager";

	}
	
	
	@RequestMapping(value = { "saveUserGroup" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void saveUserGroup(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------saveUserGroup------begin");
		String userGroupName = request.getParameter("userGroupName");
		String userGroupId = request.getParameter("userGroupId");
		String remark = request.getParameter("remark");
		JSONObject json = new JSONObject();
		try {
			if(userGroupName!=null&&!"".equals(userGroupName)){
				UserGroupBean bean=new UserGroupBean();
				bean.setName(userGroupName);
				bean.setRemark(remark);
				if("".equals(userGroupId)||userGroupId==null){
					userGroupService.addUserGroup(bean);
				}else{
					bean.setId(Integer.parseInt(userGroupId));
					userGroupService.updateUserGroup(bean);
				}
				
			}
			
			json.put("result", "success");
			logger.info("------ saveUserGroup info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "getUserGroupRole" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void getUserGroupRole(HttpServletRequest request, HttpServletResponse response) {
		String userGroupId=request.getParameter("userGroupId");
		JSONObject json = new JSONObject();
		try {
			List<RoleBean> roleList=null;
			if(userGroupId!=null&&!"".equals(userGroupId)){
				roleList=roleService.listRoleByUserGroupId(Integer.parseInt(userGroupId), systemCode);
			}
			json.put("result", "success");
			json.put("roleList", JSONArray.toJSON(roleList));
		} catch (Exception e) {
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);
	}


	@RequestMapping(value = { "deleteUserGroup" })
	public void deleteUserGroup(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			int id = Integer.valueOf(request.getParameter("userGroupId"));
			userGroupService.deleteUserGroup(id);
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "删除失败");
		}
		outJsonString(response, json);
	}
	
	@RequestMapping(value = { "authUserGroupRole" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void authUserGroupRole(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------authUserGroupRole------begin");
		String userGroupId = request.getParameter("userGroupId");
		String roleIds = request.getParameter("roleIds");
		JSONObject json = new JSONObject();
		try {
			if(roleIds!=null&&!"".equals(roleIds)){
				String u[]=roleIds.split(",");
				Integer[] ids=new Integer[u.length];
				for (int i = 0; i < u.length; i++) {
					ids[i]=Integer.parseInt(u[i]);
				}
				userGroupService.updateUserGroupRoles(Integer.parseInt(userGroupId), ids);
			}
			json.put("result", "success");
			logger.info("------ authUserGroupRole info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "getUserGroup" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void getUserGroup(HttpServletRequest request, HttpServletResponse response) {
		String userid=request.getParameter("userid");
		JSONObject json = new JSONObject();
		try {
			List<UserGroupBean> list=new ArrayList<UserGroupBean>();
			if(userid!=null&&!"".equals(userid)){
				list=userGroupService.listUserGroupByUserId(Integer.parseInt(userid));
			}
			json.put("result", "success");
			json.put("userGroupList", JSONArray.toJSON(list));
		} catch (Exception e) {
			 e.getMessage();
			json.put("result","获取用户组失败");
		}
		outJsonString(response, json);
	}
	
	@RequestMapping(value = { "queryAllUserGroup" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public void queryAllUserGroup(HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject json = new JSONObject();
		try {
			List<UserGroupBean> list=userGroupService.listAllUserGroup();
			json.put("result", "success");
			json.put("userGroupList", JSONArray.toJSON(list));
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "获取用户组失败");
		}
		outJsonString(response, json);
	}

	
	 public void outJsonString(HttpServletResponse response, JSONObject json)
	    {
	        response.setContentType("text/javascript;charset=UTF-8");
	        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
	        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	        response.setHeader("Pragma", "no-cache");
	        try
	        {
	            PrintWriter out = response.getWriter();
	            out.write(json.toString());
	            out.close();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    }
	
}