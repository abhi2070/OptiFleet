package org.thingsboard.server.common.data.integration;

import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.BaseDataWithAdditionalInfo;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasCustomerId;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class Integration extends BaseDataWithAdditionalInfo<IntegrationId> implements HasTenantId, HasCustomerId, ExportableEntity<IntegrationId> {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @NoXss
    @Length(fieldName = "enable_integration")
    private Boolean enable_integration;
    @NoXss
    @Length(fieldName = "debug_mode")
    private Boolean debug_mode;
    @NoXss
    @Length(fieldName = "allow_create_device_or_assets")
    private Boolean allow_create_device_or_assets;

    @NoXss
    @Length(fieldName = "Uplink_Name")
    private String uplinkName;
    @NoXss
    @Length(fieldName = "uplink_Data_Converter_Name")
    private String uplink_data_converter_name;
    @NoXss
    @Length(fieldName = "Uplink_Debug_Mode")
    private Boolean uplink_debugMode;

    @NoXss
    @Length(fieldName = "Downlink_Name")
    private String downlinkName;
    @NoXss
    @Length(fieldName = "Downlink_Data_Converter_Name")
    private String downlink_data_converter_name;
    @NoXss
    @Length(fieldName = "Downlink_Debug_Mode")
    private Boolean downlink_debugMode;
    @NoXss
    @Length(fieldName = "host")
    private String host;
    @NoXss
    @Length(fieldName = "port")
    private Integer port;
    @NoXss
    @Length(fieldName = "enable_SSL")
    private Boolean enableSSL;
    @NoXss
    @Length(fieldName = "password")
    private String password;
    @NoXss
    @Length(fieldName = "username")
    private String username;
    @NoXss
    @Length(fieldName = "topic_filter")
    private String topicFilters;

    @Getter
    @Setter
    private IntegrationId externalId;

    public Integration() {
        super();
    }

    public Integration(IntegrationId id) {
        super(id);
    }

    public Integration(Integration integration) {
        super(integration);
        this.tenantId = integration.getTenantId();
        this.customerId = integration.getCustomerId();
        this.name = integration.getName();
        this.type = integration.getType();
        this.enable_integration = integration.getEnable_integration();
        this.debug_mode = integration.getDebug_mode();
        this.allow_create_device_or_assets = integration.getAllow_create_device_or_assets();
        this.uplinkName=integration.getUplinkName();
        this.uplink_debugMode=integration.getUplink_debugMode();
        this.uplink_data_converter_name= integration.getUplink_data_converter_name();
        this.downlinkName=integration.getDownlinkName();
        this.downlink_debugMode=integration.getDownlink_debugMode();
        this.downlink_data_converter_name=integration.getDownlink_data_converter_name();
        this.host=integration.getHost();
        this.port=integration.getPort();
        this.enableSSL=integration.getEnableSSL();
        this.password=integration.getPassword();
        this.username=integration.getUsername();
        this.topicFilters= integration.getTopicFilters();
        this.externalId = integration.getExternalId();
    }

    public void update(Integration integration) {
        this.tenantId = integration.getTenantId();
        this.customerId = integration.getCustomerId();
        this.name = integration.getName();
        this.type = integration.getType();
        this.enable_integration = integration.getEnable_integration();
        this.debug_mode = integration.getDebug_mode();
        this.allow_create_device_or_assets = integration.getAllow_create_device_or_assets();
        this.uplinkName=integration.getUplinkName();
        this.uplink_debugMode=integration.getUplink_debugMode();
        this.uplink_data_converter_name= integration.getUplink_data_converter_name();
        this.downlinkName=integration.getDownlinkName();
        this.downlink_debugMode=integration.getDownlink_debugMode();
        this.downlink_data_converter_name=integration.getDownlink_data_converter_name();
        this.host=integration.getHost();
        this.port=integration.getPort();
        this.enableSSL=integration.getEnableSSL();
        this.password=integration.getPassword();
        this.username=integration.getUsername();
        this.topicFilters= integration.getTopicFilters();
        this.externalId = integration.getExternalId();
    }

    @Override
    public IntegrationId getId() {
        return super.getId();
    }

    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    public TenantId getTenantId() { return tenantId; }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnable_integration() {
        return enable_integration;
    }

    public void setEnable_integration(Boolean enable_integration) {
        this.enable_integration = enable_integration;
    }

    public Boolean getDebug_mode() {
        return debug_mode;
    }

    public void setDebug_mode(Boolean debug_mode) {
        this.debug_mode = debug_mode;
    }

    public Boolean getAllow_create_device_or_assets() {
        return allow_create_device_or_assets;
    }

    public void setAllow_create_device_or_assets(Boolean allow_create_device_or_assets) {
        this.allow_create_device_or_assets = allow_create_device_or_assets;
    }

    public String getUplinkName() {
        return uplinkName;
    }

    public void setUplinkName(String uplinkName) {
        this.uplinkName = uplinkName;
    }

    public String getUplink_data_converter_name() {
        return uplink_data_converter_name;
    }

    public void setUplink_data_converter_name(String uplink_data_converter_name) {
        this.uplink_data_converter_name = uplink_data_converter_name;
    }

    public Boolean getUplink_debugMode() {
        return uplink_debugMode;
    }

    public void setUplink_debugMode(Boolean uplink_debugMode) {
        this.uplink_debugMode = uplink_debugMode;
    }

    public String getDownlinkName() {
        return downlinkName;
    }

    public void setDownlinkName(String downlinkName) {
        this.downlinkName = downlinkName;
    }

    public String getDownlink_data_converter_name() {
        return downlink_data_converter_name;
    }

    public void setDownlink_data_converter_name(String downlink_data_converter_name) {
        this.downlink_data_converter_name = downlink_data_converter_name;
    }

    public Boolean getDownlink_debugMode() {
        return downlink_debugMode;
    }

    public void setDownlink_debugMode(Boolean downlink_debugMode) {
        this.downlink_debugMode = downlink_debugMode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(Boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTopicFilters() {
        return topicFilters;
    }

    public void setTopicFilters(String topicFilters) {
        this.topicFilters = topicFilters;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Integration [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", enable_integration=");
        builder.append(enable_integration);
        builder.append(", debug_mode=");
        builder.append(debug_mode);
        builder.append(", allow_create_device_or_assets=");
        builder.append(allow_create_device_or_assets);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
}
