package com.techiethoughts.util;

import com.techiethoughts.domains.core.ModelDetail;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rajesh Bandarupalli
 */

public class ByteCodeGenUtil {

    private static final Logger logger = Logger.getLogger(ByteCodeGenUtil.class.getName());

    private ByteCodeGenUtil() {
        throw new IllegalStateException("AgentUtil is an Utility class");
    }

    public static String getFullyQualifiedClassName(ModelDetail model) {
        StringBuilder className = new StringBuilder();
        if (StringUtils.isNotBlank(model.getPackageName()))
            className.append(model.getPackageName())
                    .append(".")
                    .append(model.getName());
        return className.toString();
    }

    public static String getHostName() {
        String hName = "Not Available";
        try {
            hName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.log(Level.WARNING, AppConstants.LOG_CONSTANT.code + " Host name could not be determined ", ex);
        }
        return hName;
    }
}

