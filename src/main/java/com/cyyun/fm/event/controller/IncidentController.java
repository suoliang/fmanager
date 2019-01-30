/**
 *
 */
package com.cyyun.fm.event.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.authim.shiro.AuthUtil;
import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.StringUtil;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.bean.CustDataBean;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.bean.CustDataQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.process.service.EventService;
import com.cyyun.process.service.bean.EventBean;
import com.cyyun.process.service.bean.EventQueryBean;
import com.cyyun.process.service.exception.ServiceExcepiton;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author cyyun
 *
 */
@Controller
@RequestMapping("/incident")
public class IncidentController extends BaseController
{

	@Autowired
    private EventService eventService;
	@Autowired
    private CustDataCategoryService custDataCategoryService;
	@Autowired
    private UserService userService;

    /**
     * sharefun2068 2014-12-11 function:事件模块列表首页
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "index"
    })
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/index";
    }

    /**
     * sharefun2068 2014-12-11 function:事件模块列表首页布局设置
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "layout"
    })
    public String layout(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/layout";
    }

    /**
     * sharefun2068 2014-12-11 function:具体事件目前态势
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "presentSituation"
    })
    public String presentSituation(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        int id = Integer.parseInt(request.getParameter("id"));

        out.put("eventId", id);
        return "/eventfocus/presentSituation";
    }

    /**
     * sharefun2068 2014-12-11 function:具体事件传播情况
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "spreadSituation"
    })
    public String spreadSituation(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/spreadSituation";
    }

    /**
     * sharefun2068 2014-12-11 function:具体事件事件溯源
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "eventTraceOrigins"
    })
    public String eventTraceOrigins(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/eventTraceOrigins";
    }

    /**
     * sharefun2068 2014-12-11 function:具体事件各方观点
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "allPartiesView"
    })
    public String allPartiesView(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/allPartiesView";
    }

    /**
     * sharefun2068 2014-12-11 function:具体事件综合分析
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "integratedAnalysis"
    })
    public String integratedAnalysis(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {

        return "/eventfocus/integratedAnalysis";
    }

    /**
     * sharefun2068 2014-12-11 function:事件管理列表
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "eventManager"
    })
    public String eventManager(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws CustDataCategoryServiceException
    {
        //获取分类列表。参数：cid，人物类型 (加载当前客户下的所有分类)
        Integer cid= FMContext.getCurrent().getCustomerId();
        CustDataCategoryQueryBean custDataCategoryQueryBean = new CustDataCategoryQueryBean();
	custDataCategoryQueryBean.setCustId(cid);
	custDataCategoryQueryBean.setType(1);
	//查询所有当前客户所有的分类
	List<CustDataCategoryBean> listCDCategoryBean = custDataCategoryService.queryCustDataCategoryBeanListByCondition(custDataCategoryQueryBean);

        out.put("custdataList",listCDCategoryBean);
        return "/eventfocus/eventManager";
    }

    /**
     * sharefun2068 2014-12-15 function:事件管理-->列表 ajax实现方式
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "listPageByAjax"
    })
    public String queryListPageByAjax(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws ServiceExcepiton, CustDataCategoryServiceException
    {

        Integer cid= FMContext.getCurrent().getCustomerId();
        EventQueryBean query = new EventQueryBean();
        String keywords = request.getParameter("keywords");
        String currentpage=request.getParameter("currentpage");
         String pagesize=request.getParameter("pagesize");
        if (StringUtils.isNotEmpty(keywords))
        {
            query.setName(keywords);
        }
	//根据分类查询中间表，获取人物ids的字符串集合
        String categoryId = request.getParameter("categoryId");//0表示全部，其他表示对应的类型
        
        CustDataQueryBean custDataQueryBean = new CustDataQueryBean();
        if(categoryId!=null)custDataQueryBean.setCateId(Integer.parseInt(categoryId));
        custDataQueryBean.setCustId(cid);
        custDataQueryBean.setType(1);

        String eventIds = custDataCategoryService.queryCustDataIdsByCondition(custDataQueryBean);
        
        query.setPageNo(Integer.parseInt(currentpage));
        query.setPageSize(Integer.parseInt(pagesize));
        query.setEventIds(StringUtil.buildList(eventIds, ",", Integer.class));
        
        PageInfoBean<EventBean> page = this.eventService.queryEventPage(query);
        
         //处理列表中的每条记录的来源
          if(page.getData()!=null){
              
            //获取创建人  
            List<Integer> createIds=BeanUtil.buildListByFieldName(page.getData(), "createrId");
            //获取事件分类
             List<Integer> dataIds=BeanUtil.buildListByFieldName(page.getData(), "id");
             
            CustDataCategoryQueryBean custDataCategoryQueryBean = new CustDataCategoryQueryBean();
            custDataCategoryQueryBean.setCustId(cid);
            custDataCategoryQueryBean.setType(1);
            custDataCategoryQueryBean.setDataIds((Integer[]) dataIds.toArray());
            
            List<CustDataCategoryBean> custDataList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(custDataCategoryQueryBean);  
            if(custDataList!=null){
                page.setData(changeListDisplayCustDataCat(page.getData(),custDataList));
            }
            
            List<UserBean> ulist=this.userService.listUserById(createIds);
              if(ulist!=null){
                page.setData(changeListDisplayProp(page.getData(),ulist));
              }  
          }
        
        out.put("page", page);

        return "/eventfocus/eventList";
    }
     private List<EventBean>  changeListDisplayCustDataCat(List<EventBean> clist,List<CustDataCategoryBean> cudatalist){
         
         for(EventBean bean:clist){   
             for(CustDataCategoryBean custdata:cudatalist){
                 if(custdata.getDataId().equals(bean.getId())){
                     bean.setCustDataCatNames(bean.getCustDataCatNames()+custdata.getName()+",");
                 }  
             }
         }  
      return clist;    
     }
     private List<EventBean>  changeListDisplayProp(List<EventBean> clist,List<UserBean> ulist){
         
         for(EventBean bean:clist){
             
             for(UserBean user:ulist){
                 if(user.getId().equals(bean.getCreaterId())){
                     bean.setCreatorName(user.getRealName());
                     break;
                 }  
             }
             
         }  
      return clist;    
     }     
    /**
     * sharefun2068 2014-12-11 function:事件管理-->进入新增/编辑页面
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "addEvent"
    })
    public String addAndUPDEventBean(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws ServiceExcepiton
    {
        int id = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        EventBean bean = null;
        if (id > 0)
        {
            bean = this.eventService.queryEventById(id);
            out.put("eventTitle", "编辑事件");
        }
        else
        {
            out.put("eventTitle", "新增事件");
        }
        out.put("beanVO", bean);
        return "/eventfocus/addAndUPDEvent";
    }

    /**
     * sharefun2068 2014-12-11 function:事件管理-->新增/编辑提交
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "addEventSubmit"
    })
    public String addEventBeanSubmit(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws ServiceExcepiton
    {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String keywords = request.getParameter("keywords");
        String id = request.getParameter("id");

        EventBean vo = new EventBean();
        vo.setName(name);
        vo.setContent(content);
        vo.setKeywords(keywords);

        vo.setId(id == null || "".equals(id) ? 0 : Integer.parseInt(id));


        if (vo.getId() > 0)
        {

            this.eventService.updateEvent(vo);
        }
        else
        {

            vo.setStatus(0);
            vo.setCreaterId(FMContext.getCurrent().getUserId());
            vo.setCustId(FMContext.getCurrent().getCustomerId());
            vo.setCategoryId(0);
            vo.setOwner(1);
            vo.setSentiment(0);

            this.eventService.addEvent(vo);
        }


        return "redirect:/incident/eventManager.htm";
    }

    /**
     * sharefun2068 2014-12-17 function:事件管理-->事件详情
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "detail"
    })
    public String queryEventBeanById(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws ServiceExcepiton
    {
        try
        {
            int id = Integer.parseInt(request.getParameter("id"));

            EventBean vo = this.eventService.queryEventById(id);
            CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
            query.setCustId(vo.getCustId());
            query.setType(1);
            query.setDataId(vo.getId());
            out.put("cateList", this.custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(query));
            out.put("beanVO", vo);

        }
        catch (CustDataCategoryServiceException ex)
        {
            Logger.getLogger(IncidentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "/eventfocus/custDataCategoryDetail";
    }

    /**
     * sharefun2068 2014-12-19 function:事件管理--》进入分类设置
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "setCategory"
    })
    public void setCustDataCategoryByDataId(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        String id = request.getParameter("dataId");
        String type = request.getParameter("type");
        Integer custId =FMContext.getCurrent().getCustomerId();
        try
        {
            CustDataCategoryQueryBean catQuery = new CustDataCategoryQueryBean();
            catQuery.setCustId(custId); //客户ID
            catQuery.setType(Integer.parseInt(type)); //数据分类类型

            List<CustDataCategoryBean> catList = this.custDataCategoryService.queryCustDataCategoryBeanListByCondition(catQuery);
            json.put("catList", catList);

            CustDataQueryBean custdataQuery = new CustDataQueryBean();
            custdataQuery.setType(Integer.parseInt(type));
            custdataQuery.setDataId(Integer.parseInt(id));
            custdataQuery.setCustId(custId); //客户ID

            List<CustDataBean> custdataList = this.custDataCategoryService.queryCustDataBeanListByCondition(custdataQuery);
            json.put("custdataList", custdataList);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            json.put("result", e.toString());
        }
        outJsonString(response, json);

    }

    /**
     * sharefun2068 2014-12-19 function:事件管理--》查询条件中的分类获取
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "getQueryCategoryJson"
    })
    public void getQueryCategoryJson(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        String type = request.getParameter("type");
        Integer custId =FMContext.getCurrent().getCustomerId();
        try
        {
            CustDataCategoryQueryBean catQuery = new CustDataCategoryQueryBean();
            catQuery.setCustId(custId); //客户ID
            catQuery.setType(Integer.parseInt(type)); //数据分类类型

            List<CustDataCategoryBean> catList = this.custDataCategoryService.queryCustDataCategoryBeanListByCondition(catQuery);
            json.put("catList", catList);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            json.put("result", e.toString());
        }
        outJsonString(response, json);

    }

    /**
     * sharefun2068 2014-12-19 function:事件管理--》提交事件分类设置
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value =
    {
        "submitSetCategory"
    })
    public void submitCustDataCategoryByDataId(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        String id = request.getParameter("dataId");
        String type = request.getParameter("type");
        String catIds = request.getParameter("catIds");
        Integer custId = FMContext.getCurrent().getCustomerId();

        try
        {
            this.custDataCategoryService.addCustDataListForDataId(Integer.parseInt(id), Integer.parseInt(type), catIds, custId);
            json.put("result", "success");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            json.put("result", e.toString());
        }
        outJsonString(response, json);

    }

    public void outJsonString(HttpServletResponse response, JSONObject json)
    {
        response.setContentType("text/javascript;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        try
        {
            PrintWriter out = response.getWriter();
            out.write(json.toString());
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
