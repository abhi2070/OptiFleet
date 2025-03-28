
package org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class LwM2MServerSecurityConfig {

    @ApiModelProperty(position = 1, value = "Server short Id. Used as link to associate server Object Instance. This identifier uniquely identifies each LwM2M Server configured for the LwM2M Client. " +
            "This Resource MUST be set when the Bootstrap-Server Resource has a value of 'false'. " +
            "The values ID:0 and ID:65535 values MUST NOT be used for identifying the LwM2M Server.", example = "123", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected Integer shortServerId = 123;
    /** Security -> ObjectId = 0 'LWM2M Security' */
    @ApiModelProperty(position = 2, value = "Is Bootstrap Server or Lwm2m Server. " +
            "The LwM2M Client MAY be configured to use one or more LwM2M Server Account(s). " +
            "The LwM2M Client MUST have at most one LwM2M Bootstrap-Server Account. " +
            "(*) The LwM2M client MUST have at least one LwM2M server account after completing the boot sequence specified.", example = "true or false", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected boolean bootstrapServerIs = false;
    @ApiModelProperty(position = 3, value = "Host for 'No Security' mode", example = "0.0.0.0", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected String host;
    @ApiModelProperty(position = 4, value = "Port for  Lwm2m Server: 'No Security' mode: Lwm2m Server or Bootstrap Server", example = "'5685' or '5687'", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected Integer port;
    @ApiModelProperty(position = 7, value = "Client Hold Off Time. The number of seconds to wait before initiating a Client Initiated Bootstrap once the LwM2M Client has determined it should initiate this bootstrap mode. (This information is relevant for use with a Bootstrap-Server only.)", example = "1", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected Integer clientHoldOffTime = 1;
    @ApiModelProperty(position = 8, value = "Server Public Key for 'Security' mode (DTLS): RPK or X509. Format: base64 encoded", example = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEAZ0pSaGKHk/GrDaUDnQZpeEdGwX7m3Ws+U/kiVat\n" +
            "+44sgk3c8g0LotfMpLlZJPhPwJ6ipXV+O1r7IZUjBs3LNA==", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected String serverPublicKey;
    @ApiModelProperty(position = 9, value = "Server Public Key for 'Security' mode (DTLS): X509. Format: base64 encoded", example = "MMIICODCCAd6gAwIBAgIUI88U1zowOdrxDK/dOV+36gJxI2MwCgYIKoZIzj0EAwIwejELMAkGA1UEBhMCVUs\n" +
            "xEjAQBgNVBAgTCUt5aXYgY2l0eTENMAsGA1UEBxMES3lpdjEUMBIGA1UEChMLVGhpbmdzYm9hcmQxFzAVBgNVBAsMDkRFVkVMT1BFUl9URVNUMRkwFwYDVQQDDBBpbnRlcm1lZGlhdGVfY2EwMB4XDTIyMDEwOTEzMDMwMFoXDTI3MDEwODEzMDMwMFowFDESMBAGA1UEAxM\n" +
            "JbG9jYWxob3N0MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEUO3vBo/JTv0eooY7XHiKAIVDoWKFqtrU7C6q8AIKqpLcqhCdW+haFeBOH3PjY6EwaWkY04Bir4oanU0s7tz2uKOBpzCBpDAOBgNVHQ8BAf8EBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDAYDVR0TAQH/\n" +
            "BAIwADAdBgNVHQ4EFgQUEjc3Q4a0TxzP/3x3EV4fHxYUg0YwHwYDVR0jBBgwFoAUuSquGycMU6Q0SYNcbtSkSD3TfH0wLwYDVR0RBCgwJoIVbG9jYWxob3N0LmxvY2FsZG9tYWlugglsb2NhbGhvc3SCAiAtMAoGCCqGSM49BAMCA0gAMEUCIQD7dbZObyUaoDiNbX+9fUNp\n" +
            "AWrD7N7XuJUwZ9FcN75R3gIgb2RNjDkHoyUyF1YajwkBk+7XmIXNClmizNJigj908mw=", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    protected String serverCertificate;
    @ApiModelProperty(position = 10, value = "Bootstrap Server Account Timeout (If the value is set to 0, or if this resource is not instantiated, the Bootstrap-Server Account lifetime is infinite.)", example = "0", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    Integer bootstrapServerAccountTimeout = 0;

    /** Config -> ObjectId = 1 'LwM2M Server' */
    @ApiModelProperty(position = 11, value = "Specify the lifetime of the registration in seconds.", example = "300", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer lifetime = 300;
    @ApiModelProperty(position = 12, value = "The default value the LwM2M Client should use for the Minimum Period of an Observation in the absence of this parameter being included in an Observation. " +
            "If this Resource doesn’t exist, the default value is 0.", example = "1", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer defaultMinPeriod = 1;
    /** ResourceID=6 'Notification Storing When Disabled or Offline' */
    @ApiModelProperty(position = 13, value = "If true, the LwM2M Client stores “Notify” operations to the LwM2M Server while the LwM2M Server account is disabled or the LwM2M Client is offline. After the LwM2M Server account is enabled or the LwM2M Client is online, the LwM2M Client reports the stored “Notify” operations to the Server. " +
            "If false, the LwM2M Client discards all the “Notify” operations or temporarily disables the Observe function while the LwM2M Server is disabled or the LwM2M Client is offline. " +
            "The default value is true.", example = "true", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private boolean notifIfDisabled = true;
    @ApiModelProperty(position = 14, value = "This Resource defines the transport binding configured for the LwM2M Client. " +
            "If the LwM2M Client supports the binding specified in this Resource, the LwM2M Client MUST use that transport for the Current Binding Mode.", example = "U", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String binding = "U";
}
