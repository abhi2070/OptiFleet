
package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "image")
public class AssetProfileInfo extends EntityInfo {

    @ApiModelProperty(position = 3, value = "Either URL or Base64 data of the icon. Used in the mobile application to visualize set of asset profiles in the grid view. ")
    private final String image;
    @ApiModelProperty(position = 4, value = "Reference to the dashboard. Used in the mobile application to open the default dashboard when user navigates to asset details.")
    private final DashboardId defaultDashboardId;

    @ApiModelProperty(position = 5, value = "Tenant id.")
    private final TenantId tenantId;

    @JsonCreator
    public AssetProfileInfo(@JsonProperty("id") EntityId id,
                            @JsonProperty("tenantId") TenantId tenantId,
                            @JsonProperty("name") String name,
                            @JsonProperty("image") String image,
                            @JsonProperty("defaultDashboardId") DashboardId defaultDashboardId) {
        super(id, name);
        this.tenantId = tenantId;
        this.image = image;
        this.defaultDashboardId = defaultDashboardId;
    }

    public AssetProfileInfo(UUID uuid, UUID tenantId, String name, String image, UUID defaultDashboardId) {
        super(EntityIdFactory.getByTypeAndUuid(EntityType.ASSET_PROFILE, uuid), name);
        this.tenantId = new TenantId(tenantId);
        this.image = image;
        this.defaultDashboardId = defaultDashboardId != null ? new DashboardId(defaultDashboardId) : null;
    }

    public AssetProfileInfo(AssetProfile profile) {
        this(profile.getId(), profile.getTenantId(), profile.getName(), profile.getImage(), profile.getDefaultDashboardId());
    }

}
