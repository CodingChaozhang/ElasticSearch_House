package com.lcz.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.modelmapper.ModelMapper;

/**
 * 
 * @author LvChaoZhang
 *
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware{
	@Value("${spring.thymeleaf.cache}")
	private boolean thymeleafCacheEnable = true;
	
	 private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=applicationContext;
	}
	/**
	 * 静态资源加载
	 */

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
	
	/**
	 * 模板资源解析器
	 * @return
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.thymeleaf")
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver=new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(thymeleafCacheEnable);
		return templateResolver;
	}
	/**
	 * Thymeleaf标准方言解释器
	 */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine=new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		//支持spring el表达式
		templateEngine.setEnableSpringELCompiler(true);
		//支持springsecurity方言
		SpringSecurityDialect securityDialect=new SpringSecurityDialect();
		templateEngine.addDialect(securityDialect);
		return templateEngine;
	}
	
	/**
	 * 视图解析器
	 */
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver=new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}
	/**
     * Bean Util
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
