package com.cyyun.fm.focus.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.vo.ArticleVoInter;
import com.cyyun.process.service.bean.FocusInfoBean;
import com.cyyun.process.service.bean.WarningRuleBean;

public class FocusInfoVo implements ArticleVoInter{
	
		private Set<String> keywordSet;
		private BaseSupport support;
		
		public FocusInfoVo(){
			
		}
		public FocusInfoVo(BaseSupport support, String keyword){
			this.support = support;
			this.keywordSet = support.toStringSet(keyword);
		}
		private Integer[] notifyType;
		private Integer[] custTagIds;
		private String[] articleIds;
		private Integer id;
		private Integer custId;
		private Integer type;
		private Integer level;
		private Integer status;
		private Integer articleType;
		private String content;
		private String articleId;
		private String title;
		private String url;
		private Date createTime;
		private Date updateTime;
		private Integer sentiment;
		private Integer[] label;
		private String summary;
		private Integer[] mediaType;
		private String guidGroup;
		private Integer articleStyle;
		
		
		
		
		public Integer getArticleStyle() {
			return articleStyle;
		}
		public void setArticleStyle(Integer articleStyle) {
			this.articleStyle = articleStyle;
		}
		public String getGuidGroup() {
			return guidGroup;
		}
		public void setGuidGroup(String guidGroup) {
			this.guidGroup = guidGroup;
		}
		public Integer[] getMediaType() {
			return mediaType;
		}
		public void setMediaType(Integer[] mediaType) {
			this.mediaType = mediaType;
		}
		/**
		 * 0 - 人工输入 1 - 系统采集                   在文章列表作相似文数量使用
		 */
		private Integer source;
		/**
		 * 预警规则id
		 */
		private Integer[] ruleIds;
		private List<WarningRuleBean> warningRules;
		
		public Integer getSource() {
			return source;
		}

		public void setSource(Integer source) {
			this.source = source;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public Integer[] getNotifyType() {
			return notifyType;
		}

		public void setNotifyType(Integer[] notifyType) {
			this.notifyType = notifyType;
		}

		public Integer[] getCustTagIds() {
			return custTagIds;
		}

		public void setCustTagIds(Integer[] custTagIds) {
			this.custTagIds = custTagIds;
		}

		public String[] getArticleIds() {
			return articleIds;
		}

		public void setArticleIds(String[] articleIds) {
			this.articleIds = articleIds;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getCustId() {
			return custId;
		}

		public void setCustId(Integer custId) {
			this.custId = custId;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Integer getArticleType() {
			return articleType;
		}

		public void setArticleType(Integer articleType) {
			this.articleType = articleType;
		}

		public String getContent() {
			return showContentLight();
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getArticleId() {
			return articleId;
		}

		public void setArticleId(String articleId) {
			this.articleId = articleId;
		}

		public String getTitle() {
			return showTitleLight();
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public Integer[] getLabel() {
			return label;
		}

		public void setLabel(Integer[] label) {
			this.label = label;
		}

		public Integer getSentiment() {
			return sentiment;
		}

		public void setSentiment(Integer sentiment) {
			this.sentiment = sentiment;
		}

		public Integer[] getRuleIds() {
			return ruleIds;
		}

		public void setRuleIds(Integer[] ruleIds) {
			this.ruleIds = ruleIds;
		}

		public List<WarningRuleBean> getWarningRules() {
			return warningRules;
		}

		public void setWarningRules(List<WarningRuleBean> warningRules) {
			this.warningRules = warningRules;
		}

		@Override
		public String getAreanames() {
			return null;
		}

		@Override
		public String getIndustrynames() {
			return null;
		}

		@Override
		public String getAuthorName() {
			return null;
		}

		@Override
		public String getSentiments() {
			if(sentiment != null){
				switch (sentiment) {
				case 0:
					return FMConstant.ARTICLE_SENTIMENT.zero.getValue();
				case 1:
					return FMConstant.ARTICLE_SENTIMENT.one.getValue();
				case 2:
					return FMConstant.ARTICLE_SENTIMENT.two.getValue();
				case 3:
					return FMConstant.ARTICLE_SENTIMENT.three.getValue();
				default:
					return FMConstant.ARTICLE_SENTIMENT.zero.getValue();
				}
			}
			return FMConstant.ARTICLE_SENTIMENT.zero.getValue();
		}
		/*预警等级*/
		public String getLevelName(){
			if(level != null){
				switch (level) {
				case 1:
					return "紧急";
				case 2:
					return "重要";
				case 3:
					return "一般";
				default:
					return "";
				}
			}
			return "";
		}
		/*预警等级颜色*/
		public String getLevelColor(){
			if(level != null){
				switch (level) {
				case 1:
					return "c_emergency c_fl c_mt10";
				case 2:
					return "c_important c_fl c_mt10";
				case 3:
					return "c_general c_fl c_mt10";
				default:
					return "";
				}
			}
			return "";
		}
		/*相似文*/
		public Integer getRelatedArticles(){
			if(ArrayUtils.isNotEmpty(articleIds)){
				return articleIds.length;
			}
			return 0;
		}
		/*预警规则名称*/
		public String getWarningRuleNames() {
			if(CollectionUtils.isNotEmpty(warningRules)){
				List<String> warningRuleNameList = new ArrayList<String>();
				for(WarningRuleBean warningRuleBean: warningRules){
					warningRuleNameList.add(warningRuleBean.getName());
				}
				return StringUtils.join(warningRuleNameList, ",");
			}
			return null;
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
				if(StringUtils.isNotBlank(content)){
					String cont = content.replaceAll("[\\t\\n\\r]", "");
					return support.markKeyword(cont, keywordSet);
				}
			} catch (Exception e) {
				logger.error("showContentLight::", e);
			}
			return null;
		}

}
