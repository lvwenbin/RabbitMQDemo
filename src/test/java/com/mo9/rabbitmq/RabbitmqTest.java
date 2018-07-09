package com.mo9.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.mo9.rabbitmq.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitmqApplication.class)
@EnableAutoConfiguration
public class RabbitmqTest {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private CachingConnectionFactory connectionFactory;
	@Value("${spring.rabbitmq.testDirectExchange}")
	private String testDirectExchange;
	@Value("${spring.rabbitmq.testTopicExchange}")
	private String testTopicExchange;
	@Value("${spring.rabbitmq.testFanoutExchange}")
	private String testFanoutExchange;
	@Value("${spring.rabbitmq.topicRoutingKey}")
	private String topicRoutingKey;
	@Value("${spring.rabbitmq.directRoutingKey}")
	private String directRoutingKey;

	/**
	 * 完全匹配类型(根据RoutingKey的模完全匹配)
	 */
	@Test
	public void directExchangeTest() {

		String message = "directExchangeTest sending message";
		JSONObject request = this.getRequest(message);

		rabbitTemplate.convertAndSend(testDirectExchange, directRoutingKey, request.toString());

	}

	/**
	 * 主题匹配类型(根据RoutingKey的模糊匹配)
	 * 
	 */
	@Test
	public void topicExchangeTest() {
		String message = "topicExchangetest sending message";
		JSONObject request = this.getRequest(message);
		rabbitTemplate.convertAndSend(testTopicExchange, topicRoutingKey, request.toString());

	}

	/**
	 * 广播式类型
	 */
	@Test
	public void fanoutExchangeTest() {
		String message = "fanoutExchangeTest sending message";
		JSONObject request = this.getRequest(message);
		rabbitTemplate.convertAndSend(testFanoutExchange, null, request.toString());

	}

	/**
	 * 确认机制 ConfirmCallbackListener 会收到确认的信息
	 */
	@Test
	public void confirmTest() {
		String message = "confirmTest sending message";
		JSONObject request = this.getRequest(message);
		CorrelationData correlationData = new CorrelationData(message);
		rabbitTemplate.convertAndSend(testDirectExchange, directRoutingKey, request.toString(), correlationData);

	}

	/**
	 * 事务
	 * 
	 * @throws IOException
	 */
	@Test
	public void transactionTest() throws IOException {
		String message = "transactionTest sending message";
		JSONObject request = this.getRequest(message);
		Connection connection = connectionFactory.createConnection();
		connectionFactory.setPublisherConfirms(false);
		Channel channel = connection.createChannel(true);// 创建一个支持事务的通道

		try {

			channel.txSelect();

			channel.basicPublish(testDirectExchange, directRoutingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,
					request.toString().getBytes());// MessageProperties.PERSISTENT_TEXT_PLAIN
													// 表示为持久化类型
			int result = 1 / 0;// 异常代码加上后会触发回滚
			channel.txCommit();
		} catch (Exception e) {
			logger.info("Exception " + e);
			channel.txRollback();
			logger.info("Rollback 消息回滚成功");
		}

	}

	private JSONObject getRequest(String message) {
		JSONObject request = new JSONObject();
		request.put("timestamp", System.currentTimeMillis());
		request.put("messageId", UUID.randomUUID().toString());
		request.put("message", message);
		return request;
	}

}
