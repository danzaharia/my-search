package com.oup.alfresco.multisearch.webscripts;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MultiSearchWebScript extends DeclarativeWebScript {

	private Map<String, List> searchUrlMap;

	private static Log LOGGER = LogFactory.getLog(MultiSearchWebScript.class);

	@Override
	public Map<String, Object> executeImpl(WebScriptRequest req, Status status,
			Cache cache) {

		Map<String, Object> result = new HashMap<String, Object>();

		String type = req.getParameter("type");
		List<String> searchUrls = searchUrlMap.get(type);
		List<String> urls = new ArrayList<String>();
		// searchValue

		Map<String, Object> model = new HashMap<String, Object>();
		for (String url : searchUrls) { 
			for (String param : req.getParameterNames()) {
				model.put(param, req.getParameter(param));
			}
			try {
				urls.add(process(model, url));
			} catch (TemplateException e) {
				result.put("error", e.getMessage());
			}
		}
		result.put("urls", urls);

		return result;
	}

	@Required
	public void setSearchUrlMap(Map<String, List> searchUrlMap) {
		this.searchUrlMap = searchUrlMap;
	}

	public static String process(Map<String, Object> model,
			String templateString) throws TemplateException {

		try {
			Template tmpl = new Template("output", new StringReader(
					templateString), new Configuration());
			Writer out = new StringWriter();
			tmpl.process(model, out);
			return out.toString();

		} catch (Exception ex) {
			throw new TemplateException(ex.getMessage(), null);
		}
	}
}
