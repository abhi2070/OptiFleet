

import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EntityComponent } from '@home/components/entity/entity.component';
import {
  Resource,
  ResourceType,
  ResourceTypeExtension,
  ResourceTypeMIMETypes,
  ResourceTypeTranslationMap
} from '@shared/models/resource.models';
import { filter, startWith, takeUntil } from 'rxjs/operators';
import { ActionNotificationShow } from '@core/notification/notification.actions';
import { isDefinedAndNotNull } from '@core/utils';
import { getCurrentAuthState } from '@core/auth/auth.selectors';

@Component({
  selector: 'tb-resources-library',
  templateUrl: './resources-library.component.html'
})
export class ResourcesLibraryComponent extends EntityComponent<Resource> implements OnInit, OnDestroy {

  readonly resourceType = ResourceType;
  readonly resourceTypes: ResourceType[] = Object.values(this.resourceType);
  readonly resourceTypesTranslationMap = ResourceTypeTranslationMap;

  maxResourceSize = getCurrentAuthState(this.store).maxResourceSize;

  private destroy$ = new Subject<void>();

  constructor(protected store: Store<AppState>,
              protected translate: TranslateService,
              @Inject('entity') protected entityValue: Resource,
              @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<Resource>,
              public fb: FormBuilder,
              protected cd: ChangeDetectorRef) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  ngOnInit() {
    super.ngOnInit();
    this.entityForm.get('resourceType').valueChanges.pipe(
      startWith(ResourceType.JS_MODULE),
      filter(() => this.isAdd),
      takeUntil(this.destroy$)
    ).subscribe((type) => {
      if (type === this.resourceType.LWM2M_MODEL) {
        this.entityForm.get('title').disable({emitEvent: false});
        this.entityForm.patchValue({title: ''}, {emitEvent: false});
      } else {
        this.entityForm.get('title').enable({emitEvent: false});
      }
      this.entityForm.patchValue({
        data: null,
        fileName: null
      }, {emitEvent: false});
    });
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.destroy$.next();
    this.destroy$.complete();
  }

  hideDelete() {
    if (this.entitiesTableConfig) {
      return !this.entitiesTableConfig.deleteEnabled(this.entity);
    } else {
      return false;
    }
  }

  buildForm(entity: Resource): FormGroup {
    return this.fb.group({
      title: [entity ? entity.title : '', [Validators.required, Validators.maxLength(255)]],
      resourceType: [entity?.resourceType ? entity.resourceType : ResourceType.JS_MODULE, Validators.required],
      fileName: [entity ? entity.fileName : null, Validators.required],
      data: [entity ? entity.data : null, Validators.required]
    });
  }

  updateForm(entity: Resource) {
    if (this.isEdit) {
      this.entityForm.get('resourceType').disable({emitEvent: false});
      if (entity.resourceType !== ResourceType.JS_MODULE) {
        this.entityForm.get('fileName').disable({emitEvent: false});
        this.entityForm.get('data').disable({emitEvent: false});
      }
    }
    this.entityForm.patchValue({
      resourceType: entity.resourceType,
      fileName: entity.fileName,
      title: entity.title,
      data: entity.data
    });
  }

  prepareFormValue(formValue: Resource): Resource {
    if (this.isEdit && !isDefinedAndNotNull(formValue.data)) {
      delete formValue.data;
    }
    return super.prepareFormValue(formValue);
  }

  getAllowedExtensions() {
    try {
      return ResourceTypeExtension.get(this.entityForm.get('resourceType').value);
    } catch (e) {
      return '';
    }
  }

  getAcceptType() {
    try {
      return ResourceTypeMIMETypes.get(this.entityForm.get('resourceType').value);
    } catch (e) {
      return '*/*';
    }
  }

  convertToBase64File(data: string): string {
    return window.btoa(data);
  }

  onResourceIdCopied() {
    this.store.dispatch(new ActionNotificationShow(
      {
        message: this.translate.instant('resource.idCopiedMessage'),
        type: 'success',
        duration: 750,
        verticalPosition: 'bottom',
        horizontalPosition: 'right'
      }));
  }
}
