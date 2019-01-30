package com.cyyun.fm.setting.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.service.OrganizationService;
import com.cyyun.authim.service.RoleService;
import com.cyyun.authim.service.UserGroupService;
import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.OrganizationBean;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.authim.service.bean.UserGroupBean;
import com.cyyun.authim.service.bean.UserQueryBean;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustContactBean;
import com.cyyun.customer.service.bean.CustContactQueryBean;
import com.cyyun.customer.service.bean.CustUserBean;
import com.cyyun.message.service.MessageService;
import com.cyyun.message.service.bean.SmsBean;

/**
 * @author baifan 2015-1-15
 */
@Controller(value = "userController")
@RequestMapping(value = { "userMange" }, method = {
		RequestMethod.POST,
		RequestMethod.GET })
public class UserManagerController extends BaseController {
	@Autowired
	private UserService userService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	OrganizationService  organizationService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CustomerConfigService customerConfigService;
	@Autowired
	private MessageService  messageService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private RoleControllerSupport roleControllerSupport;
	@Value("${system.code}")
	private String systemCode;
	
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserManagerController.class.getName());


	@RequestMapping(value = { "queryUser" })
	@ResponseBody
	public MessageBean queryUserList(HttpServletRequest request, HttpServletResponse response, ModelMap out, UserQueryBean userBean) {
		try {
			userBean.setUsername("".equals(userBean.getUsername()) ? null:userBean.getUsername());
			userBean.setRealName("".equals(userBean.getRealName()) ? null:userBean.getRealName());
			userBean.setOrgName("".equals(userBean.getOrgName()) ? null:userBean.getOrgName());
				
			if (userBean.getUsername() != null&&!"".equals(userBean.getUsername()))
				userBean.setUsername(CyyunSqlUtil.dealSql("".equals(userBean.getUsername()) ? null:userBean.getUsername()));
			if (userBean.getRealName()!= null&&!"".equals(userBean.getRealName()))
				userBean.setRealName(CyyunSqlUtil.dealSql("".equals(userBean.getRealName()) ? null:userBean.getRealName()));
			if(userBean.getOrgName()!=null&&!"".equals(userBean.getOrgName())){
				userBean.setOrgName(CyyunSqlUtil.dealSql("".equals(userBean.getOrgName()) ? null:userBean.getOrgName()));
			}
			if(userBean.getRoleId()!=null&&!"".equals(userBean.getRoleId())){
				userBean.setRoleId(userBean.getRoleId());
			}
			List<Integer> userids=customerConfigService.listUidByCid(FMContext.getCurrent().getCustomerId());
			if(userids!=null&&userids.size()>0){
				Integer[] userIds=new Integer[userids.size()];
				userids.toArray(userIds);
				userBean.setUserIds(userIds);
			}else{
				userBean.setUserIds(new Integer[]{-1});
			}
			
			PageInfoBean<UserBean> page = this.userService.queryUserByPage(userBean);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询用户成功", page);
		} catch (Exception e) {
			e.printStackTrace();
			return buildMessage(MESSAGE_TYPE_ERROR, "查询用户失败");
		}
	}
	
	@RequestMapping(value = { "list" })
	public String queryUser(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		return "system/userManager";
	}
	
