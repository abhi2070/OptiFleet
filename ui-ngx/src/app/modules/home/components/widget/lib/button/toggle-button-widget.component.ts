

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { BasicActionWidgetComponent, ValueSetter } from '@home/components/widget/lib/action/action-widget.models';
import { ImagePipe } from '@shared/pipe/image.pipe';
import { DomSanitizer } from '@angular/platform-browser';
import { ValueType } from '@shared/models/constants';
import { WidgetButtonAppearance } from '@shared/components/button/widget-button.models';
import {
  toggleButtonDefaultSettings,
  ToggleButtonWidgetSettings
} from '@home/components/widget/lib/button/toggle-button-widget.models';
import { Observable } from 'rxjs';
import { backgroundStyle, ComponentStyle, overlayStyle } from '@shared/models/widget-settings.models';

@Component({
  selector: 'tb-toggle-button-widget',
  templateUrl: './toggle-button-widget.component.html',
  styleUrls: ['../action/action-widget.scss', './toggle-button-widget.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ToggleButtonWidgetComponent extends
  BasicActionWidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  settings: ToggleButtonWidgetSettings;

  backgroundStyle$: Observable<ComponentStyle>;
  overlayStyle: ComponentStyle = {};

  value = false;
  disabled = false;

  autoScale: boolean;
  horizontalFill: boolean;
  verticalFill: boolean;
  appearance: WidgetButtonAppearance;

  private checkValueSetter: ValueSetter<boolean>;
  private uncheckValueSetter: ValueSetter<boolean>;

  constructor(protected imagePipe: ImagePipe,
              protected sanitizer: DomSanitizer,
              protected cd: ChangeDetectorRef) {
    super(cd);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.settings = {...toggleButtonDefaultSettings, ...this.ctx.settings};

    this.autoScale = this.settings.autoScale;
    this.horizontalFill = this.settings.horizontalFill;
    this.verticalFill = this.settings.verticalFill;

    this.backgroundStyle$ = backgroundStyle(this.settings.background, this.imagePipe, this.sanitizer);
    this.overlayStyle = overlayStyle(this.settings.background.overlay);

    const getInitialStateSettings =
      {...this.settings.initialState, actionLabel: this.ctx.translate.instant('widgets.rpc-state.initial-state')};
    this.createValueGetter(getInitialStateSettings, ValueType.BOOLEAN, {
      next: (value) => this.onValue(value)
    });

    const disabledStateSettings =
      {...this.settings.disabledState, actionLabel: this.ctx.translate.instant('widgets.button-state.disabled-state')};
    this.createValueGetter(disabledStateSettings, ValueType.BOOLEAN, {
      next: (value) => this.onDisabled(value)
    });

    const checkStateSettings = {...this.settings.checkState,
      actionLabel: this.ctx.translate.instant('widgets.toggle-button.check')};
    this.checkValueSetter = this.createValueSetter(checkStateSettings);

    const uncheckStateSettings = {...this.settings.uncheckState,
      actionLabel: this.ctx.translate.instant('widgets.toggle-button.uncheck')};
    this.uncheckValueSetter = this.createValueSetter(uncheckStateSettings);

    this.appearance = this.value ? this.settings.checkedAppearance : this.settings.uncheckedAppearance;
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  ngOnDestroy() {
    super.ngOnDestroy();
  }

  public onInit() {
    super.onInit();
    const borderRadius = this.ctx.$widgetElement.css('borderRadius');
    this.overlayStyle = {...this.overlayStyle, ...{borderRadius}};
    this.cd.detectChanges();
  }

  private onValue(value: boolean): void {
    const newValue = !!value;
    if (this.value !== newValue) {
      this.value = newValue;
      this.appearance = this.value ? this.settings.checkedAppearance : this.settings.uncheckedAppearance;
      this.cd.markForCheck();
    }
  }

  private onDisabled(value: boolean): void {
    const newDisabled = !!value;
    if (this.disabled !== newDisabled) {
      this.disabled = newDisabled;
      this.cd.markForCheck();
    }
  }

  public onClick(_$event: MouseEvent) {
    if (!this.ctx.isEdit && !this.ctx.isPreview) {
      this.onValue(!this.value);
      const targetValue = this.value;
      const targetSetter = targetValue ? this.checkValueSetter : this.uncheckValueSetter;
      this.updateValue(targetSetter, targetValue, {
        next: () => {
          this.onValue(targetValue);
        },
        error: () => {
          this.onValue(!targetValue);
        }
      });
    }
  }
}
