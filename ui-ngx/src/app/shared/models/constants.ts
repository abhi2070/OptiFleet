import { InjectionToken } from '@angular/core';
import { IModulesMap } from '@modules/common/modules-map.models';
import { EntityType } from '@shared/models/entity-type.models';

export const Constants = {
  serverErrorCode: {
    general: 2,
    authentication: 10,
    jwtTokenExpired: 11,
    tenantTrialExpired: 12,
    credentialsExpired: 15,
    permissionDenied: 20,
    invalidArguments: 30,
    badRequestParams: 31,
    itemNotFound: 32,
    tooManyRequests: 33,
    tooManyUpdates: 34,
    passwordViolation: 45
  },
  entryPoints: {
    login: '/api/auth/login',
    tokenRefresh: '/api/auth/token',
    nonTokenBased: '/api/noauth'
  }
};

export const serverErrorCodesTranslations = new Map<number, string>([
  [Constants.serverErrorCode.general, 'server-error.general'],
  [Constants.serverErrorCode.authentication, 'server-error.authentication'],
  [Constants.serverErrorCode.jwtTokenExpired, 'server-error.jwt-token-expired'],
  [Constants.serverErrorCode.tenantTrialExpired, 'server-error.tenant-trial-expired'],
  [Constants.serverErrorCode.credentialsExpired, 'server-error.credentials-expired'],
  [Constants.serverErrorCode.permissionDenied, 'server-error.permission-denied'],
  [Constants.serverErrorCode.invalidArguments, 'server-error.invalid-arguments'],
  [Constants.serverErrorCode.badRequestParams, 'server-error.bad-request-params'],
  [Constants.serverErrorCode.itemNotFound, 'server-error.item-not-found'],
  [Constants.serverErrorCode.tooManyRequests, 'server-error.too-many-requests'],
  [Constants.serverErrorCode.tooManyUpdates, 'server-error.too-many-updates'],
]);

export const MediaBreakpoints = {
  xs: 'screen and (max-width: 599px)',
  sm: 'screen and (min-width: 600px) and (max-width: 959px)',
  md: 'screen and (min-width: 960px) and (max-width: 1279px)',
  lg: 'screen and (min-width: 1280px) and (max-width: 1919px)',
  xl: 'screen and (min-width: 1920px) and (max-width: 5000px)',
  'lt-sm': 'screen and (max-width: 599px)',
  'lt-md': 'screen and (max-width: 959px)',
  'lt-lg': 'screen and (max-width: 1279px)',
  'lt-xl': 'screen and (max-width: 1919px)',
  'gt-xs': 'screen and (min-width: 600px)',
  'gt-sm': 'screen and (min-width: 960px)',
  'gt-md': 'screen and (min-width: 1280px)',
  'gt-lg': 'screen and (min-width: 1920px)',
  'gt-xl': 'screen and (min-width: 5001px)',
  'md-lg': 'screen and (min-width: 960px) and (max-width: 1819px)'
};

export const resolveBreakpoint = (breakpoint: string): string => {
  if (MediaBreakpoints[breakpoint]) {
    return MediaBreakpoints[breakpoint];
  }
  return breakpoint;
};

export const helpBaseUrl = 'https://optifleet.com';

