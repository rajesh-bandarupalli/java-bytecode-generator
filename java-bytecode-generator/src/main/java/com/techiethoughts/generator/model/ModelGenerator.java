package com.techiethoughts.generator.model;

import com.techiethoughts.domains.core.AttributeDetail;
import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.generator.annotation.AnnotationGenerator;
import com.techiethoughts.generator.attribute.AttributeGenerator;
import com.techiethoughts.util.AppConstants;
import com.techiethoughts.util.PublisherContext;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.techiethoughts.util.ByteCodeGenUtil.getFullyQualifiedClassName;

/**
 * Generates a java class based on given metadata from ModelDetail instance.
 *
 * @author Rajesh Bandarupalli
 */

@Getter
@Setter
public class ModelGenerator {

    private final Logger logger = Logger.getLogger(ModelGenerator.class.getName());

    @Autowired
    private AttributeGenerator attributeGenerator;

    public DynamicType.Unloaded<?> createType(ModelDetail model, PublisherContext context) {
        logger.log(Level.FINE, AppConstants.LOG_CONSTANT.code + " Class generation for the model " + model.getName() + " Started...");

        ByteBuddy byteBuddy = new ByteBuddy();
        Builder<?> builder;

        if (StringUtils.isNotBlank(model.getSuperClass()))
            builder = byteBuddy.subclass(context.getClass(model.getSuperClass()));
        else
            builder = byteBuddy.subclass(Object.class);

        builder = createAttributes(model, context, builder);

        String fullyQualifiedClassName = getFullyQualifiedClassName(model);

        DynamicType.Unloaded<?> outerClazz = builder.name(fullyQualifiedClassName)
                .annotateType(AnnotationGenerator.getAllModelAnnotations(model, context))
                .make();

        context.addBuilder(fullyQualifiedClassName, outerClazz);
        logger.log(Level.FINE, AppConstants.LOG_CONSTANT.code + " Class generation for the model " + model.getName() + " Completed...");

        return outerClazz;
    }

    private Builder<?> createAttributes(ModelDetail model, PublisherContext context, Builder<?> builder) {

        List<AttributeDetail> attributes = Optional.ofNullable(model.getAttributes()).orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(AttributeDetail::getAttributeOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(attributes)) {
            for (AttributeDetail attribute : attributes) {
                builder = attributeGenerator.createType(attribute, builder, context);
            }
        }
        return builder;
    }
}
