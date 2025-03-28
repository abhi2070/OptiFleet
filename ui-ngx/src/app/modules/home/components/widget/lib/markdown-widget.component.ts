

import { ChangeDetectorRef, Component, Inject, Input, OnInit, Type } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { WidgetContext } from '@home/models/widget-component.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DatasourceData, FormattedData } from '@shared/models/widget.models';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';
import {
  createLabelFromPattern,
  flatDataWithoutOverride,
  formattedDataFormDatasourceData,
  hashCode,
  isDefinedAndNotNull,
  isNotEmptyStr,
  parseFunction,
  safeExecute
} from '@core/utils';
import cssjs from '@core/css/css';
import { UtilsService } from '@core/services/utils.service';
import { HOME_COMPONENTS_MODULE_TOKEN, WIDGET_COMPONENTS_MODULE_TOKEN } from '@home/components/tokens';
import { EntityDataPageLink } from '@shared/models/query/query.models';

interface MarkdownWidgetSettings {
  markdownTextPattern: string;
  useMarkdownTextFunction: boolean;
  markdownTextFunction: string;
  applyDefaultMarkdownStyle: boolean;
  markdownCss: string;
}

type MarkdownTextFunction = (data: FormattedData[], ctx: WidgetContext) => string;

@Component({
  selector: 'tb-markdown-widget',
  templateUrl: './markdown-widget.component.html'
})
export class MarkdownWidgetComponent extends PageComponent implements OnInit {

  settings: MarkdownWidgetSettings;
  markdownTextFunction: MarkdownTextFunction;

  markdownClass: string;

  @Input()
  ctx: WidgetContext;

  markdownText: string;

  additionalStyles: string[];

  applyDefaultMarkdownStyle = true;

  constructor(protected store: Store<AppState>,
              private utils: UtilsService,
              @Inject(HOME_COMPONENTS_MODULE_TOKEN) public homeComponentsModule: Type<any>,
              @Inject(WIDGET_COMPONENTS_MODULE_TOKEN) public widgetComponentsModule: Type<any>,
              private cd: ChangeDetectorRef) {
    super(store);
  }

  ngOnInit(): void {
    this.ctx.$scope.markdownWidget = this;
    this.settings = this.ctx.settings;
    this.markdownTextFunction = this.settings.useMarkdownTextFunction ?
      parseFunction(this.settings.markdownTextFunction, ['data', 'ctx']) : null;
    let cssString = this.settings.markdownCss;
    if (isNotEmptyStr(cssString)) {
      const cssParser = new cssjs();
      this.markdownClass = 'markdown-widget-' + hashCode(cssString);
      cssParser.cssPreviewNamespace = this.markdownClass;
      cssParser.testMode = false;
      cssString = cssParser.applyNamespacing(cssString);
      cssString = cssParser.getCSSForEditor(cssString);
      this.additionalStyles = [cssString];
    }
    if (isDefinedAndNotNull(this.settings.applyDefaultMarkdownStyle)) {
      this.applyDefaultMarkdownStyle = this.settings.applyDefaultMarkdownStyle;
    }
    const pageSize = isDefinedAndNotNull(this.ctx.widgetConfig.pageSize) &&
                      this.ctx.widgetConfig.pageSize > 0 ? this.ctx.widgetConfig.pageSize : 16384;
    const pageLink: EntityDataPageLink = {
      page: 0,
      pageSize,
      textSearch: null,
      dynamic: true
    };
    if (this.ctx.widgetConfig.datasources.length) {
      this.ctx.defaultSubscription.subscribeAllForPaginatedData(pageLink, null);
    } else {
      this.onDataUpdated();
    }
  }

  public onDataUpdated() {
    let initialData: DatasourceData[];
    if (this.ctx.data?.length) {
      initialData = this.ctx.data;
    } else if (this.ctx.datasources?.length) {
      initialData = [
        {
          datasource: this.ctx.datasources[0],
          dataKey: {
            type: DataKeyType.attribute,
            name: 'empty'
          },
          data: []
        }
      ];
    } else {
      initialData = [];
    }
    const data = formattedDataFormDatasourceData(initialData);
    let markdownText = this.settings.useMarkdownTextFunction ?
      safeExecute(this.markdownTextFunction, [data, this.ctx]) : this.settings.markdownTextPattern;
    const allData: FormattedData = flatDataWithoutOverride(data);
    markdownText = createLabelFromPattern(markdownText, allData);
    if (this.markdownText !== markdownText) {
      this.markdownText = this.utils.customTranslation(markdownText, markdownText);
      this.cd.detectChanges();
    }
  }

  markdownClick($event: MouseEvent) {
    this.ctx.actionsApi.elementClick($event);
  }

}
