

import { Component, forwardRef, Input, OnInit, Renderer2, ViewContainerRef } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validator,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import {
  entityTypesWithoutRelatedData,
  EntityTypeVersionLoadConfig,
  exportableEntityTypes
} from '@shared/models/vc.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { EntityType, entityTypeTranslations } from '@shared/models/entity-type.models';
import { MatCheckbox } from '@angular/material/checkbox';
import { TbPopoverService } from '@shared/components/popover.service';
import { RemoveOtherEntitiesConfirmComponent } from '@home/components/vc/remove-other-entities-confirm.component';

@Component({
  selector: 'tb-entity-types-version-load',
  templateUrl: './entity-types-version-load.component.html',
  styleUrls: ['./entity-types-version.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EntityTypesVersionLoadComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => EntityTypesVersionLoadComponent),
      multi: true
    }
  ]
})
export class EntityTypesVersionLoadComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @Input()
  disabled: boolean;

  private modelValue: {[entityType: string]: EntityTypeVersionLoadConfig};

  private propagateChange = null;

  public entityTypesVersionLoadFormGroup: UntypedFormGroup;

  entityTypes = EntityType;
  entityTypesWithoutRelatedData = entityTypesWithoutRelatedData;

  loading = true;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private popoverService: TbPopoverService,
              private renderer: Renderer2,
              private viewContainerRef: ViewContainerRef,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  ngOnInit(): void {
    this.entityTypesVersionLoadFormGroup = this.fb.group({
      entityTypes: this.fb.array([], [])
    });
    this.entityTypesVersionLoadFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.entityTypesVersionLoadFormGroup.disable({emitEvent: false});
    } else {
      this.entityTypesVersionLoadFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: {[entityType: string]: EntityTypeVersionLoadConfig} | undefined): void {
    this.modelValue = value;
    this.entityTypesVersionLoadFormGroup.setControl('entityTypes',
      this.prepareEntityTypesFormArray(value), {emitEvent: false});
  }

  public validate(c: UntypedFormControl) {
    return this.entityTypesVersionLoadFormGroup.valid && this.entityTypesFormGroupArray().length ? null : {
      entityTypes: {
        valid: false,
      },
    };
  }

  private prepareEntityTypesFormArray(entityTypes: {[entityType: string]: EntityTypeVersionLoadConfig} | undefined): UntypedFormArray {
    const entityTypesControls: Array<AbstractControl> = [];
    if (entityTypes) {
      for (const entityType of Object.keys(entityTypes)) {
        const config = entityTypes[entityType];
        entityTypesControls.push(this.createEntityTypeControl(entityType as EntityType, config));
      }
    }
    return this.fb.array(entityTypesControls);
  }

  private createEntityTypeControl(entityType: EntityType, config: EntityTypeVersionLoadConfig): AbstractControl {
    const entityTypeControl = this.fb.group(
      {
        entityType: [entityType, [Validators.required]],
        config: this.fb.group({
          loadRelations: [config.loadRelations, []],
          loadAttributes: [config.loadAttributes, []],
          loadCredentials: [config.loadCredentials, []],
          removeOtherEntities: [config.removeOtherEntities, []],
          findExistingEntityByName: [config.findExistingEntityByName, []]
        })
      }
    );
    return entityTypeControl;
  }

  entityTypesFormGroupArray(): UntypedFormGroup[] {
    return (this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray).controls as UntypedFormGroup[];
  }

  entityTypesFormGroupExpanded(entityTypeControl: AbstractControl): boolean {
    return !!(entityTypeControl as any).expanded;
  }

  public trackByEntityType(index: number, entityTypeControl: AbstractControl): any {
    return entityTypeControl;
  }

  public removeEntityType(index: number) {
    (this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray).removeAt(index);
  }

  public addEnabled(): boolean {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    return entityTypesArray.length < exportableEntityTypes.length;
  }

  public addEntityType() {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    const config: EntityTypeVersionLoadConfig = {
      loadAttributes: true,
      loadRelations: true,
      loadCredentials: true,
      removeOtherEntities: false,
      findExistingEntityByName: true
    };
    const allowed = this.allowedEntityTypes();
    let entityType: EntityType = null;
    if (allowed.length) {
      entityType = allowed[0];
    }
    const entityTypeControl = this.createEntityTypeControl(entityType, config);
    (entityTypeControl as any).expanded = true;
    entityTypesArray.push(entityTypeControl);
    this.entityTypesVersionLoadFormGroup.updateValueAndValidity();
  }

  public removeAll() {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    entityTypesArray.clear();
    this.entityTypesVersionLoadFormGroup.updateValueAndValidity();
  }

  entityTypeText(entityTypeControl: AbstractControl): string {
    const entityType: EntityType = entityTypeControl.get('entityType').value;
    if (entityType) {
      return this.translate.instant(entityTypeTranslations.get(entityType).typePlural);
    } else {
      return 'Undefined';
    }
  }

  allowedEntityTypes(entityTypeControl?: AbstractControl): Array<EntityType> {
    let res = [...exportableEntityTypes];
    const currentEntityType: EntityType = entityTypeControl?.get('entityType')?.value;
    const value: [{entityType: string; config: EntityTypeVersionLoadConfig}] =
      this.entityTypesVersionLoadFormGroup.get('entityTypes').value || [];
    const usedEntityTypes = value.map(val => val.entityType).filter(val => val);
    res = res.filter(entityType => !usedEntityTypes.includes(entityType) || entityType === currentEntityType);
    return res;
  }

  onRemoveOtherEntities(removeOtherEntitiesCheckbox: MatCheckbox, entityTypeControl: AbstractControl) {
    const removeOtherEntities: boolean = entityTypeControl.get('config.removeOtherEntities').value;
    if (removeOtherEntities) {
      entityTypeControl.get('config').get('removeOtherEntities').patchValue(false, {emitEvent: true});
      const trigger = $('.mdc-checkbox__background', removeOtherEntitiesCheckbox._elementRef.nativeElement)[0];
      if (this.popoverService.hasPopover(trigger)) {
        this.popoverService.hidePopover(trigger);
      } else {
        const removeOtherEntitiesConfirmPopover = this.popoverService.displayPopover(trigger, this.renderer,
          this.viewContainerRef, RemoveOtherEntitiesConfirmComponent, 'bottom', true, null,
          {
            onClose: (result: boolean | null) => {
              removeOtherEntitiesConfirmPopover.hide();
              if (result) {
                entityTypeControl.get('config').get('removeOtherEntities').patchValue(true, {emitEvent: true});
              }
            }
          }, {}, {}, {}, false);
      }
    }
  }

  private updateModel() {
    const value: [{entityType: string; config: EntityTypeVersionLoadConfig}] =
      this.entityTypesVersionLoadFormGroup.get('entityTypes').value || [];
    let modelValue: {[entityType: string]: EntityTypeVersionLoadConfig} = null;
    if (value && value.length) {
      modelValue = {};
      value.forEach((val) => {
        modelValue[val.entityType] = val.config;
      });
    }
    this.modelValue = modelValue;
    this.propagateChange(this.modelValue);
  }
}
