
package org.thingsboard.server.service.install;

public interface EntityDatabaseSchemaService extends DatabaseSchemaService {

    void createOrUpdateDeviceInfoView(boolean activityStateInTelemetry);

    void createOrUpdateViewsAndFunctions() throws Exception;

}
