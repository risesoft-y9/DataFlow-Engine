package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.DataArrangeEntity;

public interface DataArrangeRepository
    extends JpaRepository<DataArrangeEntity, String>, JpaSpecificationExecutor<DataArrangeEntity> {

    List<DataArrangeEntity> findByPattern(Integer pattern);

}
