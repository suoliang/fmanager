package com.cyyun.fm.people.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.fm.people.bean.RelationView;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.process.service.PeopleService;
import com.cyyun.process.service.bean.PeopleBean;
import com.cyyun.process.service.bean.PeopleQueryBean;
import com.cyyun.process.service.bean.VirtualIdentityBean;
import com.cyyun.process.service.bean.VirtualIdentityQueryBean;
import com.cyyun.process.service.exception.ServiceExcepiton;

/**
 * <h3>人物身份关联支持</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Component
public class PeopleRelationentitySuppport {
	@Autowired
	private PeopleService peopleService;
	@Autowired
	private CustSiteService custSiteService;

	/**
	 * @param peopleQuery
	 * 页面初始化
	 * @return
	 * @throws
	 */
	public PageInfoBean<RelationView> relationentity(PeopleQueryBean peopleQuery) throws ServiceExcepiton {
		PageInfoBean<RelationView> pageInfo = new PageInfoBean<RelationView>(null, null);
		// 排序
		peopleQuery.setOrderByClause("a.create_time desc");
		peopleQuery.setCustId(FMContext.getCurrent().getCustomerId());
		peopleQuery.setUserCreateFlag("all");//默认来源：all
		peopleQuery.setRealName(CyyunSqlUtil.dealSql(peopleQuery.getRealName()));
		peopleQuery.setVirtualName(CyyunSqlUtil.dealSql(peopleQuery.getVirtualName()));
		PageInfoBean<PeopleBean> relationPeople = peopleService.queryPeopleVirtualPage(peopleQuery);
		List<PeopleBean> data = relationPeople.getData();
		List<RelationView> dataview = ListBuilder.newArrayList();
		CustSiteQueryBean csqb = new CustSiteQueryBean();
		for (PeopleBean peopleBeanItem : data) {
			List<VirtualIdentityBean> virtualBeanList = peopleBeanItem.getVirtualList();
			for (VirtualIdentityBean virtualIdentityBeanItem : virtualBeanList) {
				RelationView view = BeanUtil.copy(peopleBeanItem, RelationView.class);
				csqb.setId(virtualIdentityBeanItem.getSiteId());// 站点ID
				Integer[] i = { csqb.getId() };
				List<CustSiteBean> cList = custSiteService.listSite(i);//得到站点集合
				if (cList.size() != 0) {
					view.setSiteIdName(cList.get(0).getName());// 站点名字
				}
				view.setVirtualName(virtualIdentityBeanItem.getNickname());// 昵称
				view.setVid(virtualIdentityBeanItem.getId());// 虚拟人物id
				view.setTrueName(peopleBeanItem.getRealName());// 真实人物名字

				dataview.add(view);// 返回数据
			}
		}
		pageInfo = new PageInfoBean<RelationView>(dataview, relationPeople.getTotalRecords(), relationPeople.getPageSize(), relationPeople.getPageNo());
		return pageInfo;
	}

	/**
	 * @param vid
	 * 取消关联
	 */
	public void removeRelationPeople(Integer id) {
		peopleService.removePeopleVirtual(id);
	}

	/**
	 * 
	 * 新增关联页面站点数据绑定
	 */
	public List<CustSiteBean> boundCustSite() {
		CustSiteQueryBean custSiteQueryBean=new CustSiteQueryBean();
		custSiteQueryBean.setCustId(FMContext.getCurrent().getCustomerId());//设置cid属性
		List<CustSiteBean> custSiteBeans=custSiteService.querySite(custSiteQueryBean).getData();
		return custSiteBeans;
	}

	/**
	 * 
	 * 新增关联页面虚拟身份数据绑定
	 */
	public List<VirtualIdentityBean> boundVirtualPeople(VirtualIdentityQueryBean query) throws ServiceExcepiton {
		query.setNickname(CyyunSqlUtil.dealSql(query.getNickname()));
		query.setUserCreateFlag("all");//默认来源：all
		query.setNeedPaging(false);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		PageInfoBean<VirtualIdentityBean> pageInfo = peopleService.queryVirtualIdentityPage(query);
		return pageInfo.getData();
	}

	/**
	 * @param query
	 * 新增关联页面真实身份绑定
	 * @return
	 * @throws
	 */
	public List<PeopleBean> boundTruePeople(PeopleQueryBean peopleQuery) throws ServiceExcepiton {
		peopleQuery.setRealName(CyyunSqlUtil.dealSql(peopleQuery.getRealName()));
		peopleQuery.setUserCreateFlag("all");//默认来源：all
		peopleQuery.setNeedPaging(false);
		peopleQuery.setCustId(FMContext.getCurrent().getCustomerId());
		List<PeopleBean> data = peopleService.queryPeoplePage(peopleQuery).getData();
		return data;
	}

	/**
	 * @param query
	 * 真实身份绑定(自动填充ID)
	 * @return
	 * @throws
	 */
	public List<PeopleBean> boundTruePeopleSupport(PeopleQueryBean peopleQuery) throws ServiceExcepiton {
		List<PeopleBean> PeopleBeans = peopleService.queryPeopleByRealName(peopleQuery);
		return PeopleBeans;
	}

	public List<VirtualIdentityBean> boundVirtualPeopleSupport(VirtualIdentityQueryBean virtualQuery) throws ServiceExcepiton {
		virtualQuery.setNeedPaging(false);
		List<VirtualIdentityBean> virtualBeanList = peopleService.queryVirtualByNameAndSiteId(virtualQuery.getNickname(), virtualQuery.getSiteId());
		return virtualBeanList;
	}

	/**
	 * @param query
	 * 新增关联点击确定
	 * @return
	 * @throws
	 */
	public void addRelationPeople(Integer id, Integer vid) throws ServiceExcepiton {
		peopleService.addPeopleVirtual(id, vid);
	}

}
