

import { Component, EventEmitter, forwardRef, Input, OnInit, Output, SkipSelf } from '@angular/core';
import {
  ControlValueAccessor, FormGroupDirective,
  NG_VALUE_ACCESSOR, NgForm,
  UntypedFormBuilder, UntypedFormControl,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DocumentationLink } from '@shared/models/user-settings.models';
import { ErrorStateMatcher } from '@angular/material/core';

@Component({
  selector: 'tb-doc-link',
  templateUrl: './doc-link.component.html',
  styleUrls: ['./link.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DocLinkComponent),
      multi: true
    },
    {provide: ErrorStateMatcher, useExisting: DocLinkComponent}
  ]
})
export class DocLinkComponent extends PageComponent implements OnInit, ControlValueAccessor, ErrorStateMatcher {

  @Input()
  disabled: boolean;

  @Input()
  addOnly = false;

  @Input()
  disableEdit = false;

  @Output()
  docLinkAdded = new EventEmitter<DocumentationLink>();

  @Output()
  docLinkAddCanceled = new EventEmitter<void>();

  @Output()
  docLinkUpdated = new EventEmitter<DocumentationLink>();

  @Output()
  docLinkDeleted = new EventEmitter<void>();

  @Output()
  editModeChanged = new EventEmitter<boolean>();

  editMode = false;
  addMode = false;

  docLink: DocumentationLink;

  private propagateChange = null;

  public editDocLinkFormGroup: UntypedFormGroup;

  private submitted = false;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher) {
    super(store);
  }

  ngOnInit(): void {
    this.addMode = this.addOnly;
    this.editDocLinkFormGroup = this.fb.group({
      icon: [null, [Validators.required]],
      name: [null, [Validators.required]],
      link: [null, [Validators.required]]
    });
  }

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.editDocLinkFormGroup.disable({emitEvent: false});
    } else {
      this.editDocLinkFormGroup.enable({emitEvent: false});
    }
  }

  writeValue(value: DocumentationLink): void {
    this.docLink = value;
    this.editDocLinkFormGroup.patchValue(
      value, {emitEvent: false}
    );
    if (!this.editDocLinkFormGroup.valid) {
      this.addMode = true;
      this.editModeChanged.emit(true);
    }
  }

  switchToEditMode() {
    if (!this.disableEdit && !this.editMode) {
      this.submitted = false;
      this.editDocLinkFormGroup.patchValue(
        this.docLink, {emitEvent: false}
      );
      this.editMode = true;
      this.editModeChanged.emit(true);
    }
  }

  apply() {
    this.submitted = true;
    this.updateModel();
    if (this.editDocLinkFormGroup.valid) {
      this.editMode = false;
      this.editModeChanged.emit(false);
      this.docLinkUpdated.next(this.editDocLinkFormGroup.value);
    }
  }

  cancelEdit() {
    this.submitted = false;
    this.editMode = false;
    this.editModeChanged.emit(false);
  }

  add() {
    this.submitted = true;
    this.updateModel();
    if (this.editDocLinkFormGroup.valid) {
      if (!this.addOnly) {
        this.addMode = false;
        this.editModeChanged.emit(false);
      }
      this.docLinkAdded.next(this.editDocLinkFormGroup.value);
    }
  }

  cancelAdd() {
    this.editModeChanged.emit(false);
    this.docLinkAddCanceled.emit();
  }

  delete() {
    this.docLinkDeleted.emit();
  }

  isEditing() {
    return this.editMode || this.addMode;
  }

  private updateModel() {
    if (this.editDocLinkFormGroup.valid) {
      this.docLink = this.editDocLinkFormGroup.value;
      this.propagateChange(this.editDocLinkFormGroup.value);
    } else {
      this.propagateChange(null);
    }
  }

}
