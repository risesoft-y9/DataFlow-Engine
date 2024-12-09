package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataApiOnlineEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DataApiOnlineRepository extends JpaRepository<DataApiOnlineEntity, String>, JpaSpecificationExecutor<DataApiOnlineEntity> {
	
	List<DataApiOnlineEntity> findByParentIdOrderByCreateTime(String parentId);
	
	@Transactional
	@Modifying
	@Query("delete from DataApiOnlineEntity p where p.parentId = ?1")
	void deleteByParentId(String parentId);
	
	@Query("select p.id from DataApiOnlineEntity p where p.parentId = ?1")
	List<String> findByParentId(String parentId);
	
	List<DataApiOnlineEntity> findByNameLikeAndTypeOrderByCreateTime(String name, String type);
	
}
