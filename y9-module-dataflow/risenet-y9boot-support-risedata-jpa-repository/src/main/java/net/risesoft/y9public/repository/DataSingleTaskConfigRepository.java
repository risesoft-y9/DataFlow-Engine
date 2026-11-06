package net.risesoft.y9public.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.DataSingleTaskConfigEntity;

public interface DataSingleTaskConfigRepository
    extends JpaRepository<DataSingleTaskConfigEntity, String>, JpaSpecificationExecutor<DataSingleTaskConfigEntity> {

}
