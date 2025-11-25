package training.springbootreactiv.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import training.springbootreactiv.dto.EmployeeDto;

@Configuration(proxyBeanMethods = false)
public class CacheConfig {
    @Bean
    public ReactiveRedisTemplate<Long, EmployeeDto> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        RedisSerializer<EmployeeDto> redisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, EmployeeDto.class);
        RedisSerializationContext<Long, EmployeeDto> context =
                RedisSerializationContext.<Long, EmployeeDto>newSerializationContext(
                                RedisSerializer.string())
                        .key(new GenericToStringSerializer<>(Long.class))
                        .hashKey(new Jackson2JsonRedisSerializer<>(Long.class))
                        .value(redisSerializer)
                        .hashValue(redisSerializer)
                        .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
