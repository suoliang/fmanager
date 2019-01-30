///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.cyyun.base.mq;
//
//import javax.jms.Destination;
//import javax.jms.ExceptionListener;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.log4j.Logger;
//
///**
// * 
// * @author sunzhu
// */
//public class ArticleProducer implements ExceptionListener {
//	protected static Logger logger = Logger.getLogger(ArticleProducer.class);
//	String brokerUrl;
//	String queueName;
//	ActiveMQConnectionFactory connectionFactory;
//	javax.jms.Connection mqCon;
//	MessageProducer producer;
//	Session session;
//	Destination queue;
//	boolean isRunOnce = false;
//
//	/**
//	 * 构造函数
//	 * 
//	 * @param brokerUrl
//	 * @param queue
//	 * @param logger
//	 */
//	public ArticleProducer(String brokerUrl, String queue) {
//		this.brokerUrl = brokerUrl;// 192.168.1.166
//		queueName = queue;// com.cyyun.webmon.spider.datasvc.article
//		connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
//		try {
//			initMQ();
//		} catch (Exception e) {
//			logger.error("init error ", e);
//		}
//	}
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
//		session = mqCon.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		queue = session.createQueue(queueName);
//
//		producer = session.createProducer(queue);
//		mqCon.setExceptionListener(this);
//	}
//
//	public boolean send(String text) {
//		boolean flag = true;
//		try {
//			Message message = session.createTextMessage(text);
//			MessageProducer producer = session.createProducer(queue);
//			producer.send(message);
//		} catch (Exception e) {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
//			try {
//				closeMQ();
//			} catch (Exception e1) {
//				logger.error("closeMQ fail ", e);
//			}
//			try {
//				initMQ();
//			} catch (Exception e1) {
//				logger.error("initMQ fail ", e);
//			}
//			flag = false;
//			logger.error("send message fail ", e);
//		}
//		return flag;
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
//				logger.error("Failed to re-connect to MQ server! Wait 30 seconds to reconnect.", ex);
//				try {
//					Thread.sleep(30 * 1000l);
//				} catch (InterruptedException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
//			}
//		}
//	}
//
//	/**
//	 * 关闭MQ资源
//	 */
//	protected void closeMQ() {
//		try {
//			producer.close();
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
//
//}
