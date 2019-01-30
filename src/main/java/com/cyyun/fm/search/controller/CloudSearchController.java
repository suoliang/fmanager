package com.cyyun.fm.search.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.service.SpiderService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.fm.search.bean.CloudSearchParam;
import com.cyyun.fm.search.bean.CloudSearchShowEnum;
import com.cyyun.fm.search.bean.CloudSearchUrlEnum;
import com.cyyun.fm.search.bean.CloudSearchView;
import com.cyyun.fm.search.bean.EngineParam;
import com.cyyun.fm.service.FmEngineService;

/**
 * <h3>云搜索</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "CloudSearch" })
public class CloudSearchController extends BaseController {
	@Autowired
	private SpiderService spiderService;
	@Autowired
	private FmEngineService fmEngineService;

	//搜索引擎map  1.id , name
	//private Map<Integer, String> map = new HashMap<Integer, String>();
	
	@RequestMapping("toFrame")
	public ModelAndView toFrame() {
		return view("/search/frame-index");
	}
	
	/**
	 * URL展示的左侧和右侧的链接
	 * @param urlLeft
	 * @param urlRight
	 * @return
	 */
	@RequestMapping("toFrameList")
	public ModelAndView toFrameList(String urlLeft,String urlRight) {
		return view("/search/frame-list").addObject("urlLeft", urlLeft).addObject("urlRight", urlRight);
	}
	
	@RequestMapping("toMiddle")
	public String toMiddle() {
		return "/search/middle";
	}
	
	/**
	 * 加载搜索引擎
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("index")
	public ModelAndView index() throws Exception {
		try {
			Map<String, String> searchShowMap = CloudSearchShowEnum.getSearchShowMap();
			Map<String, String> searchUrlMap = CloudSearchUrlEnum.getSearchUrlMap();
			return view("/search/frame-index").addObject("searchShowMap", searchShowMap).addObject("defaultMap", MapBuilder.put("1","1").build())
					.addObject("searchUrlMap", searchUrlMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "搜索引擎加载失败");
		}
	}

	/**
	 * 搜索
	 * @param cloudSearchParam
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Search")
	public ModelAndView Search(CloudSearchParam cloudSearchParam,HttpServletRequest request) throws Exception { 
		String keyword = "";
		if (cloudSearchParam.getKeyword()!=null) {
			keyword = new String(cloudSearchParam.getKeyword().getBytes("ISO-8859-1"), "UTF-8");
		}
		String encodeKeyword = keyword;
		System.out.println(encodeKeyword);
		
		String decodeKeyword = URLDecoder.decode(encodeKeyword, "UTF-8");
		System.out.println(decodeKeyword);
		
		Map<String, String> searchShowMap = CloudSearchShowEnum.getSearchShowMap();
		Map<String, String> searchUrlMap = CloudSearchUrlEnum.getSearchUrlMap();
		Map<String, String> defaultMap = new HashMap<String, String>();
		
		List<EngineParam> engineIds = cloudSearchParam.getEngineIds();
		String url = "";
		String urlLeft = "";
		String urlRight = "";
		if (CollectionUtils.isNotEmpty(engineIds)) {
			if (engineIds.size() == 1) {
				Integer engineId = engineIds.get(0).getEngineId();
				url = CloudSearchUrlEnum.parseCode(engineId.toString());
				defaultMap.put(engineId.toString(), "1");
				return view("/search/frame-index").addObject("keyword", decodeKeyword).addObject("urlLeft", url + encodeKeyword).addObject("searchShowMap", searchShowMap)
						.addObject("defaultMap", defaultMap).addObject("searchUrlMap", searchUrlMap);
			}
			for (EngineParam engineParam : engineIds) {
				url += CloudSearchUrlEnum.parseCode(engineParam.getEngineId().toString()) + ",";
				defaultMap.put(engineParam.getEngineId().toString(), "1");
			}
			String[] strings = url.split("\\,");
			urlLeft = strings[0];
			urlRight = strings[1];
		}
		
		return view("/search/frame-index").addObject("keyword", decodeKeyword).addObject("urlLeft", urlLeft + encodeKeyword).addObject("urlRight", urlRight + encodeKeyword)
				.addObject("searchShowMap", searchShowMap).addObject("defaultMap", defaultMap).addObject("searchUrlMap", searchUrlMap);
	}
	
	/**
	 * 搜索
	 * @param cloudSearchParam
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping("Search")
//	public ModelAndView Search(CloudSearchParam cloudSearchParam,HttpServletRequest request) throws Exception { //spiderId
//		String keyword = "";
//		if (cloudSearchParam.getKeyword()!=null) {
//			keyword = new String(cloudSearchParam.getKeyword().getBytes("ISO-8859-1"), "UTF-8");
//		}
//		SpiderQueryBean queryBean=new SpiderQueryBean();
//		//1.每个搜索引擎的条数（下一页累加）  2. 最后时间
//		queryBean.setEndTime(new Date());
//		queryBean.setStartTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));//对数据没影响
//		queryBean.setPageSize(1);
//		queryBean.setNeedSort(false);
//		Map<Integer, Integer> ids= new HashMap<Integer, Integer>();
//		if (cloudSearchParam.getEngineIds().size()!=0) {
//			for (int i = 0; i < cloudSearchParam.getEngineIds().size(); i++) {
//				int a=Integer.parseInt((cloudSearchParam.getEngineIds().get(i).getEngineId().toString()));
//				ids.put(a, 0);
//			}
//		}
//		
//		queryBean.setSpiderId(ids);
//		queryBean.setKeyword(keyword);
//		queryBean.setPageNo(cloudSearchParam.getPageNo());//设置页数
//		CyyunCheckParams.checkParams(queryBean);
//		List<ArticleBean> list = new ArrayList<ArticleBean>();
//		try {
//			list = spiderService.searchArticleByKeywords(queryBean);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return message(MESSAGE_TYPE_ERROR, "全网查询文章失败");
//		}
//		List<ArticleBean> articleBeans=distinctArticleBeans(list);
//		List<CloudSearchView> cloudSearchViews=AddBoardIds(articleBeans);
//		//自然排序
//		SortUtil.sortByMethod2(cloudSearchViews, "getTitle", false);
//		
//		List<Integer> pagingList=new ArrayList<Integer>();
//		Boolean flag=true;//搜索关键字为空时特殊处理的标记
//		if (cloudSearchParam.getKeyword()!="") {
//			//分页
//			int start=1;
//			int end=11;
//			int currentpageNo=cloudSearchParam.getPageNo();
//			if(currentpageNo>=6){
//				start=start+(currentpageNo-6);
//				end=end+(currentpageNo-6);
//			}
//			if(cloudSearchViews.size()==0){
//				flag=false;
//			}
//			for (int i = start; i < end; i++) {
//				pagingList.add(i);
//			}
//		}else{
//			flag=false;
//		}
//		return view("/search/cloudSearch-index").addObject("pageInfo", cloudSearchViews).addObject("map", map)
//				.addObject("pagingList", pagingList).addObject("keyword", keyword).addObject("engineIds", ids)
//				.addObject("currentpageNo", cloudSearchParam.getPageNo()).addObject("flag", flag);
//	}
	
	/**
	 * 多个引擎文章去重
	 * @param articleBeancs
	 * @return
	 */
	public List<ArticleBean> distinctArticleBeans(List<ArticleBean> articleBeancs) {
		Map<String, ArticleBean> map = new HashMap<String, ArticleBean>();
		List<ArticleBean> ArticleBeans = new ArrayList<ArticleBean>();;
		if (articleBeancs!=null) {
			for (int i = 0; i < articleBeancs.size(); i++) {
				map.put(articleBeancs.get(i).getUrl(), articleBeancs.get(i));
			}
	        for (Map.Entry<String, ArticleBean> entry : map.entrySet()) {  
	        	ArticleBeans.add(entry.getValue());
	        }  
		}
		return ArticleBeans;
	}
	/**
	 * 来源展示多个引擎
	 * @param articleBeancs
	 * @return
	 */
	public List<CloudSearchView> AddBoardIds(List<ArticleBean> articleBeancs) {
		List<CloudSearchView> cloudSearchViews=new ArrayList<CloudSearchView>();
		if (articleBeancs!=null) {
			//cloudSearchViews=BeanUtil.copyList(articleBeancs, CloudSearchView.class);
			for(ArticleBean articleBean:articleBeancs){
				CloudSearchView target = new CloudSearchView();
				BeanUtils.copyProperties(articleBean, target);
				cloudSearchViews.add(target);
			}
			List<CloudSearchView> cloudSearchViews1 = cloudSearchViews;
			for (int i = 0; i < cloudSearchViews.size(); i++) {
				for (int j = 0; j < cloudSearchViews1.size(); j++) {
					if (cloudSearchViews.get(i).getUrl()==cloudSearchViews1.get(j).getUrl()) {
						cloudSearchViews.get(i).getBoardIds().add(cloudSearchViews.get(i).getBoardId());
						cloudSearchViews.get(i).getBoardIds().add(cloudSearchViews1.get(j).getBoardId());
						
						//cloudSearchViews.get(i).getBoardIds()集合去重
						for (int t = 0; t < cloudSearchViews.get(i).getBoardIds().size(); t++)  //外循环是循环的次数
			            {
			                for (int l = cloudSearchViews.get(i).getBoardIds().size() - 1 ; l > t; l--)  //内循环是 外循环一次比较的次数
			                {
			                    if (cloudSearchViews.get(i).getBoardIds().get(t) == cloudSearchViews.get(i).getBoardIds().get(l))
			                    {
			                    	cloudSearchViews.get(i).getBoardIds().remove(l);
			                    }
			                }
			            }
					}
				}
			}
		}
		return cloudSearchViews;
	}
	
	

	
	
}
