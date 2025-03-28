

import {
  Component,
  forwardRef,
  Injectable,
  Input,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewContainerRef
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ColorRange, ComponentStyle } from '@shared/models/widget-settings.models';
import { MatButton } from '@angular/material/button';
import { TbPopoverService } from '@shared/components/popover.service';
import { ColorRangePanelComponent } from '@home/components/widget/lib/settings/common/color-range-panel.component';

@Injectable()
export class ColorRangeSettingsComponentService {

  private colorSettingsComponents = new Set<ColorRangeSettingsComponent>();

  constructor() {}

  public registerColorSettingsComponent(comp: ColorRangeSettingsComponent) {
    this.colorSettingsComponents.add(comp);
  }

  public unregisterColorSettingsComponent(comp: ColorRangeSettingsComponent) {
    this.colorSettingsComponents.delete(comp);
  }

  public getOtherColorSettingsComponents(comp: ColorRangeSettingsComponent): ColorRangeSettingsComponent[] {
    const result: ColorRangeSettingsComponent[] = [];
    for (const component of this.colorSettingsComponents.values()) {
      if (component.settingsKey && component.modelValue && component !== comp) {
        result.push(component);
      }
    }
    return result;
  }

}

@Component({
  selector: 'tb-color-range-settings',
  templateUrl: './color-range-settings.component.html',
  styleUrls: [],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ColorRangeSettingsComponent),
      multi: true
    }
  ]
})
export class ColorRangeSettingsComponent implements OnInit, ControlValueAccessor, OnDestroy {

  @Input()
  disabled: boolean;

  @Input()
  settingsKey: string;

  modelValue: Array<ColorRange>;

  colorStyle: ComponentStyle = {};

  private propagateChange = null;

  constructor(private popoverService: TbPopoverService,
              private renderer: Renderer2,
              private viewContainerRef: ViewContainerRef,
              private colorSettingsComponentService: ColorRangeSettingsComponentService) {}

  ngOnInit(): void {
    this.colorSettingsComponentService.registerColorSettingsComponent(this);
  }

  ngOnDestroy() {
    this.colorSettingsComponentService.unregisterColorSettingsComponent(this);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    this.updateColorStyle();
  }

  writeValue(value: Array<ColorRange>): void {
    this.modelValue = value;
    this.updateColorStyle();
  }

  openColorRangeSettingsPopup($event: Event, matButton: MatButton) {
    if ($event) {
      $event.stopPropagation();
    }
    const trigger = matButton._elementRef.nativeElement;
    if (this.popoverService.hasPopover(trigger)) {
      this.popoverService.hidePopover(trigger);
    } else {
      const ctx: any = {
        colorRangeSettings: this.modelValue,
        settingsComponents: this.colorSettingsComponentService.getOtherColorSettingsComponents(this)
      };
      const colorRangeSettingsPanelPopover = this.popoverService.displayPopover(trigger, this.renderer,
        this.viewContainerRef, ColorRangePanelComponent, 'left', true, null,
        ctx,
        {},
        {}, {}, true);
      colorRangeSettingsPanelPopover.tbComponentRef.instance.popover = colorRangeSettingsPanelPopover;
      colorRangeSettingsPanelPopover.tbComponentRef.instance.colorRangeApplied.subscribe((colorRangeSettings) => {
        colorRangeSettingsPanelPopover.hide();
        this.modelValue = colorRangeSettings;
        this.updateColorStyle();
        this.propagateChange(this.modelValue);
      });
    }
  }

  private updateColorStyle() {
    if (!this.disabled && this.modelValue) {
      let colors: string[] = [];
      if (this.modelValue.length) {
        const rangeColors = this.modelValue.slice(0, Math.min(3, this.modelValue.length)).map(r => r.color);
        colors = colors.concat(rangeColors);
      }
      if (!colors.length) {
        this.colorStyle = {};
      } else if (colors.length === 1) {
        this.colorStyle = {backgroundColor: colors[0]};
      } else {
        const gradientValues: string[] = [];
        const step = 100 / colors.length;
        for (let i = 0; i < colors.length; i++) {
          gradientValues.push(`${colors[i]} ${step*i}%`);
          gradientValues.push(`${colors[i]} ${step*(i+1)}%`);
        }
        this.colorStyle = {background: `linear-gradient(90deg, ${gradientValues.join(', ')})`};
      }
    } else {
      this.colorStyle = {};
    }
  }

}
