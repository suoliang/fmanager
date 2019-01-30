package com.cyyun.fm.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.service.bean.ArticleCustomerAttrBean;
import com.cyyun.base.service.bean.ArticleHomeWebsite;
import com.cyyun.base.service.bean.ArticleIndustryBean;
import com.cyyun.base.service.bean.ArticleReplyBean;
import com.cyyun.fm.base.controller.BaseSupport;

/**
 * <h3>文章信息Vo</h3>
 * 
 * @author yaodw
 * @version 1.0.0
 * 
 */
public class ArticleVo implements ArticleVoInter{

	private BaseSupport support;
	
	private Set<String> keywordSet;
	
	public ArticleVo(){
		
	}
	
	public ArticleVo(BaseSupport support, String keyword){
		this.support = support;
		this.keywordSet = support.toStringSet(keyword);
	}
	private static final Logger logger = LoggerFactory
			.getLogger(ArticleVo.class);
	
	/*Attributes related to customer*/
	private Map<String, ArticleCustomerAttrBean> custAttrs;
	/** 1论坛，2博客，3新闻，4微博，5平面媒体，6微信 ， 7App新闻 */
	private Integer style;
	/**
	 * 微博类型，仅在style为4（微博）时有效。1为原创，2为转载，支持多个，用逗号隔开 <br>
	 * 原tid
	 */
	private Integer weiboType;
	/** 版面ID */
	private Integer boardId;
	/** 版面 URL */
	private String boardUrl;
	/** 版面名称 */
	private String boardName;
	/** 根据标题算出的hash值，用于同主题匹配 */
	private String matchCode;
	/** 热度 */
	private Long hot;
	/** 地域Id列表 */
	private Integer[] areaIds;
	/*地域名称集合*/
//	private String areanames;
	/** 行业Id列表 */
	private Integer[] industryIds;
	/*行业名称集合*/
//	private String industrynames;
	/** 行业对象 */
	private List<ArticleIndustryBean> industrys;
	/** 站点Id 对应fid */
	private Integer websiteId;
	/** 父站点Id 对应rfid */
	private Integer parentWebsiteId;
	/** 站点名称 */
	private String websiteName;
	/** 站点URL */
	private String websiteUrl;
	/** 客户ID */
	private Integer[] custIds;
	/** 专题ID列表 */
	private Integer[] topicIds;
	/** 客户专题id列表 */
	private Integer[] custTopicIds;
	/** 系统专题ID列表 */
	private Integer[] sysTopicIds;
	/** 关键词id列表 */
	private Integer[] keywordIds;
	/** 关键词列表 */
	private String[] keywords;
	/** 摘要 */
	private String abContent;
	/** 文章回复数 */
	private Integer replyCount;
	/** 文章的回复，如果有的话 */
	private List<ArticleReplyBean> replies;
	/** 阅读点击数 */
	private Integer readCount;
	/** 文章转发数 */
	private Integer copyCount;
	/** 文章中图片Url */
	private String[] imageUrls;
	/** ‘赞’数 */
	private Integer likeCount;
	/** 内容中包含的URL */
	private String[] enclosingUrls;
	/** 文章GUID */
	private String guid;
	/** 标题 */
	private String title;
	/** 内容 */
	private String content;
	/** URL */
	private String url;
	/** (cid)+url的md5 hash */
	private String urlHash;
	/** 作者 */
	private String author;
	/** 正负面 */
	private Integer sentiment;
	/**
	 * 0-刚采集的文章 <br>
	 * 10-系统自动分析完成<br>
	 * 11-从垃圾文或回收站恢复<br>
	 * 15-归档<br>
	 * 20-预警<br>
	 * 21-紧急预警<br>
	 * 22-已通知过的文章
	 */
	private Integer stage;
	/** 发文时间 */
	private Date postTime;
	/** 采集时间 */
	private Date spiderTime;
	/** 采集类型 */
	private Integer spiderType;
	/** 系统更新文章时间 */
	private Date updateTime;
	/** 最后一次回帖时间 */
	private Date lastReplyTime;
	/** 用于查询相似文列表，相似文数量 */
	private String guidGroup;
	/*文章全局处理状态值*/
	private Integer status;
	/*文章信息处理处理状态值*/
	private Map<Integer, Integer> archiveStatus;
	/* 文章预警页处理状态值*/
	private Map<Integer, Integer> warnStatus;
	/** 相似文数量，目前仅查询相似文列表可用 */
	private Integer similarCount;
	/** similar 默认为0，表示在系统中是第一次出现这样的文章。值为1时，表示该类似文章在系统中出现不是第一次（即属于相似文） */
	private Integer similar;
	/** 文章分类类型 */
	private Integer[] category;
	private Boolean trashFlag;
	/** 回帖主帖的GUID，仅对回帖有效 */
	private String origGuid;
	/** 文章内容中带有专题关键词的部分截出来的值  */
	private String titleLight;
	private String contentLight;
	/** 0主帖 1回帖 */
	private Integer isReply;
	/*文章储存时间*/
	private Date timeStore;
	/** 操作人员ID */
	private String operatorId;
	/** 查询转帖条件，如果查询转帖，条件是2 */
	private String tid;
	private String sourceSiteName;
	/** 文章在站点首页展示过的对应首页站点表id */
	private Long websiteHomeId;
	/** 文章在最后站点首页展示的时间 */
	private Date timeHome;
	/** 文章所显示的站点首页集合 */
	private List<ArticleHomeWebsite> homeWebsites;
	/**全局的重要性得分*/
	private double importance;
	/**微博转发内容*/
	private String weiboRelayContent;
	/**微博原发内容*/
	private String weiboContent;
	
