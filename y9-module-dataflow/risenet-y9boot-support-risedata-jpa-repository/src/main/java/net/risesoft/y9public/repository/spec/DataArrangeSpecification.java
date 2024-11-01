package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataArrangeEntity;

public class DataArrangeSpecification implements Specification<DataArrangeEntity> {

	private static final long serialVersionUID = 9170391321871431040L;
	
	private String name;
	private Integer pattern;
	
	public DataArrangeSpecification() {
		super();
	}

	public DataArrangeSpecification(String name, Integer pattern) {
		super();
		this.name = name;
		this.pattern = pattern;
	}

	@Override
	public Predicate toPredicate(Root<DataArrangeEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if(StringUtils.isNotBlank(name)) {
			expressions.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
		}
		if(pattern != null) {
			expressions.add(cb.equal(root.get("pattern").as(Integer.class), pattern));
		}
		return predicate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

}
