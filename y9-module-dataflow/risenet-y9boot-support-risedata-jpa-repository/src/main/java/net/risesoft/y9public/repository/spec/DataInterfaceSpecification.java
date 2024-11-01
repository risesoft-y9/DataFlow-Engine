package net.risesoft.y9public.repository.spec;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import net.risesoft.y9public.entity.DataInterfaceEntity;

public class DataInterfaceSpecification implements Specification<DataInterfaceEntity> {

	private static final long serialVersionUID = 4032299954629222163L;
	
	private String search;
	private String requestType;
	private Integer dataType;
	
	public DataInterfaceSpecification() {
		super();
	}

	public DataInterfaceSpecification(String search, String requestType, Integer dataType) {
		super();
		this.search = search;
		this.requestType = requestType;
		this.dataType = dataType;
	}

	@Override
	public Predicate toPredicate(Root<DataInterfaceEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = cb.conjunction();
		List<Expression<Boolean>> expressions = predicate.getExpressions();

		if(StringUtils.isNotBlank(requestType)) {
			expressions.add(cb.equal(root.get("requestType").as(String.class), requestType));
		}
		if(StringUtils.isNotBlank(search)) {
			expressions.add(cb.or(cb.like(root.get("interfaceName").as(String.class), "%" + search + "%"), 
					cb.like(root.get("interfaceUrl").as(String.class), "%" + search + "%")));
		}
		if(dataType != null) {
			expressions.add(cb.equal(root.get("dataType").as(Integer.class), dataType));
		}
		return predicate;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

}
