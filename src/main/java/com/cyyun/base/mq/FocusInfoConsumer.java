///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.cyyun.mobile.tools.mq;
//
//import javax.jms.Destination;
//import javax.jms.ExceptionListener;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageConsumer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.log4j.Logger;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.cyyun.process.service.bean.FocusInfoMq;
//
///**
// * 
// * @author sunzhu
// */
//public class FocusInfoConsumer implements Runnable, ExceptionListener {
//	protected static Logger logger = Logger.getLogger(FocusInfoConsumer.class);
//	String brokerUrl;
//	String queueName;
//	ActiveMQConnectionFactory connectionFactory;
//	javax.jms.Connection mqCon;
//	MessageConsumer consumer;
//	Session session;
//	Destination queue;
//	JsonParser jsonParser = new JsonParser();
//	boolean isRunOnce = false;
//
//	/**
//	 * 构造函数
//	 * 
//	 * @param brokerUrl
//	 * @param queue
//	 * @param logger
//	 */
//	public FocusInfoConsumer(String brokerUrl, String queue) {
//		this.brokerUrl = brokerUrl;// 192.168.1.166
//		queueName = queue;// com.cyyun.webmon.spider.datasvc.article
//		connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
//
//	}
//
//	public void process(Message m) {
//		try {
//			if (m == null) {
//				MiscTools.pause(2000);
//				return;
//			}
//			if (m instanceof TextMessage) {
//				TextMessage txtMsg = (TextMessage) m;
//				FocusInfoMq focusInfo=JSON.parseObject(txtMsg.getText(), FocusInfoMq.class);
//				if(focusInfo!=null){
//					FocusInfoProducer producer=ExtractMQUtil.getFocusInfoProducer(""+focusInfo.getCustId());
//					producer.send(JSONArray.toJSONString(focusInfo));
//				}
//				
//			}
//			
//		} catch (Exception e) {
//			logger.error("Failed to do updates!", e);
//		} finally {
//			try {
//				if (m != null) {
//					m.acknowledge();
//				}
//			} catch (JMSException e) {
//				logger.error(e);
//			}
//		}
//	}
//	
//
//
//	@Override
//	public void run() {
//
//		while (true) {
//			logger.info("ArticleUpdator for " + brokerUrl + " started.");
//			try {
//				initMQ();
//			} catch (Exception ex) {
//				logger.error("Failed to connect to MQ!", ex);
//				MiscTools.pause(60000);
//			}
//
//			try {
//				while (true) {
//					javax.jms.Message m = consumer.receive(1);// 此处不用单独try因为，这个外层就是try，单独加try和现在的外层try意义其实是一致的。都是从新开始运行此处代码。
//					process(m);
//				}
//			} catch (Exception e1) {
//				logger.error(e1.getMessage(), e1);
//				MiscTools.pause(60000);
//			} finally {
//				closeMQ();
//			}
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//
////		new Thread(new ArticleConsumer(ExtractMQUtil.CONSUMER_MQ_URL,
////				ExtractMQUtil.ARTICLE_QUEUE_NAME)).start();
////		Thread.sleep(1000000);
//
//		
////		  String json = IOUtils .toString(new
////		  FileInputStream("c:\\ip\\test.json"),"utf-8");
////		  System.out.println(json); IndexDoc indexDoc = JSON.parseObject(json,
////		  IndexDoc.class); System.out.println(indexDoc.custAttrs); JsonObject
////		  obj = (JsonObject) new JsonParser() .parse(json);
////		  System.out.println(obj.get("author").getAsString());
////		 
//
//		
//		  JsonObject obj = (JsonObject) new JsonParser().parse("{\"lids\":[3928,3949,266,4153]}");
//		  JsonElement custAttrsElement = obj.get("lids");
//		  String s=custAttrsElement.getAsJsonArray().toString();
//		  if
//		  (custAttrsElement == null) { logger.info("custAttrsElement is null");
//		  return; }
//		  
//		 JsonObject jsonElement = custAttrsElement.getAsJsonObject();
//		  
//		  System.out.println(jsonElement.get("731"));
//		 
//
//	}
//
//	/**
//	 * 测试模式，只从MQ检查一次数据更新
//	 */
//	public void runOnce() {
//		isRunOnce = true;
//		run();
//	}
//
//
//
//	/**
//	 * 连接MQ
//	 * 
//	 * @throws Exception
//	 */
//	protected void initMQ() throws Exception {
//		long start = System.currentTimeMillis();
//		while (true) {
//			try {
//				mqCon = connectionFactory.createConnection();
//				break;
//			} catch (Exception e) {
//				if (System.currentTimeMillis() - start < 5 * 60000)// try for 5
//																	// mins
//				{
//					Thread.sleep(5000);
//				} else
//					throw e;
//			}
//		}
//		mqCon.start();
//		// Create the session
//		session = mqCon.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//		queue = session.createQueue(queueName);
//		consumer = session.createConsumer(queue);
//		mqCon.setExceptionListener(this);
//	}
//
//	/**
//	 * 处理MQ连接出现异常
//	 * 
//	 * @param jmse
//	 */
//	@Override
//	public void onException(JMSException jmse) {
//		closeMQ();
//		while (true) {
//			logger.warn("Problem occurred on connection to MQ. Now trying to re-connect...");
//			try {
//				Thread.sleep(60000);
//				initMQ();
//				break;
//			} catch (Exception ex) {
//				logger.error(
//						"Failed to re-connect to MQ server! Wait 30 seconds to reconnect.",
//						ex);
//				MiscTools.pause(30 * 1000l);
//			}
//		}
//	}
//
//	/**
//	 * 解析消息
//	 * 
//	 * @param message
//	 * @return
//	 * @throws Exception
//	 */
//	protected JsonObject parseMessage(javax.jms.Message message)
//			throws Exception {
//		if (message instanceof TextMessage) {
//			TextMessage txtMsg = (TextMessage) message;
//			try {
//				JsonObject obj = (JsonObject) jsonParser
//						.parse(txtMsg.getText());
//				return obj;
//			} catch (Exception e) {
//				logger.error(e);
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 关闭MQ资源
//	 */
//	protected void closeMQ() {
//		try {
//			consumer.close();
//		} catch (Exception ex) {
//		}
//		try {
//			session.close();
//		} catch (Exception ex) {
//		}
//		try {
//			mqCon.close();
//		} catch (Exception ex) {
//		}
//	}
//}
