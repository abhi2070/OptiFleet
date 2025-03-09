
package org.thingsboard.server.dao.resource;

import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.HasImage;
import org.thingsboard.server.common.data.TbImageDeleteResult;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;

public interface ImageService {

    TbResourceInfo saveImage(TbResource image);

    TbResourceInfo saveImageInfo(TbResourceInfo imageInfo);

    TbResourceInfo getImageInfoByTenantIdAndKey(TenantId tenantId, String key);

    TbResourceInfo getPublicImageInfoByKey(String publicResourceKey);

    PageData<TbResourceInfo> getImagesByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<TbResourceInfo> getAllImagesByTenantId(TenantId tenantId, PageLink pageLink);

    byte[] getImageData(TenantId tenantId, TbResourceId imageId);

    byte[] getImagePreview(TenantId tenantId, TbResourceId imageId);

    TbImageDeleteResult deleteImage(TbResourceInfo imageInfo, boolean force);

    TbResourceInfo findSystemOrTenantImageByEtag(TenantId tenantId, String etag);

    boolean replaceBase64WithImageUrl(HasImage entity, String type);

    boolean replaceBase64WithImageUrl(Dashboard dashboard);

    boolean replaceBase64WithImageUrl(WidgetTypeDetails widgetType);

    void inlineImage(HasImage entity);

    void inlineImages(Dashboard dashboard);

    void inlineImages(WidgetTypeDetails widgetTypeDetails);

    void inlineImageForEdge(HasImage entity);

    void inlineImagesForEdge(Dashboard dashboard);

    void inlineImagesForEdge(WidgetTypeDetails widgetTypeDetails);
}