//	@RequestMapping(value = { "test1" })
//	public String test1(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
//		return "test1";
//	}

	
	@RequestMapping(value = { "changeUserStatus" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void changeUserStatus(HttpServletRequest request, HttpServletResponse response) {
		logger.info("changeUserStatus------------begin");
		String id = request.getParameter("userId");
		String status = request.getParameter("status");
		JSONObject json = new JSONObject();
		try {
			userService.updateUserStatus(Integer.parseInt(id), Integer.parseInt(status));
			json.put("result", "success");
			logger.info("changeUserStatus-------------success");
		} catch (Exception e) {
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "getUserRole" })
	public void getRoleList(HttpServletRequest request, HttpServletResponse response) {
		String userid = request.getParameter("userid");
		JSONObject json = new JSONObject();
		try {
			if (userid != null && !"".equals(userid)) {
				json.put(
						"roleList",
						JSONArray.toJSON(roleService.listRoleByUserId(
								Integer.parseInt(userid), systemCode)));
			} else {
				json.put("roleList",
						JSONArray.toJSON(roleControllerSupport.queryAllRole().getData()));
			}
			json.put("result", MESSAGE_TYPE_SUCCESS);
		} catch (Exception e) {
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);
	}
	
	
	
	@RequestMapping(value = { "getUserOrg" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void getUserOrg(HttpServletRequest request, HttpServletResponse response) {
		String userid=request.getParameter("userid");
		JSONObject json = new JSONObject();
		try {
			List<OrganizationBean> list=new ArrayList<OrganizationBean>();
			if(userid!=null&&!"".equals(userid)){
				list=organizationService.listOrganizationByUser(Integer.parseInt(userid));
			}
			json.put("result", "success");
			json.put("orgList", JSONArray.toJSON(list));
		} catch (Exception e) {
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);
	}


	
	@RequestMapping(value = { "toAddUser" })
	public String toAddUser(HttpServletRequest request, HttpServletResponse response, ModelMap out) {

		return "system/addUser";

	}
	
	@RequestMapping(value = { "addUser" })
	public void addUser(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		try {
			String userName=request.getParameter("userName");
			String password=request.getParameter("password");
			String userType=request.getParameter("userType");
			String realName=request.getParameter("realName");
			String mobile=request.getParameter("mobile");
			String email=request.getParameter("email");
			String remark = request.getParameter("remark");
			String effectTime=request.getParameter("effectTime");
			String expiredTime=request.getParameter("expiredTime");
			String userId=request.getParameter("userId");
			
			UserBean user=null;
			if(userId==null){
				user=new UserBean();
			}else{
				user=userService.findUser(Integer.parseInt(userId));
			}
			if(userName!=null&&!"".equals(userName)){
				user.setUsername(userName.toLowerCase().trim());
			}
			if(userType!=null&&!"".equals(userType)){
				user.setType(Integer.parseInt(userType));
			}
			
			if(realName!=null&&!"".equals(realName)){
				user.setRealName(realName.trim());
			}
			if(password!=null&&!"".equals(password)){
				user.setPassword(password);
			}
			if(mobile!=null&&!"".equals(mobile)){
				user.setMobile(mobile);
			}
			user.setEmail(email);
			user.setRegisterIp(getIPAddress(request));
			user.setRemark(remark);
			SimpleDateFormat format = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
			if(effectTime!=null&&!"".equals(effectTime)){
				user.setEffectiveTime(format.parse(effectTime+" 00:00:00"));
			}
			
			if(expiredTime!=null&&!"".equals(expiredTime)){
				user.setExpiredTime(format.parse(expiredTime+" 00:00:00"));
			}
			
			if(userId==null){
				UserBean u=userService.addUser(user);
				CustUserBean custUser=new CustUserBean();
				custUser.setCustId(FMContext.getCurrent().getCustomerId());
				custUser.setUserId(u.getId());
				customerConfigService.addCustUser(custUser);
			}else{
				userService.updateUser(user);
				/**更新IM客户联系人相应的数据*/
				CustContactQueryBean query = new CustContactQueryBean ();
				query.setCustId(FMContext.getCurrent().getCustomerId());
				query.setUserIds(new Integer[]{user.getId()});
				CustContactBean bean = customerService.findCustContactByUserId(query);
				if (ObjectUtils.notEqual(bean, null)) {
					bean.setRealName(realName.trim());
					bean.setMobile(mobile.trim());
					bean.setEmail(email);
					bean.setRemark(remark);
					
					customerService.updateCustContactGeneralInfo(bean);
				}

			}
			
			json.put("result", "success");
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "changeAllUserStatus" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void changeAllUserStatus(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------changeAllUserStatus------begin");
		String userIds = request.getParameter("userIds");
		String status = request.getParameter("status");
		JSONObject json = new JSONObject();
		try {
			if(userIds!=null&&!"".equals(userIds)){
				String u[]=userIds.split(",");
				for (int i = 0; i < u.length; i++) {
					int userid=Integer.parseInt(u[i]);
					userService.updateUserStatus(userid, Integer.parseInt(status));
				}
			}
			
			json.put("result", "success");
			logger.info("------ changeAllUserStatus info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	
	
	//授权角色
	@RequestMapping(value = { "authUserRole" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void authUserRole(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------authUserRole------begin");
		String userId = request.getParameter("userId");
		String roleIds = request.getParameter("roleIds");
		JSONObject json = new JSONObject();
		try {
			if(roleIds!=null&&!"".equals(roleIds)){
				String u[]=roleIds.split(",");
				Integer[] ids=new Integer[u.length];
				for (int i = 0; i < u.length; i++) {
					ids[i]=Integer.parseInt(u[i]);
				}
				userService.updateUserRoles(Integer.parseInt(userId), ids);
			}
			json.put("result", "success");
			logger.info("------ authUserRole info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	//所属组织
	@RequestMapping(value = { "authUserOrg" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void authUserOrg(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------authUserOrg------begin");
		String userId = request.getParameter("userId");
		String orgIds = request.getParameter("orgIds");
		JSONObject json = new JSONObject();
		try {
			if(orgIds!=null&&!"".equals(orgIds)){
				String u[]=orgIds.split(",");
				Integer[] ids=new Integer[u.length];
				for (int i = 0; i < u.length; i++) {
					ids[i]=Integer.parseInt(u[i]);
				}
				userService.updateUserOrganization(Integer.parseInt(userId), ids);
			}else{
				userService.updateUserOrganization(Integer.parseInt(userId), null);;
			}
			json.put("result", "success");
			logger.info("------ authUserOrg info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
		}
		outJsonString(response, json);
	}
	
	@RequestMapping(value = { "toUpdateUser" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public String toUpdateUser(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		String id=request.getParameter("userId");
		UserBean user=userService.findUser(Integer.parseInt(id));
		out.put("user", user);
		return "system/updateUser";
	}
	
	
	@RequestMapping(value = { "showUserInfo" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public String showUserInfo(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		String type=request.getParameter("type");
		UserBean user=userService.findUser(FMContext.getCurrent().getUserId());
		List<OrganizationBean> list=organizationService.listOrganizationByUser(user.getId());
		String orgNames="";
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				if(i==0){
					orgNames=list.get(i).getName();
				}else{
					orgNames=orgNames+"、"+list.get(i).getName();
				}
			}
			
		}
		
		out.put("user", user);
		out.put("orgNames", orgNames);
		if("update".equals(type)||"updatePwd".equals(type)){
			out.put("updateType", type);
			return "system/editUserInfo";
		}
		return "system/userInfo";
	}
	
	
	
	@RequestMapping(value = { "updateUserInfo" })
	public void updateUserInfo(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		try {
			String password=request.getParameter("passwordOld");
			String passwordNew=request.getParameter("passwordNew");
			String realName=request.getParameter("realName");
			String mobile=request.getParameter("mobile");
			String email=request.getParameter("email");
			String remark = request.getParameter("remark");
			String userId=request.getParameter("userId");
			
			String updateType=request.getParameter("updateType");
			
			String status=request.getParameter("status");
			String userType=request.getParameter("userType");
			
			UserBean user=userService.findUser(Integer.parseInt(userId));
			String result="success";
			if("2".equals(updateType)){
				userService.updateUserPassword(user.getId(), password, passwordNew);
			}else{
				if(userType!=null&&!"".equals(userType)){
					user.setType(Integer.parseInt(userType));
				}
				if(realName!=null&&!"".equals(realName)){
					user.setRealName(realName.trim());
				}
//				if(mobile!=null&&!"".equals(mobile)){
//					user.setMobile(mobile);
//				}
				user.setMobile(mobile);
				user.setEmail(email);
				if(status!=null&&!"".equals(status)){
					user.setStatus(Integer.parseInt(status));
				}
				
				user.setRemark(remark);
				userService.updateUser(user);
				
				/**更新IM客户联系人相应的数据*/
				CustContactQueryBean query = new CustContactQueryBean ();
				query.setCustId(FMContext.getCurrent().getCustomerId());
				query.setUserIds(new Integer[]{user.getId()});
				CustContactBean bean = customerService.findCustContactByUserId(query);
				if (ObjectUtils.notEqual(bean, null)) {
					bean.setRealName(realName.trim());
					bean.setMobile(mobile.trim());
					bean.setEmail(email);
					bean.setRemark(remark);
					
					customerService.updateCustContactGeneralInfo(bean);
				}
			}
			
			json.put("result", result);
		}  catch (Exception e) {
			logger.error("updateUserInfo::保存个人信息失败", e);
			json.put("result", FMConstant.PASSWORD_ERROR);
		}
		outJsonString(response, json);
	}
	
	
	@RequestMapping(value = { "sendPwd" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void sendPwd(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter("userId");
		String mobile = request.getParameter("mobile");
		JSONObject json = new JSONObject();
		try {
			String pwd=userService.resetUserPassword(Integer.parseInt(userId));
			SmsBean bean=new SmsBean();
			bean.setContent("【中青华云】您的“舆情快递”登录密码已重置为"+pwd+"，请及时修改，此短信请勿回复。");
			bean.setReceiver(mobile);
			bean.setSystemCode(systemCode);
			messageService.send(bean);
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result","密码发送失败");
		}
		outJsonString(response, json);
	}
	
	@RequestMapping(value = { "getUserUserGroup" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void getUserUserGroup(HttpServletRequest request, HttpServletResponse response) {
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
			e.printStackTrace();
			json.put("result", "获取用户组失败");
		}
		outJsonString(response, json);
	}
	//所属用户组
	@RequestMapping(value = { "authUserGroup" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void authUserGroup(HttpServletRequest request, HttpServletResponse response) {
		logger.info("------authUserGroup------begin");
		String userId = request.getParameter("userId");
		String userGroupIds = request.getParameter("userGroupIds");
		JSONObject json = new JSONObject();
		try {
			Integer[] id={0};
			if(userGroupIds!=null&&!"".equals(userGroupIds)){
				String u[]=userGroupIds.split(",");
				Integer[] ids=new Integer[u.length];
				for (int i = 0; i < u.length; i++) {
					ids[i]=Integer.parseInt(u[i]);
				}
				userService.updateUserUserGroup(Integer.parseInt(userId), ids);
				
			}else{
				userService.removeGroupFromUser(Integer.parseInt(userId), id);
			}
			json.put("result", "success");
			logger.info("------ authUserGroup info-------success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "操作失败");
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