package com.techiethoughts.publisher;

import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.exception.CodeGenerationException;
import com.techiethoughts.generator.model.ModelGenerator;
import com.techiethoughts.util.AppConstants;
import com.techiethoughts.util.PublisherContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

/**
 * Creates classes using Metadata from {@code} ModelDetail instance.
 * <p> all the classes created for a given model ({@code}ModelDetail instance) will be added to the {@code} PublisherContext. </p>
 *
 * @author Rajesh Bandarupalli
 */

public class ModelPublisher {

    private final Logger logger = Logger.getLogger(ModelPublisher.class.getName());
    @Autowired
    private ModelGenerator modelGenerator;


    /**
     * <p> Reads the metadata from {@code} ModelDetail instance, and creates bytecode.
     *
     * @param model   model for which classes should be created.
     *
     */
    public void publishModel(ModelDetail model) {

        try {
            PublisherContext context = new PublisherContext(model);
            modelGenerator.createType(model, context);
            context.loadAllClassesIntoClassloader();
        } catch (Exception exc) {
            throw new CodeGenerationException(AppConstants.LOG_CONSTANT.code + "Error occurred while generating the model " + model.getName() + " .....", exc);
        }
    }


}