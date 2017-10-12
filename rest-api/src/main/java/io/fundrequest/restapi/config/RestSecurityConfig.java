package io.fundrequest.restapi.config;

import io.fundrequest.core.user.UserService;
import io.fundrequest.restapi.security.UserJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationManager customAuthManager;

    @Autowired
    private CivicAuthClient civicAuthClient;

    @Autowired
    private UserJsonParser userJsonParser;

    @Autowired
    private UserService userService;


    public RestSecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().and()
                .anonymous().and()
                .servletApi().and()
                .authorizeRequests()
                .antMatchers("/api").authenticated()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .addFilterBefore(new StatelessAuthenticationFilter(civicAuthClient, userJsonParser, userService),
                        UsernamePasswordAuthenticationFilter.class);

    }


    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return customAuthManager;
    }

    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return customAuthManager;
    }

}
