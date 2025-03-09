package org.thingsboard.server.service.roles;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import org.thingsboard.server.dao.roles.RolesService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.roles.TbRolesService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class RolesBulkImportService extends AbstractBulkImportService<Roles> {
    private final RolesService rolesService;
    private final TbRolesService tbRolesService;


    @Override
    protected void setEntityFields(Roles entity, Map<BulkImportColumnType, String> fields) {
        ObjectNode additionalInfo = getOrCreateAdditionalInfoObj(entity);
        fields.forEach((columnType, value) -> {
            switch (columnType) {
                case NAME:
                    entity.setName(value);
                    break;
                case TYPE:
                    entity.setType(value);
                    break;
                case DESCRIPTION:
                    additionalInfo.set("description", new TextNode(value));
                    break;
            }
        });
        entity.setAdditionalInfo(additionalInfo);
    }

    @Override
    @SneakyThrows
    protected Roles saveEntity(SecurityUser user, Roles entity, Map<BulkImportColumnType, String> fields) {

        return tbRolesService.save(entity, user);
    }

    @Override
    protected Roles findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(rolesService.findRolesByTenantIdAndName(tenantId, name))
                .orElseGet(Roles::new);
    }

    @Override
    protected void setOwners(Roles entity, SecurityUser user) {
        entity.setTenantId(user.getTenantId());
        entity.setCustomerId(user.getCustomerId());
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.ROLES;

    }

}
