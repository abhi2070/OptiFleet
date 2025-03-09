
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.dao.model.ModelConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Immutable
@Table(name = ModelConstants.DEVICE_INFO_VIEW_TABLE_NAME)
public class DeviceInfoEntity extends AbstractDeviceEntity<DeviceInfo> {

    @Column(name = ModelConstants.DEVICE_CUSTOMER_TITLE_PROPERTY)
    private String customerTitle;
    @Column(name = ModelConstants.DEVICE_CUSTOMER_IS_PUBLIC_PROPERTY)
    private boolean customerIsPublic;
    @Column(name = ModelConstants.DEVICE_DEVICE_PROFILE_NAME_PROPERTY)
    private String deviceProfileName;
    @Column(name = ModelConstants.DEVICE_ACTIVE_PROPERTY)
    private boolean active;

    public DeviceInfoEntity() {
        super();
    }


    @Override
    public DeviceInfo toData() {
        return new DeviceInfo(super.toDevice(), customerTitle, customerIsPublic, deviceProfileName, active);
    }

}
