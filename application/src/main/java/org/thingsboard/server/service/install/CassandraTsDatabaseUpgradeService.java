
package org.thingsboard.server.service.install;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.util.NoSqlTsDao;

@Service
@NoSqlTsDao
@Profile("install")
@Slf4j
public class CassandraTsDatabaseUpgradeService extends AbstractCassandraDatabaseUpgradeService implements DatabaseTsUpgradeService {

    @Override
    public void upgradeDatabase(String fromVersion) throws Exception {
        switch (fromVersion) {
            default:
                throw new RuntimeException("Unable to upgrade Cassandra database, unsupported fromVersion: " + fromVersion);
        }
    }

}
