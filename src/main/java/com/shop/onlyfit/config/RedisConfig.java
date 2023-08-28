package com.shop.onlyfit.config;

import com.shop.onlyfit.dto.ChatDto;
import com.shop.onlyfit.dto.MessageDto;
import com.shop.onlyfit.service.RedisSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean("chatConnectionFactory")
    public LettuceConnectionFactory chatConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setDatabase(1);

        LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(redisConfig, lettuceConfig);
    }

    @Bean("jwtConnectionFactory")
    public LettuceConnectionFactory jwtConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setDatabase(0);

        LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(redisConfig, lettuceConfig);
    }

    @Bean
    public RedisTemplate<String, Object> jwtRedisTemplate(@Qualifier("jwtConnectionFactory")
                                                          RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("jwtConnectionFactory")
                                                       RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean("chatRedisTemplate")
    public RedisTemplate<String, MessageDto> chatRedisTemplate(@Qualifier("chatConnectionFactory")
                                                               RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MessageDto> chatRedisTemplate = new RedisTemplate<>();
        chatRedisTemplate.setConnectionFactory(connectionFactory);
        chatRedisTemplate.setKeySerializer(new StringRedisSerializer());

        chatRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return chatRedisTemplate;
    }

    // redis pub/sub 메세지를 처리하는 listener 설정
    @Bean
    public RedisMessageListenerContainer redisMessageListener(@Qualifier("chatConnectionFactory") RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }

    // 메세지를 구독자에게 보내는 역할
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        return new MessageListenerAdapter(redisSubscriber, "sendMessage");
    }

    @Bean("chatHashOps")
    public HashOperations<String, String, ChatDto.MessageResponse> hashOperations(@Qualifier("chatRedisTemplate") RedisTemplate<String, MessageDto> chatRedisTemplate) {
        return chatRedisTemplate.opsForHash();
    }
}