package org.thingsboard.server.common.data.vehicle;

import lombok.Data;

@Data
public class VehicleDocuments {

    private byte[] registrationCertificate;
    private byte[] insuranceCertificate;
    private byte[] pucCertificate;
    private byte[] requiredPermits;
}
