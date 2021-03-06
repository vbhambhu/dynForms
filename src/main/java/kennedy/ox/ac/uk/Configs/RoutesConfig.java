package kennedy.ox.ac.uk.Configs;

/**
 * Created by vinod on 06/10/2016.
 */


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class RoutesConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/home").setViewName("home");
//        registry.addViewController("/").setViewName("home");
//        registry.addViewController("/hello").setViewName("hello");
          registry.addViewController("/login").setViewName("/account/login");
        registry.addViewController("/forgot-password").setViewName("account/forgot-password");
    }

}