

import { BackgroundSettings, BackgroundType, customDateFormat, Font } from '@shared/models/widget-settings.models';
import { LegendPosition } from '@shared/models/widget.models';
import { EChartsTooltipWidgetSettings } from '@home/components/widget/lib/chart/echarts-widget.models';

export interface BarChartWithLabelsWidgetSettings extends EChartsTooltipWidgetSettings {
  showBarLabel: boolean;
  barLabelFont: Font;
  barLabelColor: string;
  showBarValue: boolean;
  barValueFont: Font;
  barValueColor: string;
  showLegend: boolean;
  legendPosition: LegendPosition;
  legendLabelFont: Font;
  legendLabelColor: string;
  background: BackgroundSettings;
}

export const barChartWithLabelsDefaultSettings: BarChartWithLabelsWidgetSettings = {
  showBarLabel: true,
  barLabelFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '400',
    lineHeight: '12px'
  },
  barLabelColor: 'rgba(0, 0, 0, 0.54)',
  showBarValue: true,
  barValueFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '700',
    lineHeight: '12px'
  },
  barValueColor: 'rgba(0, 0, 0, 0.76)',
  showLegend: true,
  legendPosition: LegendPosition.top,
  legendLabelFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '400',
    lineHeight: '16px'
  },
  legendLabelColor: 'rgba(0, 0, 0, 0.76)',
  showTooltip: true,
  tooltipValueFont: {
    family: 'Roboto',
    size: 12,
    sizeUnit: 'px',
    style: 'normal',
    weight: '500',
    lineHeight: '16px'
  },
  tooltipValueColor: 'rgba(0, 0, 0, 0.76)',
  tooltipShowDate: true,
  tooltipDateFormat: customDateFormat('MMMM y'),
  tooltipDateFont: {
    family: 'Roboto',
    size: 11,
    sizeUnit: 'px',
    style: 'normal',
    weight: '400',
    lineHeight: '16px'
  },
  tooltipDateColor: 'rgba(0, 0, 0, 0.76)',
  tooltipBackgroundColor: 'rgba(255, 255, 255, 0.76)',
  tooltipBackgroundBlur: 4,
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
