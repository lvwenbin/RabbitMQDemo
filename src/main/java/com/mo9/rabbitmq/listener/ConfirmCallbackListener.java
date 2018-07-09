package com.mo9.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

@Service
public class ConfirmCallbackListener implements ConfirmCallback {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {

		if (ack) {
			if (null != correlationData) {
				logger.info("消息发送成功-------------------------->message:" + correlationData.getId());
			}
		} else {
			logger.info("消息发送失败--------------------------->message:" + correlationData.getId() + " cause:" + cause);
		}

	}

}
