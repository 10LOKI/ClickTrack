package com.youcode.clicktrack.config;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// En XML, les controllers ont un @RequestMapping mais aucun @Controller.
public class XmlRequestMappingHandlerMapping extends RequestMappingHandlerMapping
{
    @Override
    protected boolean isHandler(Class<?> beanType)
    {
        return AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class);
    }
}
