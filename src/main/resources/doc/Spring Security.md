## 核心组件
1. SecurityContextHolder
2. SecurityContext
3. Authentication Objects


## 认证
1. ExceptionTranslationFilter
2. AuthenticationEntryPoint
3. AbstractSecurityInterceptor
4. Authentication Mechanism
    *  form-base login 
    *  Basic authentication
    *  ...
5. AuthenticationManager
6. ProviderManager
7. AuthenticationProvider
8. UserDetails
9. UserDetailsService


## 访问受限资源
1. Secure Objects 
2. AbstractSecurityInterceptor  
3. AccessDecisionManager
4. configuration attributes


## 协同工作的组件
1. 无法通过授权时
    * ExceptionTranslationFilter
    * AuthenticationEntryPoint
    * AccessDeniedHandler

2. 不同请求之间安全上下文的载入和存储
    * SecurityContextPersistenceFilter
    * SecurityContextRepository

3. fslg










