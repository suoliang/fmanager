package com.cyyun.fm.homepage.controller;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.TypeReference;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.MapKeyComparator;
import com.cyyun.base.util.TagUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.fm.constant.BoardType;
import com.cyyun.fm.constant.ModuleInterfaceType;
import com.cyyun.fm.homepage.task.QueryPlatformTalNumTask;
import com.cyyun.fm.service.QueryLogService;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.service.bean.ModuleBean;
import com.cyyun.fm.service.exception.WebpageBoardException;

/**
 * <h3>首页控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/home")
public class HomepageController extends BaseController {

	@Autowired
	private HomepageSupport support;
	
	@Autowired
	private ConstUtil constUtil;
	
	@Autowired
	private TagUtil tagUtil;
	
	@Autowired
	private QueryLogService queryLogService;


	protected static Logger logger = Logger.getLogger(HomepageController.class);
	
	@RequestMapping("test")
	public ModelAndView test() {
		return view("/test");
	}

	@RequestMapping("item1")
	public ModelAndView item1() {
		return view("/inputItem1");
	}

	@RequestMapping("item2")
	public ModelAndView item2() {
		return view("/inputItem2");
	}

	@RequestMapping("item3")
	public ModelAndView item3() {
		return view("/inputItem3");
	}

	//###########################################   index   ###########################################//

	/**
	 * <h3>首页页面</h3>
	 * 
	 * @return 页面
	 */
	//@RequestMapping("index")
	public ModelAndView index() {
		return view("/homepage/index");
	}

	/**
	 * <h3>配置页面</h3>
	 * 
	 * @return 页面
	 */
	@RequestMapping("index")
	public ModelAndView config() {
		return view("/homepage/config");
	}

	/**
	 * <h3>检索用户所有可见板块</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listViewBoard")
	@ResponseBody
	public MessageBean listViewBoard() {
		try {
			List<BoardBean> boards = support.listUserBoard(FMContext.getCurrent().getUserId());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "检索网页板块成功", boards);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块失败");
		}
	}

	/**
	 * <h3>检索用户所有可配置板块</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listAllBoard")
	@ResponseBody
	public MessageBean listAllBoard() {
		try {
			List<BoardBean> boards = support.listUserBoard(FMContext.getCurrent().getUserId());

			return buildMessage(MESSAGE_TYPE_SUCCESS, "检索网页板块成功", boards);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块失败");
		}
	}

	/**
	 * <h3>检索模板配置</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listModule")
	@ResponseBody
	public MessageBean listModule() {
		try {
			List<ModuleBean> modules = support.listModule();

			return buildMessage(MESSAGE_TYPE_SUCCESS, "检索模块成功", modules);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索模块失败");
		}
	}

	/**
	 * <h3>首页页面</h3>
	 * 
	 * @return 页面
	 */
	@RequestMapping("input")
	public ModelAndView boardConfig(Integer boardId) {
		ModelAndView view = view("/homepage/input");
		try {
			List<ModuleBean> modules = support.listModule();
			support.setInputParam(modules);

			if (boardId != null) {
				view.addObject("board", support.findBoard(boardId));
			}

			return view.addObject("modules", modules);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "检索板块配置失败");
		}
	}

	/**
	 * 跳转模块专题tab
	 * @return
	 */
	@RequestMapping(value = { "/topic" })
	public ModelAndView forwardTopic(HttpServletRequest request, String userConfigInfo) {
		ModelAndView view = view("/homepage/topic-board-input");
		try {
			final String topicId = request.getParameter("topicId") == null ? "" : request.getParameter("topicId");
			final List<Map<String, String>> list = JsonUtil.getObject(userConfigInfo, new TypeReference<List<Map<String, String>>>() {
			});
			if (topicId != null) {
				view.addObject("topicId", topicId);
			}
			List<CustTopicBean> custTopicList = support.queryCustTopicList();
			if (custTopicList != null) {
				custTopicList = ListBuilder.newRecombinedList(custTopicList, new ListBuilder.Combiner<CustTopicBean>() {

					public boolean isUseable(CustTopicBean condition) {
						for (Map<String, String> param : list) {
							if (!topicId.equals(param.get("topicId")) && ("" + condition.getId()).equals(param.get("topicId"))) {
								return false;
							}
						}
						return true;
					}
				});
			}
			return view.addObject("topicList", custTopicList).addObject("startTimeInput", request.getParameter("startTimeInput")).addObject("endTimeInput", request.getParameter("endTimeInput"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "专题板块配置加载失败");
		}
	}
	
	/**
	 * 面板模块专题文章列表
	 * @return
	 */
	@RequestMapping(value = { "/topicView" })
	public ModelAndView forwardTopicView(HttpServletRequest request) {
		ModelAndView view = view("homepage/topic-board-view");
		try {
			Long startTime = System.currentTimeMillis();
			Map<String, String> queryMap = CyyunStringUtils.request2Map(request.getParameterMap());
			Integer id=Integer.valueOf(queryMap.get("topicId"));
			CustTopicBean custTopicBean = support.findCustTopic(id);
			if (custTopicBean.getId() != null && custTopicBean.getStatus() != 0) {
				view.addObject("articles", support.queryCustTopicArticles(request.getParameterMap(), request));
			} else if (custTopicBean.getStatus()!=null && custTopicBean.getStatus() == 0) {	
				view.addObject("isDisable", true);
				view.addObject("articles", ListBuilder.newArrayList());
			} else {
				view.addObject("isDelete", true);
				view.addObject("articles", ListBuilder.newArrayList());
			}
			Long endTime = System.currentTimeMillis();
			
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题板块查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "TopicBoard", 3000L);
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载专题文章失败");
		}
	}
	
	/**
	 * 跳转推荐模块tab
	 * @return
	 */
	@RequestMapping(value = { "/recommended" })
	public ModelAndView recommended(final Integer type, String userConfigInfo) {
		ModelAndView view = view("/homepage/recommended-board-input");
		try {
			List<ConstantBean> recommendeds = constUtil.list("recommendedCategory");
			List<ConstantBean> filterRecommendeds = new ArrayList<ConstantBean>();
			Integer customerId = FMContext.getCurrent().getCustomerId();
			for (ConstantBean constantBean : recommendeds) {
				//目前“热点”板块只对国信办客户显示
				//3 : 热点板块标识
				//1112 : 国信办客户cid
				if ("3".equals(constantBean.getValue())) {
					if (Integer.parseInt("1112") == customerId) {
						filterRecommendeds.add(constantBean);
					}
				}else{
					filterRecommendeds.add(constantBean);
				}
			}
			final List<Map<String, String>> list = JsonUtil.getObject(userConfigInfo, new TypeReference<List<Map<String, String>>>() {
			});
			if (type != null) {
				view.addObject("type", type);
			}

			filterRecommendeds = ListBuilder.newRecombinedList(filterRecommendeds, new ListBuilder.Combiner<ConstantBean>() {

				public boolean isUseable(ConstantBean constantBean) {
					for (Map<String, String> param : list) {
						//start----------如用户已在首页系统tab中添加了“词云”板块，则不让“词云”显示在推荐tab中
						if (constantBean.getName().equals(param.get("boardName"))) {
							return false;
						}
						//eng
						if (!("" + type).equals(param.get("type")) && ("" + constantBean.getValue()).equals(param.get("type"))) {
							return false;
						}
					}
					return true;
				}
			});
			return view.addObject("recommendeds", filterRecommendeds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载推荐失败");
		}
	}

	//###########################################   board   ###########################################//

	/**
	 * <h3>创建页面板块</h3>
	 * 
	 * @param board 网页板块
	 */
	@RequestMapping(value = { "saveBoard" })
	@ResponseBody
	public MessageBean saveBoard(BoardBean board) {
		try {
			board = support.saveBoard(board, FMContext.getCurrent().getUserId());

			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存网页板块成功", board);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存网页板块失败");
		}
	}

	/**
	 * <h3>删除网页板块</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "deleteBoard" })
	@ResponseBody
	public MessageBean deleteBoard(Integer boardId) {
		try {
			support.deleteBoard(boardId, FMContext.getCurrent().getUserId(), BoardType.HOMEPAGE);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除网页板块成功");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除网页板块失败");
		}
	}

	/**
	 * <h3>查找网页板块</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "findBoard" })
	@ResponseBody
	public MessageBean findBoard(Integer id) {
		try {
			BoardBean board = support.findBoard(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查找网页板块成功", board);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查找网页板块失败");
		}
	}

	/**
	 * <h3>更新网页板块索引</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "updateBoardIndex" })
	@ResponseBody
	public MessageBean updateBoardIndex(Integer index, Integer boardId) {
		try {
			support.updateBoardIndex(index, boardId, FMContext.getCurrent().getUserId());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "更新网页板块索引成功");

		} catch (WebpageBoardException e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "更新网页板块索引失败");
		}
	}

	//###########################################   system   ###########################################//

	@RequestMapping("input-def-comp")
	public ModelAndView inputDefComp(Integer def, String boardName) {
		ModelAndView view = view("/homepage/input-def-comp");
		try {
			if (def != null) {
				view.addObject("def", def);
			}
			if (boardName != null) {
				boardName = URLDecoder.decode(boardName, "UTF-8");
				view.addObject("boardName", boardName);
			}
			List<BoardBean> hideDefBoards = support.listHideDefBoard(FMContext.getCurrent().getUserId());
			view.addObject("hideDefBoards", hideDefBoards);
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "系统配置页请求失败");
		}
	}

	@RequestMapping("view-def-comp")
	public ModelAndView viewDefComp(Integer def) {
		try {
			ModuleBean module = support.findBoard(def).getModule();
			return view("forward:" + module.findInterface(ModuleInterfaceType.VIEW).getUrl());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "系统视图页请求失败");
		}
	}

	@RequestMapping("more-def-comp")
	public ModelAndView moreDefComp(Integer def) {
		try {
			ModuleBean module = support.findModule(def);
			return view("forward:" + module.findInterface(ModuleInterfaceType.MORE).getUrl());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "系统更多页请求失败");
		}
	}

	@RequestMapping("situation")
	public ModelAndView situation(String viewName) {
		try {
			Long startTime = System.currentTimeMillis();
			Map<String, Long> situation = support.getSituation();
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------舆情概况【系统】查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "SituationSystem", 3000L);
			DecimalFormat df = new DecimalFormat("#,###");
			String total = df.format(situation.get("total"));
			situation.remove("total");
			situation = MapKeyComparator.sortMapByKey(situation);
//			for (Entry<String, Long> entry : situation.entrySet()) {
//				total += entry.getValue();
//			}
			String view = "/homepage/comp-situation";
			if (StringUtils.isNoneBlank(viewName)) {
				view=view+"-"+viewName;
			}
			return view(view)//
					.addObject("situation", situation)//
					.addObject("total", total)//
					.addObject("platformTotalNumToday", QueryPlatformTalNumTask.platformTotalNumToday)//
					.addObject("data", JsonUtil.toJson(situation.entrySet()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "舆情概况加载失败");
		}
	}
	
	/**
	 * 获取平台今日采集数
	 * @return
	 */
	@RequestMapping("platformTotalNumToday")
	@ResponseBody
	public MessageBean platformTotalNumToday() {
		try {
			String platformTotalNumToday  = QueryPlatformTalNumTask.platformTotalNumToday;
			platformTotalNumToday = platformTotalNumToday.replaceAll(",", "");
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询平台今日采集成功", platformTotalNumToday);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查询平台今日采集失败");
		}
	}
	
	@RequestMapping("alert")
	public ModelAndView alert() {
		return view("/homepage/comp-alert");
	}

	@RequestMapping("inform")
	public ModelAndView inform() {
		try {
//			Integer archive = support.getArchive();
			Long startTime = System.currentTimeMillis();
			Integer alert = support.getAlert();
			Map<String, Long> situation = support.getSituation();
			DecimalFormat df = new DecimalFormat("#,###");
			String total = df.format(situation.get("total"));
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------系统通知【系统】查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "NoticeSystem", 3000L);
			return view("/homepage/comp-inform").addObject("archiveInfo", null).addObject("alertInfo", alert).addObject("total", total);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "系统通知加载失败");
		}
	}
	
	/**
	 * 热词云图
	 * @return
	 * @author LIUJUNWU
	 */
	@RequestMapping(value = { "/hotKeyWords" })
	public ModelAndView hotKeyWords(HttpSession session) {
		ModelAndView view = view("/homepage/hotKeyWords");
		List<Entry<String, Double>> res = new ArrayList<Map.Entry<String,Double>>();
		try {
			Long startTime = System.currentTimeMillis();
			res=support.getHotKeyWordsDatas(session);
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------词云【系统】查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "WordCloud", 4000L);
		} catch (ArticleServiceException e) {
			e.printStackTrace();
		}
		return view.addObject("data", JsonUtil.toJson(res));
	}
	
	/**取值用message*/
	@ResponseBody
	@RequestMapping("getSimilarNum")
	public MessageBean getSimilarnum(String guid,String guidGroup) {
		String similarnum = tagUtil.getSimilarnum(guid, guidGroup);
		if (StringUtils.isBlank(similarnum) || "0".equals(similarnum)) {
			return super.buildMessage(MESSAGE_TYPE_SUCCESS, "");
		}
		return super.buildMessage(MESSAGE_TYPE_SUCCESS, similarnum);
	}
	
	
	
}