package org.thingsboard.server.service.entity.vehicle;

import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbVehicleService extends SimpleTbEntityService<Vehicle> {

    Vehicle updateDocument(Vehicle vehicle, User user, MultipartFile registrationCertificate, MultipartFile insuranceCertificate,
                       MultipartFile pucCertificate, MultipartFile requiredPermits) throws Exception;

    boolean isVerifiedVehicle(Vehicle vehicle, User user) throws Exception;
}
