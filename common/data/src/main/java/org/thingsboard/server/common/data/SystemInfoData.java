
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemInfoData {
    @ApiModelProperty(position = 1, value = "Service Id.")
    private String serviceId;
    @ApiModelProperty(position = 2, value = "Service type.")
    private String serviceType;
    @ApiModelProperty(position = 3, value = "CPU usage, in percent.")
    private Long cpuUsage;
    @ApiModelProperty(position = 4, value = "Total CPU usage.")
    private Long cpuCount;
    @ApiModelProperty(position = 5, value = "Memory usage, in percent.")
    private Long memoryUsage;
    @ApiModelProperty(position = 6, value = "Total memory in bytes.")
    private Long totalMemory;
    @ApiModelProperty(position = 7, value = "Disk usage, in percent.")
    private Long discUsage;
    @ApiModelProperty(position = 8, value = "Total disc space in bytes.")
    private Long totalDiscSpace;

}
