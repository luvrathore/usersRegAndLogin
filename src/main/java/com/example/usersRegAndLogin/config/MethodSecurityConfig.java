//package com.example.usersRegAndLogin.config;
//
//import com.example.usersRegAndLogin.security.CustomMethodSecurityExpressionHandler;
//import com.example.usersRegAndLogin.security.CustomPermissionEvaluator;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
//
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        // final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        final CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
//        // no setting on roles yet
//        // no setting  on authority yet
//        return expressionHandler;
//    }
//}