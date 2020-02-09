package com.techiethoughts.generator.annotation;

import com.techiethoughts.AbstractPersistableUnitTests;
import com.techiethoughts.domains.core.AttributeDetail;
import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.domains.core.ValidationConstraintsDetail;
import com.techiethoughts.util.ByteCodeGenUtil;
import com.techiethoughts.util.PublisherContext;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.techiethoughts.domains.core.AttributeDetail.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Rajesh Bandarupalli
 */

public class NotNullAnnotationTest extends AbstractPersistableUnitTests {


    @Test
    public void testNotNullAnnotation() {

        ModelDetail clazzMetadata = new ModelDetail();
        clazzMetadata.setPackageName("com.techiethoughts");
        clazzMetadata.setName("Person");
        ValidationConstraintsDetail notNullMetaData = new ValidationConstraintsDetail();
        notNullMetaData.setConstraintType(ValidationConstraintsDetail.ConstraintType.NotNull);
        notNullMetaData.setMessage("Field is required.");

        AttributeDetail q1 = new AttributeDetail();
        q1.setName("firstName");
        q1.setType(STRING);
        q1.setId(1L);
        q1.setConstraintsDetail(Arrays.asList(notNullMetaData));

        clazzMetadata.setAttributes(Arrays.asList(q1));

        try {
            publisher.publishModel(clazzMetadata);
            Class<?> clazz = Class.forName(ByteCodeGenUtil.getFullyQualifiedClassName(clazzMetadata), false,
                    PublisherContext.class.getClassLoader());
            Field field = clazz.getDeclaredField(q1.getName());
            NotNull notNull = field.getDeclaredAnnotation(NotNull.class);
            assertEquals("Field is required.", notNull.message());

        } catch (Exception e) {
            assertTrue(false);
        }

    }

}

