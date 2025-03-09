
package org.thingsboard.server.service.install;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.util.SqlTsDao;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

@Service
@Profile("install")
@Slf4j
@SqlTsDao
public class SqlTsDatabaseUpgradeService extends AbstractSqlTsDatabaseUpgradeService implements DatabaseTsUpgradeService {

    @Override
    public void upgradeDatabase(String fromVersion) throws Exception {
        switch (fromVersion) {
            default:
                throw new RuntimeException("Unable to upgrade SQL database, unsupported fromVersion: " + fromVersion);
        }
    }

    @Override
    protected void loadSql(Connection conn, String fileName, String version) {
        Path schemaUpdateFile = Paths.get(installScripts.getDataDir(), "upgrade", version, fileName);
        try {
            loadFunctions(schemaUpdateFile, conn);
            log.info("Functions successfully loaded!");
        } catch (Exception e) {
            log.info("Failed to load PostgreSQL upgrade functions due to: {}", e.getMessage());
        }
    }
}
