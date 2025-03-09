package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;
import static org.thingsboard.server.dao.model.ModelConstants.EXTERNAL_ID_PROPERTY;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractDataConverterEntity<T extends DataConverter> extends BaseSqlEntity<T> {

    @Column(name = DATACONVERTER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = DATACONVERTER_NAME_PROPERTY)
    private String name;

    @Column(name = DATACONVERTER_TYPE_PROPERTY)
    private String type;

    @Column(name = DATACONVERTER_DEBUG_MODE)
    private Boolean debug_mode;

    @Column(name = DATACONVERTER_SCRIPTLANG_PROPERTY)
    private String scriptLang;

    @Column(name = DATACONVERTER_PAYLOAD_PROPERTY)
    private String payload;

    @Column(name = DATACONVERTER_METADATA)
    private String metadata;

    @Column(name = DATACONVERTER_FUNCTION_DECODER, columnDefinition = "TEXT")
    private String function_Decoder;


    @Column(name = DATACONVERTER_FUNCTION_DECODER_JAVASCRIPT, columnDefinition = "TEXT")
    private String function_Decoder_Javascript;

    @Column(name = EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    @Type(type = "json")
    @Column(name = ModelConstants.DATACONVERTER_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    @Type(type = "json")
    @Column(name = ModelConstants.DATACONVERTER_CONFIGURATION)
    private JsonNode configuration;

    public AbstractDataConverterEntity() {
        super();
    }

    public AbstractDataConverterEntity(DataConverter dataConverter) {
        if (dataConverter.getId() != null) {
            this.setUuid(dataConverter.getId().getId());
        }
        this.setCreatedTime(dataConverter.getCreatedTime());
        if (dataConverter.getTenantId() != null) {
            this.tenantId = dataConverter.getTenantId().getId();
        }
        this.name = dataConverter.getName();
        this.type = dataConverter.getType();
        this.debug_mode = dataConverter.getDebug_mode();
        this.scriptLang = dataConverter.getScriptLang();
        this.payload = dataConverter.getPayload();
        this.metadata = dataConverter.getMetadata();
        this.function_Decoder = dataConverter.getFunction_Decoder();
        this.function_Decoder_Javascript = dataConverter.getFunction_Decoder_Javascript();
        this.additionalInfo = dataConverter.getAdditionalInfo();
        this.configuration = dataConverter.getConfiguration();
        if (dataConverter.getExternalId() != null) {
            this.externalId = dataConverter.getExternalId().getId();
        }
    }

    public AbstractDataConverterEntity(DataConverterEntity dataConverterEntity) {
        this.setId(dataConverterEntity.getId());
        this.setCreatedTime(dataConverterEntity.getCreatedTime());
        this.tenantId = dataConverterEntity.getTenantId();
        this.type = dataConverterEntity.getType();
        this.name = dataConverterEntity.getName();
        this.debug_mode = dataConverterEntity.getDebug_mode();
        this.scriptLang = dataConverterEntity.getScriptLang();
        this.payload = dataConverterEntity.getPayload();
        this.metadata = dataConverterEntity.getMetadata();
        this.function_Decoder = dataConverterEntity.getFunction_Decoder();
        this.function_Decoder_Javascript = dataConverterEntity.getFunction_Decoder_Javascript();
        this.additionalInfo = dataConverterEntity.getAdditionalInfo();
        this.configuration = dataConverterEntity.getConfiguration();
        this.externalId = dataConverterEntity.getExternalId();
    }

    protected DataConverter toDataConverter() {
        DataConverter dataConverter = new DataConverter(new DataConverterId(id));
        dataConverter.setCreatedTime(createdTime);
        if (tenantId != null) {
            dataConverter.setTenantId(TenantId.fromUUID(tenantId));
        }
        dataConverter.setName(name);
        dataConverter.setType(type);
        dataConverter.setDebug_mode(debug_mode);
        dataConverter.setScriptLang(scriptLang);
        dataConverter.setPayload(payload);
        dataConverter.setMetadata(metadata);
        dataConverter.setFunction_Decoder(function_Decoder);
        dataConverter.setFunction_Decoder_Javascript(function_Decoder_Javascript);
        dataConverter.setAdditionalInfo(additionalInfo);
        dataConverter.setConfiguration(configuration);
        if (externalId != null) {
            dataConverter.setExternalId(new DataConverterId(externalId));
        }
        return dataConverter;
    }
}
