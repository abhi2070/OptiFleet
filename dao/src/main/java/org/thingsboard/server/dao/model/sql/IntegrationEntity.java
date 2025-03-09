package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.INTEGRATION_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = INTEGRATION_TABLE_NAME)
public final class IntegrationEntity extends AbstractIntegrationEntity<Integration>{

    public IntegrationEntity() {
        super();
    }

    public IntegrationEntity(Integration integration) {
        super(integration);
    }

    @Override
    public Integration toData() {
        return super.toIntegration();
    }

}
