

import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { COUNTRIES } from '@home/models/contact.models';

@Component({
  selector: 'tb-contact',
  templateUrl: './contact.component.html'
})
export class ContactComponent {

  @Input()
  parentForm: UntypedFormGroup;

  @Input() isEdit: boolean;

  countries = COUNTRIES;

}
