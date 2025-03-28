

import { TbEditorCompleter, TbEditorCompletions } from '@shared/models/ace/completion.models';
import { widgetContextCompletions } from '@shared/models/ace/widget-completion.models';
import { entityIdHref, entityTypeHref, serviceCompletions } from '@shared/models/ace/service-completion.models';
import { CustomActionDescriptor, WidgetAction } from '@shared/models/widget.models';
import { deepClone, isDefined, isUndefined } from '@core/utils';
import customSampleJs from '!raw-loader!./custom-sample-js.raw';
import customSampleCss from '!raw-loader!./custom-sample-css.raw';
import customSampleHtml from '!raw-loader!./custom-sample-html.raw';

const customActionCompletions: TbEditorCompletions = {
  ...{
    $event: {
      meta: 'argument',
      type: 'Event',
      description: 'The DOM event that triggered this action.'
    },
    widgetContext: widgetContextCompletions.ctx,
    entityId: {
      meta: 'argument',
      type: entityIdHref,
      description: 'Id of the entity for which the action was triggered.',
      children: {
        id: {
          meta: 'property',
          type: 'string',
          description: 'UUID Id string'
        },
        entityType: {
          meta: 'property',
          type: entityTypeHref,
          description: 'Entity type'
        }
      }
    },
    entityName: {
      meta: 'argument',
      type: 'string',
      description: 'Name of the entity for which the action was triggered.'
    },
    additionalParams: {
      meta: 'argument',
      type: 'object',
      description: 'Optional object holding additional information.'
    },
    entityLabel: {
      meta: 'argument',
      type: 'string',
      description: 'Label of the entity for which the action was triggered.'
    }
  },
  ...serviceCompletions
};

const customPrettyActionCompletions: TbEditorCompletions = {
  ...{
    htmlTemplate: {
      meta: 'argument',
      type: 'string',
      description: 'HTML template used to render custom dialog.'
    }
  },
  ...customActionCompletions
};

export const toCustomAction = (action: WidgetAction): CustomActionDescriptor => {
  let result: CustomActionDescriptor;
  if (!action || (isUndefined(action.customFunction) && isUndefined(action.customHtml) && isUndefined(action.customCss))) {
    result = {
      customHtml: customSampleHtml,
      customCss: customSampleCss,
      customFunction: customSampleJs
    };
  } else {
    result = {
      customHtml: action.customHtml,
      customCss: action.customCss,
      customFunction: action.customFunction
    };
  }
  result.customResources = action && isDefined(action.customResources) ? deepClone(action.customResources) : [];
  return result;
};

export const CustomActionEditorCompleter = new TbEditorCompleter(customActionCompletions);
export const CustomPrettyActionEditorCompleter = new TbEditorCompleter(customPrettyActionCompletions);
