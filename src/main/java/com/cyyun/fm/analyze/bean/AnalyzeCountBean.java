package com.cyyun.fm.analyze.bean;

import java.util.Map;

/** 
 * @author  SuoLiang  
 * @version 2016年8月17日
 */
public class AnalyzeCountBean {
	
	public AnalyzeCountBean(Map<String, Long> readInMedia,Map<String, Long> replyInMedia) {
		this.readInMedia = readInMedia;
		this.replyInMedia = replyInMedia;
	}
	
	private Map<String, Long> readInMedia;
	
	private Map<String, Long> replyInMedia;
	
	public Map<String, Long> getReadInMedia() {
		return readInMedia;
	}
	
	public void setReadInMedia(Map<String, Long> readInMedia) {
		this.readInMedia = readInMedia;
	}
	
	public Map<String, Long> getReplyInMedia() {
		return replyInMedia;
	}
	
	public void setReplyInMedia(Map<String, Long> replyInMedia) {
		this.replyInMedia = replyInMedia;
	}
	
	
}
