package com.cyyun.fm.people.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.people.bean.RelationView;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.process.service.bean.PeopleBean;
import com.cyyun.process.service.bean.PeopleQueryBean;
import com.cyyun.process.service.bean.VirtualIdentityBean;
import com.cyyun.process.service.bean.VirtualIdentityQueryBean;

/**
 * <h3>人物身份关联</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Controller
@RequestMapping("/people")
public class PeopleRelationentityController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private PeopleRelationentitySuppport support;

	/**
	 * @param query
	 * 页面初始化
	 * @return
	 * @throws
	 */
	@RequestMapping("Relationentity")
	public ModelAndView relationentity(PeopleQueryBean query) {
		try {
			query.setPageNo(PAGE_NO);
			query.setPageSize(PAGE_SIZE);
			PageInfoBean<RelationView> pageInfo = support.relationentity(query);
			List<CustSiteBean> custSiteList = support.boundCustSite();
			return view("/people/personRelation").addObject("pageInfo", pageInfo).addObject("custSites", custSiteList);
		} catch (BaseException e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询身份关联数据失败！");
		}
	}

	/**
	 * @param query
	 * 分页
	 * @return
	 * @throws
	 */
	@RequestMapping("listRelationPeople")
	public ModelAndView listRelationPeople(PeopleQueryBean query) {
		try {
			PageInfoBean<RelationView> pageInfo = support.relationentity(query);
			return view("/people/personRelationPage-paging").addObject("pageInfo", pageInfo);
		} catch (BaseException e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "分页查询身份关联数据失败！");
		}
	}

	/**
	 * @param query
	 * 取消关联 
	 * @return
	 * @throws
	 */
	@RequestMapping("removeRelationPeople")
	@ResponseBody
	public MessageBean removeRelationPeople(Integer vid) {
		try {
			support.removeRelationPeople(vid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "取消关联操作成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "取消关联操作失败！");
		}
	}

	/**
	 * @param query
	 * 新增关联页面真实身份绑定
	 * @return
	 * @throws
	 */
	@RequestMapping("boundTruePeople")
	@ResponseBody
	public MessageBean boundTruePeople(PeopleQueryBean peopleQuery) {
		try {
			List<PeopleBean> peoples = support.boundTruePeople(peopleQuery);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "新增关联页面真实身份绑定成功", peoples);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "新增关联页面真实身份绑定失败");
		}
	}

	/**
	 * @param query
	 * 真实身份绑定(自动填充ID)
	 * @return
	 * @throws
	 */
	@RequestMapping("boundTruePeopleSupport")
	@ResponseBody
	public MessageBean boundTruePeopleSupport(PeopleQueryBean peopleQuery) {
		try {
			List<PeopleBean> PeopleBeans = support.boundTruePeopleSupport(peopleQuery);
			Integer id = PeopleBeans.get(0).getId();
			return buildMessage(MESSAGE_TYPE_SUCCESS, "真实身份绑定(自动填充ID)成功", id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "真实身份绑定(自动填充ID)失败");
		}
	}

	/**
	 * @param query
	 * 新增关联页面虚拟身份数据绑定
	 * @return
	 * @throws
	 */
	@RequestMapping("boundVirtualPeople")
	@ResponseBody
	public MessageBean boundVirtualPeople(VirtualIdentityQueryBean query) {
		try {
			List<VirtualIdentityBean> virtualIdentitys = support.boundVirtualPeople(query);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "新增关联页面虚拟身份数据绑定成功", virtualIdentitys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "新增关联页面虚拟身份数据绑定失败");
		}
	}

	/**
	 * @param query
	 * 虚拟身份绑定(自动填充ID)
	 * @return
	 * @throws
	 */
	@RequestMapping("boundVirtualPeopleSupport")
	@ResponseBody
	public MessageBean boundVirtualPeopleSupport(VirtualIdentityQueryBean virtualQuery) {
		try {
			List<VirtualIdentityBean> virtualIdentitys = support.boundVirtualPeopleSupport(virtualQuery);
			Integer id = virtualIdentitys.get(0).getId();
			return buildMessage(MESSAGE_TYPE_SUCCESS, "虚拟身份绑定(自动填充ID)成功", id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "虚拟身份绑定(自动填充ID)失败");
		}
	}

	/**
	 * @param query
	 * 新增关联 
	 * @return
	 * @throws
	 */
	@RequestMapping("addRelationPeople")
	@ResponseBody
	public MessageBean addRelationPeople(Integer id, Integer vid) {
		try {
			support.addRelationPeople(id, vid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "新增关联 成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "新增关联 失败");
		}
	}

}
