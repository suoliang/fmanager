/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyyun.fm.setting.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.service.OrganizationService;
import com.cyyun.authim.service.bean.OrganizationBean;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.customer.service.CustomerService;

/**
 * 2013/12/11
 *
 * @author renhc
 */
@Controller(value = "orgController")
@RequestMapping(value = { "orgManage" }, method = {
		RequestMethod.POST,
		RequestMethod.GET })
public class OrganizationController extends BaseController {

	@Autowired
	OrganizationService  organizationService;
	@Autowired
	private CustomerService customerService;
	
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OrganizationController.class.getName());

	//<editor-fold defaultstate="collapsed" desc="ajax方式返回输出信息">  
	public void outJsonString(HttpServletResponse response, JSONObject json) {
		response.setContentType("text/javascript;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
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

	@RequestMapping(value = { "list" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public String toList(HttpServletRequest request){
		request.setAttribute("val", FMConstant.parentIdVal);
		return "system/orgManager";
		
	}
	
	@RequestMapping(value = { "queryOrgList" })
	public void queryOrgList(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		String orgName = request.getParameter("name");
		logger.info("get queryOrgList info-------------begin--------------orgName:" + orgName);
		try {
			Integer parentId=-2;
			Integer[] orgids=customerService.findCustomer(FMContext.getCurrent().getCustomerId()).getOrgIds();
			if(orgids!=null&&orgids.length>0){
				parentId=orgids[0];
			}
			List<OrganizationBean> deptList=null;
			if (orgName != null&&!"".equals(orgName)) {
				deptList=organizationService.listOrganizationByName(CyyunSqlUtil.dealSql(orgName),parentId);
			}else{
				deptList=organizationService.listOrganizationByParentId(parentId);
			}
			Map<Integer,Integer> map=organizationService.countUserByParentId(parentId);
			json.put("countUserMap", JSONArray.toJSON(map));
			json.put("data", JSONArray.toJSON(deptList));
			json.put("type", MESSAGE_TYPE_SUCCESS);
		logger.info("get queryOrgList info-------------success--------------");
		} catch (Exception e) {
			logger.error("queryOrgList{}", e);
			json.put("type", MESSAGE_TYPE_ERROR);
			json.put("message", FMConstant.QUERY_ORGANIZATION_FAILURE);
		}
		outJsonString(response, json);
	}
	

	@RequestMapping(value = { "getSubOrgList" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void getSubDeptList(HttpServletRequest request, HttpServletResponse response) {
		
		String parentId=request.getParameter("orgid");
		JSONObject json = new JSONObject();
		try {
			if(FMConstant.parentIdVal.equals(parentId)){
				Integer[] orgids=customerService.findCustomer(FMContext.getCurrent().getCustomerId()).getOrgIds();
				if(orgids!=null&&orgids.length>0){
					parentId=""+orgids[0];
				}
			}
			List<OrganizationBean> deptList=organizationService.listOrganizationByParentId(Integer.parseInt(parentId));
			json.put("result", "success");
			json.put("subList", JSONArray.toJSON(deptList));
			Map<Integer,Integer> map=organizationService.countUserByParentId(Integer.parseInt(parentId));
			json.put("countUserMap", JSONArray.toJSON(map));
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "获取组织失败");
		}
		outJsonString(response, json);
	}

	
	@RequestMapping(value = { "saveOrg" }, method = {
			RequestMethod.GET,
			RequestMethod.POST })
	public void saveOrg(HttpServletRequest request, HttpServletResponse response) {
		String orgName=request.getParameter("orgName");
		String parentOrgId=request.getParameter("parentOrgId");
		String orgId=request.getParameter("orgId");
		JSONObject json = new JSONObject();
		try {
			OrganizationBean bean=new OrganizationBean();
			bean.setName(orgName.toLowerCase());
			bean.setParentId(Integer.parseInt(parentOrgId));
			if(orgId!=null&&!"".equals(orgId)){
				bean.setId((Integer.parseInt(orgId)));
				organizationService.updateOrganization(bean);
			}else{
				if(FMConstant.parentIdVal.equals(parentOrgId)){
					Integer parentId=-1;
					Integer[] orgids=customerService.findCustomer(FMContext.getCurrent().getCustomerId()).getOrgIds();
					if(orgids!=null&&orgids.length>0){
						parentId=orgids[0];
					}
					if(parentId==-1){
						OrganizationBean parent=new OrganizationBean();
						parent.setName(FMContext.getCurrent().getCustomer().getName());
						parent.setParentId(0);
						OrganizationBean p=organizationService.addOrganization(parent);
						parentId=p.getId();
						Integer[] custs=new Integer[]{p.getId()};
						customerService.updateCustOrganizations(FMContext.getCurrent().getCustomerId(),custs);
					}
					
					bean.setParentId(parentId);
				}
				organizationService.addOrganization(bean);
			}
			json.put("result", BaseController.MESSAGE_TYPE_SUCCESS);
			
		} catch (Exception e) {
			logger.error("saveOrg::新增组织失败", e);
			json.put("result", FMConstant.ORGANIZATION_ERROR);
		}
		outJsonString(response, json);
	}
	
	
	
	@RequestMapping(value = { "deleteOrg" })
	public void deleteDeptXHR(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			int deptid = Integer.valueOf(request.getParameter("orgId"));
			organizationService.deleteOrganization(deptid);
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "删除失败");
		}
		outJsonString(response, json);
	}
	
	@RequestMapping(value = { "countUserByParentId" })
	public void countUserByParentId(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			Integer parentId=-2;
			Integer[] orgids=customerService.findCustomer(FMContext.getCurrent().getCustomerId()).getOrgIds();
			if(orgids!=null&&orgids.length>0){
				parentId=orgids[0];
			}
			Map<Integer,Integer> map=organizationService.countUserByParentId(parentId);
			json.put("countUserMap", JSONArray.toJSON(map));
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "查询成员数失败");
		}
		outJsonString(response, json);
	}
	
	

}