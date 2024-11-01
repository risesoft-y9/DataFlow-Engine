package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.DataTableField;

public interface DataTableFieldRepository extends JpaRepository<DataTableField, String>, JpaSpecificationExecutor<DataTableField> {

	Page<DataTableField> findByTableId(String tableId, Pageable pageable);
	
	DataTableField findByTableIdAndName(String tableId,String name);
	
	List<DataTableField> findByTableIdAndNameContaining(String tableId,String name);
	
	List<DataTableField> findByTableIdOrderByDisplayOrderAsc(String tableId);

	@Transactional
	@Modifying
	@Query("delete from DataTableField dtf where dtf.tableId =?1 ")
	void deleteByTableId(String tableId);

	@Query("from DataTableField dtf where dtf.tableId =?1 order by dtf.displayOrder asc")
	List<DataTableField> findTableListByTableId(String tableId);

	@Query("select name from DataTableField dtf where dtf.tableId = ?1 and dtf.fieldPk = 'Y'")
	String findFieldPK(String tableId);
	
	@Query("select max(displayOrder) from DataTableField p where p.tableId=?1")
	Integer getMaxTabIndex(String tableId);
}
