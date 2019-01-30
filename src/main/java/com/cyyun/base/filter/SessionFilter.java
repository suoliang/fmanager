package com.cyyun.base.filter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cyyun.authim.service.PermResourceService;
import com.cyyun.authim.service.bean.MenuBean;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.constant.SessionConstant;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.customer.constant.DataCode;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.constant.UserConfigMeta;
import com.cyyun.fm.service.AgentService;
import com.cyyun.fm.service.UserConfigService;
import com.cyyun.fm.service.bean.AgentBean;
import com.cyyun.fm.service.bean.UserConfigBean;
import com.cyyun.fm.service.exception.FmAgentException;
import com.cyyun.fm.service.exception.UserConfigException;

/**
 * <h3>会话过滤器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component("sessionFilter")
public class SessionFilter extends OncePerRequestFilter {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${system.code}")
	private String systemCode;

	@Autowired
	private PermResourceService permResourceService;

	@Autowired
	private UserConfigService userConfigService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AgentService agentService;

	@Autowired
	protected CustomerConfigService customerConfigService;
	
	@Autowired
	private CustTopicService custTopicService;

	@Value("${logo.path}")
	private String logoPath;

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		FMContext.getCurrent().setSystemCode(systemCode);

		HttpSession session = request.getSession(true);
		UserBean user = FMContext.getLoginUser();
		if (user != null) {
			FMContext.getCurrent().setUserId(user.getId());

			processExpired(request, session);
			processCustomer(request, session);
			processMenu(request, session);
			processTheme(request, session);
			processAgentLogo(request, session);
			processCustData(request, session);
			processCustTopicsWithShare(request,session);
		}
		try {
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void processCustTopicsWithShare(HttpServletRequest request,HttpSession session) {
		try {
			List<String> custTopicIds = getAllCustTopicId();
			if (CollectionUtils.isNotEmpty(custTopicIds)) {
				session.setAttribute("CUSTTOPICIDS_WITHSHARE", custTopicIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public List<String> getAllCustTopicId() throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(0);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setNeedPaging(false);/**查询所有，不分页查询*/
		List<CustTopicBean> topics = custTopicService.listCustTopicWithShare(query);
		StringBuffer custTopics = new StringBuffer();

		getCustTopicIds(topics, custTopics);

		//获取当前客户下的可用专题
		String allCustTopicIds = custTopics.toString();
		if (StringUtils.isNotBlank(allCustTopicIds)) {
			allCustTopicIds = allCustTopicIds.substring(0, allCustTopicIds.length() - 1);
		}
		return Arrays.asList(StringUtils.trimToEmpty(allCustTopicIds).split(","));
	}

	public void getCustTopicIds(List<CustTopicBean> topics, StringBuffer custTopics) throws CustTopicServiceException {
		for (CustTopicBean custTopicBean : topics) {
			CustTopicQueryBean query = new CustTopicQueryBean();
			query.setParentId(custTopicBean.getId());
			query.setCustId(FMContext.getCurrent().getCustomerId());
			query.setStatus(1);
			query.setCreaterId(FMContext.getCurrent().getUserId());
			custTopics = custTopics.append(custTopicBean.getId() + ",");
			List<CustTopicBean> sonCustTopic = custTopicService.listCustTopicWithShare(query);

			getCustTopicIds(sonCustTopic, custTopics);
		}
	}
	
	/** <h3></h3>
	 * @param request
	 * @param session
	 */
	private void processCustData(HttpServletRequest request, HttpSession session) {
		try {
			Integer[] areaIds = (Integer[]) session.getAttribute("CUSTDATA_AREA");
			if (areaIds == null) {
				areaIds = customerConfigService.findDataIdsByCid(FMContext.getCurrent().getCustomerId(), DataCode.AREA);
				if (areaIds != null) {
					session.setAttribute("CUSTDATA_AREA", areaIds);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void processAgentLogo(HttpServletRequest request, HttpSession session) {
		try {
			UserBean user = FMContext.getLoginUser();
			CustomerBean customerBean = customerService.findCustomerByUid(user.getId());
			int custId = customerBean.getId();
			AgentBean agentBean = agentService.queryAgentBycustIds(custId);
			if (agentBean != null) {
				request.setAttribute("agentLogo", logoPath + agentBean.getLogoPath().trim());
			}
		} catch (FmAgentException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void processExpired(HttpServletRequest request, HttpSession session) {
		String expiredTimeflag = (String) session.getAttribute("expiredTimeflag");
		if (expiredTimeflag == null) {
			Date date = new Date();
			SimpleDateFormat sdf = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.DatePattern);
			String sysTime = sdf.format(date);
			String cTime = null;
			if (FMContext.getLoginUser().getExpiredTime() != null && !FMContext.getLoginUser().getExpiredTime().equals("")) {
				cTime = sdf.format(FMContext.getLoginUser().getExpiredTime());
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(sdf.parse(sysTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long time1 = cal.getTimeInMillis();
				try {
					cal.setTime(sdf.parse(cTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long time2 = cal.getTimeInMillis();
				long between_days = (time2 - time1) / (1000 * 3600 * 24);
				request.setAttribute("expiredTimeflag", between_days);
			} else {
				request.setAttribute("expiredTimeflag", -1);
			}
		}
	}

	private void processTheme(HttpServletRequest request, HttpSession session) {
		try {
			String theme = (String) session.getAttribute("theme");
			if (theme == null) {
				UserConfigBean userConfig = userConfigService.findUserConfig(FMContext.getCurrent().getUserId(), UserConfigMeta.UE.THEME);
				if (userConfig != null) {
					session.setAttribute("theme", userConfig.getValue());
				}
			}
		} catch (UserConfigException e) {
			log.error(e.getMessage());
		}
	}

	private void processCustomer(HttpServletRequest request, HttpSession session) {
		//customer
		CustomerBean customer = (CustomerBean) session.getAttribute(SessionConstant.CUSTOMER);
		if (customer == null) {
			customer = customerService.findCustomerByUid(FMContext.getCurrent().getUserId());
			session.setAttribute(SessionConstant.CUSTOMER, customer);
		}
		FMContext.getCurrent().setCustomer(customer);
		FMContext.getCurrent().setCustomerId(customer.getId());
	}

	@SuppressWarnings("unchecked")
	private void processMenu(HttpServletRequest request, HttpSession session) {
		//menus
		List<MenuBean> menus = (List<MenuBean>) session.getAttribute(SessionConstant.MENUS);
		if (CollectionUtils.isEmpty(menus)) {
			menus = permResourceService.listMenu(FMContext.getCurrent().getUserId(), systemCode);
			if (menus == null) {
				menus = ListBuilder.newArrayList();
			}
			session.setAttribute(SessionConstant.MENUS, menus);
		}

		//current menu
		MenuBean selectedMenu = getSelectedMenu(request.getRequestURI(), menus);
		if (selectedMenu != null) {
			session.setAttribute(SessionConstant.MENU_SELECTED, selectedMenu);
		}
	}

	private MenuBean getSelectedMenu(String uri, List<MenuBean> menus) {
		for (MenuBean menu : menus) {
			if (StringUtils.isNoneBlank(menu.getUrl()) && uri.contains(menu.getUrl())) {
				MenuBean selectedMenu = new MenuBean();
				selectedMenu.setId(menu.getId());
				selectedMenu.setUrl(menu.getUrl());
				return selectedMenu;
			}
			if (CollectionUtils.isNotEmpty(menu.getChildren())) {

				MenuBean selectedMenu = getSelectedMenu(uri, menu.getChildren());//迭代

				if (selectedMenu != null) {
					MenuBean selectedParentMenu = new MenuBean();
					selectedParentMenu.setId(menu.getId());
					selectedParentMenu.setUrl(menu.getUrl());
					List<MenuBean> childMenu = ListBuilder.newArrayList();
					childMenu.add(selectedMenu);
					selectedParentMenu.setChildren(childMenu);
					return selectedParentMenu;
				}
			}
		}
		return null;
	}
}