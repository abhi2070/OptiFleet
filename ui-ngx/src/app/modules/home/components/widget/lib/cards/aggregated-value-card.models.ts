

import {
  BackgroundSettings,
  BackgroundType,
  ColorProcessor,
  ColorSettings,
  ColorType,
  ComponentStyle,
  constantColor,
  DateFormatSettings,
  Font,
  lastUpdateAgoDateFormat,
  textStyle
} from '@shared/models/widget-settings.models';
import { ComparisonResultType, DataEntry, DataKey, DatasourceData } from '@shared/models/widget.models';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';
import { AggregationType } from '@shared/models/time/time.models';

export interface AggregatedValueCardWidgetSettings {
  autoScale: boolean;
  showSubtitle: boolean;
  subtitle: string;
  subtitleFont: Font;
  subtitleColor: string;
  showDate: boolean;
  dateFormat: DateFormatSettings;
  dateFont: Font;
  dateColor: string;
  showChart: boolean;
  background: BackgroundSettings;
}

export enum AggregatedValueCardKeyPosition {
  center = 'center',
  rightTop = 'rightTop',
  rightBottom = 'rightBottom',
  leftTop = 'leftTop',
  leftBottom = 'leftBottom'
}

export const aggregatedValueCardKeyPositionTranslations = new Map<AggregatedValueCardKeyPosition, string>(
  [
    [AggregatedValueCardKeyPosition.center, 'widgets.aggregated-value-card.position-center'],
    [AggregatedValueCardKeyPosition.rightTop, 'widgets.aggregated-value-card.position-right-top'],
    [AggregatedValueCardKeyPosition.rightBottom, 'widgets.aggregated-value-card.position-right-bottom'],
    [AggregatedValueCardKeyPosition.leftTop, 'widgets.aggregated-value-card.position-left-top'],
    [AggregatedValueCardKeyPosition.leftBottom, 'widgets.aggregated-value-card.position-left-bottom']
  ]
);

export interface AggregatedValueCardKeySettings {
  position: AggregatedValueCardKeyPosition;
  font: Font;
  color: ColorSettings;
  showArrow: boolean;
}

export interface AggregatedValueCardValue {
  key: DataKey;
  value: string;
  units: string;
  style: ComponentStyle;
  color: ColorProcessor;
  center: boolean;
  showArrow: boolean;
  upArrow: boolean;
  downArrow: boolean;
}

export const computeAggregatedCardValue =
  (dataKeys: DataKey[], keyName: string, position: AggregatedValueCardKeyPosition): AggregatedValueCardValue => {
  const key = dataKeys.find(dataKey => ( dataKey.name === keyName && (dataKey.settings?.position === position ||
                                         (!dataKey.settings?.position && position === AggregatedValueCardKeyPosition.center)) ));
  if (key) {
    const settings: AggregatedValueCardKeySettings = key.settings;
    return {
      key,
      value: '',
      units: key.units,
      style: textStyle(settings.font),
      color: ColorProcessor.fromSettings(settings.color),
      center: position === AggregatedValueCardKeyPosition.center,
      showArrow: settings.showArrow,
      upArrow: false,
      downArrow: false
    };
  }
};

export const getTsValueByLatestDataKey = (latestData: Array<DatasourceData>, dataKey: DataKey): DataEntry => {
  if (latestData?.length) {
    const dsData = latestData.find(data => data.dataKey === dataKey);
    if (dsData?.data?.length) {
      return dsData.data[0];
    }
  }
  return null;
};

export const aggregatedValueCardDefaultSettings: AggregatedValueCardWidgetSettings = {
  autoScale: true,
  showSubtitle: true,
  subtitle: '${entityName}',
  subtitleFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '400',
    lineHeight: '16px'
  },
  subtitleColor: 'rgba(0, 0, 0, 0.38)',
  showDate: true,
  dateFormat: lastUpdateAgoDateFormat(),
  dateFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '400',
    lineHeight: '16px'
  },
  dateColor: 'rgba(0, 0, 0, 0.38)',
  showChart: true,
  background: {
    type: BackgroundType.color,
    color: '#fff',
    overlay: {
      enabled: false,
      color: 'rgba(255,255,255,0.72)',
      blur: 3
    }
  }
};

export const aggregatedValueCardDefaultKeySettings: AggregatedValueCardKeySettings = {
  position: AggregatedValueCardKeyPosition.center,
  font: {
    family: 'Roboto',
    size: 14,
    sizeUnit: 'px',
    style: 'normal',
    weight: '500',
    lineHeight: '1'
  },
  color: constantColor('rgba(0, 0, 0, 0.87)'),
  showArrow: false
};
