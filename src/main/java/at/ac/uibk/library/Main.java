package at.ac.uibk.library;

import at.ac.uibk.library.configs.CustomServletContextInitializer;
import at.ac.uibk.library.configs.WebSecurityConfig;
import at.ac.uibk.library.utils.ViewScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.faces.webapp.FacesServlet;
import java.util.HashMap;

/**
 * Spring boot application. Execute maven with <code>mvn spring-boot:run</code>
 * to start this web application.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableScheduling
public class Main extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application
				.sources(new Class[] { Main.class, CustomServletContextInitializer.class, WebSecurityConfig.class });
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		FacesServlet servlet = new FacesServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet, "*.xhtml");
		servletRegistrationBean.setName("Faces Servlet");
		servletRegistrationBean.setAsyncSupported(true);
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}

	@Bean
	public CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
		HashMap<String, Object> customScopes = new HashMap<>();
		customScopes.put("view", new ViewScope());
		customScopeConfigurer.setScopes(customScopes);
		return customScopeConfigurer;
	}

}
