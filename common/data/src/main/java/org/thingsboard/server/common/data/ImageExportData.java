
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApiModel
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageExportData {

    private String mediaType;
    private String fileName;
    private String title;
    private String resourceKey;
    private boolean isPublic;
    private String publicResourceKey;
    private String data;

}