export const HelpLinks = {
  linksMap: {
    outgoingMailSettings: helpBaseUrl + '',
    smsProviderSettings: helpBaseUrl + '',
    slackSettings: helpBaseUrl + '',
    securitySettings: helpBaseUrl + '',
    oauth2Settings: helpBaseUrl + '',
    twoFactorAuthSettings: helpBaseUrl + '',
    ruleEngine: helpBaseUrl + '',
    ruleNodeCheckRelation: helpBaseUrl + '',
    ruleNodeCheckExistenceFields: helpBaseUrl + '',
    ruleNodeGpsGeofencingFilter: helpBaseUrl + '',
    ruleNodeJsFilter: helpBaseUrl + '',
    ruleNodeJsSwitch: helpBaseUrl + '',
    ruleNodeAssetProfileSwitch: helpBaseUrl + '',
    ruleNodeDeviceProfileSwitch: helpBaseUrl + '',
    ruleNodeCheckAlarmStatus: helpBaseUrl + '',
    ruleNodeMessageTypeFilter: helpBaseUrl + '',
    ruleNodeMessageTypeSwitch: helpBaseUrl + '',
    ruleNodeOriginatorTypeFilter: helpBaseUrl + '',
    ruleNodeOriginatorTypeSwitch: helpBaseUrl + '',
    ruleNodeOriginatorAttributes: helpBaseUrl + '',
    ruleNodeOriginatorFields: helpBaseUrl + '',
    ruleNodeOriginatorTelemetry: helpBaseUrl + '',
    ruleNodeCustomerAttributes: helpBaseUrl + '',
    ruleNodeCustomerDetails: helpBaseUrl + '',
    ruleNodeDeviceAttributes: helpBaseUrl + '',
    ruleNodeRelatedAttributes: helpBaseUrl + '',
    ruleNodeTenantAttributes: helpBaseUrl + '',
    ruleNodeTenantDetails: helpBaseUrl + '',
    ruleNodeChangeOriginator: helpBaseUrl + '',
    ruleNodeTransformMsg: helpBaseUrl + '',
    ruleNodeMsgToEmail: helpBaseUrl + '',
    ruleNodeAssignToCustomer: helpBaseUrl + '',
    ruleNodeUnassignFromCustomer: helpBaseUrl + '',
    ruleNodeClearAlarm: helpBaseUrl + '',
    ruleNodeCreateAlarm: helpBaseUrl + '',
    ruleNodeCreateRelation: helpBaseUrl + '',
    ruleNodeDeleteRelation: helpBaseUrl + '',
    ruleNodeMsgDelay: helpBaseUrl + '',
    ruleNodeMsgGenerator: helpBaseUrl + '',
    ruleNodeGpsGeofencingEvents: helpBaseUrl + '',
    ruleNodeLog: helpBaseUrl + '',
    ruleNodeRpcCallReply: helpBaseUrl + '',
    ruleNodeRpcCallRequest: helpBaseUrl + '',
    ruleNodeSaveAttributes: helpBaseUrl + '',
    ruleNodeSaveTimeseries: helpBaseUrl + '',
    ruleNodeSaveToCustomTable: helpBaseUrl + '',
    ruleNodeRuleChain: helpBaseUrl + '',
    ruleNodeOutputNode: helpBaseUrl + '',
    ruleNodeAwsSns: helpBaseUrl + '',
    ruleNodeAwsSqs: helpBaseUrl + '',
    ruleNodeKafka: helpBaseUrl + '',
    ruleNodeMqtt: helpBaseUrl + '',
    ruleNodeAzureIotHub: helpBaseUrl + '',
    ruleNodeRabbitMq: helpBaseUrl + '',
    ruleNodeRestApiCall: helpBaseUrl + '',
    ruleNodeSendEmail: helpBaseUrl + '',
    ruleNodeSendSms: helpBaseUrl + '',
    ruleNodeMath: helpBaseUrl + '',
    ruleNodeCalculateDelta: helpBaseUrl + '',
    tenants: helpBaseUrl + '',
    tenantProfiles: helpBaseUrl + '',
    customers: helpBaseUrl + '',
    users: helpBaseUrl + '',
    devices: helpBaseUrl + '',
    deviceProfiles: helpBaseUrl + '',
    assetProfiles: helpBaseUrl + '',
    edges: helpBaseUrl + '',
    assets: helpBaseUrl + '',
    entityViews: helpBaseUrl + '',
    entitiesImport: helpBaseUrl + '',
    rulechains: helpBaseUrl + '',
    lwm2mResourceLibrary: helpBaseUrl + '',
    dashboards: helpBaseUrl + '',
    otaUpdates: helpBaseUrl + '',
    widgetTypes: helpBaseUrl + '',
    widgetsBundles: helpBaseUrl + '',
    widgetsConfig:  helpBaseUrl + '',
    widgetsConfigTimeseries:  helpBaseUrl + '',
    widgetsConfigLatest: helpBaseUrl +  '',
    widgetsConfigRpc: helpBaseUrl +  '',
    widgetsConfigAlarm: helpBaseUrl +  '',
    widgetsConfigStatic: helpBaseUrl +  '',
    ruleNodePushToCloud: helpBaseUrl + '',
    ruleNodePushToEdge: helpBaseUrl + '',
    queue: helpBaseUrl + '',
    repositorySettings: helpBaseUrl + '',
    autoCommitSettings: helpBaseUrl + '',
    twoFactorAuthentication: helpBaseUrl + '',
    sentNotification: helpBaseUrl + '',
    templateNotifications: helpBaseUrl + '',
    recipientNotifications: helpBaseUrl + '',
    ruleNotifications: helpBaseUrl + '',
    jwtSecuritySettings: helpBaseUrl + '',
    gatewayInstall: helpBaseUrl + '',
  }
};

export interface ValueTypeData {
  name: string;
  icon: string;
}

export enum ValueType {
  STRING = 'STRING',
  INTEGER = 'INTEGER',
  DOUBLE = 'DOUBLE',
  BOOLEAN = 'BOOLEAN',
  JSON = 'JSON'
}

export enum DataType {
  STRING = 'STRING',
  LONG = 'LONG',
  BOOLEAN = 'BOOLEAN',
  DOUBLE = 'DOUBLE',
  JSON = 'JSON'
}

export const DataTypeTranslationMap = new Map([
  [DataType.STRING, 'value.string'],
  [DataType.LONG, 'value.integer'],
  [DataType.BOOLEAN, 'value.boolean'],
  [DataType.DOUBLE, 'value.double'],
  [DataType.JSON, 'value.json']
]);

export const valueTypesMap = new Map<ValueType, ValueTypeData>(
  [
    [
      ValueType.STRING,
      {
        name: 'value.string',
        icon: 'mdi:format-text'
      }
    ],
    [
      ValueType.INTEGER,
      {
        name: 'value.integer',
        icon: 'mdi:numeric'
      }
    ],
    [
      ValueType.DOUBLE,
      {
        name: 'value.double',
        icon: 'mdi:numeric'
      }
    ],
    [
      ValueType.BOOLEAN,
      {
        name: 'value.boolean',
        icon: 'mdi:checkbox-marked-outline'
      }
    ],
    [
      ValueType.JSON,
      {
        name: 'value.json',
        icon: 'mdi:code-json'
      }
    ]
  ]
);

export interface ContentTypeData {
  name: string;
  code: string;
}

export enum ContentType {
  JSON = 'JSON',
  TEXT = 'TEXT',
  BINARY = 'BINARY'
}

export const contentTypesMap = new Map<ContentType, ContentTypeData>(
  [
    [
      ContentType.JSON,
      {
        name: 'content-type.json',
        code: 'json'
      }
    ],
    [
      ContentType.TEXT,
      {
        name: 'content-type.text',
        code: 'text'
      }
    ],
    [
      ContentType.BINARY,
      {
        name: 'content-type.binary',
        code: 'text'
      }
    ]
  ]
);

export const hidePageSizePixelValue = 550;
export const customTranslationsPrefix = 'custom.';
export const i18nPrefix = 'i18n';

export const MODULES_MAP = new InjectionToken<IModulesMap>('ModulesMap');
