package org.thingsboard.server.common.data.data_converter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.BaseDataWithAdditionalInfo;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class DataConverter extends BaseDataWithAdditionalInfo<DataConverterId> implements HasTenantId, ExportableEntity<DataConverterId> {

    private static final long serialVersionUID = 2807343040519543363L;
    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @NoXss
    @Length(fieldName = "debug_mode")
    private Boolean debug_mode;
    @NoXss
    @Length(fieldName = "scriptLang")
    private String scriptLang;
    @NoXss
    @Length(fieldName = "payLoad")
    private String payload;

    @NoXss
    @ApiModelProperty(position = 11, value = "Configuration for the data converter", dataType = "com.fasterxml.jackson.databind.JsonNode")
    private JsonNode configuration;

    @NoXss
    @Length(fieldName = "metadata")
    private String metadata;

    @Getter
    @NoXss
    private String function_Decoder;

    @NoXss
    private String function_Decoder_Javascript;

    @Getter
    @Setter
    private DataConverterId externalId;

    public DataConverter() {
        super();
    }

    public DataConverter(DataConverterId id) {
        super(id);
    }

    public DataConverter(DataConverter dataConverter) {
        super(dataConverter);
        this.tenantId = dataConverter.getTenantId();
        this.name = dataConverter.getName();
        this.type = dataConverter.getType();
        this.debug_mode = dataConverter.getDebug_mode();
        this.scriptLang = dataConverter.getScriptLang();
        this.payload = dataConverter.getPayload();
        this.metadata = dataConverter.getMetadata();
        this.scriptLang = dataConverter.getScriptLang();
        this.function_Decoder = dataConverter.getFunction_Decoder();
        this.function_Decoder_Javascript = dataConverter.getFunction_Decoder_Javascript();
        this.externalId = dataConverter.getExternalId();
        this.configuration = dataConverter.getConfiguration();
    }

    public void update(DataConverter dataConverter) {
        this.tenantId = dataConverter.getTenantId();
        this.name = dataConverter.getName();
        this.type = dataConverter.getType();
        this.debug_mode = dataConverter.getDebug_mode();
        this.scriptLang = dataConverter.getScriptLang();
        this.payload = dataConverter.getPayload();
        this.metadata = dataConverter.getMetadata();
        this.function_Decoder = dataConverter.getFunction_Decoder();
        this.function_Decoder_Javascript = dataConverter.getFunction_Decoder_Javascript();
        Optional.ofNullable(dataConverter.getAdditionalInfo()).ifPresent(this::setAdditionalInfo);
        Optional.ofNullable(dataConverter.getConfiguration()).ifPresent(this::setConfiguration);
        this.externalId = dataConverter.getExternalId();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the data-converter Id. " + "Specify this field to update the data-converter. " + "Referencing non-existing asset Id will cause error. " + "Omit this field to create new data-converter.")
    @Override
    public DataConverterId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the data-converter creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, required = true, value = "Unique Data Converter Name in scope of Tenant", example = "Any name")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 5, value = "Data Converter type", example = "UPLINK/DOWNLINK")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(position = 6, value = "Data Converter scripLang", example = "TBEL/Javascript")
    public String getScriptLang() {
        return scriptLang;
    }

    public void setScriptLang(String scriptLang) {
        this.scriptLang = scriptLang;
    }

    @ApiModelProperty(position = 7, value = "Data Converter payload", example = "// Decode an uplink message from a buffer\\n// payload - array of bytes\\n// metadata - key/value object\\n\\n/** Decoder **/\\n\\n// decode payload to string\\nvar payloadStr = decodeToString(payload);\\n\\n// decode payload to JSON\\n// var data = decodeToJson(payload);\\n\\nvar deviceName = 'Device A';\\nvar deviceType = 'thermostat';\\nvar customerName = 'Customer C';\\nvar groupName = 'thermostat devices';\\nvar manufacturer = 'Example corporation';\\n// use assetName and assetType instead of deviceName and deviceType\\n// to automatically create assets instead of devices.\\n// var assetName = 'Asset A';\\n// var assetType = 'building';\\n\\n// Result object with device/asset attributes/telemetry data\\nvar result = {\\n// Use deviceName and deviceType or assetName and assetType, but not both.\\n   deviceName: deviceName,\\n   deviceType: deviceType,\\n// assetName: assetName,\\n// assetType: assetType,\\n// customerName: customerName,\\n   groupName: groupName,\\n   attributes: {\\n       model: 'Model A',\\n       serialNumber: 'SN111',\\n       integrationName: metadata['integrationName'],\\n       manufacturer: manufacturer\\n   },\\n   telemetry: {\\n       temperature: 42,\\n       humidity: 80,\\n       rawData: payloadStr\\n   }\\n};\\n\\n/** Helper functions **/\\n\\nfunction decodeToString(payload) {\\n   return String.fromCharCode.apply(String, payload);\\n}\\n\\nfunction decodeToJson(payload) {\\n   // covert payload to string.\\n   var str = decodeToString(payload);\\n\\n   // parse string to JSON\\n   var data = JSON.parse(str);\\n   return data;\\n}\\n\\nreturn result;")
    public String getPayload() {
        return scriptLang;
    }

    public void setPayload(String payLoad) {
        this.payload = payLoad;
    }

    public Boolean getDebug_mode() {
        return debug_mode;
    }

    public void setDebug_mode(Boolean debug_mode) {
        this.debug_mode = debug_mode;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }


    public void setFunction_Decoder(String function_Decoder) {
        this.function_Decoder = function_Decoder;
    }


    public String getFunction_Decoder_Javascript() {
        return function_Decoder_Javascript;
    }

    public void setFunction_Decoder_Javascript(String function_Decoder_Javascript) {
        this.function_Decoder_Javascript = function_Decoder_Javascript;
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
    }

    @ApiModelProperty(position = 9, value = "Additional parameters of the asset", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataConverter [tenantId=");
        builder.append(tenantId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", configuration=");
        builder.append(configuration);
        builder.append(", scriptLang=");
        builder.append(scriptLang);
        builder.append(", payLoad=");
        builder.append(payload);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}

