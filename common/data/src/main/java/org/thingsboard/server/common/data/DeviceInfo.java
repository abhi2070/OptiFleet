
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.DeviceId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceInfo extends Device {

    private static final long serialVersionUID = -3004579925090663691L;

    @ApiModelProperty(position = 13, value = "Title of the Customer that owns the device.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String customerTitle;
    @ApiModelProperty(position = 14, value = "Indicates special 'Public' Customer that is auto-generated to use the devices on public dashboards.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private boolean customerIsPublic;
    @ApiModelProperty(position = 15, value = "Name of the corresponding Device Profile.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String deviceProfileName;
    @ApiModelProperty(position = 16, value = "Device active flag.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private boolean active;

    public DeviceInfo() {
        super();
    }

    public DeviceInfo(DeviceId deviceId) {
        super(deviceId);
    }

    public DeviceInfo(Device device, String customerTitle, boolean customerIsPublic, String deviceProfileName, boolean active) {
        super(device);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
        this.deviceProfileName = deviceProfileName;
        this.active = active;
    }
}
