package com.techiethoughts.util;

/**
 * @author Rajesh Bandarupalli
 *
 */

public enum AppConstants {

    DEFAULT_BASE_PACKAGE("com.techiethoughts"),
	LOG_CONSTANT("CodeGenerator::"),
    GENERIC("generic"),
    VALIDATION_CONSTRAINTS_PACKAGE("javax.validation.constraints");

    public final String code;

    AppConstants(String code) {
        this.code = code;
    }

}
