package com.mo9.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
	@Value("${spring.rabbitmq.testDirectExchange}")
	private String testDirectExchange;
	@Value("${spring.rabbitmq.testTopicExchange}")
	private String testTopicExchange;
	@Value("${spring.rabbitmq.testFanoutExchange}")
	private String testFanoutExchange;
	@Value("${spring.rabbitmq.testDirectQueue1}")
	private String testDirectQueue1;
	@Value("${spring.rabbitmq.testDirectQueue2}")
	private String testDirectQueue2;
	@Value("${spring.rabbitmq.testTopicQueue1}")
	private String testTopicQueue1;
	@Value("${spring.rabbitmq.testTopicQueue2}")
	private String testTopicQueue2;
	@Value("${spring.rabbitmq.testFanoutQueue1}")
	private String testFanoutQueue1;
	@Value("${spring.rabbitmq.testFanoutQueue2}")
	private String testFanoutQueue2;
	@Value("${spring.rabbitmq.topicRoutingKey}")
	private String topicRoutingKey;
	@Value("${spring.rabbitmq.directRoutingKey}")
	private String directRoutingKey;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;
	@Value("${spring.rabbitmq.addresses}")
	private String addresses;
	@Value("${spring.rabbitmq.vhost}")
	private String vhost;

	@Autowired
	private ConfirmCallback confirmCallbackListener;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 创建队列
	@Bean(value = "directQueue1")
	public Queue queue1() {
		return new Queue(testDirectQueue1);// 默认已经开启持久化了
	}

	@Bean(value = "directQueue2")
	public Queue queue2() {
		return new Queue(testDirectQueue2);
	}

	@Bean(value = "topicQueue1")
	public Queue queue3() {
		return new Queue(testTopicQueue1);
	}

	@Bean(value = "topicQueue2")
	public Queue queue4() {
		return new Queue(testTopicQueue2);
	}

	@Bean(value = "fanoutQueue1")
	public Queue queue5() {
		return new Queue(testFanoutQueue1);
	}

	@Bean(value = "fanoutQueue2")
	public Queue queue6() {
		return new Queue(testFanoutQueue2);
	}

	// 创建一个 direct 类型的交换器
	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(testDirectExchange);

	}

	@Bean
	public Binding directBinding1(@Qualifier(value = "directQueue1") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("weather");
	}

	@Bean
	public Binding directBinding2(@Qualifier(value = "directQueue2") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("weather");
	}

	// 创建一个 topic 类型的交换器
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(testTopicExchange);
		// return new TopicExchange(TOPIC_EXCHANGE_NAME);
	}

	// 使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
	@Bean
	public Binding topicBinding1(@Qualifier(value = "topicQueue1") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("usa.#");
	}

	@Bean
	public Binding topicBinding2(@Qualifier(value = "topicQueue2") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("news.#");// 不匹配
	}

	// 创建一个 fanout 类型的交换器
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(testFanoutExchange);
	}

	@Bean
	public Binding fanoutBinding1(@Qualifier(value = "fanoutQueue1") Queue queue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(queue).to(fanoutExchange);
	}

	@Bean
	public Binding fanoutBinding2(@Qualifier(value = "fanoutQueue2") Queue queue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(queue).to(fanoutExchange);
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setPublisherConfirms(true);// 消息确认机制
		connectionFactory.setAddresses(addresses);// 集群的配置 地址用逗号隔开
		// connectionFactory.setVirtualHost(vhost);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setConfirmCallback(confirmCallbackListener);
		return rabbitTemplate;
	}

}
