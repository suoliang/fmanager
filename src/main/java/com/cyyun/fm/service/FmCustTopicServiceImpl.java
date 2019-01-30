package com.cyyun.fm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.common.core.bean.PageInfoBean;
@Service
public class FmCustTopicServiceImpl implements FmCustTopicService{

	@Autowired
	private CustTopicService custTopicService;
	
	@Override
	public List<CustTopicBean> queryCurrentCustAllTopics() throws Exception {
		CustTopicQueryBean custTopicQueryBean = new CustTopicQueryBean();
		custTopicQueryBean.setNeedPaging(false);
		custTopicQueryBean.setCustId(FMContext.getCurrent().getCustomerId());
//		custTopicQueryBean.setStatus(CUST_TOPIC_STATUS.yes.getStatus());
		custTopicQueryBean.setStatus(1);
		PageInfoBean<CustTopicBean> pageInfo = custTopicService.queryCustTopicsByPageforIM(custTopicQueryBean);
		return pageInfo.getData();
	}

	@Override
	public String[] queryCustTopicTree(Integer topicId) throws Exception {
		List<CustTopicBean> custTopicBeanList = custTopicService.listCustTopicAndSub(topicId);
		String[] result = new String[2];
		if (custTopicBeanList.get(0).getParentId() != 0) {
			List<CustTopicBean> list = new ArrayList<CustTopicBean>();
			list.add(custTopicBeanList.get(0));
			queryCustTopic(custTopicBeanList.get(0), list);
			CustTopicBean topCustTopic = list.get(list.size() - 1);// 最顶层专题
			result[0] = String.valueOf(topCustTopic.getId());
			list.remove(list.size() - 1);// 删除list中最顶层专题
			Collections.reverse(list);// 反转list的顺序
			
			StringBuffer topicSiblingBuffer = getTopSiblingCustTopic(list,topCustTopic);
			
			StringBuffer buffer = new StringBuffer();
			boolean isLast = false;
			int level = 0;
			for (int i = 0; i < list.size(); i++) {// 拼接专题树div
				isLast = (i == (list.size() - 1));
				buffer.append("<div class='c_sub_speTree' status='close' oid='"
						+ list.get(i).getId() + "' tag='item-topic'>");
				buffer.append("<div class='c_tree_list c_border_bot c_f12' selct='false' scope='option' oid='"
						+ list.get(i).getId() + "' tag='btn-click-topic'>");
				buffer.append("<span class='c_sprite c_fl c_ml"
						+ (i + 1)
						+ "0 "
						+ ((isLast && custTopicBeanList.size() > 1) ? "c_tree_open"
								: "c_tree_close") + "'  oid='"
						+ list.get(i).getId()
						+ "' tag='btn-show-topic'></span>");
				buffer.append("<div class='"
						+ (isLast ? "c_color_red c_omit" : "c_color_gay c_omit")
						+ "' value='" + list.get(i).getId()
						+ "' type='checkbox' oid='" + list.get(i).getId()
						+ "' title='" + list.get(i).getName()
						+ "' tag='btn-select-topic'>" + list.get(i).getName()
						+ "</div>");
				buffer.append("</div>");
				buffer.append("<div class='c_sub' level='"
						+ (level = i + 1)
						+ "' oid='"
						+ list.get(i).getId()
						+ "' tag='list-sub-topic' "
						+ ((isLast) ? ""
								: "loaded='loaded' status='open'") + ">");
				if (isLast) {
//					buffer.append("</div>");
//					buffer.append("</div>");
//					buffer.append("</div>");
//					for (int j = 0; j < list.size() - 1; j++) {
						buffer.append("</div>");
						buffer.append("</div>");
					}
//				}
			}
			StringBuffer lastSiblingCustTopic = getLastSiblingCustTopic(custTopicBeanList.get(0), level);
			lastSiblingCustTopic.append("</div>").append("</div>").append("</div>");
			buffer = buffer.append(lastSiblingCustTopic).append(topicSiblingBuffer);
			result[1] = buffer.toString();
		}
		return result;
	}

	/**
	 * 根据给定专题递归查询专题树
	 * @param custTopicBean
	 * @param list
	 */
	private void queryCustTopic(CustTopicBean custTopicBean,
			List<CustTopicBean> list) {
		if (custTopicBean.getParentId() != 0) {
			CustTopicBean parentCustTopicBean = custTopicService
					.queryCustTopic(custTopicBean.getParentId());
			list.add(parentCustTopicBean);
			queryCustTopic(parentCustTopicBean, list);
		}
	}
	
