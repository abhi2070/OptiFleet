
package org.thingsboard.server.dao.model.sql;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.WIDGETS_BUNDLE_TABLE_NAME)
public final class WidgetsBundleEntity extends BaseSqlEntity<WidgetsBundle> {

    @Column(name = ModelConstants.WIDGETS_BUNDLE_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.WIDGETS_BUNDLE_ALIAS_PROPERTY)
    private String alias;

    @Column(name = ModelConstants.WIDGETS_BUNDLE_TITLE_PROPERTY)
    private String title;

    @Column(name = ModelConstants.WIDGETS_BUNDLE_IMAGE_PROPERTY)
    private String image;

    @Column(name = ModelConstants.WIDGETS_BUNDLE_DESCRIPTION)
    private String description;

    @Column(name = ModelConstants.WIDGETS_BUNDLE_ORDER)
    private Integer order;

    @Column(name = ModelConstants.EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public WidgetsBundleEntity() {
        super();
    }

    public WidgetsBundleEntity(WidgetsBundle widgetsBundle) {
        if (widgetsBundle.getId() != null) {
            this.setUuid(widgetsBundle.getId().getId());
        }
        this.setCreatedTime(widgetsBundle.getCreatedTime());
        if (widgetsBundle.getTenantId() != null) {
            this.tenantId = widgetsBundle.getTenantId().getId();
        }
        this.alias = widgetsBundle.getAlias();
        this.title = widgetsBundle.getTitle();
        this.image = widgetsBundle.getImage();
        this.description = widgetsBundle.getDescription();
        this.order = widgetsBundle.getOrder();
        if (widgetsBundle.getExternalId() != null) {
            this.externalId = widgetsBundle.getExternalId().getId();
        }
    }

    @Override
    public WidgetsBundle toData() {
        WidgetsBundle widgetsBundle = new WidgetsBundle(new WidgetsBundleId(id));
        widgetsBundle.setCreatedTime(createdTime);
        if (tenantId != null) {
            widgetsBundle.setTenantId(TenantId.fromUUID(tenantId));
        }
        widgetsBundle.setAlias(alias);
        widgetsBundle.setTitle(title);
        widgetsBundle.setImage(image);
        widgetsBundle.setDescription(description);
        widgetsBundle.setOrder(order);
        if (externalId != null) {
            widgetsBundle.setExternalId(new WidgetsBundleId(externalId));
        }
        return widgetsBundle;
    }
}
