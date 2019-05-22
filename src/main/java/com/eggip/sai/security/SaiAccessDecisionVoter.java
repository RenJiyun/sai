package com.eggip.sai.security;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class SaiAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


    /**
     * 1. 根据JWT token获取当前的登录用户
     * 2. 获取该用户所有可以访问的api资源
     * 3. 判断用户是否有权限执行
     * 4. 若当前api已配置数据权限，则在ThreadLocal中设置相关数据权限的信息
     */
    @Override
    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> attributes) {
        String requestUrl = object.getRequestUrl();
        return ACCESS_DENIED;
    }


}