	/***
	 * 获取最顶层专题下的同级专题
	 * @param list
	 * @param topCustTopic
	 * @return
	 */
	private StringBuffer getTopSiblingCustTopic(List<CustTopicBean> list,
			CustTopicBean topCustTopic) {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(topCustTopic.getId());
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		/** 最顶级专题下的子专题 */
		List<CustTopicBean> topics = custTopicService.queryCustTopicsByCondition(query);
		StringBuffer siblingBuffer = new StringBuffer();
		if (CollectionUtils.isEmpty(topics)) {
			return siblingBuffer;
		}
		/** 同级专题移除 */
		for (CustTopicBean custTopicBean2 : list) {
			for (int i = 0; i < topics.size(); i++) {
				if (topics.get(i).getId().equals(custTopicBean2.getId())) {
					topics.remove(i--);// 索引回退
				}
			}
		}
		boolean siblingIsLast = false;
		for (int i = 0; i < topics.size(); i++) {// 拼接专题树div
			siblingIsLast = (i == (topics.size() - 1));
			siblingBuffer.append("<div class='c_sub_speTree' status='close' oid='"
							+ topics.get(i).getId() + "' tag='item-topic'>");
			siblingBuffer.append("<div class='c_tree_list c_border_bot c_f12' selct='false' scope='option' oid='"
							+ topics.get(i).getId()
							+ "' tag='btn-click-topic'>");
			siblingBuffer.append("<span class='c_sprite c_fl c_ml"
							+ (i + 1)
							+ "0 "
							+ ((siblingIsLast && topics.get(i).getSubCount() >= 1) ? "c_tree_open" : "c_tree_close") 
							+ "'  oid='" + topics.get(i).getId()
							+ "' tag='btn-show-topic'></span>");
			siblingBuffer.append("<div class='" + (siblingIsLast ? "c_color_gay c_omit" : "c_color_red c_omit")
					+ "' value='" + topics.get(i).getId() 
					+ "' type='checkbox' oid='" + topics.get(i).getId() 
					+ "' title='" + topics.get(i).getName()
					+ "' tag='btn-select-topic'>" + topics.get(i).getName()
					+ "</div>");
			siblingBuffer.append("</div>");
			siblingBuffer.append("<div class='c_sub' level='" + (i + 1)
					+ "' oid='" + topics.get(i).getId()
					+ "' tag='list-sub-topic' "
					+ ((siblingIsLast) ? "" : "loaded='loaded' status='open'")
					+ ">");
//			if (siblingIsLast) {
//				siblingBuffer.append("</div>");
//				siblingBuffer.append("</div>");
//				siblingBuffer.append("</div>");
//				for (int j = 0; j < topics.size() - 1; j++) {
					siblingBuffer.append("</div>");
					siblingBuffer.append("</div>");
//				}
//			}
		}
		return siblingBuffer;
	}
	
	/**
	 * 获取选中专题的同级专题
	 * @param custTopicBean
	 * @param level         层级
	 * @return
	 */
	private StringBuffer getLastSiblingCustTopic(CustTopicBean custTopicBean,int level){
		if (level == 1) {/**二级专题的同级专题已经处理过，此处return*/
			return new StringBuffer();
		}
		
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(custTopicBean.getParentId());
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		/**父专题下的同级子专题*/
		List<CustTopicBean> topics = custTopicService.queryCustTopicsByCondition(query);
		
        for (int i = 0; i < topics.size(); i++) {
            if (topics.get(i).getId().equals(custTopicBean.getId())) {
            	topics.remove(i--);// 索引回退
            }
        }
        
		StringBuffer siblingBuffer = new StringBuffer();
		boolean siblingIsLast = false;
		for (int i = 0; i < topics.size(); i++) {// 拼接专题树div
			
			siblingIsLast = (i == (topics.size() - 1));
			siblingBuffer.append("<div class='c_sub_speTree' status='close' oid='"+ topics.get(i).getId() + "' tag='item-topic'>");
			siblingBuffer.append("<div class='c_tree_list c_border_bot c_f12' selct='false' scope='option' oid='"+ topics.get(i).getId() + "' tag='btn-click-topic'>");
			siblingBuffer.append("<span class='c_sprite c_fl c_ml"
					+ (level)
					+ "0 "
					+ ((siblingIsLast && topics.get(i).getSubCount() >= 1) ? "c_tree_open" : "c_tree_close") + "'  oid='"+ topics.get(i).getId() + "' tag='btn-show-topic'></span>");
			siblingBuffer.append("<div class='"
					+ (siblingIsLast ? "c_color_gay c_omit" : "c_color_red c_omit")
					+ "' value='" + topics.get(i).getId()
					+ "' type='checkbox' oid='" + topics.get(i).getId()
					+ "' title='" + topics.get(i).getName()
					+ "' tag='btn-select-topic'>" + topics.get(i).getName()
					+ "</div>");
			siblingBuffer.append("</div>");
			siblingBuffer.append("<div class='c_sub' level='"
					+ (level)
					+ "' oid='" + topics.get(i).getId()
					+ "' tag='list-sub-topic' "
					+ ((siblingIsLast) ? "" : "loaded='loaded' status='open'") + ">");
			siblingBuffer.append("</div>");
			siblingBuffer.append("</div>");
		}
		return siblingBuffer;
	}
	

}
