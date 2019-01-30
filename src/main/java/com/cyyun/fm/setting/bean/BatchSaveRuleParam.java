package com.cyyun.fm.setting.bean;

import java.util.List;

import com.cyyun.common.core.util.ListBuilder;

/**
 * <h3>列表</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class BatchSaveRuleParam {
	private List<WarningRuleParam> rules = ListBuilder.newArrayList();

	public List<WarningRuleParam> getRules() {
		return rules;
	}

	public void setRules(List<WarningRuleParam> rules) {
		this.rules = rules;
	}
}