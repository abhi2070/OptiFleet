package org.thingsboard.server.common.data.data_converter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.DataConverterId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class DataConverterInfo extends DataConverter{
    private static final long serialVersionUID = -4094528227011066194L;

//    @ApiModelProperty(position = 10, value = "Title of the Customer that owns the dataConverter.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private String customerTitle;
//    @ApiModelProperty(position = 11, value = "Indicates special 'Public' Customer that is auto-generated to use the assets on public dashboards.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private boolean customerIsPublic;

//    @ApiModelProperty(position = 12, value = "Name of the corresponding dataConverter Profile.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
//    private String dataConverterProfileName;


    public DataConverterInfo() {
        super();
    }

    public DataConverterInfo(DataConverterId dataConverterId) {
        super(dataConverterId);
    }

    public DataConverterInfo(DataConverter dataConverter) {
        super(dataConverter);
//        this.customerTitle = customerTitle;
//        this.customerIsPublic = customerIsPublic;
//        this.dataConverterProfileName = dataConverterProfileName;
    }
}
