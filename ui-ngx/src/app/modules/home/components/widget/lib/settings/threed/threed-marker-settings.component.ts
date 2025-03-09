import { ChangeDetectorRef, Component, Input, OnInit, forwardRef } from '@angular/core';
import {
  ControlValueAccessor,
  FormBuilder,
  FormControl,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  Validators
} from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { WidgetService } from '@app/core/public-api';
import { ThreedMarkerSettings as ThreedMarkerSettings } from '@app/modules/home/components/widget/threed-view-widget/threed/threed-models';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { PageComponent } from '@shared/components/page.component';

@Component({
  selector: 'tb-threed-marker-settings',
  templateUrl: './threed-marker-settings.component.html',
  styleUrls: ['./../widget-settings.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ThreedMarkerSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ThreedMarkerSettingsComponent),
      multi: true
    }
  ]
})
export class ThreedMarkerSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @Input()
  disabled: boolean;

  functionScopeVariables = this.widgetService.getWidgetScopeVariables();
  private modelValue: ThreedMarkerSettings;
  private propagateChange = null;

  public threedMarkerSettingsFormGroup: FormGroup;

  constructor(
    protected store: Store<AppState>,
    private translate: TranslateService,
    private widgetService: WidgetService,
    private fb: FormBuilder,
    private cdRef: ChangeDetectorRef
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.threedMarkerSettingsFormGroup = this.fb.group({
      showMarker: [false, []],
      useMarkerImageFunction: [false, []],
      markerImage: [null, []],
      markerImageSize: [null, [Validators.min(1)]],
      markerImageFunction: [null, []],
      markerImages: [null, []],
    });

    this.threedMarkerSettingsFormGroup.get('showMarker').valueChanges.subscribe((value) => {
      this.updateValidators(true);
      this.cdRef.detectChanges(); 
    });

    this.threedMarkerSettingsFormGroup.get('useMarkerImageFunction').valueChanges.subscribe((value) => {
      this.updateValidators(true);
      this.cdRef.detectChanges(); 
    });

    setTimeout(() => {
      this.updateValidators(false);
      this.cdRef.detectChanges(); 
    }, 0);
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {}

  setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
    if (disabled) {
      this.threedMarkerSettingsFormGroup.disable({ emitEvent: false });
    } else {
      this.threedMarkerSettingsFormGroup.enable({ emitEvent: true });
    }
  }

  writeValue(value: ThreedMarkerSettings): void {
    this.modelValue = value;
    if (value) {
      this.threedMarkerSettingsFormGroup.patchValue(value, { emitEvent: false });
    }
    this.updateValidators(false);
  }

  public validate(c: FormControl) {
    return this.threedMarkerSettingsFormGroup.valid
      ? null
      : {
          threedMarkerSettings: {
            valid: false,
          },
        };
  }

  private updateValidators(emitEvent?: boolean): void {
    const showMarker: boolean = this.threedMarkerSettingsFormGroup.get('showMarker').value;

    if (!showMarker) {
      this.threedMarkerSettingsFormGroup.get('useMarkerImageFunction').disable({ emitEvent: false });
      this.threedMarkerSettingsFormGroup.get('markerImageFunction').disable({ emitEvent });
      this.threedMarkerSettingsFormGroup.get('markerImages').disable({ emitEvent });
      this.threedMarkerSettingsFormGroup.get('markerImage').disable({ emitEvent });
      this.threedMarkerSettingsFormGroup.get('markerImageSize').disable({ emitEvent });
    } else {
      this.threedMarkerSettingsFormGroup.get('useMarkerImageFunction').enable({ emitEvent: false });

      const useMarkerImageFunction: boolean = this.threedMarkerSettingsFormGroup.get('useMarkerImageFunction').value;
      if (useMarkerImageFunction) {
        this.threedMarkerSettingsFormGroup.get('markerImageFunction').enable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImages').enable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImage').disable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImageSize').disable({ emitEvent });
      } else {
        this.threedMarkerSettingsFormGroup.get('markerImageFunction').disable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImages').disable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImage').enable({ emitEvent });
        this.threedMarkerSettingsFormGroup.get('markerImageSize').enable({ emitEvent });
      }
    }
    this.threedMarkerSettingsFormGroup.get('markerImageFunction').updateValueAndValidity({ emitEvent: false });
    this.threedMarkerSettingsFormGroup.get('markerImages').updateValueAndValidity({ emitEvent: false });
    this.threedMarkerSettingsFormGroup.get('markerImage').updateValueAndValidity({ emitEvent: false });
    this.threedMarkerSettingsFormGroup.get('markerImageSize').updateValueAndValidity({ emitEvent: false });
  }
}
