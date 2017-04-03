package org.sevensource.support.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {
 
    @Override
    protected ModelAndView doResolveException
      (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//    	try {
//	    	if(ex instanceof EntityValidationException) {
//		response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value());
//		return new ModelAndView();
//	    	}
//    	} catch(IOException e) {
//    		throw new RuntimeException(e);
//    	}
    	
        return null;
    }
}