

import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Tenant, TenantInfo } from '@app/shared/models/tenant.model';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { TranslateService } from '@ngx-translate/core';
import { ContactBasedComponent } from '../../components/entity/contact-based.component';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import { isDefinedAndNotNull } from '@core/utils';

@Component({
  selector: 'tb-tenant',
  templateUrl: './tenant.component.html',
  styleUrls: ['./tenant.component.scss']
})
export class TenantComponent extends ContactBasedComponent<TenantInfo> {

  constructor(protected store: Store<AppState>,
              protected translate: TranslateService,
              @Inject('entity') protected entityValue: TenantInfo,
              @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<TenantInfo>,
              protected fb: UntypedFormBuilder,
              protected cd: ChangeDetectorRef) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  hideDelete() {
    if (this.entitiesTableConfig) {
      return !this.entitiesTableConfig.deleteEnabled(this.entity);
    } else {
      return false;
    }
  }

  buildEntityForm(entity: TenantInfo): UntypedFormGroup {
    return this.fb.group(
      {
        title: [entity ? entity.title : '', [Validators.required, Validators.maxLength(255)]],
        tenantProfileId: [entity ? entity.tenantProfileId : null, [Validators.required]],
        additionalInfo: this.fb.group(
          {
            description: [entity && entity.additionalInfo ? entity.additionalInfo.description : ''],
            homeDashboardId: [entity && entity.additionalInfo ? entity.additionalInfo.homeDashboardId : null],
            homeDashboardHideToolbar: [entity && entity.additionalInfo &&
            isDefinedAndNotNull(entity.additionalInfo.homeDashboardHideToolbar) ? entity.additionalInfo.homeDashboardHideToolbar : true]
          }
        )
      }
    );
  }

  updateEntityForm(entity: Tenant) {
    this.entityForm.patchValue({title: entity.title});
    this.entityForm.patchValue({tenantProfileId: entity.tenantProfileId});
    this.entityForm.patchValue({additionalInfo: {description: entity.additionalInfo ? entity.additionalInfo.description : ''}});
    this.entityForm.patchValue({additionalInfo:
        {homeDashboardId: entity.additionalInfo ? entity.additionalInfo.homeDashboardId : null}});
    this.entityForm.patchValue({additionalInfo:
        {homeDashboardHideToolbar: entity.additionalInfo &&
          isDefinedAndNotNull(entity.additionalInfo.homeDashboardHideToolbar) ? entity.additionalInfo.homeDashboardHideToolbar : true}});
  }

  updateFormState() {
    if (this.entityForm) {
      if (this.isEditValue) {
        this.entityForm.enable({emitEvent: false});
      } else {
        this.entityForm.disable({emitEvent: false});
      }
    }
  }

  onTenantIdCopied(event) {
    this.store.dispatch(new ActionNotificationShow(
      {
        message: this.translate.instant('tenant.idCopiedMessage'),
        type: 'success',
        duration: 750,
        verticalPosition: 'bottom',
        horizontalPosition: 'right'
      }));
  }

  onTenantProfileUpdated() {
    this.entitiesTableConfig.updateData(false);
  }
}
