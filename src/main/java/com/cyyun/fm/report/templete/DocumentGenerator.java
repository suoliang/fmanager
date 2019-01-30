package com.cyyun.fm.report.templete;

import java.io.Writer;
import java.util.Map;

import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

/**
 * <h3>文档生成器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class DocumentGenerator {

	@Autowired
	private VelocityConfigurer velocityConfig;

	private ToolManager manager = new ToolManager();

	public DocumentGenerator() {
		manager.configure("/toolbox.xml");
	}

	public void generate(String docPath, Map<String, Object> params, Writer writer) {
		ToolContext context = manager.createContext();
		context.putAll(params);

		velocityConfig.getVelocityEngine().mergeTemplate(docPath, "UTF-8", context, writer);
	}
}