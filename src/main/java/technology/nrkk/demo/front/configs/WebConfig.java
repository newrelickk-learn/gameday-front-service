package technology.nrkk.demo.front.configs;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import technology.nrkk.demo.front.delegator.NewRelicDelegator;
import technology.nrkk.demo.front.filters.TransactionNamingFilter;

@Configuration
@EnableWebMvc
@ConfigurationPropertiesScan
@ComponentScan(basePackages = "technology.nrkk.demo.front")
class WebConfig implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.ctx = context;
    }


    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver() {

        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.ctx);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        resolver.setCheckExistence(false);
        return resolver;

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/resources/**" URIパターンにリクエストがあった場合、classpathの"/static/"にあるリソースとマッピングします
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    public TransactionNamingFilter transactionNamingReactiveFilter() {
        return new TransactionNamingFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(transactionNamingReactiveFilter());
    }

    @Bean("newrelic")
    public NewRelicDelegator newrelic() {
        return new NewRelicDelegator();
    }
/*
    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
        @Value("classpath:/static/index.html") Resource html) {
        return route(GET("/**").and(accept(MediaType.TEXT_HTML)), request
            -> ok().contentType(MediaType.TEXT_HTML).bodyValue(new SpringResourceTemplateResource(html, Encoding.DEFAULT_CHARSET.name()))
        );
    }

 */
    @Bean
    public ISpringTemplateEngine thymeleafTemplateEngine() {
        // We override here the SpringTemplateEngine instance that would otherwise be
        // instantiated by
        // Spring Boot because we want to apply the SpringWebFlux-specific context
        // factory, link builder...
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        return templateEngine;
    }
    @Bean
    public ThymeleafViewResolver thymeleafChunkedAndDataDrivenViewResolver() {
        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
//        viewResolver.setOrder(1);
//        viewResolver.setViewNames(new String[]{"home"});
        return viewResolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafChunkedAndDataDrivenViewResolver());
    }


}
