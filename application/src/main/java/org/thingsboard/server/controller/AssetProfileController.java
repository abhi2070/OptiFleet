
package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.asset.profile.TbAssetProfileService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.List;

import static org.thingsboard.server.controller.ControllerConstants.ASSET_PROFILE_ID;
import static org.thingsboard.server.controller.ControllerConstants.ASSET_PROFILE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.ASSET_PROFILE_INFO_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.ASSET_PROFILE_SORT_PROPERTY_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.ASSET_PROFILE_TEXT_SEARCH_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.NEW_LINE;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AssetProfileController extends BaseController {

    private final TbAssetProfileService tbAssetProfileService;
    private final ImageService imageService;

    @ApiOperation(value = "Get Asset Profile (getAssetProfileById)",
            notes = "Fetch the Asset Profile object based on the provided Asset Profile Id. " +
                    "The server checks that the asset profile is owned by the same tenant. " + TENANT_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/assetProfile/{assetProfileId}", method = RequestMethod.GET)
    @ResponseBody
    public AssetProfile getAssetProfileById(
            @ApiParam(value = ASSET_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(ASSET_PROFILE_ID) String strAssetProfileId,
            @ApiParam(value = INLINE_IMAGES_DESCRIPTION)
            @RequestParam(value = INLINE_IMAGES, required = false) boolean inlineImages) throws ThingsboardException {
        checkParameter(ASSET_PROFILE_ID, strAssetProfileId);
        AssetProfileId assetProfileId = new AssetProfileId(toUUID(strAssetProfileId));
        var result = checkAssetProfileId(assetProfileId, Operation.READ);
        if (inlineImages) {
            imageService.inlineImage(result);
        }
        return result;
    }

    @ApiOperation(value = "Get Asset Profile Info (getAssetProfileInfoById)",
            notes = "Fetch the Asset Profile Info object based on the provided Asset Profile Id. "
                    + ASSET_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/assetProfileInfo/{assetProfileId}", method = RequestMethod.GET)
    @ResponseBody
    public AssetProfileInfo getAssetProfileInfoById(
            @ApiParam(value = ASSET_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(ASSET_PROFILE_ID) String strAssetProfileId) throws ThingsboardException {
        checkParameter(ASSET_PROFILE_ID, strAssetProfileId);
        AssetProfileId assetProfileId = new AssetProfileId(toUUID(strAssetProfileId));
        return new AssetProfileInfo(checkAssetProfileId(assetProfileId, Operation.READ));
    }

    @ApiOperation(value = "Get Default Asset Profile (getDefaultAssetProfileInfo)",
            notes = "Fetch the Default Asset Profile Info object. " +
                    ASSET_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/assetProfileInfo/default", method = RequestMethod.GET)
    @ResponseBody
    public AssetProfileInfo getDefaultAssetProfileInfo() throws ThingsboardException {
        return checkNotNull(assetProfileService.findDefaultAssetProfileInfo(getTenantId()));
    }

    @ApiOperation(value = "Create Or Update Asset Profile (saveAssetProfile)",
            notes = "Create or update the Asset Profile. When creating asset profile, platform generates asset profile id as " + UUID_WIKI_LINK +
                    "The newly created asset profile id will be present in the response. " +
                    "Specify existing asset profile id to update the asset profile. " +
                    "Referencing non-existing asset profile Id will cause 'Not Found' error. " + NEW_LINE +
                    "Asset profile name is unique in the scope of tenant. Only one 'default' asset profile may exist in scope of tenant. " +
                    "Remove 'id', 'tenantId' from the request body example (below) to create new Asset Profile entity. " +
                    TENANT_AUTHORITY_PARAGRAPH,
            produces = "application/json",
            consumes = "application/json")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/assetProfile", method = RequestMethod.POST)
    @ResponseBody
    public AssetProfile saveAssetProfile(
            @ApiParam(value = "A JSON value representing the asset profile.")
            @RequestBody AssetProfile assetProfile) throws Exception {
        assetProfile.setTenantId(getTenantId());
        checkEntity(assetProfile.getId(), assetProfile, Resource.ASSET_PROFILE);
        return tbAssetProfileService.save(assetProfile, getCurrentUser());
    }

    @ApiOperation(value = "Delete asset profile (deleteAssetProfile)",
            notes = "Deletes the asset profile. Referencing non-existing asset profile Id will cause an error. " +
                    "Can't delete the asset profile if it is referenced by existing assets." + TENANT_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/assetProfile/{assetProfileId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteAssetProfile(
            @ApiParam(value = ASSET_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(ASSET_PROFILE_ID) String strAssetProfileId) throws ThingsboardException {
        checkParameter(ASSET_PROFILE_ID, strAssetProfileId);
        AssetProfileId assetProfileId = new AssetProfileId(toUUID(strAssetProfileId));
        AssetProfile assetProfile = checkAssetProfileId(assetProfileId, Operation.DELETE);
        tbAssetProfileService.delete(assetProfile, getCurrentUser());
    }

    @ApiOperation(value = "Make Asset Profile Default (setDefaultAssetProfile)",
            notes = "Marks asset profile as default within a tenant scope." + TENANT_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/assetProfile/{assetProfileId}/default", method = RequestMethod.POST)
    @ResponseBody
    public AssetProfile setDefaultAssetProfile(
            @ApiParam(value = ASSET_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(ASSET_PROFILE_ID) String strAssetProfileId) throws ThingsboardException {
        checkParameter(ASSET_PROFILE_ID, strAssetProfileId);
        AssetProfileId assetProfileId = new AssetProfileId(toUUID(strAssetProfileId));
        AssetProfile assetProfile = checkAssetProfileId(assetProfileId, Operation.WRITE);
        AssetProfile previousDefaultAssetProfile = assetProfileService.findDefaultAssetProfile(getTenantId());
        return tbAssetProfileService.setDefaultAssetProfile(assetProfile, previousDefaultAssetProfile, getCurrentUser());
    }

    @ApiOperation(value = "Get Asset Profiles (getAssetProfiles)",
            notes = "Returns a page of asset profile objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/assetProfiles", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<AssetProfile> getAssetProfiles(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = ASSET_PROFILE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ASSET_PROFILE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(assetProfileService.findAssetProfiles(getTenantId(), pageLink));
    }

    @ApiOperation(value = "Get Asset Profile infos (getAssetProfileInfos)",
            notes = "Returns a page of asset profile info objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + ASSET_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/assetProfileInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<AssetProfileInfo> getAssetProfileInfos(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = ASSET_PROFILE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ASSET_PROFILE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(assetProfileService.findAssetProfileInfos(getTenantId(), pageLink));
    }

    @ApiOperation(value = "Get Asset Profile names (getAssetProfileNames)",
            notes = "Returns a set of unique asset profile names owned by the tenant."
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/assetProfile/names", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityInfo> getAssetProfileNames(
            @ApiParam(value = "Flag indicating whether to retrieve exclusively the names of asset profiles that are referenced by tenant's assets.")
            @RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return checkNotNull(assetProfileService.findAssetProfileNamesByTenantId(tenantId, activeOnly));
    }

}
