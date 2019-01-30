package com.cyyun.fm.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.common.core.util.SetBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;

public class BaseSupport {

	@Autowired
	protected CustomerConfigService customerConfigService;

	/**
	 * <h3>获取客户文章Stage</h3>
	 * 
	 * @param cid
	 * @return
	 */
	public String getArticleStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}

	/**
	 * 图片位置标签<CYYUN_IMAGE1>或<CYYUN_IMAGE1 width="32" height="24">
	 * @param Map<String, Object>
	 * @return
	 */
	public Map<String, Object> replaceImageUrlsToUnusual(String content) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(content)) {
			Matcher matcher = Pattern.compile("<CYYUN_IMAGE.*?>").matcher(content);
			List<String> list = new ArrayList<String>();
			while (matcher.find()) {
				/**匹配到的标签*/
				String value = matcher.group();
				list.add(value);
			}
			map.put("positionImageUrlsList", list);
			/**内容的图片位置标签替换为<~###~>*/
			content = matcher.replaceAll("<~###~>");
		}
		map.put("content", content);
		return map;
	}
	
	/**
	 * <h3>文字标红(HTML标签)</h3>
	 * 
	 * @param content 被标记的内容
	 * @param keywords 需要标红的关键字(单字匹配)
	 * @return
	 * @throws ArticleServiceException
	 */
	public String markKeyword(String content, Set<String> keywords) throws ArticleServiceException {
		if (StringUtils.isNotBlank(content)) {
			content = content.replace("#……#", "");
			content = content.replace("＃……＃", "");
			if(CollectionUtils.isNotEmpty(keywords)){
				for (String keyword : keywords) {
					if (StringUtils.isNoneBlank(keyword)) {
						if("\\".equals(keyword)){
							keyword = "\\\\";
						}
						content = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(content).replaceAll("＃……＃" + keyword + "#……#");
					}
				}
				content = content.replace("＃……＃", "<font color='red'>");
				content = content.replace("#……#", "</font>");
				return content;
			}
		}
		return content;
	}
	
	/**
	 * 替换文章内容中的图片
	 * <h3>图片显示在原文的内容位置</h3>
	 * @param content
	 * @param imageUrlsPosition
	 * @param articleType 因微信文章图片做了防盗链处理，这里通过反向代理方式避免这个问题。 媒体类型6为微信
	 * @return
	 */
	public String replaceImageUrls(String content,List<String> listImageUrls,Map<String, String> imageUrlsPosition,Integer articleType) {
		if (StringUtils.isNotBlank(content)) {
			/**图片位置标签从<*...*>替换回原来的位置标签*/
			for (String string : listImageUrls) {
				content = content.replaceFirst("<~###~>", string);
			}
			
			if (imageUrlsPosition != null) {
				for (Entry<String,String> entry : imageUrlsPosition.entrySet()) {
					if (articleType == 6) {
						/**如果匹配CYYUN_IMAGE1>*/
						content = content.replaceFirst(entry.getKey()+">", "img src='http://read.html5.qq.com/image?src=forum&amp;q=5&amp;r=0&amp;imgflag=7&amp;imageUrl="+entry.getValue() + "'>");
						/**如果匹配CYYUN_IMAGE1 width="24" height="24">，即CYYUN_IMAGE1 ,注意空格*/
						content = content.replaceFirst(entry.getKey()+" ", "img src='http://read.html5.qq.com/image?src=forum&amp;q=5&amp;r=0&amp;imgflag=7&amp;imageUrl="+entry.getValue()+"' ");
					} else {
						/**如果匹配CYYUN_IMAGE1>*/
						content = content.replaceFirst(entry.getKey()+">", "img src='"+entry.getValue() + "'>");
						/**如果匹配CYYUN_IMAGE1 width="24" height="24">，即CYYUN_IMAGE1 ,注意空格*/
						content = content.replaceFirst(entry.getKey()+" ", "img src='"+entry.getValue()+"' ");
					}
				}
			}
		}
		return content;
	}
	
	/**
	 * 字符串分解字符(分词)
	 * @param keyword
	 * @return
	 */
	public Set<String> toStringSet(String str) {
		if(StringUtils.isNotBlank(str)){
			Set<String> strSet = SetBuilder.newHashSet();
			String[] strArr = new String[]{};
			//去掉首尾空格，中间多个空格合并成一个空格
			str = str.trim().replaceAll(" +", " ");
			strArr=str.split(" ");
			for (String string : strArr) {
				strSet.add(string);
			}
			//--------分成逐个字符---------
//			char[] charkeys = str.toCharArray();
//			for (char charkey : charkeys) {
//					String key = new String(new char[] { charkey });
//					if (StringUtils.isNotBlank(key)) {
//						strSet.add(key);
//					}
//			}
			//---------------------------
			return strSet;
		}
		return null;
	}
	/**
	 * 标红显示
	 * @param bean
	 * @param strSet
	 * @throws ArticleServiceException
	 */
	public void showLight(ArticleBean bean,Set<String> strSet) throws ArticleServiceException{
		if(bean != null){
			if(StringUtils.isNotBlank(bean.getTitle())){
				String title = bean.getTitle().replaceAll("[\\t\\n\\r]", "");
				bean.setTitle(markKeyword(title, strSet));
			}
			if(StringUtils.isNotBlank(bean.getContent())){
				String content = bean.getContent().replaceAll("[\\t\\n\\r]", "");
				bean.setContent( markKeyword(content, strSet));
			}
			if(StringUtils.isNotBlank(bean.getAbContent())){
				String content = bean.getAbContent().replaceAll("[\\t\\n\\r]", "");
				bean.setAbContent( markKeyword(content, strSet));
			}
		}
	}
	/**
	 * 标红显示
	 * @param beans
	 * @param str
	 * @throws ArticleServiceException
	 */
	public void showLight(List<ArticleBean> beans,String str) throws ArticleServiceException{
		if(CollectionUtils.isNotEmpty(beans)){
			Set<String> keywordSet = toStringSet(str);
			for (ArticleBean article : beans) {
				showLight(article, keywordSet);
			}
		}
	}
	
	public static void main(String[] args) throws ArticleServiceException {
//		ArticleBean bean = new ArticleBean();
//		bean.setTitle("水分");
//		bean.setContent("公告期为2015年10月28日至2015年11月5日（7个工作日）。联系人：宁波市鄞州区环境保护局管理科联系电话：0574-87525619 传真：0574-87525602通讯地址：宁波市鄞州区新城区惠风东路568号B楼行政复议与行政诉讼权利告知：公民、法人或者其他组织认为公告的建设项目竣工环保验收决定侵犯其合法权益的，可以自公告期限届满之日起六十日内提起行政复议，也可以自公告期限届满之日起六个月内提起诉讼。");
		BaseSupport support = new BaseSupport();
////		support.showLight(bean, support.toStringSet("卫生执法;医疗事故;医患纠纷;药品回扣;疫情;传染病;看病难;看病贵;医药代表;以药养医;新医改;医疗体制;"));
//		support.showLight(bean, support.toStringSet("宁波-上海"));
//		System.out.println("title="+bean.getTitle());
//		System.out.println("content="+bean.getContent());
		Set<String> set = new HashSet<String>();
//		set.add("\\\\");
		set.add("(武当)");
		System.out.println(support.markKeyword("武当当", set));
		
	}
}