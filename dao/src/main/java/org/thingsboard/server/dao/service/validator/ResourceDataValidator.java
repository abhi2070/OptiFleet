
package org.thingsboard.server.dao.service.validator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.data.widget.BaseWidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.resource.TbResourceDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.widget.WidgetTypeDao;

import java.util.List;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.EntityType.TB_RESOURCE;

@Component
public class ResourceDataValidator extends DataValidator<TbResource> {

    @Autowired
    private TbResourceDao resourceDao;

    @Autowired
    private WidgetTypeDao widgetTypeDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    protected void validateCreate(TenantId tenantId, TbResource resource) {
        if (resource.getData() == null || resource.getData().length == 0) {
            throw new DataValidationException("Resource data should be specified");
        }
    }

    @Override
    protected TbResource validateUpdate(TenantId tenantId, TbResource resource) {
        if (resource.getData() != null && !resource.getResourceType().isUpdatable() &&
                tenantId != null && !tenantId.isSysTenantId()) {
            throw new DataValidationException("This type of resource can't be updated");
        }
        return resource;
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, TbResource resource) {
        validateString("Resource title", resource.getTitle());
        if (resource.getTenantId() == null) {
            resource.setTenantId(TenantId.SYS_TENANT_ID);
        }
        if (!resource.getTenantId().isSysTenantId()) {
            if (!tenantService.tenantExists(resource.getTenantId())) {
                throw new DataValidationException("Resource is referencing to non-existent tenant!");
            }
        }
        if (resource.getResourceType() == null) {
            throw new DataValidationException("Resource type should be specified!");
        }
        if (resource.getData() != null) {
            validateResourceSize(resource.getTenantId(), resource.getId(), resource.getData().length);
        }
        if (StringUtils.isEmpty(resource.getFileName())) {
            throw new DataValidationException("Resource file name should be specified!");
        }
        if (StringUtils.containsAny(resource.getFileName(), "/", "\\")) {
            throw new DataValidationException("File name contains forbidden symbols");
        }
        if (StringUtils.isEmpty(resource.getResourceKey())) {
            throw new DataValidationException("Resource key should be specified!");
        }
    }

    public void validateResourceSize(TenantId tenantId, TbResourceId resourceId, long dataSize) {
        if (!tenantId.isSysTenantId()) {
            DefaultTenantProfileConfiguration profileConfiguration = tenantProfileCache.get(tenantId).getDefaultProfileConfiguration();
            long maxResourceSize = profileConfiguration.getMaxResourceSize();
            if (maxResourceSize > 0 && dataSize > maxResourceSize) {
                throw new IllegalArgumentException("Resource exceeds the maximum size of " + FileUtils.byteCountToDisplaySize(maxResourceSize));
            }
            long maxSumResourcesDataInBytes = profileConfiguration.getMaxResourcesInBytes();
            if (resourceId != null) {
                long prevSize = resourceDao.getResourceSize(tenantId, resourceId);
                dataSize -= prevSize;
            }
            validateMaxSumDataSizePerTenant(tenantId, resourceDao, maxSumResourcesDataInBytes, dataSize, TB_RESOURCE);
        }
    }

    @Override
    public void validateDelete(TenantId tenantId, EntityId resourceId) {
        List<WidgetTypeDetails> widgets = widgetTypeDao.findWidgetTypesInfosByTenantIdAndResourceId(tenantId.getId(),
                resourceId.getId());
        if (!widgets.isEmpty()) {
            List<String> widgetNames = widgets.stream().map(BaseWidgetType::getName).collect(Collectors.toList());
            throw new DataValidationException(String.format("Following widget types uses current resource: %s", widgetNames));
        }
    }

}
