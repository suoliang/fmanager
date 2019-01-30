package com.cyyun.fm.base.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.LoginInfoBean;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.authim.service.exception.UserLoginException;
import com.cyyun.authim.service.exception.UserServiceException;
import com.cyyun.authim.shiro.AuthUtil;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.constant.SessionConstant;
import com.cyyun.common.core.util.DigestUtil;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.service.AgentService;
import com.cyyun.fm.service.bean.AgentBean;
import com.cyyun.message.service.MessageService;
import com.cyyun.message.service.bean.SmsBean;
import com.cyyun.message.service.exception.MessageServiceException;

@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

	public static final int COOKIE_MAX_TIME = 60 * 60 * 24 * 7;
	
	public static final SimpleDateFormat FORMAT = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern1); 

	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private MessageService  messageService;
	
	@Autowired
	private AgentService agentService;
	
	@RequestMapping("login")
	@ResponseBody
	public ModelAndView login(HttpServletRequest request) {
		return view("login").addAllObjects(autoFill(request));
	}
	
	//缓存验证码   格式（key：帐号  value：验证码_时间）
	private Map<String, String> validatecodeMap = new HashMap<String, String>();
	
	@RequestMapping("doLogin")
	@ResponseBody
	public MessageBean doLogin(LoginInfoBean loginInfo, HttpServletRequest request, HttpServletResponse response) {
		try {
			loginInfo.setIp(super.getIPAddress(request));
			loginInfo.setSystemCode(FMContext.getCurrent().getSystemCode());
			UserBean userBean = userService.findUser(loginInfo.getUsername());
			if (userBean != null) {
				CustomerBean customerBean = customerService.findCustomerByUid(userBean.getId());
				if (!checkExpired(customerBean)) {
					return super.buildMessage(MESSAGE_TYPE_ERROR, "有效使用时间未到或已过期");
				}
				if (customerBean != null) {
					boolean status = customerBean.getStatus() != null && customerBean.getStatus() != 0 && userBean.getStatus() != null && userBean.getStatus() != 0;
					if (status) {
						//-----针对安徽师范大学客户的定制化需求   安徽师范大学客户cid=1448
//						if (customerBean.getId().intValue() == 1448) {
//							HttpSession session = request.getSession();
//							session.setAttribute("loginInfo", loginInfo);
//							session.setAttribute("LoggingUser", userBean);
//							remember(loginInfo.getUsername(), loginInfo.getPassword(), request, response);
//							return super.buildMessage(MESSAGE_TYPE_SUCCESS, "安徽师范大学客户用户登录成功");
//						} else {
							this.userService.login(loginInfo);
							AuthUtil.login(loginInfo.getUsername(), loginInfo.getPassword());
							remember(loginInfo.getUsername(), loginInfo.getPassword(), request, response);
							return super.buildMessage(MESSAGE_TYPE_SUCCESS, "用户登录成功");
//						}
					}
					return super.buildMessage(MESSAGE_TYPE_ERROR, "用户或客户已停用");
				}
			}

			return super.buildMessage(MESSAGE_TYPE_ERROR, "用户不存在");
		} catch (UserLoginException e) {
			log.error(e.getMessage(), e);
			return super.buildMessage(MESSAGE_TYPE_ERROR, e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return super.buildMessage(MESSAGE_TYPE_ERROR, "用户登录错误");
		}
	}

	public Boolean checkExpired(CustomerBean customerBean) {
		Boolean results = true;
		Date now = new Date();
		Date expiredTime = customerBean.getExpiredTime();
		Date effectiveTime = customerBean.getEffectiveTime();
		if (expiredTime == null && effectiveTime == null) {
			return results;
		} else if (expiredTime == null && effectiveTime != null) {
			String effectiveTimeStr = FORMAT.format(effectiveTime);
			String nowTimeStr = FORMAT.format(now);
			int resultsInt = effectiveTimeStr.compareTo(nowTimeStr);//0:等于,-1:早于,1:晚于
			if (resultsInt > 0) {
				results = false;
			}
		} else if (expiredTime != null && effectiveTime == null) {
			String expiredTimeStr = FORMAT.format(expiredTime);
			String nowTimeStr = FORMAT.format(now);
			int resultsInt = expiredTimeStr.compareTo(nowTimeStr);//0:等于,-1:早于,1:晚于
			if (resultsInt <= 0) {
				results = false;
			}
		} else {
			String effectiveTimeStr = FORMAT.format(effectiveTime);
			String expiredTimeStr = FORMAT.format(expiredTime);
			String nowTimeStr = FORMAT.format(now);
			int resultsInt = effectiveTimeStr.compareTo(nowTimeStr);//0:等于,-1:早于,1:晚于
			if (resultsInt > 0) {
				results = false;
			}
			int resultsInt1 = expiredTimeStr.compareTo(nowTimeStr);//0:等于,-1:早于,1:晚于
			if (resultsInt1 <= 0) {
				results = false;
			}
		}
		return results;
	}
	
	@RequestMapping("doSignatureAuthForPc")
	public ModelAndView doSignatureAuthForPc(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginInfoBean loginInfo = new LoginInfoBean();
			loginInfo.setUsername(request.getParameter("username"));
			loginInfo.setPassword(request.getParameter("password"));
			loginInfo.setIp(super.getIPAddress(request));
			loginInfo.setSystemCode(FMContext.getCurrent().getSystemCode());
			UserBean userBean = userService.findUser(loginInfo.getUsername());
			if(userBean != null){
				CustomerBean customerBean = customerService.findCustomerByUid(userBean.getId());
				if (!checkExpired(customerBean)) {
					return super.message(MESSAGE_TYPE_ERROR, "有效使用时间未到或已过期");
				}
				if(customerBean != null){
					boolean status = customerBean.getStatus() != null && customerBean.getStatus() != 0 && userBean.getStatus() != null && userBean.getStatus() != 0;
					if(status){
						this.userService.login(loginInfo);
						AuthUtil.login(loginInfo.getUsername(), loginInfo.getPassword());
						remember(loginInfo.getUsername(), loginInfo.getPassword(), request, response);
						String url = null;
						String flag = request.getParameter("url");
						if("1".equals(flag)){
							url = "home/index.htm";
						}else if("2".equals(flag)){
							url = "focusing/QueryInfo.htm";
						}
						return view("redirect:" + url);
					}
					return super.message(MESSAGE_TYPE_ERROR, "用户或客户已停用");
				}
			}
			return super.message(MESSAGE_TYPE_ERROR, "用户不存在");
		} catch (UserLoginException e) {
			log.error(e.getMessage(), e);
			return super.message(MESSAGE_TYPE_ERROR, "用户登录错误");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return super.message(MESSAGE_TYPE_ERROR, "用户登录错误");
		}
	}
	
	/**
	 * 目前舆情助手3.0版使用到 
	 * 调用地址为：http://www.cyyun.com/yqkd/doSignatureAuthForPc2.htm
	 * token= Base64(username+"-"+password+“-”+now()) 
	 * url=1/2/3
	 * password为加密后的密码
	 */
	@RequestMapping("doSignatureAuthForPc2")
	public ModelAndView doSignatureAuthForPc2(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html; charset=GBK");
			PrintWriter out = response.getWriter();

			String[] temp = base64Decode(request.getParameter("token")).split("-");
			LoginInfoBean loginInfo = new LoginInfoBean();
			loginInfo.setUsername(temp[0]);
			loginInfo.setPassword(temp[1]);
			loginInfo.setIp(super.getIPAddress(request));
			loginInfo.setSystemCode(FMContext.getCurrent().getSystemCode());
			UserBean userBean = userService.findUser(loginInfo.getUsername());
			if (userBean != null) {
				if (!temp[1].equals(userBean.getPassword())) {
					out.write("<script>alert('密码错误！');</script>");
					return null;
				}

				long date = Long.parseLong(temp[2]);
				if (System.currentTimeMillis() - date > 43200000) {//12h后超时
					out.write("<script>alert('认证请求已超时,请重试！');</script>");
					return null;
				}

				CustomerBean customerBean = customerService.findCustomerByUid(userBean.getId());
				if (!checkExpired(customerBean)) {
					out.write("<script>alert('帐号有效使用时间未到或已过期!');</script>");
					return null;
				}

				String flag = request.getParameter("url");
//				String doMainName = "192.168.1.81:8081/fmanager";
//				String doMainName = "test.cyyun.com:8280/fmanager";//test环境配置
				String doMainName = "www.cyyun.com:8085/fmanager";//生产环境配置
				String url = null;
				AgentBean agentBean = agentService.queryAgentBycustIds(customerBean.getId());

				if ("true".equals(request.getParameter("redirectFlag"))) {
//					doMainName = (agentBean != null && agentBean.getDomainName() != null && agentBean.getDomainName().length() > 0) ? agentBean.getDomainName() : "192.168.1.81:8081/fmanager";
					//生产环境配置
					doMainName = (agentBean != null && agentBean.getDomainName() != null && agentBean.getDomainName().length() > 0) ? agentBean.getDomainName(): "www.cyyun.com:8085/fmanager";
					//test环境配置
//					doMainName = (agentBean != null && agentBean.getDomainName() != null && agentBean.getDomainName().length() > 0) ? agentBean.getDomainName(): "test.cyyun.com:8280/fmanager";
					if (customerBean != null) {
						boolean status = customerBean.getStatus() != null && customerBean.getStatus() != 0 && userBean.getStatus() != null && userBean.getStatus() != 0;
						if (status) {
							if ("1".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "http://" + doMainName + "/home/index.htm";
							} else if ("2".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "http://" + doMainName + "/focusing/QueryInfo.htm";
							} else if ("3".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "http://" + doMainName + "/focusing/openArticleDetail.htm?guid=" + request.getParameter("guid");
							}
							return view("redirect:" + url);
						}
						out.write("<script>alert('用户或客户已停用');</script>");
						return null;
					}
				} else {
					if (agentBean == null) {
						flag = "4";
					} else if (agentBean.getDomainName() != null && agentBean.getDomainName().length() > 0) {
						flag = "4";
						doMainName = agentBean.getDomainName();
					}
					if (customerBean != null) {
						boolean status = customerBean.getStatus() != null && customerBean.getStatus() != 0 && userBean.getStatus() != null && userBean.getStatus() != 0;
						if (status) {
							if ("1".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "/home/index.htm";
							} else if ("2".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "/focusing/QueryInfo.htm";
							} else if ("3".equals(flag)) {
								AuthUtil.login(loginInfo.getUsername());
								url = "/focusing/openArticleDetail.htm?guid=" + request.getParameter("guid");
							} else if ("4".equals(flag)) {
								url = "http://" + doMainName + "/doSignatureAuthForPc2.htm?token=" + request.getParameter("token") + "&url=" + request.getParameter("url") + "&guid=" + request.getParameter("guid") + "&redirectFlag=true";
							}
							return view("redirect:" + url);
						}
						out.write("<script>alert('用户或客户已停用');</script>");
						return null;
					}
				}
			}
			out.write("<script>alert('用户不存在');</script>");
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 目前IM3.0、FM2.5使用到
	 */
	@RequestMapping("doSignatureLogin")
	public ModelAndView doSignatureAuth(String identity, String signature, String url, HttpServletResponse response) {
		try {
			response.setContentType("text/html; charset=GBK");
			PrintWriter out = response.getWriter();

			identity = base64Decode(identity);

			String[] identityInfo = identity.split("\\|");
			if (identityInfo.length != 2) {
				out.write("<script>alert('错误的身份信息');</script>");
				return null;
			}

			String signatureSample = DigestUtil.md5(DigestUtil.digest(identity, DigestUtil.SHA256));
			if (!signature.equals(signatureSample)) {
				out.write("<script>alert('错误的签名信息');</script>");
				return null;
			}

			String username = identityInfo[0];
			String dateinfo = identityInfo[1];
			Date date = DateUtils.parseDate(dateinfo, "yyyyMMddHHmmss");

			if (!DateUtils.addSeconds(date, 30).after(Calendar.getInstance().getTime())) {
				out.write("<script>alert('认证请求已超时,请刷新重试！');</script>");
				return null;
			}

			AuthUtil.login(username);
			return view("redirect:" + url);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@RequestMapping("logout")
	@ResponseBody
	public MessageBean logout(HttpServletRequest request, HttpSession session) {
		try {
			this.userService.logout();
			AuthUtil.logout();
			//session.invalidate();
			session = null;
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				Cookie cookie = cookies[0];
				cookie.setMaxAge(0);
			}
			return super.buildMessage(MESSAGE_TYPE_SUCCESS, "用户登出成功");
		} catch (UserServiceException e) {
			log.error(e.getMessage(), e);
			return super.buildMessage(MESSAGE_TYPE_ERROR, e.getErrorCode(), e.getErrorMsg());
		}
	}

	/** 自动填充 */
	private Map<String, String> autoFill(HttpServletRequest request) {
		Map<String, String> tokenMap = MapBuilder.newHashMap();
		Cookie cookie = getCookie(request);
		if (null != cookie) {
			String value = cookie.getValue();
			String token = base64Decode(value);
			if (StringUtils.isNotBlank(token)) {
				String[] vals = token.split(",");
				tokenMap.put("username", vals[0]);
				tokenMap.put("password", vals[1]);
			}
		}
		return tokenMap;
	}

	/** 记住密码 */
	private void remember(String username, String password, HttpServletRequest request, HttpServletResponse response) {
		if ("true".equals(request.getParameter("remember"))) {
			String val = username + "," + password;
			String token = base64Encode(val);

			if (null == getCookie(request)) {
				Cookie cookie = new Cookie(SessionConstant.LOGIN_USER, token);
				cookie.setMaxAge(COOKIE_MAX_TIME);
				cookie.setPath("/");
				response.addCookie(cookie);
			} else {
				Cookie cookie = getCookie(request);
				cookie.setMaxAge(COOKIE_MAX_TIME);
				cookie.setValue(token);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		} else {
			Cookie cookie = getCookie(request);
			if (null != cookie) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
	}

	/** 获得客户端cookie */
	private Cookie getCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (SessionConstant.LOGIN_USER.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
	
	@RequestMapping("validateCodeSend")
	@ResponseBody
	public MessageBean validateCodeSend(String mobile, HttpServletRequest request) {
		if (!CyyunCheckParams.isMobile(mobile)) {
			return super.buildMessage(MESSAGE_TYPE_ERROR,"手机号码格式错误！");
		}
		UserBean userBean = (UserBean) request.getSession().getAttribute("LoggingUser");
		if (!mobile.equals(userBean.getMobile())) {
			return super.buildMessage(MESSAGE_TYPE_ERROR,"手机号与帐号预留手机号不一致！请重新输入");
		}
		//四位的随机数作为验证码
		Integer messageCode = 0;
		messageCode = (new Random()).nextInt(9999);
		if (messageCode < 1000) {
			messageCode += 1000;
		}
		SmsBean smsbean = new SmsBean();
		smsbean.setContent("【舆情监测服务】您的验证码是" + messageCode + "，请在10分钟内完成验证。如非本人操作，请忽略本短信。");
		smsbean.setReceiver(mobile);
		smsbean.setReceiverName(userBean.getRealName());
		smsbean.setSystemCode("FM3.0");
		try {
			messageService.send(smsbean);
			//记录验证信息
			validatecodeMap.put(userBean.getUsername(), messageCode + "_" + System.currentTimeMillis());
		} catch (MessageServiceException e) {
			log.error("发送验证码失败：", e);
			return super.buildMessage(MESSAGE_TYPE_ERROR, "发送验证码失败！");
		}
		return super.buildMessage(MESSAGE_TYPE_SUCCESS, "发送验证码成功");
	}
	
	@RequestMapping("validateCodeCheck")
	@ResponseBody
	public MessageBean validateCodeCheck(String securitycode, HttpServletRequest request, HttpServletResponse response) {
		UserBean userBean = (UserBean) request.getSession().getAttribute("LoggingUser");
		for (Entry<String, String> entry : this.validatecodeMap.entrySet()) {
			if (userBean.getUsername().equals(entry.getKey())) {
				String validateCode = entry.getValue().split("_")[0];
				long validateCodeTime = 0L;
				try {
					validateCodeTime = Long.parseLong(entry.getValue().split("_")[1]);
					if (validateCode.equals(securitycode)) {
						long betweenDate = (System.currentTimeMillis() - validateCodeTime) / (1000 * 60); //计算间隔多少分钟;
						if (betweenDate < 10) {//验证码有效期为10分钟
							LoginInfoBean loginInfo = (LoginInfoBean) request.getSession().getAttribute("loginInfo");
							//shiro身份验证通过
							this.userService.login(loginInfo);
							AuthUtil.login(loginInfo.getUsername(), loginInfo.getPassword());
							return super.buildMessage(MESSAGE_TYPE_SUCCESS, "验证码Check成功");
						} else {
							return super.buildMessage(MESSAGE_TYPE_ERROR, "验证码已失效，请重新获取。");
						}
					} else {
						return super.buildMessage(MESSAGE_TYPE_ERROR, "验证码输入错误");
					}
				} catch (Exception e) {
					log.error("验证码Check异常：" + e.getMessage(), e);
					e.printStackTrace();
				}
			}
		}
		return super.buildMessage(MESSAGE_TYPE_ERROR, "验证码Check失败");
	}
	
	public static String base64Encode(String target) {
		try {
			if (target == null)
				return null;
			byte[] bytes;
			bytes = target.getBytes(Charsets.UTF_8);
			byte[] result = Base64.encodeBase64(bytes);
			return new String(result);
		} catch (Exception e) {
			return null;
		}
	}

	public static String base64Decode(String target) {
		try {
			if (target == null)
				return null;

			if (!Base64.isBase64(target)) {
				return null;
			}

			byte[] orig = Base64.decodeBase64(target);

			return new String(orig);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		String identity = "adminauto|20161101113730";
//		String identity = "admin|123456";
		System.out.println(base64Encode(identity));
		System.out.println(DigestUtil.md5(DigestUtil.digest(identity, DigestUtil.SHA256)));

		String[] identityInfo = identity.split("\\|");
		String username = identityInfo[0];
		System.out.println(username);
		String dateinfo = identityInfo[1];
		System.out.println(dateinfo);

		Date date = DateUtils.parseDate(dateinfo, "yyyyMMddHHmmss");
		System.out.println(DateUtils.addSeconds(date, 30).after(Calendar.getInstance().getTime()));
	}
}
