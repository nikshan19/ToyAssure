package com.increff.assure.spring;

import org.hibernate.annotations.Filter;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc

@ComponentScan(value = {"com.increff.assure"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SpringConfig.class)})

@PropertySources({ //
        @PropertySource("classpath:Test.properties") //
})
public class SpringTestConfig {

}
