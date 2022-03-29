package com.bridgelabz.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
	
	public static final String MAIL_REMINDER_QUEUE = "sp5_noteReminder_queue";
	public static final String EXCHANGE = "email_exchange";
	public static final String ROUTING_KEY1 = "remindMail_routingKey";


	@Bean
	public Queue mailReminderQueue() {
		return new Queue(MAIL_REMINDER_QUEUE);
	}
	
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}
	
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(mailReminderQueue()).to(exchange()).with(ROUTING_KEY1);
	}
	
	
	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@Bean
	public AmqpTemplate template( ConnectionFactory connectionFactory ) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(converter());
		return rabbitTemplate;
	}
}

