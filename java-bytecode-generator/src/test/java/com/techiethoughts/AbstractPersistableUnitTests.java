package com.techiethoughts;

import com.techiethoughts.config.JavaByteCodeGeneratorBeanConfig;
import com.techiethoughts.publisher.ModelPublisher;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author Rajesh Bandarupalli
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestApplication.class })
@Import(value = { JavaByteCodeGeneratorBeanConfig.class })
public abstract class AbstractPersistableUnitTests {

	@Autowired
	protected ModelPublisher publisher;

}

