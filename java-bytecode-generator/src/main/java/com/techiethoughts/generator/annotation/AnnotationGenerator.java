package com.techiethoughts.generator.annotation;

import com.techiethoughts.domains.core.AttributeDetail;
import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.util.AnnotationUtils;
import com.techiethoughts.util.AppConstants;
import com.techiethoughts.util.PublisherContext;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * @author Rajesh Bandarupalli
 */

public class AnnotationGenerator {

    private static final String VALUE = "value";

    public static <T> AnnotationDescription.Builder createRepeatableAnnotation(List<T> modelConfigsMetadata, Class<? extends Annotation> repeatableType, Class<? extends Annotation> configType) {
        return AnnotationDescription.Builder
                .ofType(repeatableType)
                .defineAnnotationArray(VALUE, new TypeDescription.ForLoadedType(configType),
                        buildAnnotationDescriptionArray(modelConfigsMetadata, configType));
    }

    public static AnnotationDescription.Builder createAnnotation(Object metaData, Class<? extends Annotation> annotation) {
		final AnnotationDescription.Builder[] configBuilder = new AnnotationDescription.Builder[1];
		configBuilder[0] = AnnotationDescription.Builder.ofType(annotation);

		Stream.of(annotation.getDeclaredMethods()).map(Method::getName).forEach(attribute -> {
			try {
				Object value = PropertyUtils.getProperty(metaData, attribute);
				if (value != null)
					configBuilder[0] = AnnotationUtils.assignAnnotationValue(metaData, configBuilder[0], attribute);
			} catch (Exception exc) {
				throw new RuntimeException(AppConstants.LOG_CONSTANT.code + "Error occured  while generating annotation " + annotation.getName() + "..." + exc);
			}
		});
		return configBuilder[0];
	}


    private static AnnotationDescription[] buildAnnotationDescriptionArray(List<?> configDetails, Class<? extends Annotation> configType) {
        return configDetails
                .stream()
                .map(config -> AnnotationGenerator.createAnnotation(config, configType))
                .map(AnnotationDescription.Builder::build)
                .toArray(AnnotationDescription[]::new);
    }

    public static List<AnnotationDescription> getAllAttributeAnnotations(AttributeDetail attribute, PublisherContext context) {
        List<AnnotationDescription> allAnnotations = new ArrayList<>();
        attribute.fetchAllAnnotations()
                .forEach((annotation, metaData) -> allAnnotations.add(getAnnotation(annotation, metaData)));

        return allAnnotations;
    }


    /**
     * builds and returns annotation with given metadata, for both repeatable and non-repeatable annotations
     */
    private static AnnotationDescription getAnnotation(Class<? extends Annotation> annotation, Object metaData) {
		Repeatable repeatableAnnotation = annotation.getDeclaredAnnotation(Repeatable.class);
		if (repeatableAnnotation != null) {
			Class<? extends Annotation> repeatableType = repeatableAnnotation.value();
			if (!StringUtils.equals(AppConstants.VALIDATION_CONSTRAINTS_PACKAGE.code, repeatableType.getPackage().getName())) {
				return AnnotationGenerator.createRepeatableAnnotation((List<?>) metaData, repeatableType, annotation)
						.build();
			} else {
				return AnnotationGenerator.createAnnotation(metaData, annotation).build();
			}
		} else {
			return AnnotationGenerator.createAnnotation(metaData, annotation).build();
		}
	}


    public static AnnotationDescription[] getAllModelAnnotations(ModelDetail model, PublisherContext context) {

        if (model == null)
            return new AnnotationDescription[]{};
        List<AnnotationDescription> allAnnotations = new ArrayList<>();
        model.fetchAllAnnotations()
                .forEach((annotation, metaData) -> allAnnotations.add(getAnnotation(annotation, metaData)));
        return allAnnotations.toArray(new AnnotationDescription[0]);
    }
}