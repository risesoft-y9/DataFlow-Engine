package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.y9public.entity.DataTable;

public interface DataTableRepository extends JpaRepository<DataTable, String>, JpaSpecificationExecutor<DataTable> {
	
	Page<DataTable> findByBaseId(String baseId, Pageable pageable);
	
	Page<DataTable> findByBaseIdAndIdNotIn(String baseId, List<String> tableId, Pageable page);

	DataTable findByBaseIdAndName(String baseId, String name);

	@Query("select p.id from DataTable p where p.baseId = ?1 and p.name = ?2")
	String findIdByBaseIdAndName(String baseId, String name);

	List<DataTable> findByBaseIdOrderByNameAsc(String baseId);
	
	@Query("select p.name from DataTable p where p.baseId = ?1 order by p.name asc")
	List<String> findByBaseId(String baseId);
	
	@Query("select p.name from DataTable p where p.baseId = ?1 and p.name like ?2 order by p.name asc")
	List<String> findByBaseIdAndNameLike(String baseId, String name);

	List<DataTable> findByBaseIdOrderByName(String baseId);
	
	List<DataTable> findByIdNotInOrderByName(List<String> id);
	
	List<DataTable> findByBaseIdAndIdNotInOrderByName(String baseId,List<String> Id);
	
	Page<DataTable> findByIdNotIn(List<String> Id, Pageable page);
	
	Page<DataTable> findByNameContainingAndIdNotIn(String tableName, List<String> Id, Pageable page);
	
	Page<DataTable> findByNameContaining(String tableName, Pageable pageable);
	
	Page<DataTable> findByBaseIdAndNameContaining(String baseId, String tableName, Pageable pageable);
	
	Page<DataTable> findByBaseIdAndNameContainingAndIdNotIn(String baseId, String tableName, List<String> tableId, Pageable page);

}
