package com.techiethoughts.config;

import com.techiethoughts.generator.attribute.AttributeGenerator;
import com.techiethoughts.generator.model.ModelGenerator;
import com.techiethoughts.publisher.ModelPublisher;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rajesh Bandarupalli
 */

@Configuration
public class JavaByteCodeGeneratorBeanConfig {

    @Bean
    public ModelPublisher modelPublisher() {
        return new ModelPublisher();
    }

    @Bean
    public ModelGenerator modelGenerator() {
        return new ModelGenerator();
    }

    @Bean
    public AttributeGenerator attributeGenerator() {
        return new AttributeGenerator();
    }

    @Bean
    public String init() {
        ByteBuddyAgent.install();
        return "true";
    }

}
