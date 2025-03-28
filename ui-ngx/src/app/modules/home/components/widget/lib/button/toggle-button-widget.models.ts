

import {
  WidgetButtonAppearance,
  widgetButtonDefaultAppearance,
  WidgetButtonType
} from '@shared/components/button/widget-button.models';
import {
  DataToValueType,
  GetValueAction,
  GetValueSettings,
  SetValueAction,
  SetValueSettings,
  ValueToDataType
} from '@shared/models/action-widget-settings.models';
import { BackgroundSettings, BackgroundType } from '@shared/models/widget-settings.models';
import { AttributeScope } from '@shared/models/telemetry/telemetry.models';

export interface ToggleButtonWidgetSettings {
  initialState: GetValueSettings<boolean>;
  disabledState: GetValueSettings<boolean>;
  checkState: SetValueSettings;
  uncheckState: SetValueSettings;
  autoScale: boolean;
  horizontalFill: boolean;
  verticalFill: boolean;
  checkedAppearance: WidgetButtonAppearance;
  uncheckedAppearance: WidgetButtonAppearance;
  background: BackgroundSettings;
}

export const toggleButtonDefaultSettings: ToggleButtonWidgetSettings = {
  initialState: {
    action: GetValueAction.EXECUTE_RPC,
    defaultValue: false,
    executeRpc: {
      method: 'getState',
      requestTimeout: 5000,
      requestPersistent: false,
      persistentPollingInterval: 1000
    },
    getAttribute: {
      key: 'state',
      scope: null
    },
    getTimeSeries: {
      key: 'state'
    },
    dataToValue: {
      type: DataToValueType.NONE,
      compareToValue: true,
      dataToValueFunction: '/* Should return boolean value */\nreturn data;'
    }
  },
  disabledState: {
    action: GetValueAction.DO_NOTHING,
    defaultValue: false,
    getAttribute: {
      key: 'state',
      scope: null
    },
    getTimeSeries: {
      key: 'state'
    },
    dataToValue: {
      type: DataToValueType.NONE,
      compareToValue: true,
      dataToValueFunction: '/* Should return boolean value */\nreturn data;'
    }
  },
  checkState: {
    action: SetValueAction.EXECUTE_RPC,
    executeRpc: {
      method: 'setState',
      requestTimeout: 5000,
      requestPersistent: false,
      persistentPollingInterval: 1000
    },
    setAttribute: {
      key: 'state',
      scope: AttributeScope.SERVER_SCOPE
    },
    putTimeSeries: {
      key: 'state'
    },
    valueToData: {
      type: ValueToDataType.CONSTANT,
      constantValue: true,
      valueToDataFunction: '/* Convert input boolean value to RPC parameters or attribute/time-series value */\nreturn value;'
    }
  },
  uncheckState: {
    action: SetValueAction.EXECUTE_RPC,
    executeRpc: {
      method: 'setState',
      requestTimeout: 5000,
      requestPersistent: false,
      persistentPollingInterval: 1000
    },
    setAttribute: {
      key: 'state',
      scope: AttributeScope.SERVER_SCOPE
    },
    putTimeSeries: {
      key: 'state'
    },
    valueToData: {
      type: ValueToDataType.CONSTANT,
      constantValue: false,
      valueToDataFunction: '/* Convert input boolean value to RPC parameters or attribute/time-series value */ \n return value;'
    }
  },
  autoScale: true,
  horizontalFill: true,
  verticalFill: false,
  checkedAppearance: {...widgetButtonDefaultAppearance,
    type: WidgetButtonType.outlined, mainColor: '#198038', label: 'Opened', icon: 'mdi:lock-open-variant', borderRadius: '4px'},
  uncheckedAppearance: {...widgetButtonDefaultAppearance,
    type: WidgetButtonType.filled, mainColor: '#D12730', label: 'Closed', icon: 'lock', borderRadius: '4px'},
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
