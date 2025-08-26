package net.risesoft.y9public.repository;

import net.risesoft.y9public.entity.DataCacheEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataCacheRepository extends JpaRepository<DataCacheEntity, String>, JpaSpecificationExecutor<DataCacheEntity> {

	Page<DataCacheEntity> findByCacheKey(String cacheKey, Pageable pageable);

}
