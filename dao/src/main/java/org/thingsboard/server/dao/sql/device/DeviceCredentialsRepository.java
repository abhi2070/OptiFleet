
package org.thingsboard.server.dao.sql.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.model.sql.DeviceCredentialsEntity;

import java.util.UUID;


public interface DeviceCredentialsRepository extends JpaRepository<DeviceCredentialsEntity, UUID> {

    DeviceCredentialsEntity findByDeviceId(UUID deviceId);

    DeviceCredentialsEntity findByCredentialsId(String credentialsId);

    @Transactional
    @Query(value = "DELETE FROM device_credentials WHERE device_id = :deviceId RETURNING *", nativeQuery = true)
    DeviceCredentialsEntity deleteByDeviceId(@Param("deviceId") UUID deviceId);

}
