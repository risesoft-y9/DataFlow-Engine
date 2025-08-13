package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataMappingEntity;

public class DataMappingSpecification implements Specification<DataMappingEntity>{

	private static final long serialVersionUID = 4564318035018488417L;
	
	private String typeName;
	private String className;
	
	public DataMappingSpecification() {
		super();
	}

	public DataMappingSpecification(String typeName, String className) {
		super();
		this.typeName = typeName;
		this.className = className;
	}

	@Override
	public Predicate toPredicate(Root<DataMappingEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if (StringUtils.isNotBlank(typeName)) {
			expressions.add(cb.equal(root.get("typeName").as(String.class), typeName));
		}
		if (StringUtils.isNotBlank(className)) {
			expressions.add(cb.like(root.get("className").as(String.class), "%" + className + "%"));
		}
		return predicate;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
