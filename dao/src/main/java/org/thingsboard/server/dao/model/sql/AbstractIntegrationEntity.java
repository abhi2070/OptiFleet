package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.MapToJsonConverter;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import org.w3c.dom.Text;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractIntegrationEntity<T extends Integration> extends BaseSqlEntity<T> {

    @Column(name = INTEGRATION_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = INTEGRATION_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Column(name = INTEGRATION_NAME_PROPERTY)
    private String name;

    @Column(name = INTEGRATION_TYPE_PROPERTY)
    private String type;

    @Column(name = INTEGRATION_ENABLE_INTEGRATION)
    private Boolean enable_integration;

    @Column(name = INTEGRATION_DEBUG_MODE)
    private Boolean debug_mode;

    @Column(name = INTEGRATION_ALLOW_CREATE_DEVICE_OR_ASSETS)
    private Boolean allow_create_device_or_assets;

    @Column(name = INTEGRATION_UPLINK_NAME)
    private String uplinkName;

    @Column(name = INTEGRATION_UPLINK_DEBUG_MODE)
    private Boolean uplink_debugMode;

    @Column(name = INTEGRATION_UPLINK_DATA_CONVERTER_NAME)
    private String uplink_data_converter_name;

    @Column(name = INTEGRATION_DOWNLINK_NAME)
    private String downlinkName;

    @Column(name = INTEGRATION_DOWNLINK_DEBUG_MODE)
    private Boolean downlink_debugMode;

    @Column(name = INTEGRATION_DOWNLINK_DATA_CONVERTER_NAME)
    private String downlink_data_converter_name;

    @Column(name = INTEGRATION_HOST)
    private String host;

    @Column(name = INTEGRATION_PORT)
    private Integer port;

    @Column(name = INTEGRATION_ENABLE_SSL)
    private Boolean enableSSL;

    @Column(name = INTEGRATION_PASSWORD)
    private String password;

    @Column(name = INTEGRATION_USERNAME)
    private String username;

    @Column(name = INTEGRATION_TOPIC_FILTERS)
    private String topicFilters;

    @Column(name = EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public AbstractIntegrationEntity() {
        super();
    }

    public AbstractIntegrationEntity(Integration integration) {
        if (integration.getId() != null) {
            this.setUuid(integration.getId().getId());
        }
        this.setCreatedTime(integration.getCreatedTime());
        if (integration.getTenantId() != null) {
            this.tenantId = integration.getTenantId().getId();
        }
        if (integration.getCustomerId() != null) {
            this.customerId = integration.getCustomerId().getId();
        }
        this.name = integration.getName();
        this.type = integration.getType();
        this.enable_integration=integration.getEnable_integration();
        this.debug_mode=integration.getDebug_mode();
        this.allow_create_device_or_assets=integration.getAllow_create_device_or_assets();
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
        if (integration.getExternalId() != null) {
            this.externalId = integration.getExternalId().getId();
        }
    }

    public AbstractIntegrationEntity(IntegrationEntity integrationEntity) {
        this.setId(integrationEntity.getId());
        this.setCreatedTime(integrationEntity.getCreatedTime());
        this.tenantId = integrationEntity.getTenantId();
        this.customerId = integrationEntity.getCustomerId();
        this.type = integrationEntity.getType();
        this.name = integrationEntity.getName();
        this.enable_integration=integrationEntity.getEnable_integration();
        this.debug_mode=integrationEntity.getDebug_mode();
        this.allow_create_device_or_assets=integrationEntity.getAllow_create_device_or_assets();
        this.uplinkName=integrationEntity.getUplinkName();
        this.uplink_debugMode=integrationEntity.getUplink_debugMode();
        this.uplink_data_converter_name= integrationEntity.getUplink_data_converter_name();
        this.downlinkName=integrationEntity.getDownlinkName();
        this.downlink_debugMode=integrationEntity.getDownlink_debugMode();
        this.downlink_data_converter_name=integrationEntity.getDownlink_data_converter_name();
        this.host=integrationEntity.getHost();
        this.port=integrationEntity.getPort();
        this.enableSSL=integrationEntity.getEnableSSL();
        this.password=integrationEntity.getPassword();
        this.username=integrationEntity.getUsername();
        this.topicFilters= integrationEntity.getTopicFilters();
        this.externalId = integrationEntity.getExternalId();
    }

    protected Integration toIntegration() {
        Integration integration = new Integration(new IntegrationId(id));
        integration.setCreatedTime(createdTime);
        if (tenantId != null) {
            integration.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            integration.setCustomerId(new CustomerId(customerId));
        }
        integration.setName(name);
        integration.setType(type);
        integration.setEnable_integration(enable_integration);
        integration.setDebug_mode(debug_mode);
        integration.setAllow_create_device_or_assets(allow_create_device_or_assets);
        integration.setUplinkName(uplinkName);
        integration.setUplink_debugMode(uplink_debugMode);
        integration.setUplink_data_converter_name(uplink_data_converter_name);
        integration.setDownlinkName(downlinkName);
        integration.setDownlink_debugMode(downlink_debugMode);
        integration.setDownlink_data_converter_name(downlink_data_converter_name);
        integration.setHost(host);
        integration.setPort(port);
        integration.setEnableSSL(enableSSL);
        integration.setPassword(password);
        integration.setUsername(username);
        integration.setTopicFilters(topicFilters);
        if (externalId != null) {
            integration.setExternalId(new IntegrationId(externalId));
        }
        return integration;
    }
}
