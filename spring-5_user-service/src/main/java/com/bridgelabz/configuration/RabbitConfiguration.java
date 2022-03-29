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
	
	public static final String VERIFY_EMAIL_QUEUE = "sp5_emailVerify_queue";
	public static final String PASSWORD_RESET_QUEUE = "sp5_forgetPassword_queue";
	public static final String EXCHANGE = "email_exchange";
	public static final String ROUTING_KEY1 = "email_routingKey";
	public static final String ROUTING_KEY2 = "password_routingKey";


	@Bean
	public Queue emailVerifyQueue() {
		return new Queue(VERIFY_EMAIL_QUEUE);
	}
	
	@Bean
	public Queue forgetPasswordQueue() {
		return new Queue(PASSWORD_RESET_QUEUE);
	}
	
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}
	
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(emailVerifyQueue()).to(exchange()).with(ROUTING_KEY1);
	}
	
	@Bean
	public Binding binding2() {
		return BindingBuilder.bind(forgetPasswordQueue()).to(exchange()).with(ROUTING_KEY2);
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
