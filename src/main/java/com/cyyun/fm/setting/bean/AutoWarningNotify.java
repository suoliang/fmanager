package com.cyyun.fm.setting.bean;

import java.util.List;

import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;

/**
 * <h3>自动预警通知</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class AutoWarningNotify {
	private String guid;//自动预警文章
	private List<NotifyInfo> notifyInfos;//自动通知信息

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<NotifyInfo> getNotifyInfos() {
		return notifyInfos;
	}

	public void setNotifyInfos(List<NotifyInfo> notifyInfos) {
		this.notifyInfos = notifyInfos;
	}

	public static class NotifyInfo {
		private Integer cid;//客户ID
		private Integer kid;//关键字ID
		private int rid;   // 预警规则ID
		private String ruleName;   // 预警规则名称
		
		public Integer getCid() {
			return cid;
		}

		public void setCid(Integer cid) {
			this.cid = cid;
		}

		public Integer getKid() {
			return kid;
		}

		public void setKid(Integer kid) {
			this.kid = kid;
		}

		public int getRid() {
			return rid;
		}

		public void setRid(int rid) {
			this.rid = rid;
		}

		public String getRuleName() {
			return ruleName;
		}

		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}
		
	}

	public static void main(String[] args) {
		AutoWarningNotify notifyWarning = new AutoWarningNotify();
		notifyWarning.setGuid("abcdefghijklmn");

		AutoWarningNotify.NotifyInfo notifyInfo = new AutoWarningNotify.NotifyInfo();
		notifyInfo.setCid(700);
		notifyInfo.setKid(1);

		notifyWarning.setNotifyInfos(ListBuilder.add(notifyInfo).build());

		String json = JsonUtil.toJson(notifyWarning);

		AutoWarningNotify notifyWarning2 = JsonUtil.getObject(json, AutoWarningNotify.class);

		System.out.println(json);
		System.out.println(notifyWarning2.getGuid());
		System.out.println(notifyWarning2.getNotifyInfos().get(0).getCid());
		System.out.println(notifyWarning2.getNotifyInfos().get(0).getKid());
	}
}