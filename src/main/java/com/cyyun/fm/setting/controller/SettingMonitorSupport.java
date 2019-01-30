package com.cyyun.fm.setting.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.base.service.bean.query.KeywordQueryBean;
import com.cyyun.base.service.exception.KeywordServiceException;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;

/**
 * <h3>监测设置控制器支持</h3>
 * @author LIUJUNWU
 */
@Component
public class SettingMonitorSupport {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KeywordService keywordService;

	public PageInfoBean<KeywordBean> queryMonitorKeywordByPage(KeywordBean keywordBean, Integer pageNo, Integer pageSize) {
		KeywordQueryBean query = BeanUtil.copy(keywordBean, KeywordQueryBean.class);
		if (query == null) {
			query = new KeywordQueryBean();
		}
		query.setPageNo(pageNo);
		query.setPageSize(pageSize);
		query.setSource("FM");
		query.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
		query.setActiveFilter(null);//覆盖默认值
		return keywordService.queryKeywordByPage(query);
	}

	public KeywordBean findMonitorKeyword(Integer kid) throws KeywordServiceException {
		return keywordService.findKeywordById(kid);
	}
	
	public void deleteMonitorKeyword(Integer kid) throws KeywordServiceException {
		KeywordBean keywordBean = keywordService.findKeywordById(kid);
		if (keywordBean != null) {
			Integer[] custIds = keywordBean.getCustIds();
			if (custIds.length > 1) {//有多个cid则修改记录
				List<Integer> custIdsList = new ArrayList<Integer>();
				for (Integer custId : custIds) {
					if (!custId.equals(FMContext.getCurrent().getCustomerId())) {
						custIdsList.add(custId);
					}
				}
				Integer[] custIdsNew = new Integer[custIdsList.size()];
				for (int i = 0; i < custIdsList.size(); i++) {
					custIdsNew[i] = custIdsList.get(i);
				}
				keywordBean.setUpdateId(FMContext.getCurrent().getUserId());
				keywordBean.setCustIds(custIdsNew);
				keywordService.updateKeywordById(keywordBean);
			} else {//否则删除记录
				keywordService.deleteKeywordById(kid);
			}
		} else {
			throw new KeywordServiceException("deleteMonitorKeyword.error", "此监测关键词不存在");
		}
	}

	public void updateMonitorKeyword(Map<String, String[]> parameterMap) throws KeywordServiceException {
		KeywordBean keywordBean = new KeywordBean();
		keywordBean.setActiveFilter(true);//打开过滤开关
		keywordBean.setSource("FM");//Source默认值为"FM"
		CyyunStringUtils.request2Bean(parameterMap, keywordBean, "kid", "word");
		if (keywordBean.getKid() == null) {//新增
			keywordBean.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
			keywordBean.setOpId(FMContext.getCurrent().getUserId());//设置操作人id
			keywordService.addKeyword(keywordBean);
		} else {
			Integer itselfKid = keywordBean.getKid();
			KeywordBean keywordBeanItself = keywordService.findKeywordById(itselfKid);
			if (keywordBeanItself != null) {
				Integer[] custIds = keywordBeanItself.getCustIds();
				if (custIds.length > 1) {//有多个cid则先修改记录，再新增一条记录
					//修改
					List<Integer> custIdsList = new ArrayList<Integer>();
					for (Integer custId : custIds) {
						if (!custId.equals(FMContext.getCurrent().getCustomerId())) {
							custIdsList.add(custId);
						}
					}
					Integer[] custIdsNew = new Integer[custIdsList.size()];
					for (int i = 0; i < custIdsList.size(); i++) {
						custIdsNew[i] = custIdsList.get(i);
					}
					keywordBeanItself.setUpdateId(FMContext.getCurrent().getUserId());
					keywordBeanItself.setCustIds(custIdsNew);
					keywordService.updateKeywordById(keywordBeanItself);
					//新增
					keywordBean.setKid(null);//清空kid
					keywordBean.setOpId(FMContext.getCurrent().getUserId());//设置操作人id
					keywordBean.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
					keywordService.addKeyword(keywordBean);
				} else {//否则修改记录
					Integer[] custIds1 = new Integer[] {};
					List<Integer> custIdsList = new ArrayList<Integer>();
					Integer kid = 0;
					KeywordQueryBean keywordQueryBean = BeanUtil.copy(keywordBean, KeywordQueryBean.class);
					PageInfoBean<KeywordBean> pageInfo = keywordService.queryKeywordByPage(keywordQueryBean);
					List<KeywordBean> keywordBeanList = pageInfo.getData();
					if (keywordBeanList.size() > 0) {
						keywordBean.setKid(null);//清空kid
						Boolean isExist = false;
						for (KeywordBean item : keywordBeanList) {
							//Word、ActiveFilter属性相等，则视为同一条记录
							if (item.getWord().equals(keywordQueryBean.getWord()) && item.getActiveFilter() == keywordQueryBean.getActiveFilter()) {
								custIds1 = item.getCustIds();
								kid = item.getKid();
								isExist = true;
							}
						}
						if (isExist) {
							for (Integer custId : custIds1) {
								if (!custId.equals(FMContext.getCurrent().getCustomerId())) {
									custIdsList.add(custId);
								}
							}
							custIdsList.add(FMContext.getCurrent().getCustomerId());
							Integer[] custIdsNew = new Integer[custIdsList.size()];
							for (int i = 0; i < custIdsList.size(); i++) {
								custIdsNew[i] = custIdsList.get(i);
							}
							keywordBean.setCustIds(custIdsNew);
							keywordBean.setKid(kid);
							keywordBean.setUpdateId(FMContext.getCurrent().getUserId());
							keywordService.updateKeywordById(keywordBean);
							//删除本身这条记录
							deleteMonitorKeyword(keywordBeanItself.getKid());
						} else {
							keywordBean.setKid(itselfKid);
							keywordBean.setUpdateId(FMContext.getCurrent().getUserId());
							keywordBean.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
							keywordService.updateKeywordById(keywordBean);
						}
					} else {
						keywordBean.setUpdateId(FMContext.getCurrent().getUserId());
						keywordBean.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
						keywordService.updateKeywordById(keywordBean);
					}
				}
			} else {
				throw new KeywordServiceException("deleteMonitorKeyword.error", "此监测关键词不存在");
			}
		}
	}
	
	public void checkMonitorKeywordIsExist(Map<String, String[]> parameterMap) throws KeywordServiceException {
		KeywordQueryBean keywordQueryBean = new KeywordQueryBean();
		keywordQueryBean.setSource("FM");
		keywordQueryBean.setCustIds(new Integer[] { FMContext.getCurrent().getCustomerId() });
		keywordQueryBean.setActiveFilter(true);//打开过滤开关
		CyyunStringUtils.request2Bean(parameterMap, keywordQueryBean, "word");
		String kid = parameterMap.get("kid")[0];
		PageInfoBean<KeywordBean> pageInfo = keywordService.queryKeywordByPage(keywordQueryBean);

		if (pageInfo != null) {
			List<KeywordBean> keywordBeanList = pageInfo.getData();
			for (KeywordBean item : keywordBeanList) {
				//Word、ActiveFilter属性相等，则视为同一条记录
				if (item.getWord().equals(keywordQueryBean.getWord()) && item.getActiveFilter() == keywordQueryBean.getActiveFilter()) {
					if (!item.getKid().toString().equals(kid)) {//排除是关键词本身的可能性
						throw new KeywordServiceException("monitorKeywordExisting.error", "监测关键词已存在");
					}
				}
			}
		}
	}
}