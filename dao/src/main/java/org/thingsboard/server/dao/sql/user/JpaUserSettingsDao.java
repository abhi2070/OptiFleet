
package org.thingsboard.server.dao.sql.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsCompositeKey;
import org.thingsboard.server.common.data.settings.UserSettingsType;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserSettingsEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDaoListeningExecutorService;
import org.thingsboard.server.dao.user.UserSettingsDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;

@Slf4j
@Component
@SqlDao
public class JpaUserSettingsDao extends JpaAbstractDaoListeningExecutorService implements UserSettingsDao {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Override
    public UserSettings save(TenantId tenantId, UserSettings userSettings) {
        return DaoUtil.getData(userSettingsRepository.save(new UserSettingsEntity(userSettings)));
    }

    @Override
    public UserSettings findById(TenantId tenantId, UserSettingsCompositeKey id) {
        return DaoUtil.getData(userSettingsRepository.findById(id));
    }

    @Override
    public void removeById(TenantId tenantId, UserSettingsCompositeKey id) {
        userSettingsRepository.deleteById(id);
    }

    @Override
    public List<UserSettings> findByTypeAndPath(TenantId tenantId, UserSettingsType type, String... path) {
        return DaoUtil.convertDataList(userSettingsRepository.findByTypeAndPathExisting(type.name(), path));
    }

}
