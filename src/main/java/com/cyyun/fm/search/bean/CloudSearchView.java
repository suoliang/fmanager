package com.cyyun.fm.search.bean;

import java.util.ArrayList;
import java.util.List;

import com.cyyun.base.service.bean.ArticleBean;

/**
 * <h3>页面展示帮助类</h3>
 * 
 * @author 	LIUJUNWU
 * @version 1.0.0
 */
public class CloudSearchView extends ArticleBean{
	/** 版面ID(引擎ID)数组 */
	private List<Integer> boardIds=new ArrayList<Integer>();
//	private List<String> boardIds=new ArrayList<String>();
	/** 摘要 */
	private String abContent;
	/** 内容 */
	private String content;
	

	public String getAbContent() {
		return abContent;
	}

	public void setAbContent(String abContent) {
		this.abContent = abContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Integer> getBoardIds() {
		return boardIds;
	}

	public void setBoardIds(List<Integer> boardIds) {
		this.boardIds = boardIds;
	}


	


	
	
	
	
}