	/**图片的地址*/
	private Map<String, String> imageUrlsPostion;
	
	
//	/** 地域对象 */
//	private List<ArticleAreaBean> areas;
//	
//	public List<ArticleAreaBean> getAreas() {
//		return areas;
//	}
//
//	public void setAreas(List<ArticleAreaBean> areas) {
//		this.areas = areas;
//	}
//	/** 文章分类得分 */
//	private List<ArticleCategoryScoreBean> categoryScores;
//	
//	public List<ArticleCategoryScoreBean> getCategoryScores() {
//		return categoryScores;
//	}
//
//	public void setCategoryScores(List<ArticleCategoryScoreBean> categoryScores) {
//		this.categoryScores = categoryScores;
//	}

	
	
	
	
	public Map<String, String> getImageUrlsPostion() {
		return imageUrlsPostion;
	}

	public void setImageUrlsPostion(Map<String, String> imageUrlsPostion) {
		this.imageUrlsPostion = imageUrlsPostion;
	}

	public double getImportance() {
		return importance;
	}
	
	public String getWeiboRelayContent() {
		return showWeiboRelayContentLight();
	}

	public void setWeiboRelayContent(String weiboRelayContent) {
		this.weiboRelayContent = weiboRelayContent;
	}

	public String getWeiboContent() {
		return showWeiboContentLight();
	}

