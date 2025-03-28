

import { Component, ElementRef, ViewChild } from '@angular/core';
import { WidgetActionType, WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { Observable, of } from 'rxjs';
import { map, mergeMap, startWith } from 'rxjs/operators';

@Component({
  selector: 'tb-dashboard-state-widget-settings',
  templateUrl: './dashboard-state-widget-settings.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class DashboardStateWidgetSettingsComponent extends WidgetSettingsComponent {

  @ViewChild('dashboardStateInput') dashboardStateInput: ElementRef;

  dashboardStateWidgetSettingsForm: UntypedFormGroup;

  filteredDashboardStates: Observable<Array<string>>;
  dashboardStateSearchText = '';

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  protected settingsForm(): UntypedFormGroup {
    return this.dashboardStateWidgetSettingsForm;
  }

  protected defaultSettings(): WidgetSettings {
    return {
      stateId: '',
      defaultAutofillLayout: true,
      defaultMargin: 0,
      defaultBackgroundColor: '#fff',
      syncParentStateParams: true
    };
  }

  protected onSettingsSet(settings: WidgetSettings) {
    this.dashboardStateWidgetSettingsForm = this.fb.group({
      stateId: [settings.stateId, []],
      defaultAutofillLayout: [settings.defaultAutofillLayout, []],
      defaultMargin: [settings.defaultMargin, [Validators.min(0)]],
      defaultBackgroundColor: [settings.defaultBackgroundColor, []],
      syncParentStateParams: [settings.syncParentStateParams, []]
    });
    this.dashboardStateSearchText = '';
    this.filteredDashboardStates = this.dashboardStateWidgetSettingsForm.get('stateId').valueChanges
      .pipe(
        startWith(''),
        map(value => value ? value : ''),
        mergeMap(name => this.fetchDashboardStates(name) )
      );
  }

  public clearDashboardState(value: string = '') {
    this.dashboardStateInput.nativeElement.value = value;
    this.dashboardStateWidgetSettingsForm.get('stateId').patchValue(value, {emitEvent: true});
    setTimeout(() => {
      this.dashboardStateInput.nativeElement.blur();
      this.dashboardStateInput.nativeElement.focus();
    }, 0);
  }

  private fetchDashboardStates(searchText?: string): Observable<Array<string>> {
    this.dashboardStateSearchText = searchText;
    const stateIds = Object.keys(this.dashboard.configuration.states);
    const result = searchText ? stateIds.filter(this.createFilterForDashboardState(searchText)) : stateIds;
    if (result && result.length) {
      return of(result);
    } else {
      return of([searchText]);
    }
  }

  private createFilterForDashboardState(query: string): (stateId: string) => boolean {
    const lowercaseQuery = query.toLowerCase();
    return stateId => stateId.toLowerCase().indexOf(lowercaseQuery) === 0;
  }
}
