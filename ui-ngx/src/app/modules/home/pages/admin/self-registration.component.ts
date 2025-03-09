/* eslint-disable max-len */
import { Component, OnInit } from '@angular/core';
import { FormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { HasConfirmForm } from '@app/core/guards/confirm-on-exit.guard';
import { AdminService } from '@app/core/public-api';
import { PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { reCaptchaVersion, RegistrationSettings } from '@shared/models/settings.models';

@Component({
  selector: 'tb-self-registration',
  templateUrl: './self-registration.component.html',
  styleUrls: ['./self-registration.component.scss']
})
export class SelfRegistrationComponent extends PageComponent implements HasConfirmForm, OnInit {

  selfRegistrationFormGroup: UntypedFormGroup;
  version = Object.values(reCaptchaVersion);
  isDeleteEnabled = true;
  isSaveEnabled = false;
  isFieldsetVisible = false;
  selectedCaptchaVersion: string;
  secretKey: string;
  isCheckboxEnabled = true;
  domainSignupLink = '';
  private registrationSettings: RegistrationSettings;

  editorConfig = {
    base_url: '/tinymce',
    suffix: '.min',
    toolbar_mode: 'floating',
    plugins: 'lists link image table wordcount fullscreen code',
    menubar: 'edit insert tools view format table',
    toolbar: 'fontfamily fontsize | blocks | bold italic strikethrough forecolor backcolor | link | table | image | alignleft aligncenter alignright alignjustify | indent outdent | removeformat | code | fullscreen',
  };

  constructor(protected store: Store<AppState>,
    private adminService: AdminService,
    private fb: FormBuilder) {
    super(store);
    this.selectedCaptchaVersion = this.version[0];
    this.buildSelfRegistrationSettingsForm();
    this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').disable();
    this.adminService.getRegistrationSettings().subscribe(
      registrationSettings => {
        this.processSelfRegistrationSettings(registrationSettings);
        const domainName = registrationSettings.domainSettings.domainName;
        const isSecretKey = registrationSettings.mobileSettings.applicationSecret;
        if (domainName) {
          this.domainSignupLink = `${domainName}/signup`;
        }
        this.isEnabledMobileSettings();
        if (isSecretKey) {
          this.secretKey = isSecretKey;
          this.selfRegistrationFormGroup.get('mobileSettings.applicationSecret').setValue(this.secretKey);
        }
        else {
          this.secretKey = this.generateKey();
          this.selfRegistrationFormGroup.get('mobileSettings.applicationSecret').setValue(this.secretKey);
        }
      }
    );
  }

  private processSelfRegistrationSettings(registrationSettings: RegistrationSettings) {
    this.registrationSettings = registrationSettings;
    const domainName = this.registrationSettings.domainSettings.domainName;
    if (domainName) {
      this.isDeleteEnabled = false;
      this.selfRegistrationFormGroup.reset(this.registrationSettings);
    }
    this.selfRegistrationFormGroup.valueChanges.subscribe(() => {
      this.isSaveEnabled = false;
    });
  };

  buildSelfRegistrationSettingsForm() {
    this.selfRegistrationFormGroup = this.fb.group({
      enablemobileSelfRegistration: [false],
      privacyPolicy: [null],
      termsOfUse: [null],
      showPrivacyPolicy: [false],
      showTermOfUse: [false],
      domainSettings: this.fb.group({
        domainName: ['', [Validators.required]],
        version: [this.selectedCaptchaVersion],
        logActionName: [''],
        siteKey: ['', [Validators.required]],
        secretKey: ['', [Validators.required]],
      }),
      generalSettings: this.fb.group({
        notificationMail: ['', [Validators.required, Validators.email]],
        signUpPageMessage: [''],
        dashboard: [null],
        dashboardFullScreen: [false]
      }),
      mobileSettings: this.fb.group({
        applicationPackage: [''],
        applicationSecret: [''],
        applicationUrlScheme: [''],
        applicationUrlHostname: [''],
      })
    });
  };

  ngOnInit() {
    this.selfRegistrationFormGroup.valueChanges.subscribe(() => {
      this.isSaveEnabled = false;
    });
    this.selfRegistrationFormGroup.get('domainSettings.version').valueChanges.subscribe(value => {
      this.selectedCaptchaVersion = value;
    });
    this.selfRegistrationFormGroup.get('generalSettings.dashboard').valueChanges.subscribe((value) => {
      if (value) {
        this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').enable();
      } else {
        this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').disable();
        this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').setValue(false);
      }
    });
    this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').valueChanges.subscribe((checked) => {
      this.selfRegistrationFormGroup.get('generalSettings.dashboardFullScreen').setValue(!!checked, { emitEvent: false });
    });
    this.selfRegistrationFormGroup.get('enablemobileSelfRegistration').valueChanges.subscribe(enabled => {
      if (enabled) {
        if (!this.selfRegistrationFormGroup.get('mobileSettings.applicationSecret').value) {
          this.selfRegistrationFormGroup.get('mobileSettings.applicationSecret').setValue(this.secretKey);
        }
        this.selfRegistrationFormGroup.get('mobileSettings.applicationPackage').setValidators([Validators.required]);
        this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlScheme').setValidators([Validators.required]);
        this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlHostname').setValidators([Validators.required]);
      } else {
        this.selfRegistrationFormGroup.get('mobileSettings.applicationPackage').clearValidators();
        this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlScheme').clearValidators();
        this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlHostname').clearValidators();
      }
      this.selfRegistrationFormGroup.get('mobileSettings.applicationPackage').updateValueAndValidity();
      this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlScheme').updateValueAndValidity();
      this.selfRegistrationFormGroup.get('mobileSettings.applicationUrlHostname').updateValueAndValidity();
    });
  }

  save(): void {
    this.registrationSettings = { ...this.registrationSettings, ...this.selfRegistrationFormGroup.value };
    this.adminService.saveRegistrationSettings(this.registrationSettings).subscribe(
      registrationSettings => {
        this.processSelfRegistrationSettings(registrationSettings);
        this.isSaveEnabled = true;
        const domain = this.selfRegistrationFormGroup.get('domainSettings.domainName')?.value;
        if (domain) {
          this.domainSignupLink = `${domain}/signup`;
        }
        const privacyPolicy = this.selfRegistrationFormGroup.get('privacyPolicy')?.value;
        const termsOfUse = this.selfRegistrationFormGroup.get('termsOfUse')?.value;
        if (!privacyPolicy) { this.selfRegistrationFormGroup.get('privacyPolicy').setValue('null'); }
        if (!termsOfUse) { this.selfRegistrationFormGroup.get('termsOfUse').setValue('null'); }
        this.isEnabledMobileSettings();
      }
    );
    this.isDeleteEnabled = false;
  };

  delete(): void {
    if (this.registrationSettings) {
      this.adminService.deleteRegistrationSettings().subscribe({
        next: () => {
          this.selfRegistrationFormGroup.reset();
          this.isDeleteEnabled = true;
          this.disableAllValidators(this.selfRegistrationFormGroup);
          this.domainSignupLink = '';
        },
        error: (error) => {
          console.log('Failed to delete registration settings ', error);
        }
      });
    }
  };

  private isEnabledMobileSettings(): void {
    const applicationPackage = this.selfRegistrationFormGroup.get('mobileSettings.applicationPackage')?.value;
    if (applicationPackage) {
      this.selfRegistrationFormGroup.get('enablemobileSelfRegistration')?.setValue(true);
    }
    else {
      this.selfRegistrationFormGroup.get('enablemobileSelfRegistration')?.setValue(false);
    }
  }

  private disableAllValidators(formGroup: UntypedFormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control instanceof UntypedFormGroup) {
        this.disableAllValidators(control);
      } else {
        control?.clearValidators();
        control?.updateValueAndValidity();
      }
    });
  };

  generateKey(length: number = 24): string {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  };

  confirmForm(): UntypedFormGroup {
    return this.selfRegistrationFormGroup;
  };
}
