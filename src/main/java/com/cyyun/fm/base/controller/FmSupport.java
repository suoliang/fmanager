package com.cyyun.fm.base.controller;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cyyun.common.core.util.SetBuilder;

@Service("FmSupport")
public class FmSupport extends BaseSupport{

	public Set<String> toStringSet(String keywords) {
		if (StringUtils.isNotBlank(keywords)) {
			Set<String> strSet = SetBuilder.newHashSet();
			for (String keyword : keywords.split(",")) {
				if (StringUtils.isNotBlank(keyword)) {
					if (keyword.indexOf("(") != -1
							|| keyword.indexOf(")") != -1
							|| keyword.indexOf("|") != -1) {
						String[] wordArr = keyword.split("-");
						String words = wordArr[0];
						words = words.replaceAll("\\(|\\)|\\|", "");
						for (String word : words.split(" ")) {
							if (StringUtils.isNotBlank(word)) {
								strSet.add(word.trim());
							}
						}
					} else {
						for (String word : keyword.split(" ")) {
							if (StringUtils.isNotBlank(word)) {
								if (word.indexOf("-") == -1) {
									strSet.add(word.trim());
								}
							}
						}
					}
				}
			}
			return strSet;
		}
		return null;
	}
	
	public static void main(String[] args) {
//		String str = "胜多负少 (是范德萨的)";
		String str = "eweawf azsdfasd (sdf | sfsd | sdfsfsf) -(thtd | htftgt)";
		Set<String> set = new FmSupport().toStringSet(str);
		System.out.println(set);
	}
}
