package com.shop.onlyfit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // 채팅 메시지를 저장하는 데 사용되는 Redis 데이터베이스 연결 설정
    @Bean("chatConnectionFactory")
    public LettuceConnectionFactory chatConnectionFactory() {
        // localhost에 실행중인 Redis 서버의 6379 포트로 연결 설정, 사용할 데이터베이스는 1번
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setDatabase(1);

        LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(redisConfig, lettuceConfig);
    }

    // JWT 토큰을 저장하는 데 사용되는 Redis 데이터베이스 연결 설정
    @Bean("jwtConnectionFactory")
    public LettuceConnectionFactory jwtConnectionFactory() {
        // localhost에 실행중인 Redis 서버의 6379 포트로 연결 설정, 사용할 데이터베이스는 0번
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setDatabase(0);

        LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(redisConfig, lettuceConfig);
    }

    // JWT 토큰을 위한 레디스 템플릿 생성
    @Bean("jwtRedisTemplate")
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
        // ObjectMapper를 생성하고 JSR310 모듈(Java 8 날짜/시간 API)을 등록합니다.
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        // Jackson2JsonRedisSerializer에 새로운 ObjectMapper를 사용합니다.
        Jackson2JsonRedisSerializer<MessageDto> serializer = new Jackson2JsonRedisSerializer<>(MessageDto.class);
        serializer.setObjectMapper(mapper);

        // 이 시리얼라이저를 RedisTemplate 설정에서 사용합니다.
        RedisTemplate<String, MessageDto> chatRedisTemplate = new RedisTemplate<>();

        // 연결 팩토리를 설정합니다.
        chatRedisTemplate.setConnectionFactory(connectionFactory);

        chatRedisTemplate.setKeySerializer(new StringRedisSerializer());

        // 키의 시리얼라이저로 StringRedisSerializer를 사용합니다.
        chatRedisTemplate.setValueSerializer(serializer);
        chatRedisTemplate.setHashValueSerializer(serializer);
        chatRedisTemplate.setHashKeySerializer(serializer);


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