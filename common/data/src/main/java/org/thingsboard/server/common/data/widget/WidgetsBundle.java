
package org.thingsboard.server.common.data.widget;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasImage;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.HasTitle;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class WidgetsBundle extends BaseData<WidgetsBundleId> implements HasName, HasTenantId, ExportableEntity<WidgetsBundleId>, HasTitle, HasImage {

    private static final long serialVersionUID = -7627368878362410489L;

    @Getter
    @Setter
    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;

    @NoXss
    @Length(fieldName = "alias")
    @Getter
    @Setter
    @ApiModelProperty(position = 4, value = "Unique alias that is used in widget types as a reference widget bundle", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String alias;

    @NoXss
    @Length(fieldName = "title")
    @Getter
    @Setter
    @ApiModelProperty(position = 5, value = "Title used in search and UI", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String title;

    @Getter
    @Setter
    @ApiModelProperty(position = 6, value = "Relative or external image URL. Replaced with image data URL (Base64) in case of relative URL and 'inlineImages' option enabled.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String image;

    @NoXss
    @Length(fieldName = "description", max = 1024)
    @Getter
    @Setter
    @ApiModelProperty(position = 7, value = "Description", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String description;

    @Getter
    @Setter
    @ApiModelProperty(position = 8, value = "Order", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer order;

    @Getter
    @Setter
    private WidgetsBundleId externalId;

    public WidgetsBundle() {
        super();
    }

    public WidgetsBundle(WidgetsBundleId id) {
        super(id);
    }

    public WidgetsBundle(WidgetsBundle widgetsBundle) {
        super(widgetsBundle);
        this.tenantId = widgetsBundle.getTenantId();
        this.alias = widgetsBundle.getAlias();
        this.title = widgetsBundle.getTitle();
        this.image = widgetsBundle.getImage();
        this.description = widgetsBundle.getDescription();
        this.order = widgetsBundle.getOrder();
        this.externalId = widgetsBundle.getExternalId();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Widget Bundle Id. " +
            "Specify this field to update the Widget Bundle. " +
            "Referencing non-existing Widget Bundle Id will cause error. " +
            "Omit this field to create new Widget Bundle." )
    @Override
    public WidgetsBundleId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Widget Bundle creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "Same as title of the Widget Bundle. Read-only field. Update the 'title' to change the 'name' of the Widget Bundle.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WidgetsBundle{");
        sb.append("tenantId=").append(tenantId);
        sb.append(", alias='").append(alias).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
