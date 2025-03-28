
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.function.UnaryOperator;

@ApiModel
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class TbResourceInfo extends BaseData<TbResourceId> implements HasName, HasTenantId, ExportableEntity<TbResourceId> {

    private static final long serialVersionUID = 7282664529021651736L;

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Tenant Id of the resource can't be changed.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "title")
    @ApiModelProperty(position = 4, value = "Resource title.", example = "BinaryAppDataContainer id=19 v1.0")
    private String title;
    @ApiModelProperty(position = 5, value = "Resource type.", example = "LWM2M_MODEL", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private ResourceType resourceType;
    @NoXss
    @Length(fieldName = "resourceKey")
    @ApiModelProperty(position = 6, value = "Resource key.", example = "19_1.0", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String resourceKey;
    private boolean isPublic;
    private String publicResourceKey;
    @ApiModelProperty(position = 7, value = "Resource search text.", example = "19_1.0:binaryappdatacontainer", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String searchText;

    @ApiModelProperty(position = 8, value = "Resource etag.", example = "33a64df551425fcc55e4d42a148795d9f25f89d4", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String etag;
    @NoXss
    @Length(fieldName = "file name")
    @ApiModelProperty(position = 9, value = "Resource file name.", example = "19.xml", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String fileName;
    private JsonNode descriptor;

    private TbResourceId externalId;

    public TbResourceInfo() {
        super();
    }

    public TbResourceInfo(TbResourceId id) {
        super(id);
    }

    public TbResourceInfo(TbResourceInfo resourceInfo) {
        super(resourceInfo);
        this.tenantId = resourceInfo.tenantId;
        this.title = resourceInfo.title;
        this.resourceType = resourceInfo.resourceType;
        this.resourceKey = resourceInfo.resourceKey;
        this.searchText = resourceInfo.searchText;
        this.isPublic = resourceInfo.isPublic;
        this.publicResourceKey = resourceInfo.publicResourceKey;
        this.etag = resourceInfo.etag;
        this.fileName = resourceInfo.fileName;
        this.descriptor = resourceInfo.descriptor != null ? resourceInfo.descriptor.deepCopy() : null;
        this.externalId = resourceInfo.externalId;
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Resource Id. " +
            "Specify this field to update the Resource. " +
            "Referencing non-existing Resource Id will cause error. " +
            "Omit this field to create new Resource.")
    @Override
    public TbResourceId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the resource creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getLink() {
        if (resourceType == ResourceType.IMAGE) {
            String type = (tenantId != null && tenantId.isSysTenantId()) ? "system" : "tenant"; // tenantId is null in case of export to git
            return "/api/images/" + type + "/" + resourceKey;
        }
        return null;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getPublicLink() {
        if (resourceType == ResourceType.IMAGE && isPublic) {
            return "/api/images/public/" + getPublicResourceKey();
        }
        return null;
    }

    @JsonIgnore
    public String getSearchText() {
        return title;
    }

    public <T> T getDescriptor(Class<T> type) throws JsonProcessingException {
        return descriptor != null ? mapper.treeToValue(descriptor, type) : null;
    }

    public <T> void updateDescriptor(Class<T> type, UnaryOperator<T> updater) throws JsonProcessingException {
        T descriptor = getDescriptor(type);
        descriptor = updater.apply(descriptor);
        setDescriptorValue(descriptor);
    }

    @JsonIgnore
    public void setDescriptorValue(Object value) {
        this.descriptor = value != null ? mapper.valueToTree(value) : null;
    }

}
