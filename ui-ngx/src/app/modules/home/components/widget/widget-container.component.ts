import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Output,
  Renderer2,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { DashboardWidget, DashboardWidgets } from '@home/models/dashboard-component.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { SafeStyle } from '@angular/platform-browser';
import { isNotEmptyStr } from '@core/utils';
import { GridsterItemComponent } from 'angular-gridster2';
import { UtilsService } from '@core/services/utils.service';

export enum WidgetComponentActionType {
  MOUSE_DOWN,
  CLICKED,
  CONTEXT_MENU,
  EDIT,
  EXPORT,
  REMOVE
}

export class WidgetComponentAction {
  event: MouseEvent;
  actionType: WidgetComponentActionType;
}

// @dynamic
@Component({
  selector: 'tb-widget-container',
  templateUrl: './widget-container.component.html',
  styleUrls: ['./widget-container.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WidgetContainerComponent extends PageComponent implements OnInit, AfterViewInit, OnDestroy {

  @HostBinding('class')
  widgetContainerClass = 'tb-widget-container';

  @ViewChild('tbWidgetElement', {static: true})
  tbWidgetElement: ElementRef;

  @Input()
  gridsterItem: GridsterItemComponent;

  @Input()
  widget: DashboardWidget;

  @Input()
  dashboardStyle: {[klass: string]: any};

  @Input()
  backgroundImage: SafeStyle | string;

  @Input()
  isEdit: boolean;

  @Input()
  isPreview: boolean;

  @Input()
  isMobile: boolean;

  @Input()
  dashboardWidgets: DashboardWidgets;

  @Input()
  isEditActionEnabled: boolean;

  @Input()
  isExportActionEnabled: boolean;

  @Input()
  isRemoveActionEnabled: boolean;

  @Input()
  disableWidgetInteraction = false;

  @Output()
  widgetFullscreenChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  widgetComponentAction: EventEmitter<WidgetComponentAction> = new EventEmitter<WidgetComponentAction>();

  private cssClass: string;

  constructor(protected store: Store<AppState>,
              private cd: ChangeDetectorRef,
              private renderer: Renderer2,
              private utils: UtilsService) {
    super(store);
  }

  ngOnInit(): void {
    this.widget.widgetContext.containerChangeDetector = this.cd;
    const cssString = this.widget.widget.config.widgetCss;
    if (isNotEmptyStr(cssString)) {
      this.cssClass =
        this.utils.applyCssToElement(this.renderer, this.gridsterItem.el, 'tb-widget-css', cssString);
    }
  }

  ngAfterViewInit(): void {
    this.widget.widgetContext.$widgetElement = $(this.tbWidgetElement.nativeElement);
  }

  ngOnDestroy(): void {
    if (this.cssClass) {
      this.utils.clearCssElement(this.renderer, this.cssClass);
    }
  }

  isHighlighted(widget: DashboardWidget) {
    return this.dashboardWidgets.isHighlighted(widget);
  }

  isNotHighlighted(widget: DashboardWidget) {
    return this.dashboardWidgets.isNotHighlighted(widget);
  }

  onFullscreenChanged(expanded: boolean) {
    if (expanded) {
      this.renderer.addClass(this.tbWidgetElement.nativeElement, this.cssClass);
    } else {
      this.renderer.removeClass(this.tbWidgetElement.nativeElement, this.cssClass);
    }
    this.widgetFullscreenChanged.emit(expanded);
  }

  onMouseDown(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.MOUSE_DOWN
    });
  }

  onClicked(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.CLICKED
    });
  }

  onContextMenu(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.CONTEXT_MENU
    });
  }

  onEdit(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.EDIT
    });
  }

  onExport(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.EXPORT
    });
  }

  onRemove(event: MouseEvent) {
    this.widgetComponentAction.emit({
      event,
      actionType: WidgetComponentActionType.REMOVE
    });
  }

}
