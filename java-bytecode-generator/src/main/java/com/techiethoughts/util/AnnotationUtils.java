package com.techiethoughts.util;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.stream.Stream;

import static net.bytebuddy.description.annotation.AnnotationValue.*;

/**
 * @author Rajesh Bandarupalli
 */

public class AnnotationUtils {

    /**
     * For All Primitives,String Array,Enum, Class<?>[]
     */
    public static AnnotationDescription.Builder assignAnnotationValue(Object source, AnnotationDescription.Builder builder, String propertyName) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(source.getClass(), propertyName);
        if (pd != null && pd.getReadMethod() != null) {
            Object value;
            try {
                value = pd.getReadMethod().invoke(source);
                if (value != null) {
                    if (value.getClass().getComponentType() == null && value.getClass().isEnum())
                        builder = builder.define(propertyName, ForEnumerationDescription.<Enum>of(new EnumerationDescription.ForLoadedEnumeration((Enum) value)));
                    else if (value.getClass().getComponentType() != null && !StringUtils.equals(value.getClass().getComponentType().getSimpleName(), "String")) {
                        builder = assignAnnotationArrayValue(value, builder, propertyName, TypeDescription.ForLoadedType.of(value.getClass().getComponentType()));
                    } else
                        builder = builder.define(propertyName, ForConstant.of(value));
                }
            } catch (Exception exc) {
                throw new RuntimeException(AppConstants.LOG_CONSTANT.code + "Error occurred while assigning value to the Annotation attribute " + propertyName, exc);
            }
        }
        return builder;
    }

    /**
     * For Enumeration array , Class<?>[]
     */
    public static AnnotationDescription.Builder assignAnnotationArrayValue(Object value, AnnotationDescription.Builder builder, String propertyName, TypeDescription arrayType) {
        if (value != null && arrayType.isEnum()) {
            try {
                String[] stringArray = Stream.of((Enum<?>[]) value).map(Enum::name).toArray(String[]::new);
                builder = builder.defineEnumerationArray(propertyName, arrayType, stringArray);
            } catch (Exception exc) {
                throw new RuntimeException(AppConstants.LOG_CONSTANT.code + "Error occurred while assigning Enum array value to the Annotation attribute " + propertyName + " .......", exc);
            }
        }
        return builder;
    }
}

