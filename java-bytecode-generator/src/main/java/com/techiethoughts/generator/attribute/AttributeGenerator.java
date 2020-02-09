package com.techiethoughts.generator.attribute;

import com.techiethoughts.domains.core.AttributeDetail;
import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.exception.CodeGenerationException;
import com.techiethoughts.generator.annotation.AnnotationGenerator;
import com.techiethoughts.generator.model.ModelGenerator;
import com.techiethoughts.util.AppConstants;
import com.techiethoughts.util.PublisherContext;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.techiethoughts.util.ByteCodeGenUtil.getFullyQualifiedClassName;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.SEVERE;

/**
 * {@code} AttributeGenerator creates the fields/attributes of a class from the metadata of {@code} AttributeDetail instance,
 * in it's corresponding class({@code}ModelDetail instance holds class metadata).
 *
 * @author Rajesh Bandarupalli
 */

public class AttributeGenerator {

    private Logger logger = Logger.getLogger(AttributeGenerator.class.getName());

    @Autowired
    private ModelGenerator modelGenerator;

    public Builder<?> createType(AttributeDetail attribute, Builder<?> builder, PublisherContext context) {
        logger.log(CONFIG, AppConstants.LOG_CONSTANT.code + " Creating Attribute " + attribute.getName() + " of type " + attribute.getType() + " started....");

        if (StringUtils.isNotBlank(attribute.getType().code)) {
            switch (attribute.getType().code) {
                case "generic":
                    builder = buildGenericType(attribute, builder, context);
                    break;
                case "model":
                    builder = buildModel(attribute, builder, context);
                    break;
                case "nested-model":
                    builder = buildNestedModel(attribute, builder, context);
                    break;
                default:
                    builder = buildStandardType(attribute, builder, context);
                    break;
            }
        } else {
            logger.log(SEVERE, AppConstants.LOG_CONSTANT.code + " type is missing for attribute " + attribute.getName());
            throw new CodeGenerationException("type is missing for attribute " + attribute.getName());
        }
        logger.log(CONFIG, AppConstants.LOG_CONSTANT.code + " Creating Attribute " + attribute.getName() + " of type " + attribute.getType() + " Completed.... ==>");
        return builder;
    }

    private Builder<?> buildStandardType(AttributeDetail attribute, Builder<?> builder, PublisherContext context) {
        builder = builder.defineProperty(attribute.getName(), context.getClass(attribute.getType().code))
                .annotateField(AnnotationGenerator.getAllAttributeAnnotations(attribute, context));
        return builder;
    }

    private Builder<?> buildNestedModel(AttributeDetail attribute, Builder<?> builder, PublisherContext context) {

        if (context.getRoot() != null && attribute.getId() != null) {
            String modelName = StringUtils.capitalize(attribute.getName());
            String packageName = context.getRoot().getPackageName();
            List<AttributeDetail> attributes = Optional.ofNullable(context.getRoot().getAttributes()).orElseGet(Collections::emptyList)
                    .stream()
                    .filter(a -> attribute.getId().equals(a.getParentId()))
                    .sorted(Comparator.comparing(AttributeDetail::getAttributeOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            ModelDetail model = new ModelDetail();
            model.setName(modelName);
            model.setPackageName(packageName);
            model.setAttributes(attributes);

            modelGenerator.createType(model, context);
            builder = builder
                    .defineProperty(attribute.getName(), context.getClass(getFullyQualifiedClassName(model)))
                    .annotateField(AnnotationGenerator.getAllAttributeAnnotations(attribute, context));
        }
        return builder;
    }

    private Builder<?> buildModel(AttributeDetail attribute, Builder<?> builder, PublisherContext context) {
        // Checks if the model is not yet loaded, get it from db. loaded model should be available in context
        ModelDetail model = attribute.getModelDetail();
        if (model != null) {
            try {
                context.getClass(getFullyQualifiedClassName(model));
            } catch (CodeGenerationException e) {
                logger.log(CONFIG, AppConstants.LOG_CONSTANT.code + " Checking whether the model of type " + attribute.getType() + "is loaded into the context.... ");
                if (!context.getBuilders().containsKey(getFullyQualifiedClassName(model))) {
                    logger.log(CONFIG, AppConstants.LOG_CONSTANT.code + " Model not available in the Context so getting details of the model from Database.... ");
//						get it from service or DB where the metaData is present
                    modelGenerator.createType(model, context);
                }
            }
            builder = builder.defineProperty(attribute.getName(), context.getClass(getFullyQualifiedClassName(model))).annotateField(AnnotationGenerator.getAllAttributeAnnotations(attribute, context));
        }
        return builder;
    }

    private Builder<?> buildGenericType(AttributeDetail attribute, Builder<?> builder, PublisherContext context) {
        if (ObjectUtils.allNotNull(attribute.getGenericInfo(), attribute.getGenericInfo().getRawType())
                && CollectionUtils.isNotEmpty(attribute.getGenericInfo().getParameters())) {
            List<Class<?>> parameters = attribute.getGenericInfo()
                    .getParameters()
                    .stream()
                    .map(context::getClass)
                    .collect(Collectors.toList());
            Generic generic = TypeDescription.Generic.Builder
                    .parameterizedType(context.getClass(attribute.getGenericInfo().getRawType()), parameters).build();
            builder = builder.defineProperty(attribute.getName(), generic)
                    .annotateField(AnnotationGenerator.getAllAttributeAnnotations(attribute, context));
        }
        return builder;
    }
}
