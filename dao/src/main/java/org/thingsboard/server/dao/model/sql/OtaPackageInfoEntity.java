
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_CHECKSUM_ALGORITHM_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_CHECKSUM_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_CONTENT_TYPE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_DATA_SIZE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_DEVICE_PROFILE_ID_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_FILE_NAME_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_TABLE_NAME;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_TAG_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_TENANT_ID_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_TILE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_TYPE_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_URL_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.OTA_PACKAGE_VERSION_COLUMN;
import static org.thingsboard.server.dao.model.ModelConstants.SEARCH_TEXT_PROPERTY;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = OTA_PACKAGE_TABLE_NAME)
public class OtaPackageInfoEntity extends BaseSqlEntity<OtaPackageInfo> {

    @Column(name = OTA_PACKAGE_TENANT_ID_COLUMN)
    private UUID tenantId;

    @Column(name = OTA_PACKAGE_DEVICE_PROFILE_ID_COLUMN)
    private UUID deviceProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = OTA_PACKAGE_TYPE_COLUMN)
    private OtaPackageType type;

    @Column(name = OTA_PACKAGE_TILE_COLUMN)
    private String title;

    @Column(name = OTA_PACKAGE_VERSION_COLUMN)
    private String version;

    @Column(name = OTA_PACKAGE_TAG_COLUMN)
    private String tag;

    @Column(name = OTA_PACKAGE_URL_COLUMN)
    private String url;

    @Column(name = OTA_PACKAGE_FILE_NAME_COLUMN)
    private String fileName;

    @Column(name = OTA_PACKAGE_CONTENT_TYPE_COLUMN)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = OTA_PACKAGE_CHECKSUM_ALGORITHM_COLUMN)
    private ChecksumAlgorithm checksumAlgorithm;

    @Column(name = OTA_PACKAGE_CHECKSUM_COLUMN)
    private String checksum;

    @Column(name = OTA_PACKAGE_DATA_SIZE_COLUMN)
    private Long dataSize;

    @Type(type = "json")
    @Column(name = ModelConstants.OTA_PACKAGE_ADDITIONAL_INFO_COLUMN)
    private JsonNode additionalInfo;

    @Transient
    private boolean hasData;

    public OtaPackageInfoEntity() {
        super();
    }

    public OtaPackageInfoEntity(OtaPackageInfo otaPackageInfo) {
        this.createdTime = otaPackageInfo.getCreatedTime();
        this.setUuid(otaPackageInfo.getUuidId());
        this.tenantId = otaPackageInfo.getTenantId().getId();
        this.type = otaPackageInfo.getType();
        if (otaPackageInfo.getDeviceProfileId() != null) {
            this.deviceProfileId = otaPackageInfo.getDeviceProfileId().getId();
        }
        this.title = otaPackageInfo.getTitle();
        this.version = otaPackageInfo.getVersion();
        this.tag = otaPackageInfo.getTag();
        this.url = otaPackageInfo.getUrl();
        this.fileName = otaPackageInfo.getFileName();
        this.contentType = otaPackageInfo.getContentType();
        this.checksumAlgorithm = otaPackageInfo.getChecksumAlgorithm();
        this.checksum = otaPackageInfo.getChecksum();
        this.dataSize = otaPackageInfo.getDataSize();
        this.additionalInfo = otaPackageInfo.getAdditionalInfo();
    }

    public OtaPackageInfoEntity(UUID id, long createdTime, UUID tenantId, UUID deviceProfileId, OtaPackageType type, String title, String version, String tag,
                                String url, String fileName, String contentType, ChecksumAlgorithm checksumAlgorithm, String checksum, Long dataSize,
                                Object additionalInfo, boolean hasData) {
        this.id = id;
        this.createdTime = createdTime;
        this.tenantId = tenantId;
        this.deviceProfileId = deviceProfileId;
        this.type = type;
        this.title = title;
        this.version = version;
        this.tag = tag;
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.checksumAlgorithm = checksumAlgorithm;
        this.checksum = checksum;
        this.dataSize = dataSize;
        this.hasData = hasData;
        this.additionalInfo = JacksonUtil.convertValue(additionalInfo, JsonNode.class);
    }

    @Override
    public OtaPackageInfo toData() {
        OtaPackageInfo otaPackageInfo = new OtaPackageInfo(new OtaPackageId(id));
        otaPackageInfo.setCreatedTime(createdTime);
        otaPackageInfo.setTenantId(TenantId.fromUUID(tenantId));
        if (deviceProfileId != null) {
            otaPackageInfo.setDeviceProfileId(new DeviceProfileId(deviceProfileId));
        }
        otaPackageInfo.setType(type);
        otaPackageInfo.setTitle(title);
        otaPackageInfo.setVersion(version);
        otaPackageInfo.setTag(tag);
        otaPackageInfo.setUrl(url);
        otaPackageInfo.setFileName(fileName);
        otaPackageInfo.setContentType(contentType);
        otaPackageInfo.setChecksumAlgorithm(checksumAlgorithm);
        otaPackageInfo.setChecksum(checksum);
        otaPackageInfo.setDataSize(dataSize);
        otaPackageInfo.setAdditionalInfo(additionalInfo);
        otaPackageInfo.setHasData(hasData);
        return otaPackageInfo;
    }
}
