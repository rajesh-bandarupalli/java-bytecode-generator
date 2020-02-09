package com.techiethoughts.domains.core;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Rajesh Bandarupalli
 */

@Getter @Setter
public class ValidationConstraintsDetail {

    private Long value;
    private Integer min;
    private Integer max;
    private String message;
    private String regexp;

    private String paramState;
    private String paramName;

    @NotNull
    private ConstraintType constraintType;

    /*Properties of @NotNull Annotation */
    private Class<?>[] groups;
    private Class<? extends Payload>[] payload;
    private List<Integer> groupsList;

    public enum ConstraintType {
        Max,
        Min,
        Pattern,
        Size,
        NotNull
    }
}
