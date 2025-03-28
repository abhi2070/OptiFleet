
package org.thingsboard.server.dao.sql.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.device.DeviceCredentialsDao;
import org.thingsboard.server.dao.model.sql.DeviceCredentialsEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;


@Slf4j
@Component
@SqlDao
public class JpaDeviceCredentialsDao extends JpaAbstractDao<DeviceCredentialsEntity, DeviceCredentials> implements DeviceCredentialsDao {

    @Autowired
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Override
    protected Class<DeviceCredentialsEntity> getEntityClass() {
        return DeviceCredentialsEntity.class;
    }

    @Override
    protected JpaRepository<DeviceCredentialsEntity, UUID> getRepository() {
        return deviceCredentialsRepository;
    }

    @Transactional
    @Override
    public DeviceCredentials saveAndFlush(TenantId tenantId, DeviceCredentials deviceCredentials) {
        DeviceCredentials result = save(tenantId, deviceCredentials);
        deviceCredentialsRepository.flush();
        return result;
    }

    @Override
    public DeviceCredentials findByDeviceId(TenantId tenantId, UUID deviceId) {
        return DaoUtil.getData(deviceCredentialsRepository.findByDeviceId(deviceId));
    }

    @Override
    public DeviceCredentials findByCredentialsId(TenantId tenantId, String credentialsId) {
        log.trace("[{}] findByCredentialsId [{}]", tenantId, credentialsId);
        return DaoUtil.getData(deviceCredentialsRepository.findByCredentialsId(credentialsId));
    }

    @Override
    public DeviceCredentials removeByDeviceId(TenantId tenantId, DeviceId deviceId) {
        return DaoUtil.getData(deviceCredentialsRepository.deleteByDeviceId(deviceId.getId()));
    }

}
