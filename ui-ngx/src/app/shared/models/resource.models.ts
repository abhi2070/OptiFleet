

import { BaseData, ExportableEntity, HasId } from '@shared/models/base-data';
import { TenantId } from '@shared/models/id/tenant-id';
import { TbResourceId } from '@shared/models/id/tb-resource-id';
import { NULL_UUID } from '@shared/models/id/has-uuid';
import { HasTenantId } from '@shared/models/entity.models';

export enum ResourceType {
  LWM2M_MODEL = 'LWM2M_MODEL',
  PKCS_12 = 'PKCS_12',
  JKS = 'JKS',
  JS_MODULE = 'JS_MODULE'
}

export const ResourceTypeMIMETypes = new Map<ResourceType, string>(
  [
    [ResourceType.LWM2M_MODEL, 'application/xml,text/xml'],
    [ResourceType.PKCS_12, 'application/x-pkcs12'],
    [ResourceType.JKS, 'application/x-java-keystore'],
    [ResourceType.JS_MODULE, 'text/javascript,application/javascript']
  ]
);

export const ResourceTypeExtension = new Map<ResourceType, string>(
  [
    [ResourceType.LWM2M_MODEL, 'xml'],
    [ResourceType.PKCS_12, 'p12,pfx'],
    [ResourceType.JKS, 'jks'],
    [ResourceType.JS_MODULE, 'js']
  ]
);

export const ResourceTypeTranslationMap = new Map<ResourceType, string>(
  [
    [ResourceType.LWM2M_MODEL, 'resource.type.lwm2m-model'],
    [ResourceType.PKCS_12, 'resource.type.pkcs-12'],
    [ResourceType.JKS, 'resource.type.jks'],
    [ResourceType.JS_MODULE, 'resource.type.js-module']
  ]
);

export interface TbResourceInfo<D> extends Omit<BaseData<TbResourceId>, 'name' | 'label'>, HasTenantId, ExportableEntity<TbResourceId> {
  tenantId?: TenantId;
  resourceKey?: string;
  title?: string;
  resourceType: ResourceType;
  fileName: string;
  public: boolean;
  publicResourceKey?: string;
  descriptor?: D;
}

export type ResourceInfo = TbResourceInfo<any>;

export interface Resource extends ResourceInfo {
  data: string;
  name?: string;
}

export interface ImageDescriptor {
  mediaType: string;
  width: number;
  height: number;
  size: number;
  etag: string;
  previewDescriptor: ImageDescriptor;
}

export interface ImageResourceInfo extends TbResourceInfo<ImageDescriptor> {
  link?: string;
  publicLink?: string;
}

export interface ImageResource extends ImageResourceInfo {
  base64?: string;
}

export interface ImageExportData {
  mediaType: string;
  fileName: string;
  title: string;
  resourceKey: string;
  public: boolean;
  publicResourceKey: string;
  data: string;
}

export type ImageResourceType = 'tenant' | 'system';

export type ImageReferences = {[entityType: string]: Array<BaseData<HasId> & HasTenantId>};

export interface ImageResourceInfoWithReferences extends ImageResourceInfo {
  references: ImageReferences;
}

export interface ImageDeleteResult {
  image: ImageResourceInfo;
  success: boolean;
  imageIsReferencedError?: boolean;
  error?: any;
  references?: ImageReferences;
}

export const toImageDeleteResult = (image: ImageResourceInfo, e?: any): ImageDeleteResult => {
  if (!e) {
    return {image, success: true};
  } else {
    const result: ImageDeleteResult = {image, success: false, error: e};
    if (e?.status === 400 && e?.error?.success === false && e?.error?.references) {
      const references: ImageReferences = e?.error?.references;
      result.imageIsReferencedError = true;
      result.references = references;
    }
    return result;
  }
};

export const imageResourceType = (imageInfo: ImageResourceInfo): ImageResourceType =>
  (!imageInfo.tenantId || imageInfo.tenantId?.id === NULL_UUID) ? 'system' : 'tenant';

export const TB_IMAGE_PREFIX = 'tb-image;';

export const IMAGES_URL_REGEXP = /\/api\/images\/(tenant|system)\/(.*)/;
export const IMAGES_URL_PREFIX = '/api/images';

export const PUBLIC_IMAGES_URL_PREFIX = '/api/images/public';

export const IMAGE_BASE64_URL_PREFIX = 'data:image/';

export const removeTbImagePrefix = (url: string): string => url ? url.replace(TB_IMAGE_PREFIX, '') : url;

export const removeTbImagePrefixFromUrls = (urls: string[]): string[] => urls ? urls.map(url => removeTbImagePrefix(url)) : [];

export const prependTbImagePrefix = (url: string): string => {
  if (url && !url.startsWith(TB_IMAGE_PREFIX)) {
    url = TB_IMAGE_PREFIX + url;
  }
  return url;
};

export const prependTbImagePrefixToUrls = (urls: string[]): string[] => urls ? urls.map(url => prependTbImagePrefix(url)) : [];


export const isImageResourceUrl = (url: string): boolean => url && IMAGES_URL_REGEXP.test(url);

export const extractParamsFromImageResourceUrl = (url: string): {type: ImageResourceType; key: string} => {
  const res = url.match(IMAGES_URL_REGEXP);
  return {type: res[1] as ImageResourceType, key: res[2]};
};

export const isBase64DataImageUrl = (url: string): boolean => url && url.startsWith(IMAGE_BASE64_URL_PREFIX);

export const NO_IMAGE_DATA_URI = 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';
