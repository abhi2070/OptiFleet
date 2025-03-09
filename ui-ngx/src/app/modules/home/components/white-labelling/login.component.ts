import { Component } from '@angular/core';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

@Component({
  selector: 'tb-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);

  isDisabledDelete: boolean = true;

  save() {
    console.log('Delete action triggered');
    // Implement the delete logic here
  }

  matcher = new MyErrorStateMatcher();
}

export class InputErrorStateMatcherExample {
  emailFormControl = new FormControl('', [Validators.required, Validators.email]);

  matcher = new MyErrorStateMatcher();
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
