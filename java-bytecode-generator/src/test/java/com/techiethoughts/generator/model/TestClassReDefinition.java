package com.techiethoughts.generator.model;

import com.techiethoughts.AbstractPersistableUnitTests;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Opcodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Rajesh Bandarupalli
 */

public class TestClassReDefinition extends AbstractPersistableUnitTests {

    @Test
    public void testClassRedefinition() throws Exception {
        final String CLASS_NAME = "com.techiethoughts.Test";

        DynamicType.Unloaded<?> clazz = new ByteBuddy()
                .subclass(Object.class)
                .name(CLASS_NAME)
                .defineField("a1", String.class, Opcodes.ACC_PRIVATE)
                .make();

        Class<?> loadedClazz = loadClass(clazz, CLASS_NAME);
        Class<?> refClazz = Class.forName(CLASS_NAME, true, loadedClazz.getClassLoader());
        assertNotNull(refClazz);
        assertEquals(loadedClazz, refClazz);
        assertNotNull(refClazz.getDeclaredField("a1"));

        DynamicType.Unloaded<?> redefinedClazz = new ByteBuddy()
                .subclass(Object.class)
                .name(CLASS_NAME)
                .defineField("a1", String.class, Opcodes.ACC_PRIVATE)
                .defineField("a2", String.class, Opcodes.ACC_PRIVATE)
                .make();

        loadedClazz =  loadClass(redefinedClazz, CLASS_NAME);
        refClazz = Class.forName(CLASS_NAME, true, loadedClazz.getClassLoader());
        assertNotNull(refClazz);
        assertEquals(loadedClazz, refClazz);
        assertNotNull(refClazz.getDeclaredField("a1"));
        assertNotNull(refClazz.getDeclaredField("a2"));

    }

    private Class<?> loadClass(DynamicType.Unloaded<?> unloadedClazz, String className) {
        return unloadedClazz.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
    }
}
