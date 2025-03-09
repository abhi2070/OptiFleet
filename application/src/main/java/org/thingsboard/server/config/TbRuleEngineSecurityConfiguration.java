
package org.thingsboard.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@ConditionalOnExpression("'${service.type:null}'=='tb-rule-engine'")
public class TbRuleEngineSecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().cacheControl().and().frameOptions().disable()
                .and().cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/prometheus").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}
