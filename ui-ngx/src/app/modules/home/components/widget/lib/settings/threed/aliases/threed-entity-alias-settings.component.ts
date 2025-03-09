/* eslint-disable @angular-eslint/no-output-on-prefix */
import { Component, ElementRef, EventEmitter, forwardRef, Input, OnInit, Output, ViewChild } from '@angular/core';
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
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { IAliasController } from '@core/api/widget-api.models';
import { Observable, of } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';
import { EntityService } from '@core/http/entity.service';


export interface ThreedEntityAliasSettings {
  entityAlias: string;
}

@Component({
  selector: 'tb-threed-entity-alias-settings',
  templateUrl: './threed-entity-alias-settings.component.html',
  styleUrls: ['./../../widget-settings.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ThreedEntityAliasSettingsComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ThreedEntityAliasSettingsComponent),
      multi: true
    }
  ]
})
export class ThreedEntityAliasSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @ViewChild('entityAliasInput') entityAliasInput: ElementRef;

  @Input()
  disabled: boolean;

  @Input()
  aliasController: IAliasController;

  @Output()
  onEntityAliasChanged = new EventEmitter<string>();

  private modelValue: ThreedEntityAliasSettings;

  private propagateChange = null;

  public threedEntityAliasSettingsFormGroup: FormGroup;

  filteredEntityAliases: Observable<Array<string>>;
  aliasSearchText = '';

  private entityAliasList: Array<string> = [];

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private entityService: EntityService,
              private fb: FormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.threedEntityAliasSettingsFormGroup = this.fb.group({
      entityAlias: [null, [Validators.required]]
    });
    this.threedEntityAliasSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });

    this.filteredEntityAliases = this.threedEntityAliasSettingsFormGroup.get('entityAlias').valueChanges
      .pipe(
        tap((value) => {
          if (this.modelValue?.entityAlias !== value) {
            this.onEntityAliasChanged.emit(value);
          }
        }),
        map(value => value ? value : ''),
        mergeMap(name => this.fetchEntityAliases(name) )
      );

    if (this.aliasController) {
      const entityAliases = this.aliasController.getEntityAliases();
      for (const aliasId of Object.keys(entityAliases)) {
        this.entityAliasList.push(entityAliases[aliasId].alias);
      }
    }
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.threedEntityAliasSettingsFormGroup.disable({emitEvent: false});
    } else {
      this.threedEntityAliasSettingsFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: ThreedEntityAliasSettings): void {
    this.modelValue = value;
    this.threedEntityAliasSettingsFormGroup.patchValue(
      value, {emitEvent: false}
    );
  }

  public validate(c: FormControl) {
    return this.threedEntityAliasSettingsFormGroup.valid ? null : {
      threedEntityAliasSettings: {
        valid: false,
      },
    };
  }

  private updateModel() {
    const value: ThreedEntityAliasSettings = this.threedEntityAliasSettingsFormGroup.value;
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }

  clearEntityAlias() {
    this.threedEntityAliasSettingsFormGroup.get('entityAlias').patchValue(null, {emitEvent: true});
    setTimeout(() => {
      this.entityAliasInput.nativeElement.blur();
      this.entityAliasInput.nativeElement.focus();
    }, 0);
  }

  private fetchEntityAliases(searchText?: string): Observable<Array<string>> {
    this.aliasSearchText = searchText;
    let result = this.entityAliasList;
    if (searchText && searchText.length) {
      result = this.entityAliasList.filter((entityAlias) => entityAlias.toLowerCase().includes(searchText.toLowerCase()));
    }
    return of(result);
  }
  
  onEntityAliasFocus() {
    this.threedEntityAliasSettingsFormGroup.get('entityAlias').updateValueAndValidity({onlySelf: true, emitEvent: true});
  }

  
}