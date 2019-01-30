/**
 *
 */
package com.cyyun.fm.setting.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cyyun.authim.service.PermResourceService;
import com.cyyun.authim.service.bean.MenuBean;
import com.cyyun.authim.service.exception.PermResourceServiceException;
import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;

/**
 * @author zhangfei
 *
 */
@Controller
@RequestMapping("/setting")
public class SettingCustColumnController extends BaseController {
	@Autowired
	private PermResourceService permResourceService;

	@Value("${system.code}")
	private String systemCode;

	@RequestMapping(value = { "queryMainMenu" })
	public String querySiteListByPage(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		/*
		 * 查询主菜单列表数据
		 */
		int userId=FMContext.getCurrent().getUserId();//
		List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);

		request.setAttribute("listMenuSys", listMenuSys);
		return "/mainMenu/mainMenu";
	}

	/*
	 * 向上
	 */
	@RequestMapping(value = { "toUp" })
	public String toUp(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		int userId=FMContext.getCurrent().getUserId();//
		//得到页面传递的参数 开始
		String idt = request.getParameter("id");
		String lastStr = request.getParameter("index");
		String lastOidStr = request.getParameter("lastOid");
		String needTrueStr = request.getParameter("needTrue");
		String lastTrueStr = request.getParameter("lastTrue");
		boolean needTrue = Boolean.parseBoolean(needTrueStr);
		boolean lastTrue = Boolean.parseBoolean(lastTrueStr);
		int id = Integer.valueOf(idt);
		int index = Integer.valueOf(lastStr);
		int last = Integer.valueOf(lastOidStr);
		//得到页面传递的参数 结束

		int indexResult = index - 1;//向上本条记录的index需要减去1
		MenuBean mb = new MenuBean();//本条记录对象
		mb.setId(id);//ID
		mb.setIndex(indexResult);//索引
		mb.setNeedShow(needTrue);//是否显示
		mb.setSystemCode(systemCode);//系统关键字
		MenuBean mbLast = new MenuBean();//上一条记录对象
		mbLast.setId(last);
		mbLast.setIndex(index);
		mbLast.setNeedShow(lastTrue);//是否显示
		mbLast.setSystemCode(systemCode);//系统关键字

		List<MenuBean> menus = new ArrayList<MenuBean>();
		menus.add(mb);//本条记录
		menus.add(mbLast);//上一条记录
		try {
			permResourceService.saveUserMenuConfig(userId, menus,systemCode);
		} catch (PermResourceServiceException e) {
			System.out.println("向上---->保存用户菜单配置失败");
			e.printStackTrace();
		}

		List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
		//		 for (MenuBean menuBean : listMenuSys) {
		//			 if(menuBean.getParentId()==0){
		//				System.out.println("---------------->"+menuBean.getName());
		//				System.out.println("---------------->"+menuBean.getIndex());
		//			 }
		//		}
		request.setAttribute("listMenuSys", listMenuSys);

		return "/mainMenu/mainMenu";
	}

	/*
	 * 向下
	 */
	@RequestMapping(value = { "toDown" })
	public String toDown(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		int userId=FMContext.getCurrent().getUserId();//
		//得到页面传递的参数 开始
		String idt = request.getParameter("id");
		String indexStr = request.getParameter("index");
		String nextOidStr = request.getParameter("nextOid");
		String needTrueStr = request.getParameter("needTrue");
		String nestNeedTrueStr = request.getParameter("nextNeedTrue");
		boolean needTrue = Boolean.parseBoolean(needTrueStr);
		boolean nextNeedTrue = Boolean.parseBoolean(nestNeedTrueStr);
		int id = Integer.valueOf(idt);
		int index = Integer.valueOf(indexStr);
		int next = Integer.valueOf(nextOidStr);
		//得到页面传递的参数 结束

		int indexrResult = index + 1;//本条记录向下需要的index
		MenuBean mb = new MenuBean();//本条记录对象
		mb.setId(id);//ID
		mb.setIndex(indexrResult);//索引indexrResult
		mb.setNeedShow(needTrue);//是否显示
		mb.setSystemCode(systemCode);//系统关键字

		MenuBean mbNext = new MenuBean();//下一条记录对象
		int indexN = index;//把之前的index赋值给下一个记录
		mbNext.setId(next);
		mbNext.setIndex(indexN);
		mbNext.setNeedShow(nextNeedTrue);//是否显示
		mbNext.setSystemCode(systemCode);//系统关键字

		List<MenuBean> menus = new ArrayList<MenuBean>();
		menus.add(mb);//本条记录
		menus.add(mbNext);//=下一条记录
		try {
			permResourceService.saveUserMenuConfig(userId, menus,systemCode);
		} catch (PermResourceServiceException e) {
			System.out.println("向下---->保存用户菜单配置失败");
			e.printStackTrace();
		}

		List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
		
		request.setAttribute("listMenuSys", listMenuSys);
		return "/mainMenu/mainMenu";
	}

	/*
	 *是否显示 
	 */
	@RequestMapping(value = "WhetherTF")
	public String WhetherTF(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		int userId=FMContext.getCurrent().getUserId();//
		//接收和格式化页面传递的参数 开始 
		String idStr = request.getParameter("id");
		String needShowStr = request.getParameter("needShow");
		String indeStr = request.getParameter("index");
		int id = Integer.valueOf(idStr);
		boolean needShow = Boolean.valueOf(needShowStr);
		int index = Integer.valueOf(indeStr);
		//接收和格式化页面传递的参数 结束

		MenuBean mb = new MenuBean();
		mb.setId(id);
		mb.setIndex(index);
		mb.setSystemCode(systemCode);
		mb.setNeedShow(!needShow);

		List<MenuBean> menus = new ArrayList<MenuBean>();
		menus.add(mb);
		try {
			permResourceService.saveUserMenuConfig(userId, menus,systemCode);
		} catch (PermResourceServiceException e) {
			System.out.println("---------------------->修改显示隐藏失败");
			e.printStackTrace();
		}
		//	  List<MenuBean> listMenuSys = permResourceService.listMenu(2, systemCode);
		List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
		for (MenuBean menuBean : listMenuSys) {
			if (menuBean.getParentId() == 0) {
				System.out.println("---------------->" + menuBean.getName());
			}
		}
		request.setAttribute("listMenuSys", listMenuSys);
		return "/mainMenu/mainMenu";
	}

	/*
	 * 子菜单列表
	 */
	@RequestMapping(value = "subMenu")
	public String subMenu(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		int userId=FMContext.getCurrent().getUserId();//
		//查询所有菜单
		List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
		List<MenuBean> listMenu = new ArrayList<MenuBean>();
		//遍历菜单
		for (MenuBean menuBean : listMenuSys) {
			if (menuBean.getChildren() == null || menuBean.getChildren().equals("")) {
			} else {
				listMenu.add(menuBean);//得到有子菜单的主菜单
			}
		}
		request.setAttribute("listMenuSys", listMenuSys);
		request.setAttribute("listMenuChildren", listMenu);
		return "/mainMenu/subMenu";
	}

	/*
	 * 子菜单页面中需要显示的模块以及菜单
	 */
	@RequestMapping(value = "subMenuShow")
	public String subMenuShow(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		int userId=FMContext.getCurrent().getUserId();//
		
		String fids = request.getParameter("fids");
		//所有选中行的父id
		String parentIdStr = request.getParameter("parentId");
		//所有未选中行的父id
		String unparentIdStr = request.getParameter("unparentId");
		//声明String数组,用来放所有未选中的父id
		String[] unparentId = new String[] {};
		if(unparentIdStr!=null && !unparentIdStr.equals("")){
			unparentId=unparentIdStr.split(",");
		}
		
		//声明String数组,用来放所有选中的父id
		String[] parentId = new String[] {};
		if(parentIdStr!=null && !parentIdStr.equals("")){
			parentId=parentIdStr.split(",");
		}
		
		
		String[] flist = new String[] {};//声明String数组
		if (fids != null && !fids.equals("")) {
			flist = fids.split(",");
		}

		String tids = request.getParameter("tids");
		String[] tlist = tids.split(",");

		List<MenuBean> menus = new ArrayList<MenuBean>();

		MenuBean mb = new MenuBean();
		//把未选中的显示设置为false 开始
		System.out.println("----------->" + flist.length);
		if (flist != null && flist.length == 0) {
		} else {
			System.out.println(flist.length);
			for (int i = 0; i < flist.length; i++) {
				System.out.println(flist.length);
				mb.setId(Integer.valueOf(flist[i]));
				mb.setIndex(Integer.valueOf(flist[i]));
				mb.setParentId(Integer.valueOf(unparentId[i]));//未选中的行的父id
				mb.setNeedShow(false);
				mb.setSystemCode(systemCode);
				menus.add(mb);
				try {
				permResourceService.saveUserMenuConfig(userId, menus,systemCode);
				} catch (PermResourceServiceException e) {
					System.out.println("---------------------------->修改未选中子菜单显示模块和菜单失败！");
					e.printStackTrace();
				}
			}
		}

		//把未选中的显示设置为false 结束
		MenuBean tmb = new MenuBean();
		//把选中的显示设置为true 开始
		if (tlist != null && tlist.length == 1) {
		} else {
			for (int i = 0; i < tlist.length; i++) {
				tmb.setId(Integer.valueOf(tlist[i]));
				tmb.setIndex(Integer.valueOf(tlist[i]));
				tmb.setParentId(Integer.valueOf(parentId[i]));//选中行的父id
				tmb.setNeedShow(true);
				tmb.setSystemCode(systemCode);
				menus.add(tmb);
				try {
					permResourceService.saveUserMenuConfig(userId, menus,systemCode);
				} catch (PermResourceServiceException e) {
					System.out.println("---------------------------->修改未选中子菜单显示模块和菜单失败！");
					e.printStackTrace();
				}
			}
		}
		return "redirect:/setting/subMenu.htm";
	}
	
	
	/**
		 * @param request
		 * @param response
		 * @param redirectAttributes
		 * @return
		 */
		@RequestMapping(value = { "refreshSort" })
		public ModelAndView refreshSort(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
			int userId=FMContext.getCurrent().getUserId();
			String index= request.getParameter("index");
			String id= request.getParameter("id");
			MenuBean mb = new MenuBean();
			List<MenuBean> menus = new ArrayList<MenuBean>();
			mb.setId(Integer.valueOf(id));//id
			mb.setIndex(Integer.valueOf(Integer.valueOf(index)));//
			//mb.setNeedShow(true);//
			mb.setSystemCode(systemCode);//
			menus.add(mb);
			List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
			try {
				permResourceService.saveUserMenuConfig(userId, menus,systemCode);
				return view("/mainMenu/index-list-menu").addObject("listMenuSys", listMenuSys);
			} catch (PermResourceServiceException e) {
				e.printStackTrace();
				return message(MESSAGE_TYPE_ERROR, "页面刷新失败");
			}
		}
		
		
		@RequestMapping(value = "listPageByAjax")
		@ResponseBody
		public MessageBean queryListPageByAjax(HttpServletRequest request, HttpServletResponse response, ModelMap out){
			int userId=FMContext.getCurrent().getUserId();//
			
			String index= request.getParameter("index");
			String id= request.getParameter("oid");
			
			MenuBean mb = new MenuBean();//
			List<MenuBean> menus = new ArrayList<MenuBean>();
			mb.setId(Integer.valueOf(id));//id
			mb.setIndex(Integer.valueOf(Integer.valueOf(index)));//
			mb.setNeedShow(true);//
			mb.setSystemCode(systemCode);//
			menus.add(mb);
			List<MenuBean> listMenuSys = permResourceService.listMenu(userId, systemCode, true);
			request.setAttribute("listMenuSys", listMenuSys);
			try {
				permResourceService.saveUserMenuConfig(userId, menus,systemCode);
				return buildMessage(MESSAGE_TYPE_SUCCESS, "操作成功");
			} catch (PermResourceServiceException e) {
				e.printStackTrace();
				return buildMessage(MESSAGE_TYPE_ERROR, "操作失败");
			}
		}
	
	
}
