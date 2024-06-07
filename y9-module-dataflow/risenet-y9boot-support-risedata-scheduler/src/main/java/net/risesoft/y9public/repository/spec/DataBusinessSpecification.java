package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataBusinessEntity;

public class DataBusinessSpecification implements Specification<DataBusinessEntity>{

	private static final long serialVersionUID = -8033435846816389785L;
	
	private String parentId;
	private String name;
	
	public DataBusinessSpecification() {
		super();
	}

	public DataBusinessSpecification(String parentId, String name) {
		super();
		this.parentId = parentId;
		this.name = name;
	}

	@Override
	public Predicate toPredicate(Root<DataBusinessEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if (StringUtils.isNotBlank(parentId)) {
			expressions.add(cb.equal(root.get("parentId").as(String.class), parentId));
		}
		if (StringUtils.isNotBlank(name)) {
			expressions.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
		}
		return predicate;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