	public void setWeiboContent(String weiboContent) {
		this.weiboContent = weiboContent;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public String getSourceSiteName() {
		return sourceSiteName;
	}

	public List<ArticleHomeWebsite> getHomeWebsites() {
		return homeWebsites;
	}

	public void setHomeWebsites(List<ArticleHomeWebsite> homeWebsites) {
		this.homeWebsites = homeWebsites;
	}

	public Long getWebsiteHomeId() {
		return websiteHomeId;
	}

	public void setWebsiteHomeId(Long websiteHomeId) {
		this.websiteHomeId = websiteHomeId;
	}

	public Date getTimeHome() {
		return timeHome;
	}

	public void setTimeHome(Date timeHome) {
		this.timeHome = timeHome;
	}

	public void setSourceSiteName(String sourceSiteName) {
		this.sourceSiteName = sourceSiteName;
	}

	public Integer getSimilar() {
		return similar;
	}

	public void setSimilar(Integer similar) {
		this.similar = similar;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public Date getTimeStore() {
		return timeStore;
	}

	public void setTimeStore(Date timeStore) {
		this.timeStore = timeStore;
	}

	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
	}

	public String getTitleLight() {
		return titleLight;
	}

	public void setTitleLight(String titleLight) {
		this.titleLight = titleLight;
	}

	public String getContentLight() {
		return contentLight;
	}

	public void setContentLight(String contentLight) {
		this.contentLight = contentLight;
	}

	public Boolean getTrashFlag() {
		trashFlag = false;
		if (category != null && category.length > 0) {
			for (Integer integer : category) {
				if (integer == 100000) {
					trashFlag = true;
					break;
				}
			}
		}
		return trashFlag;
	}

	public String getOrigGuid() {
		return origGuid;
	}

	public void setOrigGuid(String origGuid) {
		this.origGuid = origGuid;
	}

	public List<ArticleIndustryBean> getIndustrys() {
		return industrys;
	}

	public void setIndustrys(List<ArticleIndustryBean> industrys) {
		this.industrys = industrys;
	}

	public Integer[] getCategory() {
		return category;
	}

	public void setCategory(Integer[] category) {
		this.category = category;
	}

	public Integer getSimilarCount() {
		if (similarCount != null && similarCount > 0) {
			return similarCount - 1;
		} else {
			return similarCount;
		}
	}

	public void setSimilarCount(Integer similarCount) {
		this.similarCount = similarCount;
	}

	public Integer[] getIndustryIds() {
		return industryIds;
	}

	public void setIndustryIds(Integer[] industryIds) {
		this.industryIds = industryIds;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Map<Integer, Integer> getArchiveStatus() {
		return archiveStatus;
	}

	public void setArchiveStatus(Map<Integer, Integer> archiveStatus) {
		this.archiveStatus = archiveStatus;
	}

	public Map<Integer, Integer> getWarnStatus() {
		return warnStatus;
	}

	public void setWarnStatus(Map<Integer, Integer> warnStatus) {
		this.warnStatus = warnStatus;
	}

	public String getGuidGroup() {
		return guidGroup;
	}

	public void setGuidGroup(String guidGroup) {
		this.guidGroup = guidGroup;
	}

	public Map<String, ArticleCustomerAttrBean> getCustAttrs() {
		return custAttrs;
	}

	public void setCustAttrs(Map<String, ArticleCustomerAttrBean> custAttrs) {
		this.custAttrs = custAttrs;
	}

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

	public Integer getWeiboType() {
		return weiboType;
	}

	public void setWeiboType(Integer weiboType) {
		this.weiboType = weiboType;
	}

	public Integer getBoardId() {
		return boardId;
	}

	public void setBoardId(Integer boardId) {
		this.boardId = boardId;
	}

	public String getBoardUrl() {
		return boardUrl;
	}

	public void setBoardUrl(String boardUrl) {
		this.boardUrl = boardUrl;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getMatchCode() {
		return matchCode;
	}

	public void setMatchCode(String matchCode) {
		this.matchCode = matchCode;
	}

	public Long getHot() {
		return hot;
	}

	public void setHot(Long hot) {
		this.hot = hot;
	}

	public Integer[] getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(Integer[] areaIds) {
		this.areaIds = areaIds;
	}

	public Integer getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(Integer websiteId) {
		this.websiteId = websiteId;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public Integer[] getCustIds() {
		return custIds;
	}

	public void setCustIds(Integer[] custIds) {
		this.custIds = custIds;
	}

	public Integer[] getTopicIds() {
		return topicIds;
	}

	public void setTopicIds(Integer[] topicIds) {
		this.topicIds = topicIds;
	}

	public Integer[] getCustTopicIds() {
		return custTopicIds;
	}

	public void setCustTopicIds(Integer[] custTopicIds) {
		this.custTopicIds = custTopicIds;
	}

	public Integer[] getSysTopicIds() {
		return sysTopicIds;
	}

	public void setSysTopicIds(Integer[] sysTopicIds) {
		this.sysTopicIds = sysTopicIds;
	}

	public Integer[] getKeywordIds() {
		return keywordIds;
	}

	public void setKeywordIds(Integer[] keywordIds) {
		this.keywordIds = keywordIds;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public String getAbContent() {
		return showAbConentLight();
	}

	public void setAbContent(String abContent) {
		this.abContent = abContent;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public List<ArticleReplyBean> getReplies() {
		return replies;
	}

	public void setReplies(List<ArticleReplyBean> replies) {
		this.replies = replies;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public Integer getCopyCount() {
		return copyCount;
	}

	public void setCopyCount(Integer copyCount) {
		this.copyCount = copyCount;
	}

	public String[] getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String[] imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public String[] getEnclosingUrls() {
		return enclosingUrls;
	}

	public void setEnclosingUrls(String[] enclosingUrls) {
		this.enclosingUrls = enclosingUrls;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return showTitleLight();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return showContentLight();
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlHash() {
		return urlHash;
	}

	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}

	public String getAuthor() {
		if(StringUtils.isNotBlank(author)){
			return author;
		}
		return FMConstant.ARTICLEVO_AUTHOR_SHOW;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getSentiment() {
		return sentiment;
	}

	public void setSentiment(Integer sentiment) {
		this.sentiment = sentiment;
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public Date getSpiderTime() {
		return spiderTime;
	}

	public void setSpiderTime(Date spiderTime) {
		this.spiderTime = spiderTime;
	}

	public Integer getSpiderType() {
		return spiderType;
	}

	public void setSpiderType(Integer spiderType) {
		this.spiderType = spiderType;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastReplyTime() {
		return lastReplyTime;
	}

	public void setLastReplyTime(Date lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public Integer getParentWebsiteId() {
		return parentWebsiteId;
	}

	public void setParentWebsiteId(Integer parentWebsiteId) {
		this.parentWebsiteId = parentWebsiteId;
	}

	@Override
	public String toString() {
		return "ArticleVo [custAttrs=" + custAttrs + ", style=" + style + ", weiboType=" + weiboType + ", boardId=" + boardId + ", boardUrl=" + boardUrl + ", boardName=" + boardName
				+ ", matchCode=" + matchCode + ", hot=" + hot + ", areaIds=" + Arrays.toString(areaIds) + ", industryIds=" + Arrays.toString(industryIds) + ", websiteId=" + websiteId
				+ ", websiteName=" + websiteName + ", websiteUrl=" + websiteUrl + ", custIds=" + Arrays.toString(custIds) + ", topicIds=" + Arrays.toString(topicIds) + ", custTopicIds="
				+ Arrays.toString(custTopicIds) + ", sysTopicIds=" + Arrays.toString(sysTopicIds) + ", keywordIds=" + Arrays.toString(keywordIds) + ", keywords=" + Arrays.toString(keywords)
				+ ", abContent=" + abContent + ", replyCount=" + replyCount + ", replies=" + replies + ", readCount=" + readCount + ", copyCount=" + copyCount + ", imageUrls="
				+ Arrays.toString(imageUrls) + ", likeCount=" + likeCount + ", enclosingUrls=" + Arrays.toString(enclosingUrls) + ", guid=" + guid + ", title=" + title + ", content=" + content
				+ ", url=" + url + ", urlHash=" + urlHash + ", author=" + author + ", sentiment=" + sentiment + ", stage=" + stage + ", postTime=" + postTime + ", spiderTime=" + spiderTime
				+ ", spiderType=" + spiderType + ", updateTime=" + updateTime + ", lastReplyTime=" + lastReplyTime + ", guidGroup=" + guidGroup + ", status=" + status + ", archiveStatus="
				+ archiveStatus + ", warnStatus=" + warnStatus + ", importance=" + importance + ", imageUrlsPostion=" + imageUrlsPostion + "]";
	}

	public String getAreanames() {
		return FMConstant.ARTICLEVO_AREA_SHOW;
	}

	public String getIndustrynames() {
		return FMConstant.ARTICLEVO_INDUSTRY_SHOW;
	}
	
	public String getAuthorName(){
		if(StringUtils.isNotBlank(author)){
			return author;
		}
		return FMConstant.ARTICLEVO_AUTHOR_SHOW;
	}

	@Override
	public String getSentiments() {
		return FMConstant.ARTICLEVO_SENTIMENT_SHOW;
	}

	@Override
	public String showTitleLight() {
		try {
			if(StringUtils.isNotBlank(title)){
				String cont = title.replaceAll("[\\t\\n\\r]", "");
				return support.markKeyword(cont, keywordSet);
			}
		} catch (Exception e) {
			logger.error("showTitleLight::", e);
		}
		return null;
	}

	@Override
	public String showContentLight() {
		try {
			if (StringUtils.isNotBlank(content)) {
				// (\r\n|\n) 是换行符
				String cont = content.replaceAll("(\r\n|\n)", "</p><p>");
				//将图片位置标签替换，返回标签的集合和内容
				Map<String, Object> replaceImageUrlsToUnusual = support.replaceImageUrlsToUnusual(cont);
				String replaceImageUrlsContent = (String) replaceImageUrlsToUnusual.get("content");
				//标红关键词
				String markKeywordContent = support.markKeyword(replaceImageUrlsContent, keywordSet);
				List<String> list = (List<String>) replaceImageUrlsToUnusual.get("positionImageUrlsList");
				System.out.println(guid + ":图片位置imageUrlsPostion的大小为"+imageUrlsPostion.size());
				/**关键词标红后替换图片的URL*/
				return support.replaceImageUrls(markKeywordContent, list, imageUrlsPostion, style);
			}
		} catch (Exception e) {
			logger.error("showContentLight::", e);
		}
		return null;
	}

	public String showAbConentLight() {
		try {
			if(StringUtils.isNotBlank(abContent)){
				String content = abContent.replaceAll("[\\t\\n\\r]", "");
				return support.markKeyword(content, keywordSet);
			}
		} catch (Exception e) {
			logger.error("showAbConentLight::", e);
		}
		return null;
	}
	
	/*
	 * 微博原文内容与转发内容分隔符：\r\n===============================================================\r\n
	 */
	public String showWeiboRelayContentLight() {
		try {
			if (StringUtils.isNotBlank(content)) {
				// (\r\n|\n) 是换行符
				String cont = content.replaceAll("(\r\n|\n)", "</p><p>");
				//将图片位置标签替换，返回标签的集合和内容
				Map<String, Object> replaceImageUrlsToUnusual = support.replaceImageUrlsToUnusual(cont);
				String replaceImageUrlsContent = (String) replaceImageUrlsToUnusual.get("content");
				String[] temp = support.markKeyword(replaceImageUrlsContent, keywordSet).split("</p><p>===============================================================");
				String weiboRelayContent = temp.length > 1 ? temp[1] : null;
				List<String> list = (List<String>) replaceImageUrlsToUnusual.get("positionImageUrlsList");
				System.out.println(guid + ":图片位置imageUrlsPostion的大小为"+imageUrlsPostion.size());
				/**关键词标红后替换图片的URL*/
				return support.replaceImageUrls(weiboRelayContent, list, imageUrlsPostion, style);
			}
		} catch (Exception e) {
			logger.error("showWeiboRelayContentLight::", e);
		}
		return null;
	}
	
	/*
	 * 微博原文内容与转发内容分隔符：\r\n===============================================================\r\n
	 */
	public String showWeiboContentLight() {
		try {
			if (StringUtils.isNotBlank(content)) {
				// (\r\n|\n) 是换行符
				String cont = content.replaceAll("(\r\n|\n)", "</p><p>");
				//将图片位置标签替换，返回标签的集合和内容
				Map<String, Object> replaceImageUrlsToUnusual = support.replaceImageUrlsToUnusual(cont);
				String replaceImageUrlsContent = (String) replaceImageUrlsToUnusual.get("content");
				String weiboContent = support.markKeyword(replaceImageUrlsContent, keywordSet).split("</p><p>===============================================================")[0];
				List<String> list = (List<String>) replaceImageUrlsToUnusual.get("positionImageUrlsList");
				System.out.println(guid + ":图片位置imageUrlsPostion的大小为"+imageUrlsPostion.size());
				/**关键词标红后替换图片的URL*/
				return support.replaceImageUrls(weiboContent, list, imageUrlsPostion, style);
			}
		} catch (Exception e) {
			logger.error("showWeiboContentLight::", e);
		}
		return null;
	}

}
