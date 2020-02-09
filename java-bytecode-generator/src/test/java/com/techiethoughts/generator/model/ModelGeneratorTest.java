package com.techiethoughts.generator.model;

import com.techiethoughts.AbstractPersistableUnitTests;
import net.bytebuddy.ByteBuddy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Rajesh Bandarupalli
 */

public class ModelGeneratorTest extends AbstractPersistableUnitTests {

    /*
       Test case creates a simple Greet class at runtime and loads it on to JVM.
       public class Greet {

       }
    */
    @Test
    public void testComplexClassGeneration() {

        Class<?> dynamicClazz = new ByteBuddy()
                .subclass(Object.class)
                .name("com.techiethoughts.Greet")
                .make().load(getClass().getClassLoader()).getLoaded();

        assertNotNull(dynamicClazz);
        assertEquals("com.techiethoughts.Greet", dynamicClazz.getName());
    }

}