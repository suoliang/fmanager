package com.cyyun.fm.setting.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.fm.setting.bean.AutoWarningNotify;
import com.cyyun.fm.setting.bean.AutoWarningNotify.NotifyInfo;
import com.cyyun.fm.setting.bean.WarningRuleParam;
import com.cyyun.process.service.WarningRuleService;
import com.cyyun.process.service.bean.WarningRuleBean;
import com.cyyun.process.service.bean.WarningRuleQueryBean;
import com.cyyun.process.service.exception.WarningRuleServiceException;

/**
 * <h3>设置预警控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class SettingWarningSupport {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WarningRuleService warningRuleService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${mq.queue.name.warning}")
	private String warningQueueName;

	public PageInfoBean<WarningRuleBean> queryWarningRuleByPage(WarningRuleBean warningRule, Integer pageNo, Integer pageSize) throws WarningRuleServiceException {
		WarningRuleQueryBean query = BeanUtil.copy(warningRule, WarningRuleQueryBean.class);
		if (query == null) {
			query = new WarningRuleQueryBean();
		}
		query.setName(CyyunSqlUtil.dealSql(warningRule.getName()));
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setPageNo(pageNo);
		query.setPageSize(pageSize);
		return warningRuleService.queryWarningRuleByPage(query);
	}

	public WarningRuleBean findRule(Integer ruleId) throws WarningRuleServiceException {
		return warningRuleService.findWarningRule(ruleId);
	}

	public void saveRule(WarningRuleParam rule) throws WarningRuleServiceException {
		WarningRuleBean ruleBean = new WarningRuleBean();
		ruleBean.setCreaterId(FMContext.getCurrent().getUserId());
		ruleBean.setCustId(FMContext.getCurrent().getCustomerId());
		ruleBean.setId(rule.getId());
		ruleBean.setName(rule.getName());
		List<String> keywords = null;
		if (StringUtils.isNotBlank(rule.getKeyword())) {
			keywords = ListBuilder.add(rule.getKeyword().replace(";", " ")).build();
		} else {
			keywords = ListBuilder.newArrayList();
		}
		warningRuleService.saveWarningRule(ruleBean, keywords);
	}

	public void saveRule(List<WarningRuleParam> rules) throws WarningRuleServiceException {
		for (WarningRuleParam rule : rules) {
			saveRule(rule);
		}
	}

	public void changeRuleStatus(Integer ruleId, Integer stauts) throws WarningRuleServiceException {
		warningRuleService.updateRuleStatus(ruleId, stauts);
	}

	public void sendMessage(String guid, Integer cid) {
		AutoWarningNotify notify = new AutoWarningNotify();
		notify.setGuid(guid);

		NotifyInfo info = new NotifyInfo();
		info.setCid(cid);
		info.setKid(0);

		notify.setNotifyInfos(ListBuilder.add(info).build());

		final String message = JsonUtil.toJson(notify);

		jmsTemplate.send(warningQueueName, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}
	/**
	 * 保存预警规则以及关键词
	 * @param parameterMap
	 * @throws WarningRuleServiceException
	 */
	public void saveWarningRule(Map<String,String[]> parameterMap) throws WarningRuleServiceException {
		WarningRuleBean ruleBean = new WarningRuleBean();
		ruleBean.setCreaterId(FMContext.getCurrent().getUserId());
		ruleBean.setCustId(FMContext.getCurrent().getCustomerId());
		CyyunStringUtils.request2Bean(parameterMap, ruleBean,"id", "name","keyword", "excludedKeyword", "remark", "mediaType");
		warningRuleService.saveWarningRule(manageKeyword(ruleBean), jointKeyword(ruleBean));
	}
	/**
	 * 格式化用于过滤预警数据的预警规则
	 * @param ruleBean
	 * @return
	 */
	private List<String> jointKeyword(WarningRuleBean ruleBean) {
		List<String> keywords = new ArrayList<String>();
		if(StringUtils.isNotBlank(ruleBean.getKeyword())) {
			if(ruleBean.getKeyword() != null){
				String[] keywordArr = ruleBean.getKeyword().split(";");
				if(ArrayUtils.isNotEmpty(keywordArr)){
					for(String keyword: keywordArr){
						if (StringUtils.isEmpty(keyword) || " ".equals(keyword) || "".equals(keyword)) {
							continue;
						}
						StringBuffer buffer = new StringBuffer();
						buffer.append(keyword);
						if(ruleBean.getExcludedKeyword() != null){
							String[] excludedKeywordArr = ruleBean.getExcludedKeyword().split(" ");
							if(ArrayUtils.isNotEmpty(excludedKeywordArr)){
								for(String excludedKeyword: excludedKeywordArr){
									buffer.append(" -"+excludedKeyword);
								}
								keywords.add(buffer.toString());
							}
						}else{
							keywords.add(keyword);
						}
					}
				}
			}
		}
		return keywords;
	}
	/**
	 * 格式化用于界面显示的包含关键词
	 * @param ruleBean
	 * @return
	 */
	private WarningRuleBean manageKeyword(WarningRuleBean ruleBean) {
		StringBuffer keyworBuffer = new StringBuffer();
		if(StringUtils.isNotBlank(ruleBean.getKeyword())) {
			if(ruleBean.getKeyword() != null){
				String[] keywordArr = ruleBean.getKeyword().split(";");
				if(ArrayUtils.isNotEmpty(keywordArr)){
					for(String keyword: keywordArr){
						if (StringUtils.isEmpty(keyword) || " ".equals(keyword) || "".equals(keyword)) {
							continue;
						}
						keyworBuffer.append(keyword+";");
					}
				}
			}
			if(keyworBuffer.toString().endsWith(";")){
				ruleBean.setKeyword(keyworBuffer.toString().substring(0, keyworBuffer.toString().length()-1));
			}
		}
		return ruleBean;
	}
}