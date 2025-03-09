
package org.thingsboard.server.dao.sql.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.settings.UserSettingsCompositeKey;
import org.thingsboard.server.dao.model.sql.UserSettingsEntity;

import java.util.List;

public interface UserSettingsRepository extends JpaRepository<UserSettingsEntity, UserSettingsCompositeKey> {

    @Query(value = "SELECT * FROM user_settings WHERE type = :type AND (settings #> :path) IS NOT NULL", nativeQuery = true)
    List<UserSettingsEntity> findByTypeAndPathExisting(@Param("type") String type, @Param("path") String[] path);



}
