package com.techiethoughts.util;

import com.techiethoughts.domains.core.ModelDetail;
import com.techiethoughts.exception.CodeGenerationException;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.apache.commons.lang3.ClassUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Rajesh Bandarupalli
 *
 */
@Getter @Setter
public class PublisherContext {
	private final Map<String, DynamicType.Unloaded<?>> builders = new LinkedHashMap<>();
	private final ClassLoader CLASS_LOADER = this.getClass().getClassLoader();
	private final ModelDetail root;

	public PublisherContext(ModelDetail root) {
		this.root = root;
	}

	public void addBuilder(String className, DynamicType.Unloaded<?> builder) {
		builders.put(className, builder);
	}

	public Class<?> getClass(String className) {
		try {
			return ClassUtils.getClass(CLASS_LOADER, className, true);
		} catch (ClassNotFoundException e) {
			DynamicType.Unloaded<?> clazz = builders.get(className);
			if (clazz == null) {
				throw new CodeGenerationException("failed to load class " + className);
			}
			return clazz.load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION).getLoaded();
		}
	}

	public void loadAllClassesIntoClassloader() {
		builders.forEach((key, clazz) -> {
			try {
				Class.forName(key, true, CLASS_LOADER);
			} catch (ClassNotFoundException e) {
				clazz.load(CLASS_LOADER, ClassLoadingStrategy.Default.INJECTION).getLoaded();
			}
		});
	}
}

