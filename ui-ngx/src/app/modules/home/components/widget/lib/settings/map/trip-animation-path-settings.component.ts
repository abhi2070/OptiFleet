

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator, Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import {
  PolylineDecoratorSymbol,
  polylineDecoratorSymbolTranslationMap,
  PolylineSettings
} from '@home/components/widget/lib/maps/map-models';
import { WidgetService } from '@core/http/widget.service';

@Component({
  selector: 'tb-trip-animation-path-settings',
  templateUrl: './trip-animation-path-settings.component.html',
  styleUrls: ['./../widget-settings.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TripAnimationPathSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => TripAnimationPathSettingsComponent),
      multi: true
    }
  ]
})
export class TripAnimationPathSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @Input()
  disabled: boolean;

  functionScopeVariables = this.widgetService.getWidgetScopeVariables();

  private modelValue: PolylineSettings;

  private propagateChange = null;

  public tripAnimationPathSettingsFormGroup: UntypedFormGroup;

  polylineDecoratorSymbols = Object.values(PolylineDecoratorSymbol);

  polylineDecoratorSymbolTranslations = polylineDecoratorSymbolTranslationMap;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private widgetService: WidgetService,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.tripAnimationPathSettingsFormGroup = this.fb.group({
      color: [null, []],
      strokeWeight: [null, [Validators.min(0)]],
      strokeOpacity: [null, [Validators.min(0), Validators.max(1)]],
      useColorFunction: [null, []],
      colorFunction: [null, []],
      usePolylineDecorator: [null, []],
      decoratorSymbol: [null, []],
      decoratorSymbolSize: [null, [Validators.min(1)]],
      useDecoratorCustomColor: [null, []],
      decoratorCustomColor: [null, []],
      decoratorOffset: [null, []],
      endDecoratorOffset: [null, []],
      decoratorRepeat: [null, []],
    });
    this.tripAnimationPathSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
    this.tripAnimationPathSettingsFormGroup.get('useColorFunction').valueChanges.subscribe(() => {
      this.updateValidators(true);
    });
    this.tripAnimationPathSettingsFormGroup.get('usePolylineDecorator').valueChanges.subscribe(() => {
      this.updateValidators(true);
    });
    this.tripAnimationPathSettingsFormGroup.get('useDecoratorCustomColor').valueChanges.subscribe(() => {
      this.updateValidators(true);
    });
    this.updateValidators(false);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.tripAnimationPathSettingsFormGroup.disable({emitEvent: false});
    } else {
      this.tripAnimationPathSettingsFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: PolylineSettings): void {
    this.modelValue = value;
    this.tripAnimationPathSettingsFormGroup.patchValue(
      value, {emitEvent: false}
    );
    this.updateValidators(false);
  }

  public validate(c: UntypedFormControl) {
    return this.tripAnimationPathSettingsFormGroup.valid ? null : {
      tripAnimationPathSettings: {
        valid: false,
      },
    };
  }

  private updateModel() {
    const value: PolylineSettings = this.tripAnimationPathSettingsFormGroup.value;
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }

  private updateValidators(emitEvent?: boolean): void {
    const useColorFunction: boolean = this.tripAnimationPathSettingsFormGroup.get('useColorFunction').value;
    const usePolylineDecorator: boolean = this.tripAnimationPathSettingsFormGroup.get('usePolylineDecorator').value;
    const useDecoratorCustomColor: boolean = this.tripAnimationPathSettingsFormGroup.get('useDecoratorCustomColor').value;
    if (useColorFunction) {
      this.tripAnimationPathSettingsFormGroup.get('colorFunction').enable({emitEvent});
    } else {
      this.tripAnimationPathSettingsFormGroup.get('colorFunction').disable({emitEvent});
    }
    if (usePolylineDecorator) {
      this.tripAnimationPathSettingsFormGroup.get('decoratorSymbol').enable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('decoratorSymbolSize').enable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('useDecoratorCustomColor').enable({emitEvent: false});
      if (useDecoratorCustomColor) {
        this.tripAnimationPathSettingsFormGroup.get('decoratorCustomColor').enable({emitEvent});
      } else {
        this.tripAnimationPathSettingsFormGroup.get('decoratorCustomColor').disable({emitEvent});
      }
      this.tripAnimationPathSettingsFormGroup.get('decoratorOffset').enable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('endDecoratorOffset').enable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('decoratorRepeat').enable({emitEvent});
    } else {
      this.tripAnimationPathSettingsFormGroup.get('decoratorSymbol').disable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('decoratorSymbolSize').disable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('useDecoratorCustomColor').disable({emitEvent: false});
      this.tripAnimationPathSettingsFormGroup.get('decoratorCustomColor').disable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('decoratorOffset').disable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('endDecoratorOffset').disable({emitEvent});
      this.tripAnimationPathSettingsFormGroup.get('decoratorRepeat').disable({emitEvent});
    }
    this.tripAnimationPathSettingsFormGroup.get('colorFunction').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('decoratorSymbol').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('decoratorSymbolSize').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('useDecoratorCustomColor').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('decoratorCustomColor').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('decoratorOffset').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('endDecoratorOffset').updateValueAndValidity({emitEvent: false});
    this.tripAnimationPathSettingsFormGroup.get('decoratorRepeat').updateValueAndValidity({emitEvent: false});
  }
}
