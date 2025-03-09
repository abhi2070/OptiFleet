package org.thingsboard.server.service.entity.driver;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbDriverService extends SimpleTbEntityService<Driver> {

    boolean isVerifiedDriver(Driver driver, User user) throws Exception;

    Driver uploadPhotoDocument(byte[] fileData, String contentType, Driver driver, User user) throws Exception;

}
