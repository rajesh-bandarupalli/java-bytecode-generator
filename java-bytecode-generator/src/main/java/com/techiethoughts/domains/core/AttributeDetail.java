package com.techiethoughts.domains.core;


import com.techiethoughts.util.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.techiethoughts.domains.core.ValidationConstraintsDetail.ConstraintType;

/**
 * @author Rajesh Bandarupalli
 */

@Getter
@Setter
public class AttributeDetail {

    private transient Map<Class<? extends Annotation>, Object> annotations = new HashMap<>();

    /**
     * order of this attribute
     */
    private Long attributeOrder;

    /**
     * metadata for annotations under javax.validation.constraints package.
     */
    private List<ValidationConstraintsDetail> constraintsDetail;

    private String description;

    /**
     * holds the generic info of this attribute.
     */
    private GenericInfo genericInfo;

    private Long index;

    /**
     * unique id
     */
    @NotNull
    private Long id;

    /**
     *
     */
    private ModelDetail modelDetail;

    /**
     * name of the attribute
     */
    @NotNull
    private String name;

    /**
     * if parentId is NotNull then, this field can be used to identify the class to which this attribute belongs.
     */
    private Long parentId;

    /**
     * Type of this attribute.
     */
    @NotNull
    private Type type;

    @SneakyThrows
    private void addConstraints(ConstraintType constraintType, List<ValidationConstraintsDetail> constraintMetaData) {
        this.annotations.put((Class<? extends Annotation>) Class.forName(AppConstants.VALIDATION_CONSTRAINTS_PACKAGE.code + "." + constraintType.name()), constraintMetaData.get(0));
    }

    //	returns this attribute annotations whose metadata is not null
    public Map<Class<? extends Annotation>, Object> fetchAllAnnotations() {

        Map<ConstraintType, List<ValidationConstraintsDetail>> constraints = Optional.ofNullable(this.constraintsDetail)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.groupingBy(ValidationConstraintsDetail::getConstraintType));
        constraints.forEach(this::addConstraints);

        return this.annotations
                .entrySet()
                .stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public enum Type {

        MODEL("model"),
        NESTED_MODEL("nested-model"),
        STRING("java.lang.String"),
        LONG("java.lang.Long"),
        INTEGER("java.lang.Integer"),
        PRIMITIVE_BOOLEAN("boolean"),
        DOUBLE("java.lang.Double"),
        FLOAT("java.lang.Float"),
        BOOLEAN("java.lang.Boolean"),
        LOCALDATE("java.time.LocalDate"),
        ZONEDDATETIME("java.time.ZonedDateTime"),
        DATE("java.util.Date"),
        GENERIC("generic");

        public final String code;

        Type(String code) {
            this.code = code;
        }

    }

    /**
     * Represents a generic info of an attribute/field.
     * <p> eg: to define <strong> List<String> names; </strong> as an attribute, GenericInfo instance should be
     * <code>
     * <p>	GenericInfo info = new GenericInfo();</p>
     * info.setRawType("java.util.List");
     * info.setParameters(Arrays.asList("java.lang.String"));
     * </code>
     *
     * @author Rajesh Bandarupalli
     */
    @Getter
    @Setter
    public static class GenericInfo {

        /**
         * <p> {@code} fully qualified class name, represents the raw type of a parameterized class.</p>
         */
        private String rawType;
        /**
         * <p> {@code} represents the parameters that can be passed to a raw type.</p>
         * <p> order of parameters is important to define Map.</p>
         * for eg: to create Map<String, Double> scores;
         * parameters = Arrays.asList("java.lang.string", "java.lang.Double");
         * where key is of type String and value is of type Double.
         */
        private List<String> parameters;
    }

}