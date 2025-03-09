package org.thingsboard.server.service.data_converter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.data_converter.DataConverter;
//import org.thingsboard.server.common.data.data_converter.DataConverterProfile;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import org.thingsboard.server.dao.data_converter.DataConverterService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.data_converter.TbDataConverterService;
//import org.thingsboard.server.dao.data_converter.DataConverterProfileService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class DataConverterBulkImportService extends AbstractBulkImportService<DataConverter> {
    private final DataConverterService dataConverterService;
    private final TbDataConverterService tbDataConverterService;
    // private final DataConverterProfileService dataConverterProfileService;

    @Override
    protected void setEntityFields(DataConverter entity, Map<BulkImportColumnType, String> fields) {
//        ObjectNode additionalInfo = getOrCreateAdditionalInfoObj(entity);
        fields.forEach((columnType, value) -> {
            switch (columnType) {
                case NAME:
                    entity.setName(value);
                    break;
                case TYPE:
                    entity.setType(value);
                    break;
//                case LABEL:
//                    entity.setLabel(value);
//                    break;
//                case DESCRIPTION:
//                    additionalInfo.set("description", new TextNode(value));
//                    break;
            }
        });
//        entity.setAdditionalInfo(additionalInfo);
    }

    @Override
    @SneakyThrows
    protected DataConverter saveEntity(SecurityUser user, DataConverter entity, Map<BulkImportColumnType, String> fields){
//        DataConverterProfile dataConverterProfile;
//        if (StringUtils.isNotEmpty(entity.getType())) {
//            dataConverterProfile = dataConverterProfileService.findOrCreateDataConverterProfile(entity.getTenantId(), entity.getType());
//        } else {
//            dataConverterProfile = dataConverterProfileService.findDefaultDataConverterProfile(entity.getTenantId());
//        }
//        entity.setDataConverterProfileId(dataConverterProfile.getId());
        return tbDataConverterService.save(entity, user);
    }

    @Override
    protected DataConverter findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(dataConverterService.findDataConverterByTenantIdAndName(tenantId, name))
                .orElseGet(DataConverter::new);
    }

    @Override
    protected void setOwners(DataConverter entity, SecurityUser user) {
        entity.setTenantId(user.getTenantId());
//        entity.setCustomerId(user.getCustomerId());
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.DATACONVERTER;
    }

}


