package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataTable;

public class DataTableSpecification implements Specification<DataTable> {

	private static final long serialVersionUID = -607881482694873340L;
	
	private String baseId;
	private String name;
	
	public DataTableSpecification() {
		super();
	}

	public DataTableSpecification(String baseId, String name) {
		super();
		this.baseId = baseId;
		this.name = name;
	}

	@Override
	public Predicate toPredicate(Root<DataTable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if (StringUtils.isNotBlank(baseId)) {
			expressions.add(cb.equal(root.get("baseId").as(String.class), baseId));
		}
		if (StringUtils.isNotBlank(name)) {
			expressions.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
		}
		return predicate;
	}

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
