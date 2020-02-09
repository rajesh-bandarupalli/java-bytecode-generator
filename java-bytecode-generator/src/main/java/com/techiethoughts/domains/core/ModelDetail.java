package com.techiethoughts.domains.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rajesh Bandarupalli
 */

@Getter
@Setter
@ToString(callSuper = true)
public class ModelDetail {

	private transient Map<Class<? extends Annotation>, Object> annotations = new HashMap<>();
	private List<AttributeDetail> attributes;
	private String description;
//	primary key
	private Long id;
//	Represents model type, specific functionality(structure of code generation) can be implemented based on this attribute.
	private String modelType;
//	name of the class
	@NotNull
	private String name;
//	package of the class
    private String packageName;

    // represents if the specified model can be reloaded with changes to attributes/methods
    private boolean reLoadable;
    //Case where model need to extend custom  class.
    private String superClass;
    private String type;


	public String getPackageName() {
		if (this.isReLoadable() && StringUtils.isNotBlank(this.packageName)) {
			return this.packageName +
					"." +
					this.getName() +
					".v" +
					this.getId();
		} else
			return this.packageName;
	}

    //	returns this model annotations whose metadata is not null
    public Map<Class<? extends Annotation>, Object> fetchAllAnnotations() {
		return this.annotations
				.entrySet()
				.stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
