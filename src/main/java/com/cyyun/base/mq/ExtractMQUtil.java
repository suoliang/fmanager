//package com.cyyun.base.mq;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//public class ExtractMQUtil {
//	protected static Logger logger = Logger.getLogger(ExtractMQUtil.class);
//	public static Map<String, ArticleProducer> articleProducersMap = new ConcurrentHashMap<String, ArticleProducer>();
//	@Value("${mq.url}")
//	public String producerMqURl;
//	@Value("${mq.queue.focusInfo}")
//	public String articleQueueName;
//
//	public ArticleProducer getArticleProducer(String key) {
//		if (!articleProducersMap.containsKey(key)) {
//			ArticleProducer articleProducer = new ArticleProducer(producerMqURl, articleQueueName + key);
//			articleProducersMap.put(key, articleProducer);
//			logger.info("create ArticleProducer");
//		}
//		return articleProducersMap.get(key);
//	}
//
//}
