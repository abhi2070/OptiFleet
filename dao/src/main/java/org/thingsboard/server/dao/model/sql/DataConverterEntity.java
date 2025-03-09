package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.DATACONVERTER_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = DATACONVERTER_TABLE_NAME)
public final class DataConverterEntity extends AbstractDataConverterEntity<DataConverter> {

    public DataConverterEntity() {
        super();
    }

    public DataConverterEntity(DataConverter dataConverter) { super(dataConverter); }

    @Override
    public DataConverter toData() { return super.toDataConverter(); }
}




