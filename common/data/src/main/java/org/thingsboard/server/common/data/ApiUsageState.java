
package org.thingsboard.server.common.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.thingsboard.server.common.data.id.ApiUsageStateId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class ApiUsageState extends BaseData<ApiUsageStateId> implements HasTenantId {

    private static final long serialVersionUID = 8250339805336035966L;

    private TenantId tenantId;
    private EntityId entityId;
    private ApiUsageStateValue transportState;
    private ApiUsageStateValue dbStorageState;
    private ApiUsageStateValue reExecState;
    private ApiUsageStateValue jsExecState;
    private ApiUsageStateValue tbelExecState;
    private ApiUsageStateValue emailExecState;
    private ApiUsageStateValue smsExecState;
    private ApiUsageStateValue alarmExecState;

    public ApiUsageState() {
        super();
    }

    public ApiUsageState(ApiUsageStateId id) {
        super(id);
    }

    public ApiUsageState(ApiUsageState ur) {
        super(ur);
        this.tenantId = ur.getTenantId();
        this.entityId = ur.getEntityId();
        this.transportState = ur.getTransportState();
        this.dbStorageState = ur.getDbStorageState();
        this.reExecState = ur.getReExecState();
        this.jsExecState = ur.getJsExecState();
        this.tbelExecState = ur.getTbelExecState();
        this.emailExecState = ur.getEmailExecState();
        this.smsExecState = ur.getSmsExecState();
        this.alarmExecState = ur.getAlarmExecState();
    }

    public boolean isTransportEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(transportState);
    }

    public boolean isReExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(reExecState);
    }

    public boolean isDbStorageEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(dbStorageState);
    }

    public boolean isJsExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(jsExecState);
    }

    public boolean isTbelExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(tbelExecState);
    }

    public boolean isEmailSendEnabled(){
        return !ApiUsageStateValue.DISABLED.equals(emailExecState);
    }

    public boolean isSmsSendEnabled(){
        return !ApiUsageStateValue.DISABLED.equals(smsExecState);
    }

    public boolean isAlarmCreationEnabled() {
        return alarmExecState != ApiUsageStateValue.DISABLED;
    }
}
