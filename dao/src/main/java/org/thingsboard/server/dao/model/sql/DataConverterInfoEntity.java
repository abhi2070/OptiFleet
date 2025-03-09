//package org.thingsboard.server.dao.model.sql;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import org.thingsboard.server.common.data.asset.AssetInfo;
//import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DataConverterInfoEntity extends AbstractDataConverterEntity<DataConverterInfo>{
//
//    public static final Map<String,String> dataConverterInfoColumnMap = new HashMap<>();
//    static {
//        dataConverterInfoColumnMap.put("customerTitle", "c.title");
//        dataConverterInfoColumnMap.put("dataConverterProfileName", "p.name");
//    }
//
//    private String customerTitle;
//    private boolean customerIsPublic;
//    private String dataConverterProfileName;
//
//    public DataConverterInfoEntity() {
//        super();
//    }
//
//    public DataConverterInfoEntity(DataConverterEntity dataConverterEntity,
//                                   String customerTitle,
//                                   Object customerAdditionalInfo
//    ) {
//        super(dataConverterEntity);
//        this.customerTitle = customerTitle;
//        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
//            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
//        } else {
//            this.customerIsPublic = false;
//        }
//        this.dataConverterProfileName = dataConverterProfileName;
//    }
//
//    @Override
//    public DataConverterInfo toData() {
//        return new DataConverterInfo(super.toDataConverter(), customerTitle, customerIsPublic);
//    }
//}

package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataConverterInfoEntity extends AbstractDataConverterEntity<DataConverterInfo>{

    public DataConverterInfoEntity() { super(); }

    public DataConverterInfoEntity(DataConverterEntity dataConverterEntity) {
        super(dataConverterEntity);
    }

    @Override
    public DataConverterInfo toData() {
        return new DataConverterInfo(super.toDataConverter());
    }

}


