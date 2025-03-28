

import { Component, Inject, OnDestroy, OnInit, SkipSelf } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  FormGroupDirective,
  NgForm,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { DialogComponent } from '@app/shared/components/dialog.component';
import {
  WidgetActionCallbacks,
  WidgetActionDescriptorInfo,
  WidgetActionsData
} from '@home/components/widget/action/manage-widget-actions.component.models';
import { UtilsService } from '@core/services/utils.service';
import {
  actionDescriptorToAction, defaultWidgetAction,
  WidgetActionSource,
  widgetType
} from '@shared/models/widget.models';
import { takeUntil } from 'rxjs/operators';
import { CustomActionEditorCompleter } from '@home/components/widget/lib/settings/common/action/custom-action.models';
import { WidgetService } from '@core/http/widget.service';

export interface WidgetActionDialogData {
  isAdd: boolean;
  callbacks: WidgetActionCallbacks;
  actionsData: WidgetActionsData;
  action?: WidgetActionDescriptorInfo;
  widgetType: widgetType;
}

@Component({
  selector: 'tb-widget-action-dialog',
  templateUrl: './widget-action-dialog.component.html',
  providers: [{provide: ErrorStateMatcher, useExisting: WidgetActionDialogComponent}],
  styleUrls: []
})
export class WidgetActionDialogComponent extends DialogComponent<WidgetActionDialogComponent,
                                                 WidgetActionDescriptorInfo> implements OnInit, OnDestroy, ErrorStateMatcher {

  private destroy$ = new Subject<void>();

  widgetActionFormGroup: UntypedFormGroup;

  isAdd: boolean;
  action: WidgetActionDescriptorInfo;

  customActionEditorCompleter = CustomActionEditorCompleter;

  submitted = false;

  functionScopeVariables: string[];

  constructor(protected store: Store<AppState>,
              protected router: Router,
              private utils: UtilsService,
              private widgetService: WidgetService,
              @Inject(MAT_DIALOG_DATA) public data: WidgetActionDialogData,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              public dialogRef: MatDialogRef<WidgetActionDialogComponent, WidgetActionDescriptorInfo>,
              public fb: UntypedFormBuilder) {
    super(store, router, dialogRef);
    this.isAdd = data.isAdd;
    if (this.isAdd) {
      this.action = {
        id: this.utils.guid(),
        name: '',
        icon: 'more_horiz',
        ...defaultWidgetAction(data.widgetType !== widgetType.static)
      };
    } else {
      this.action = this.data.action;
    }
    this.functionScopeVariables = this.widgetService.getWidgetScopeVariables();
  }

  ngOnInit(): void {
    this.widgetActionFormGroup = this.fb.group({});
    this.widgetActionFormGroup.addControl('actionSourceId',
      this.fb.control(this.action.actionSourceId, [Validators.required]));
    this.widgetActionFormGroup.addControl('name',
      this.fb.control(this.action.name, [this.validateActionName(), Validators.required]));
    this.widgetActionFormGroup.addControl('icon',
      this.fb.control(this.action.icon, [Validators.required]));
    this.widgetActionFormGroup.addControl('useShowWidgetActionFunction',
      this.fb.control(this.action.useShowWidgetActionFunction, []));
    this.widgetActionFormGroup.addControl('showWidgetActionFunction',
      this.fb.control(this.action.showWidgetActionFunction || 'return true;', []));
    this.widgetActionFormGroup.addControl('widgetAction',
      this.fb.control(actionDescriptorToAction(this.action), [Validators.required]));
    this.updateShowWidgetActionForm();
    this.widgetActionFormGroup.get('actionSourceId').valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.widgetActionFormGroup.get('name').updateValueAndValidity();
      this.updateShowWidgetActionForm();
    });
    this.widgetActionFormGroup.get('useShowWidgetActionFunction').valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.updateShowWidgetActionForm();
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    super.ngOnDestroy();
  }

  displayShowWidgetActionForm(): boolean {
    return !!this.data.actionsData.actionSources[this.widgetActionFormGroup.get('actionSourceId').value]?.hasShowCondition;
  }

  getWidgetActionFunctionHelpId(): string | undefined {
    const actionSourceId = this.widgetActionFormGroup.get('actionSourceId').value;
    if (actionSourceId === 'headerButton') {
      return 'widget/action/show_widget_action_header_fn';
    } else if (actionSourceId === 'actionCellButton') {
      return 'widget/action/show_widget_action_cell_fn';
    }
    return undefined;
  }

  private updateShowWidgetActionForm() {
    const actionSourceId = this.widgetActionFormGroup.get('actionSourceId').value;
    const useShowWidgetActionFunction = this.widgetActionFormGroup.get('useShowWidgetActionFunction').value;
    if (!!this.data.actionsData.actionSources[actionSourceId]?.hasShowCondition && useShowWidgetActionFunction) {
      this.widgetActionFormGroup.get('showWidgetActionFunction').setValidators([Validators.required]);
    } else {
      this.widgetActionFormGroup.get('showWidgetActionFunction').clearValidators();
    }
    this.widgetActionFormGroup.get('showWidgetActionFunction').updateValueAndValidity();
  }

  private validateActionName(): ValidatorFn {
    return (c: UntypedFormControl) => {
      const newName = c.value;
      const valid = this.checkActionName(newName, this.widgetActionFormGroup.get('actionSourceId').value);
      return !valid ? {
        actionNameNotUnique: true
      } : null;
    };
  }

  private checkActionName(name: string, actionSourceId: string): boolean {
    let actionNameIsUnique = true;
    if (name && actionSourceId) {
      const sourceActions = this.data.actionsData.actionsMap[actionSourceId];
      if (sourceActions) {
        const result = sourceActions.filter((sourceAction) => sourceAction.name === name);
        if (result && result.length && result[0].id !== this.action.id) {
          actionNameIsUnique = false;
        }
      }
    }
    return actionNameIsUnique;
  }

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
  }

  public actionSourceName(actionSource: WidgetActionSource): string {
    if (actionSource) {
      return this.utils.customTranslation(actionSource.name, actionSource.name);
    } else {
      return '';
    }
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  save(): void {
    this.submitted = true;
    if (this.widgetActionFormGroup.valid) {
      const result: WidgetActionDescriptorInfo =
        {...this.widgetActionFormGroup.value, ...this.widgetActionFormGroup.get('widgetAction').value};
      delete (result as any).widgetAction;
      result.id = this.action.id;
      this.dialogRef.close(result);
    }
  }
}
