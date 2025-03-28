

import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import { BasicActionWidgetComponent, ValueSetter } from '@home/components/widget/lib/action/action-widget.models';
import {
  singleSwitchDefaultSettings,
  SingleSwitchLayout,
  SingleSwitchWidgetSettings
} from '@home/components/widget/lib/rpc/single-switch-widget.models';
import {
  backgroundStyle,
  ComponentStyle,
  iconStyle,
  overlayStyle,
  textStyle
} from '@shared/models/widget-settings.models';
import { Observable } from 'rxjs';
import { ResizeObserver } from '@juggle/resize-observer';
import { ImagePipe } from '@shared/pipe/image.pipe';
import { DomSanitizer } from '@angular/platform-browser';
import { ValueType } from '@shared/models/constants';
import { UtilsService } from '@core/services/utils.service';

const initialSwitchHeight = 60;
const horizontalLayoutPadding = 48;
const verticalLayoutPadding = 36;

@Component({
  selector: 'tb-single-switch-widget',
  templateUrl: './single-switch-widget.component.html',
  styleUrls: ['../action/action-widget.scss', './single-switch-widget.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SingleSwitchWidgetComponent extends
  BasicActionWidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('singleSwitchPanel', {static: false})
  singleSwitchPanel: ElementRef<HTMLElement>;

  @ViewChild('singleSwitchContent', {static: false})
  singleSwitchContent: ElementRef<HTMLElement>;

  @ViewChild('singleSwitchLabelRow', {static: false})
  singleSwitchLabelRow: ElementRef<HTMLElement>;

  @ViewChild('singleSwitchToggleRow', {static: false})
  singleSwitchToggleRow: ElementRef<HTMLElement>;

  settings: SingleSwitchWidgetSettings;

  backgroundStyle$: Observable<ComponentStyle>;
  overlayStyle: ComponentStyle = {};
  overlayInset = '12px';

  value = false;
  disabled = false;

  layout: SingleSwitchLayout;

  showIcon = false;
  icon = '';
  iconStyle: ComponentStyle = {};

  showLabel = true;
  label$: Observable<string>;
  labelStyle: ComponentStyle = {};

  showOnLabel = false;
  onLabel = '';
  onLabelStyle: ComponentStyle = {};

  showOffLabel = false;
  offLabel = '';
  offLabelStyle: ComponentStyle = {};

  disabledColor = 'rgba(0, 0, 0, 0.38)';

  autoScale = false;

  private panelResize$: ResizeObserver;

  private onValueSetter: ValueSetter<boolean>;
  private offValueSetter: ValueSetter<boolean>;

  private singleSwitchCssClass: string;

  constructor(protected imagePipe: ImagePipe,
              protected sanitizer: DomSanitizer,
              private renderer: Renderer2,
              private utils: UtilsService,
              protected cd: ChangeDetectorRef,
              private elementRef: ElementRef) {
    super(cd);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.settings = {...singleSwitchDefaultSettings, ...this.ctx.settings};

    this.backgroundStyle$ = backgroundStyle(this.settings.background, this.imagePipe, this.sanitizer);
    this.overlayStyle = overlayStyle(this.settings.background.overlay);

    this.layout = this.settings.layout;

    this.autoScale = this.settings.autoScale;

    this.showLabel = this.settings.showLabel;
    this.label$ = this.ctx.registerLabelPattern(this.settings.label, this.label$);
    this.labelStyle = textStyle(this.settings.labelFont);

    this.showIcon = this.settings.showIcon;
    this.icon = this.settings.icon;
    this.iconStyle = iconStyle(this.settings.iconSize, this.settings.iconSizeUnit );

    this.showOnLabel = this.settings.showOnLabel;
    this.onLabel = this.settings.onLabel;
    this.onLabelStyle = textStyle(this.settings.onLabelFont);

    this.showOffLabel = this.settings.showOffLabel;
    this.offLabel = this.settings.offLabel;
    this.offLabelStyle = textStyle(this.settings.offLabelFont);
    const switchVariablesCss = `.tb-single-switch-panel {\n`+
                                           `--tb-single-switch-tumbler-color-on: ${this.settings.tumblerColorOn};\n`+
                                           `--tb-single-switch-tumbler-color-off: ${this.settings.tumblerColorOff};\n`+
                                           `--tb-single-switch-tumbler-color-disabled: ${this.settings.tumblerColorDisabled};\n`+
                                           `--tb-single-switch-color-on: ${this.settings.switchColorOn};\n`+
                                           `--tb-single-switch-color-off: ${this.settings.switchColorOff};\n`+
                                           `--tb-single-switch-color-disabled: ${this.settings.switchColorDisabled};\n`+
                                      `}`;
    this.singleSwitchCssClass =
      this.utils.applyCssToElement(this.renderer, this.elementRef.nativeElement, 'tb-single-switch', switchVariablesCss);

    const getInitialStateSettings =
      {...this.settings.initialState, actionLabel: this.ctx.translate.instant('widgets.rpc-state.initial-state')};
    this.createValueGetter(getInitialStateSettings, ValueType.BOOLEAN, {
      next: (value) => this.onValue(value)
    });

    const disabledStateSettings =
      {...this.settings.disabledState, actionLabel: this.ctx.translate.instant('widgets.rpc-state.disabled-state')};
    this.createValueGetter(disabledStateSettings, ValueType.BOOLEAN, {
      next: (value) => this.onDisabled(value)
    });

    const onUpdateStateSettings = {...this.settings.onUpdateState,
      actionLabel: this.ctx.translate.instant('widgets.rpc-state.turn-on')};
    this.onValueSetter = this.createValueSetter(onUpdateStateSettings);

    const offUpdateStateSettings = {...this.settings.offUpdateState,
      actionLabel: this.ctx.translate.instant('widgets.rpc-state.turn-off')};
    this.offValueSetter = this.createValueSetter(offUpdateStateSettings);
  }

  ngAfterViewInit(): void {
    if (this.autoScale) {
      this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'overflow', 'visible');
      this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'position', 'absolute');
      this.panelResize$ = new ResizeObserver(() => {
        this.onResize();
      });
      this.panelResize$.observe(this.singleSwitchPanel.nativeElement);
      if (this.showLabel) {
        this.panelResize$.observe(this.singleSwitchLabelRow.nativeElement);
      }
      this.onResize();
    }
    super.ngAfterViewInit();
  }

  ngOnDestroy() {
    if (this.panelResize$) {
      this.panelResize$.disconnect();
    }
    if (this.singleSwitchCssClass) {
      this.utils.clearCssElement(this.renderer, this.singleSwitchCssClass);
    }
    super.ngOnDestroy();
  }

  public onInit() {
    super.onInit();
    const borderRadius = this.ctx.$widgetElement.css('borderRadius');
    this.overlayStyle = {...this.overlayStyle, ...{borderRadius}};
    this.cd.detectChanges();
  }

  public onToggleChange(event: MouseEvent) {
    if (!this.ctx.isEdit && !this.ctx.isPreview) {
      event.preventDefault();
      const targetValue = this.value;
      const targetSetter = targetValue ? this.onValueSetter : this.offValueSetter;
      this.updateValue(targetSetter, targetValue, {
        next: () => this.onValue(targetValue),
        error: () => this.onValue(!targetValue)
      });
    }
  }

  private onValue(value: boolean): void {
    this.value = !!value;
    this.cd.markForCheck();
  }

  private onDisabled(value: boolean): void {
    this.disabled = !!value;
    this.cd.markForCheck();
  }

  private onResize() {
    const height = this.singleSwitchPanel.nativeElement.getBoundingClientRect().height;
    const switchScale = height / initialSwitchHeight;
    const paddingScale = Math.min(switchScale, 1);
    const panelWidth = this.singleSwitchPanel.nativeElement.getBoundingClientRect().width - (horizontalLayoutPadding * paddingScale);
    const panelHeight = this.singleSwitchPanel.nativeElement.getBoundingClientRect().height - (verticalLayoutPadding * paddingScale);
    this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'transform', `scale(1)`);
    this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'width', 'auto');
    let contentWidth = this.singleSwitchToggleRow.nativeElement.getBoundingClientRect().width;
    let contentHeight = this.singleSwitchToggleRow.nativeElement.getBoundingClientRect().height;
    if (this.showIcon || this.showLabel) {
      contentWidth += (8 + this.singleSwitchLabelRow.nativeElement.getBoundingClientRect().width);
      contentHeight = Math.max(contentHeight, this.singleSwitchLabelRow.nativeElement.getBoundingClientRect().height);
    }
    const maxScale = Math.max(1, switchScale);
    const scale = Math.min(Math.min(panelWidth / contentWidth, panelHeight / contentHeight), maxScale);
    const width = panelWidth / scale;
    this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'width', width + 'px');
    this.renderer.setStyle(this.singleSwitchContent.nativeElement, 'transform', `scale(${scale})`);
    this.overlayInset = (Math.floor(12 * paddingScale * 100) / 100) + 'px';
    this.cd.markForCheck();
  }

}
