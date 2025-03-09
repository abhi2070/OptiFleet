
package org.thingsboard.server.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@ConditionalOnMissingBean(TbCaffeineCacheConfiguration.class)
@ConditionalOnProperty(prefix = "redis.connection", value = "type", havingValue = "sentinel")
public class TBRedisSentinelConfiguration extends TBRedisCacheConfiguration {

    @Value("${redis.sentinel.master:}")
    private String master;

    @Value("${redis.sentinel.sentinels:}")
    private String sentinels;

    @Value("${redis.sentinel.password:}")
    private String sentinelPassword;

    @Value("${redis.sentinel.useDefaultPoolConfig:true}")
    private boolean useDefaultPoolConfig;

    @Value("${redis.db:}")
    private Integer database;

    @Value("${redis.password:}")
    private String password;

    public JedisConnectionFactory loadFactory() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.setMaster(master);
        redisSentinelConfiguration.setSentinels(getNodes(sentinels));
        redisSentinelConfiguration.setSentinelPassword(sentinelPassword);
        redisSentinelConfiguration.setPassword(password);
        redisSentinelConfiguration.setDatabase(database);
        if (useDefaultPoolConfig) {
            return new JedisConnectionFactory(redisSentinelConfiguration);
        } else {
            return new JedisConnectionFactory(redisSentinelConfiguration, buildPoolConfig());
        }
    }

}
