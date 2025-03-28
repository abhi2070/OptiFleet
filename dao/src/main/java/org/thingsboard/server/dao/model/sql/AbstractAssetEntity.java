package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.ASSET_CUSTOMER_ID_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.ASSET_LABEL_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.ASSET_NAME_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.ASSET_TENANT_ID_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.ASSET_TYPE_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.EXTERNAL_ID_PROPERTY;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractAssetEntity<T extends Asset> extends BaseSqlEntity<T> {

    @Column(name = ASSET_TENANT_ID_PROPERTY)
    private UUID tenantId;
    @Column(name = ASSET_CUSTOMER_ID_PROPERTY)
    private UUID customerId;
    @Column(name = ASSET_NAME_PROPERTY)
    private String name;
    @Column(name = ASSET_TYPE_PROPERTY)
    private String type;
    @Column(name = ASSET_LABEL_PROPERTY)
    private String label;
    @Type(type = "json")
    @Column(name = ModelConstants.ASSET_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;
    @Column(name = ModelConstants.ASSET_ASSET_PROFILE_ID_PROPERTY, columnDefinition = "uuid")
    private UUID assetProfileId;
    @Column(name = EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public AbstractAssetEntity() {
        super();
    }

    public AbstractAssetEntity(Asset asset) {
        if (asset.getId() != null) {
            this.setUuid(asset.getId().getId());
        }
        this.setCreatedTime(asset.getCreatedTime());
        if (asset.getTenantId() != null) {
            this.tenantId = asset.getTenantId().getId();
        }
        if (asset.getCustomerId() != null) {
            this.customerId = asset.getCustomerId().getId();
        }
        if (asset.getAssetProfileId() != null) {
            this.assetProfileId = asset.getAssetProfileId().getId();
        }
        this.name = asset.getName();
        this.type = asset.getType();
        this.label = asset.getLabel();
        this.additionalInfo = asset.getAdditionalInfo();
        if (asset.getExternalId() != null) {
            this.externalId = asset.getExternalId().getId();
        }
    }

    public AbstractAssetEntity(AssetEntity assetEntity) {
        this.setId(assetEntity.getId());
        this.setCreatedTime(assetEntity.getCreatedTime());
        this.tenantId = assetEntity.getTenantId();
        this.customerId = assetEntity.getCustomerId();
        this.assetProfileId = assetEntity.getAssetProfileId();
        this.type = assetEntity.getType();
        this.name = assetEntity.getName();
        this.label = assetEntity.getLabel();
        this.additionalInfo = assetEntity.getAdditionalInfo();
        this.externalId = assetEntity.getExternalId();
    }

    protected Asset toAsset() {
        Asset asset = new Asset(new AssetId(id));
        asset.setCreatedTime(createdTime);
        if (tenantId != null) {
            asset.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            asset.setCustomerId(new CustomerId(customerId));
        }
        if (assetProfileId != null) {
            asset.setAssetProfileId(new AssetProfileId(assetProfileId));
        }
        asset.setName(name);
        asset.setType(type);
        asset.setLabel(label);
        asset.setAdditionalInfo(additionalInfo);
        if (externalId != null) {
            asset.setExternalId(new AssetId(externalId));
        }
        return asset;
    }

}
