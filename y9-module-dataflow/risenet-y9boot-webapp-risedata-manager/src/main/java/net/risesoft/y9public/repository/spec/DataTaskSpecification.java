package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataTaskEntity;

public class DataTaskSpecification implements Specification<DataTaskEntity>{

	private static final long serialVersionUID = 7150744199489729818L;
	
	private List<String> ids;
	private List<String> businessIds;
	private String name;
	
	public DataTaskSpecification() {
		super();
	}

	public DataTaskSpecification(List<String> ids, List<String> businessIds, String name) {
		super();
		this.ids = ids;
		this.businessIds = businessIds;
		this.name = name;
	}

	@Override
	public Predicate toPredicate(Root<DataTaskEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if (ids != null && ids.size() > 0) {
            expressions.add(root.get("id").in(ids));
        }
		if (businessIds != null && businessIds.size() > 0) {
			expressions.add(root.get("businessId").in(businessIds));
		}
		if (StringUtils.isNotBlank(name)) {
			expressions.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
		}
		return predicate;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<String> getBusinessIds() {
		return businessIds;
	}

	public void setBusinessIds(List<String> businessIds) {
		this.businessIds = businessIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
