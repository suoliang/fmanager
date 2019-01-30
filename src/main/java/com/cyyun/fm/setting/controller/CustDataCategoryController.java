/**
 *
 */
package com.cyyun.fm.setting.controller;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author cyyun
 *
 */
@Controller
@RequestMapping("/custdata")
public class CustDataCategoryController extends BaseController
{

  @Autowired
  private CustDataCategoryService custDataCategoryService;
  

  	/**
  	 * 事件分类列表
  	 * @param keywords
  	 * @param currentpage
  	 * @param pagesize
  	 * @return
  	 */
	@RequestMapping(value = { "eventList" })
	public ModelAndView eventList(String keywords, Integer currentpage, Integer pagesize) {
		try {
			Integer type=FMConstant.CLASSIFICATIOTYPE.event.getValue();
			PageInfoBean<CustDataCategoryBean> pageInfo = queryCustDataByPage(type, keywords, currentpage, pagesize);
			return view("/localsetting/custDataCategory").addObject("type", type).addObject("listTitle", FMConstant.CLASSIFICATIONAME.event.getValue()).addObject("page", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户分类失败");
		}
	}
	
  	/**
  	 * 人物分类列表
  	 * @param keywords
  	 * @param currentpage
  	 * @param pagesize
  	 * @return
  	 */
	@RequestMapping(value = { "personList" })
	public ModelAndView personList(String keywords, Integer currentpage, Integer pagesize) {
		try {
			Integer type=FMConstant.CLASSIFICATIOTYPE.person.getValue();
			PageInfoBean<CustDataCategoryBean> pageInfo = queryCustDataByPage(type, keywords, currentpage, pagesize);
			return view("/localsetting/custDataCategory").addObject("type", type).addObject("listTitle", FMConstant.CLASSIFICATIONAME.person.getValue()).addObject("page", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户分类失败");
		}
	}
	
  	/**
  	 * 站点分类列表
  	 * @param keywords
  	 * @param currentpage
  	 * @param pagesize
  	 * @return
  	 */
	@RequestMapping(value = { "siteList" })
	public ModelAndView siteList(String keywords, Integer currentpage, Integer pagesize) {
		try {
			Integer type=FMConstant.CLASSIFICATIOTYPE.site.getValue();
			PageInfoBean<CustDataCategoryBean> pageInfo = queryCustDataByPage(type, keywords, currentpage, pagesize);
			return view("/localsetting/custDataCategory").addObject("type", type).addObject("listTitle", FMConstant.CLASSIFICATIONAME.site.getValue()).addObject("page", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户分类失败");
		}
	}

	/**
	* sharefun2068 
	* 2014-12-15
	* function:设置模块--》客户数据分类列表
	* @param request
	* @param response
	* @param out
	* @return 
	*/

	@RequestMapping(value = { "listPageByAjax" })
	public ModelAndView queryListPageByAjax(Integer type, String keywords, Integer currentpage, Integer pagesize) {
		try {
			PageInfoBean<CustDataCategoryBean> pageInfo = queryCustDataByPage(type, keywords, currentpage, pagesize);
			String listTitle = type == FMConstant.CLASSIFICATIOTYPE.event.getValue() ? FMConstant.CLASSIFICATIONAME.event.getValue() : (type == FMConstant.CLASSIFICATIOTYPE.person.getValue() ? FMConstant.CLASSIFICATIONAME.person.getValue() : FMConstant.CLASSIFICATIONAME.site.getValue());
			return view("/localsetting/custDataCategoryList").addObject("type", type).addObject("listTitle", listTitle).addObject("page", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户分类失败");
		}
	}

	private PageInfoBean<CustDataCategoryBean> queryCustDataByPage(Integer type, String keywords, Integer currentpage, Integer pagesize) throws Exception {
		if (type == null) {
			type = 1;
		}
		if (currentpage == null) {
			currentpage = 1;
		}
		if (pagesize == null) {
			pagesize = 10;
		}

		CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
		query.setType(type);

		if (StringUtils.isNotEmpty(keywords)) {
			query.setName(CyyunSqlUtil.dealSql(keywords));
		}

		query.setPageNo(currentpage);
		query.setPageSize(pagesize);

		return custDataCategoryService.queryCustDataCategoryPageList(query);
	}

    /**
     * sharefun2068 
     * 2014-12-15
     * function:设置模块--》提交新增/编辑客户数据分类
     * @param request
     * @param response
     * @param out
     * @return 
     */
	//TODO
    @RequestMapping(value ={ "addSubmit"})
    @ResponseBody
    public MessageBean addSubmit(HttpServletRequest request, HttpServletResponse response,  RedirectAttributes redirectAttributes)
    {
       int type=Integer.parseInt(request.getParameter("type"));
       String name=request.getParameter("name");
       String remarks=request.getParameter("remarks");
       
       String id=request.getParameter("id");
       
       CustDataCategoryBean vo=new CustDataCategoryBean();
     
       vo.setName(name);
       vo.setRemark(remarks);
       vo.setId(id==null||"".equals(id)?0:Integer.parseInt(id));
       
      try
      {
         if(vo.getId()>0){
          custDataCategoryService.updateCustDataCategory(vo);
          }else{
            vo.setStatus(1);
            vo.setType(type);
            vo.setCustId(FMContext.getCurrent().getCustomerId());
            vo.setParentId(0);
            vo.setCreaterId(FMContext.getCurrent().getUserId());
            vo.setCreater(FMContext.getLoginUser().getRealName());

            
          custDataCategoryService.addCustDataCategory(vo);
          }
         return buildMessage(MESSAGE_TYPE_SUCCESS, "操作成功");
      }
      catch (CustDataCategoryServiceException ex)
      {
          Logger.getLogger(CustDataCategoryController.class.getName()).log(Level.SEVERE, null, ex);
          return buildMessage(MESSAGE_TYPE_ERROR, "操作失败");
      }
    }  
    
    /**
     * sharefun2068 
     * 2014-12-15 
     * function:设置模块--》删除客户数据分类
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value ={"delete"})
    public void deleteCustDataCategoryById(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        String id = request.getParameter("id");
        try
        {
            this.custDataCategoryService.deleteCustDataCategoryById(Integer.parseInt(id));
            json.put("result", "success");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            json.put("result", e.toString());
        }
        outJsonString(response, json);

    }
    /**
     * sharefun2068 
     * 2014-12-17
     * function:设置模块--》客户数据分类详情
     * @param request
     * @param response
     * @param out
     * @return 
     */
    @RequestMapping(value ={ "detail"})
    public void queryCustDataCategoryById(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        int id=Integer.parseInt(request.getParameter("id"));
      try
      {
        CustDataCategoryBean vo  = this.custDataCategoryService.getCustDataCategoryBeanById(id);
        json.put("result", "success");
        json.put("vo", vo);
      }
      catch (CustDataCategoryServiceException ex)
      {
          json.put("result", "failure");
          Logger.getLogger(CustDataCategoryController.class.getName()).log(Level.SEVERE, null, ex);
      }
         
       outJsonString(response, json);
    }
    
   /**
     * sharefun2068 
     * 2015-01-19 
     * function:设置模块--》客户分类数据 设置排序
     *
     * @param request
     * @param response
     * @param out
     * @return
     */
    @RequestMapping(value ={"setIndex"})
    public void setIndexById(HttpServletRequest request, HttpServletResponse response, ModelMap out)
    {
        JSONObject json = new JSONObject();
        String id = request.getParameter("id");
        String index = (String)request.getParameter("index");
        try
        {
            CustDataCategoryBean vo=new CustDataCategoryBean();
            vo.setId(Integer.parseInt(id));
            vo.setIndex(Integer.parseInt(index));
            
            this.custDataCategoryService.updateCustDataCategory(vo);
            
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
