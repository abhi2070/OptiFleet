

import { Component, forwardRef, Input, OnChanges, OnInit, SimpleChanges, ViewEncapsulation } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, share, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { WidgetsBundle } from '@shared/models/widgets-bundle.model';
import { WidgetService } from '@core/http/widget.service';
import { isDefined } from '@core/utils';
import { NULL_UUID } from '@shared/models/id/has-uuid';
import { getCurrentAuthState } from '@core/auth/auth.selectors';
import { coerceBoolean } from '@shared/decorators/coercion';

@Component({
  selector: 'tb-widgets-bundle-select',
  templateUrl: './widgets-bundle-select.component.html',
  styleUrls: ['./widgets-bundle-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => WidgetsBundleSelectComponent),
    multi: true
  }],
  encapsulation: ViewEncapsulation.None
})
export class WidgetsBundleSelectComponent implements ControlValueAccessor, OnInit, OnChanges {

  @Input()
  bundlesScope: 'system' | 'tenant';

  @Input()
  @coerceBoolean()
  selectFirstBundle: boolean;

  @Input()
  selectBundleAlias: string;

  @Input()
  @coerceBoolean()
  required: boolean;

  @Input()
  disabled: boolean;

  @Input()
  excludeBundleIds: Array<string>;

  widgetsBundles$: Observable<Array<WidgetsBundle>>;

  widgetsBundles: Array<WidgetsBundle>;

  widgetsBundle: WidgetsBundle | null;

  onTouched = () => {};
  private propagateChange: (value: any) => void = () => {};

  constructor(private store: Store<AppState>,
              private widgetService: WidgetService) {
  }

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  ngOnInit() {
    this.widgetsBundles$ = this.getWidgetsBundles().pipe(
      map((widgetsBundles) => {
        const authState = getCurrentAuthState(this.store);
        if (!authState.edgesSupportEnabled) {
          widgetsBundles = widgetsBundles.filter(widgetsBundle => widgetsBundle.alias !== 'edge_widgets');
        }
        return widgetsBundles;
      }),
      tap((widgetsBundles) => {
        this.widgetsBundles = widgetsBundles;
        if (this.selectFirstBundle) {
          if (widgetsBundles.length > 0) {
            if (this.widgetsBundle !== widgetsBundles[0]) {
              this.widgetsBundle = widgetsBundles[0];
              this.updateView();
            } else if (isDefined(this.selectBundleAlias)) {
              this.selectWidgetsBundleByAlias(this.selectBundleAlias);
            }
          }
        }
      }),
      share()
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (!change.firstChange && change.currentValue !== change.previousValue) {
        if (propName === 'selectBundleAlias') {
          this.selectWidgetsBundleByAlias(this.selectBundleAlias);
        }
      }
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(value: WidgetsBundle | null): void {
    this.widgetsBundle = value;
  }

  widgetsBundleChanged() {
    this.updateView();
  }

  isSystem(item: WidgetsBundle) {
    return item && item.tenantId.id === NULL_UUID;
  }

  private selectWidgetsBundleByAlias(alias: string) {
    if (this.widgetsBundles && alias) {
      const found = this.widgetsBundles.find((widgetsBundle) => widgetsBundle.alias === alias);
      if (found && this.widgetsBundle !== found) {
        this.widgetsBundle = found;
        this.updateView();
      }
    } else if (this.widgetsBundle) {
      this.widgetsBundle = null;
      this.updateView();
    }
  }

  private updateView() {
    this.propagateChange(this.widgetsBundle);
  }

  private getWidgetsBundles(): Observable<Array<WidgetsBundle>> {
    let widgetsBundlesObservable: Observable<Array<WidgetsBundle>>;
    if (this.bundlesScope) {
      if (this.bundlesScope === 'system') {
        widgetsBundlesObservable = this.widgetService.getSystemWidgetsBundles();
      } else if (this.bundlesScope === 'tenant') {
        widgetsBundlesObservable = this.widgetService.getTenantWidgetsBundles();
      }
    } else {
      widgetsBundlesObservable = this.widgetService.getAllWidgetsBundles();
    }
    if (this.excludeBundleIds && this.excludeBundleIds.length) {
      widgetsBundlesObservable = widgetsBundlesObservable.pipe(
        map((widgetBundles) =>
          widgetBundles.filter(w => !this.excludeBundleIds.includes(w.id.id)))
      );
    }
    return widgetsBundlesObservable;
  }

}
