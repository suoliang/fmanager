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
import com.cyyun.authim.service.PermissionService;
import com.cyyun.authim.service.RoleService;
import com.cyyun.authim.service.bean.PermissionBean;
import com.cyyun.authim.service.bean.RoleBean;
import com.cyyun.authim.service.bean.RoleQueryBean;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.constant.DataCode;
import com.cyyun.customer.service.CustomerConfigService;

@Controller("roleController")
@RequestMapping(value = { "roleManage" }, method = { RequestMethod.POST,
		RequestMethod.GET })
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private CustomerConfigService customerConfigService;
	@Autowired
	private RoleControllerSupport roleControllerSupport;
	
	
	@Value("${system.code}")
	private String systemCode;

	private org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(RoleController.class.getName());

	// <editor-fold defaultstate="collapsed" desc="ajax方式返回输出信息">
	public void outJsonString(HttpServletResponse response, JSONObject json) {
		response.setContentType("text/javascript;charset=UTF-8");
		response.setHeader("Cache-Control",
				"no-store, max-age=0, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter out = response.getWriter();
			out.write(json.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// </editor-fold>

	@RequestMapping(value = { "list" })
	public String list(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		return "system/roleManager";
	}

	@RequestMapping(value = { "listAllRole" })
	@ResponseBody
	public MessageBean listAllRole(HttpServletRequest request, HttpServletResponse response, ModelMap out,RoleQueryBean roleQueryBean) {
		logger.info("get roleList info-------------begin--------------systemCode:"
				+ systemCode);
		try {
			String currtPage = request.getParameter("currentPage");
			String pageSize = request.getParameter("pageSize");
			if (currtPage == null) {
				currtPage = "1";
			}
			if (pageSize == null) {
				pageSize = "20";
			}
			
			Integer[] roleIds=customerConfigService.findDataIdsByCid(FMContext.getCurrent().getCustomerId(), DataCode.ROLE);
			if(roleIds==null||roleIds.length==0){
				roleIds=new Integer[]{-1};
			}
			RoleQueryBean queryBean=new RoleQueryBean();
			queryBean.setNeedPaging(true);
			queryBean.setName(CyyunSqlUtil.dealSql(roleQueryBean.getName()));
			queryBean.setPageNo(Integer.parseInt(currtPage));
			queryBean.setPageSize(Integer.parseInt(pageSize));
			queryBean.setSystemCode(systemCode);
			queryBean.setRoleIds(roleIds);
			queryBean.setCreaterId(FMContext.getCurrent().getCustomerId());
			PageInfoBean<RoleBean> page=roleService.queryRoleByPage(queryBean);
			page.setPageSize(Integer.parseInt(pageSize));
			logger.info("get roleList info-------------success--------------"+page.getTotalRecords());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询角色成功", page);
		} catch (Exception e) {
			e.printStackTrace();
			return buildMessage(MESSAGE_TYPE_ERROR, "查询角色失败");
		}

	}

	@RequestMapping(value = { "queryPerm" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public void queryPerm(HttpServletRequest request,
			HttpServletResponse response) {

		String roleId = request.getParameter("roleId");
		JSONObject json = new JSONObject();
		try {
			List<PermissionBean> permList = new ArrayList<PermissionBean>();
			if (roleId != null && !"".equals(roleId)) {
				permList = permissionService.listPermissionByRoleId(Integer
						.parseInt(roleId));
			}
			json.put("result", "success");
			json.put("permList", JSONArray.toJSON(permList));
		} catch (Exception e) {
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "queryAllRole" })
	public void queryAllRole(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			PageInfoBean<RoleBean> page = roleControllerSupport.queryAllRole();
			json.put("result", "success");
			json.put("roleList", JSONArray.toJSON(page.getData()));
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "获取角色失败");
		}
		outJsonString(response, json);
	}

	@RequestMapping(value = { "toAddRole" })
	public String toAddRole(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		return "system/addRole";
	}

	@RequestMapping(value = { "showPerm" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public void showPerm(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		// load role
		JSONObject json = new JSONObject();
		try {
			String parentId = request.getParameter("parentId");
			String roleId=request.getParameter("roleId");
			List<PermissionBean> list = permissionService
					.listPermissionByParentId(Integer.parseInt(parentId),
							systemCode);
			if(roleId!=null&&!"".equals(roleId)){
				RoleBean role=roleService.findRole(Integer.parseInt(roleId));
				json.put("role", role);
			}
			
			json.put("result", "success");
			json.put("permList", JSONArray.toJSON(list));
			
		} catch (Exception e) {
			e.getMessage();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}

	@RequestMapping(value = { "addorUpdateRole" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public void addandUpdateRole(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		String roleid = request.getParameter("roleId");
		String roleCode = request.getParameter("roleCode");
		String roleType = request.getParameter("roleType");
		String roleName = request.getParameter("roleName");
		String remark = request.getParameter("remark");
		String permIds = request.getParameter("permIds");
		JSONObject json = new JSONObject();
		try {
			RoleBean bean = new RoleBean();
			bean.setCode(roleCode);
			bean.setName(roleName);
			bean.setRemark(remark);
			String[] systemCodes=new String[]{systemCode};
			bean.setSystemCode(systemCodes);
			bean.setType(Integer.parseInt(roleType));
			String[] pids=permIds.split(",");
			Integer[] ids=new Integer[pids.length];
			for (int i = 0; i < pids.length; i++) {
				String pid=pids[i];
				if(!"".equals(pid)){
					ids[i]=Integer.parseInt(pid);
				}
				
			}
			bean.setPermIds(ids);
			if (roleid != null && !"".equals(roleid)) {
				bean.setId(Integer.parseInt(roleid));
				roleService.updateRole(bean);
			} else {
				bean.setCreaterId(FMContext.getCurrent().getCustomerId());
				roleService.addRole(bean);
			}
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}

	@RequestMapping(value = { "toUpdateRole" })
	public String toUpdateRole(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		String roleId=request.getParameter("roleId");
		RoleBean role=roleService.findRole(Integer.parseInt(roleId));
		out.put("role", role);
		return "system/updateRole";
	}


	@RequestMapping(value = { "deleteRole" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public void doDeleteRole(HttpServletRequest request,
			HttpServletResponse response, ModelMap out) {
		String roleId = request.getParameter("roleId");
		JSONObject json = new JSONObject();
		try {
			roleService.deleteRole(Integer.parseInt(roleId));
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "删除失败");
		}
		outJsonString(response, json);
	}

}
