

import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormBuilder, UntypedFormGroup, ValidatorFn, Validators } from '@angular/forms';
import { ContactBased } from '@shared/models/contact-based.model';
import { AfterViewInit, ChangeDetectorRef, Directive } from '@angular/core';
import { POSTAL_CODE_PATTERNS } from '@home/models/contact.models';
import { HasId } from '@shared/models/base-data';
import { EntityComponent } from './entity.component';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';

@Directive()
export abstract class ContactBasedComponent<T extends ContactBased<HasId>> extends EntityComponent<T> implements AfterViewInit {

  protected constructor(protected store: Store<AppState>,
                        protected fb: UntypedFormBuilder,
                        protected entityValue: T,
                        protected entitiesTableConfigValue: EntityTableConfig<T>,
                        protected cd: ChangeDetectorRef) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  buildForm(entity: T): UntypedFormGroup {
    const entityForm = this.buildEntityForm(entity);
    entityForm.addControl('country', this.fb.control(entity ? entity.country : '', [Validators.maxLength(255)]));
    entityForm.addControl('city', this.fb.control(entity ? entity.city : '', [Validators.maxLength(255)]));
    entityForm.addControl('state', this.fb.control(entity ? entity.state : '', [Validators.maxLength(255)]));
    entityForm.addControl('zip', this.fb.control(entity ? entity.zip : '',
      this.zipValidators(entity ? entity.country : '')
    ));
    entityForm.addControl('address', this.fb.control(entity ? entity.address : '', []));
    entityForm.addControl('address2', this.fb.control(entity ? entity.address2 : '', []));
    entityForm.addControl('phone', this.fb.control(entity ? entity.phone : '', [Validators.maxLength(255)]));
    entityForm.addControl('email', this.fb.control(entity ? entity.email : '', [Validators.email]));
    return entityForm;
  }

  updateForm(entity: T) {
    this.updateEntityForm(entity);
    this.entityForm.patchValue({country: entity.country});
    this.entityForm.patchValue({city: entity.city});
    this.entityForm.patchValue({state: entity.state});
    this.entityForm.get('zip').setValidators(this.zipValidators(entity.country));
    this.entityForm.patchValue({zip: entity.zip});
    this.entityForm.patchValue({address: entity.address});
    this.entityForm.patchValue({address2: entity.address2});
    this.entityForm.patchValue({phone: entity.phone});
    this.entityForm.patchValue({email: entity.email});
  }

  ngAfterViewInit() {
    this.entityForm.get('country').valueChanges.subscribe(
      (country) => {
        this.entityForm.get('zip').setValidators(this.zipValidators(country));
        this.entityForm.get('zip').updateValueAndValidity({onlySelf: true});
        this.entityForm.get('zip').markAsTouched({onlySelf: true});
      }
    );
  }

  zipValidators(country: string): ValidatorFn[] {
    const zipValidators = [];
    if (country && POSTAL_CODE_PATTERNS[country]) {
      const postalCodePattern = POSTAL_CODE_PATTERNS[country];
      zipValidators.push(Validators.pattern(postalCodePattern));
    }
    return zipValidators;
  }

  abstract buildEntityForm(entity: T): UntypedFormGroup;

  abstract updateEntityForm(entity: T);

}
