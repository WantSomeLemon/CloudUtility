package com.Sem2.DTDM.config;

import com.Sem2.DTDM.listener.FileTaskListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {
    //host = 172.18.0.3
    //port = 6379
    //đang lỗi nên phải để bằng cách cứng rắn này
    //TODO: fix tại sao cho không cần phải fix cứng mà để nó truyền như lúc đầu ở file compose.xml, Docker file, RedisConfig

    @Value("${spring.redis.host:redis}") 
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;
    
    @Value("${app.queue.conversion-queue:default-lock}")
    private String conversionQueue;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("DEBUG: Connecting to Redis at {}:{}", redisHost, redisPort);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }
    
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(FileTaskListener fileTaskListener) {
        return new  MessageListenerAdapter(fileTaskListener, "onMessage");
    }
    
    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory, 
                                                           MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(conversionQueue));
        return container;
    }
}
