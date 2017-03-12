package org.sevensource.support.jpa.service;

public class SecuredService {

	// https://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html
	// The #this and #root variables
	// @PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
	// http://docs.spring.io/spring-security/site/docs/current/reference/html/el-access.html
	
	// org.springframework.security.access
	// public interface PermissionEvaluator
	// http://www.baeldung.com/spring-security-create-new-custom-security-expression
	
	//
//	@Retention(RetentionPolicy.RUNTIME)
//	@Target(ElementType.TYPE)
//	@SpringApplicationConfiguration
//	@ActiveProfiles("test")
//	public @interface SpringContextTest {
//
//	    @AliasFor(annotation = SpringApplicationConfiguration.class, attribute = "classes")
//	    Class<?>[] value() default {};
//
//	    @AliasFor("value")
//	    Class<?>[] classes() default {};
//	}
	
	
	
}
