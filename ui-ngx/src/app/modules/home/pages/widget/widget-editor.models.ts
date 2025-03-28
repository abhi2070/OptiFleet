/* eslint-disable max-len */
import { TbEditorCompleter, TbEditorCompletions } from '@shared/models/ace/completion.models';
import { widgetContextCompletions } from '@shared/models/ace/widget-completion.models';
import { serviceCompletions } from '@shared/models/ace/service-completion.models';

const widgetEditorCompletions: TbEditorCompletions = {
  ... {self: {
    description: 'Built-in variable <b>self</b> that is a reference to the widget instance',
    type: '<a href="https://optifleet.com">WidgetTypeInstance</a>',
    meta: 'object',
    children: {
      ...{
        onInit: {
          description: 'The first function which is called when widget is ready for initialization.<br>Should be used to prepare widget DOM, process widget settings and initial subscription information.',
          meta: 'function'
        },
        onDataUpdated: {
          description: 'Called when the new data is available from the widget subscription.<br>Latest data can be accessed from ' +
            'the <code>defaultSubscription</code> property of <a href="https://optifleet.com">widget context (<code>ctx</code>)</a>.',
          meta: 'function'
        },
        onResize: {
          description: 'Called when widget container is resized. Latest <code>width</code> and <code>height</code> can be obtained from <a href="https://optifleet.com">widget context (<code>ctx</code>)</a>.',
          meta: 'function'
        },
        onEditModeChanged: {
          description: 'Called when dashboard editing mode is changed. Latest mode is handled by <code>isEdit</code> property of <a href="https://optifleet.com">widget context (<code>ctx</code>)</a>.',
          meta: 'function'
        },
        onMobileModeChanged: {
          description: 'Called when dashboard view width crosses mobile breakpoint. Latest state is handled by <code>isMobile</code> property of <a href="https://optifleet.com">widget context (<code>ctx</code>)</a>.',
          meta: 'function'
        },
        onDestroy: {
          description: 'Called when widget element is destroyed. Should be used to cleanup all resources if necessary.',
          meta: 'function'
        },
        getSettingsSchema: {
          description: 'Optional function returning widget settings schema json as alternative to <b>Settings tab</b> of <a href="https://optifleet.com">Settings schema section</a>.',
          meta: 'function',
          return: {
            description: 'An widget settings schema json',
            type: 'object'
          }
        },
        getDataKeySettingsSchema: {
          description: 'Optional function returning particular data key settings schema json as alternative to <b>Data key settings schema</b> of <a href="https://optifleet.com">Settings schema section</a>.',
          meta: 'function',
          return: {
            description: 'A particular data key settings schema json',
            type: 'object'
          }
        },
        typeParameters: {
          description: 'Returns object describing widget datasource parameters.',
          meta: 'function',
          return: {
            description: 'An object describing widget datasource parameters.',
            type: '<a href="https://optifleet.com">WidgetTypeParameters</a>'
          }
        },
        actionSources: {
          description: 'Returns map describing available widget action sources used to define user actions.',
          meta: 'function',
          return: {
            description: 'A map of action sources by action source id.',
            type: '{[actionSourceId: string]: <a href="https://optifleet.com">WidgetActionSource</a>}'
          }
        }
      },
      ...widgetContextCompletions
    }
  }},
  ...widgetContextCompletions,
  ...serviceCompletions
};

export const widgetEditorCompleter = new TbEditorCompleter(widgetEditorCompletions);
