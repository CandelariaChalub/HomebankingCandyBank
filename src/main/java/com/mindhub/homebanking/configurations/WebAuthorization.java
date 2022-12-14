package com.mindhub.homebanking.configurations;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity /*se usa para habilitar SpringSecurity en nuestro proyecto.*/
@Configuration/*que la clase de abajo va a ser una configuracion*/
    public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Override /*estás sobrescribiendo correctamente un método*/

    /*protected permite el acceso desde subclases y miembros del mismo paquete*/
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()

                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers(HttpMethod.POST, "/api/transactions").hasAnyAuthority("CLIENT", "ADMIN")
                .antMatchers("/web/**", "/api/clients/current", "/api/transactions").hasAnyAuthority("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/clients/**","/api/accounts","/admin/**","/rest/**","/h2-console/**","/api/accounts/**").hasAuthority("ADMIN");


        http.formLogin()

                .usernameParameter("email")

                .passwordParameter("password")

                .loginPage("/api/login");


        http.logout().logoutUrl("/api/logout");

        http.csrf().disable();


        http.headers().frameOptions().disable();


        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication

        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response

        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response

        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    }
}
