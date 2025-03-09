/* eslint-disable max-len */
import { T } from '@angular/cdk/keycodes';
import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AppState, AttributeService, DeviceService, getCurrentAuthUser } from '@app/core/public-api';
import { Authority, Direction, PageComponent, PageLink, TimeseriesData } from '@app/shared/public-api';
import { LatestTimeseries } from '@app/shared/services/latest-timeseries';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable } from 'rxjs';

@Component({
  selector: 'tb-report-filter',
  templateUrl: './report-filter.component.html',
  styleUrls: ['./report-filter.component.scss']
})
export class ReportFilterComponent extends PageComponent implements OnInit {
  @ViewChild('reportFilterPanel') reportFilterPanel: TemplateRef<any>;
  @Output() filterChange = new EventEmitter<void>();

  @Input() reportType: string;

  private checkAndCapitalize(str: string): string {
    if (str && str[0] === str[0].toUpperCase()) {
      return str;
    }
    return str ? str.charAt(0).toUpperCase() + str.slice(1) : str;
  }

  reportCategory = [
    { id: '1', name: 'Device Name' },
    { id: '2', name: 'Device Type' },
  ];
  reportFilterForm: FormGroup;
  dialogRef: MatDialogRef<any>;
  panelMode = false;
  buttonMode = true;
  buttonDisplayValue = this.translate.instant('device.filter-title');
  availableOptions: { value: string; label: string }[] = [];
  reportAvailable: string;
  availableDevice: any[] = [];
  entityId: string;
  filter: object;
  pageLink: any;
  authUser: any;
  timeseriesKey: any;
  selectedCategory: string;
  deviceType: string;
  deviceTypes: Set<string> = new Set();

  @Input()
  isStateForm: boolean;

  @Input()
  deviceName: string;

  constructor(
    protected store: Store<AppState>,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private translate: TranslateService,
    private deviceService: DeviceService,
    private attributeService: AttributeService,
    private latestTelemetry: LatestTimeseries
  ) {
    super(store);
    this.authUser = getCurrentAuthUser(this.store);
  }

  ngOnInit(): void {
    this.reportFilterForm = this.fb.group({
      reportCategory: ['', Validators.required],
      reportAvailable: ['', Validators.required],
    });
    this.fetchAllDevice();
  }

  toggleReportFilterPanel(event: Event): void {
    event.stopPropagation();
    this.dialogRef = this.dialog.open(this.reportFilterPanel, {
      width: '350px',
      height: 'auto',
      panelClass: 'custom-dialog-container'
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  update(): void {
    if (this.reportFilterForm.valid) {
      if (this.selectedCategory === '1') {
        this.entityId = this.reportFilterForm.get('reportAvailable').value;
        if (typeof this.entityId === 'string') {
          this.fetchDeviceById(this.entityId);
          this.latestTelemetry.updateLatestTimeseries(this.entityId);
        }
      }
      else if (this.selectedCategory === '2') {
        this.deviceType = this.reportFilterForm.get('reportAvailable').value;
        this.fetchDevicesByType(this.deviceType);
      }
      else {
        this.latestTelemetry.updateLatestTimeseries(null);
      }
      this.dialogRef?.close();
      this.filterChange.emit();
    }
  }

  fetchDevicesByType(deviceType: string): void {
    const pageSize = 100;
    const pageLink = new PageLink(pageSize, 0, '', {
      property: 'name',
      direction: Direction.DESC
    });

    let deviceObservable: Observable<any>;

    if (this.authUser.authority === Authority.TENANT_ADMIN) {
      deviceObservable = this.deviceService.getTenantDeviceInfos(pageLink, deviceType);
    } else if (this.authUser.authority === Authority.CUSTOMER_USER) {
      deviceObservable = this.deviceService.getCustomerDeviceInfos(
        this.authUser.customerId,
        pageLink,
        deviceType
      );
    } else {
      return;
    }

    deviceObservable.subscribe({
      next: (result) => {
        const deviceIds = result.data.map(device => device.id.id);
        if (deviceIds.length > 0) {
          this.resolveLatestTimeseriesByType(deviceIds);
        } else {
          this.latestTelemetry.updateLatestTimeseries(null);
        }
      },
    });
  }

  onCategoryChange(event: any): void {
    this.selectedCategory = event.value;
    this.availableOptions = this.getAvailableOptionsForCategory(this.selectedCategory);
  }

  fetchDeviceById(deviceId: string): void {
    if (this.authUser.authority === Authority.TENANT_ADMIN || this.authUser.authority === Authority.CUSTOMER_USER) {
      this.deviceService.getDeviceInfo(deviceId).subscribe({
        next: (data) => {
          this.resolveTimeseriesKeys(data.id.entityType, deviceId, data.createdTime, Date.now());
        },
      });
    }
  }

  fetchAllDevice(): void {
    const entityNameFilter = this.isStateForm && this.deviceName ? this.deviceName : '';
    this.pageLink = new PageLink(10, 0, entityNameFilter, {
      property: 'name',
      direction: Direction.DESC
    });
    const customerId = this.authUser.customerId;

    if (this.authUser.authority === Authority.TENANT_ADMIN) {
      this.deviceService.getTenantDeviceInfos(this.pageLink, '', { ignoreLoading: true }).subscribe({
        next: (data) => {
          this.availableDevice = data.data;
          this.deviceTypes = new Set(this.availableDevice.map(device =>
            device.type || 'unknown'
          ).filter(type => type !== 'unknown'));
          this.onCategoryChange({ value: this.reportFilterForm.get('reportCategory').value });
        },
      });
    }
    else if (this.authUser.authority === Authority.CUSTOMER_USER) {
      this.deviceService.getCustomerDeviceInfos(customerId, this.pageLink, '', { ignoreLoading: true }).subscribe({
        next: (data) => {
          this.availableDevice = data.data;
          this.deviceTypes = new Set(this.availableDevice.map(device =>
            device.type || 'unknown'
          ).filter(type => type !== 'unknown'));
          this.onCategoryChange({ value: this.reportFilterForm.get('reportCategory').value });
        },
      });
    }
  }

  getAvailableOptionsForCategory(categoryId: string): { value: string; label: string }[] {
    if (categoryId === '1') {
      return this.availableDevice.map(device => (
        { value: device.id.id, label: device.name }
      ));
    }
    else if (categoryId === '2') {
      return [
        { value: 'indoor', label: 'Indoor' },
        { value: 'outdoor', label: 'Outdoor' },
      ];
    }
    return [];
  }

  resolveTimeseriesKeys(entityType: string, deviceId: string, startTime: number, endTime: number): void {
    this.attributeService.getTimeseriesKeyById(entityType, this.entityId).subscribe({
      next: (keys) => {
        this.timeseriesKey = keys;
        const convertedKeys = keys.map(key => this.checkAndCapitalize(key));

        if (this.timeseriesKey.length > 0) {
          this.resolveTimeseries(entityType, deviceId, this.timeseriesKey, convertedKeys, startTime, endTime);
        }
        else {
          this.latestTelemetry.updateLatestTimeseries(null);
        }
      }
    });
  }

  resolveTimeseries(entityType: string, deviceId: string, keys: Array<string>, displayKeys: Array<string>, startTime: number, endTime: number): void {
    this.attributeService.getAllTimeseries(entityType, deviceId, keys, startTime, endTime).subscribe({
      next: (all) => {
        const displayData = {};
        Object.keys(all).forEach((key, index) => {
          displayData[displayKeys[index]] = all[key];
        });
        this.latestTelemetry.updateLatestTimeseries(displayData);
      },
    });
  }

  resolveLatestTimeseriesByType(allDeviceId: any): void {
    if (!Array.isArray(allDeviceId) || allDeviceId.length === 0) {
      this.latestTelemetry.updateLatestTimeseries(null);
      return;
    }

    const observables = allDeviceId.map((deviceId: string) =>
      this.attributeService.getDeviceTimeseriesLatestById(deviceId)
    );

    forkJoin(observables).subscribe({
      next: (timeseriesArray) => {
        const timeseriesWithIds = timeseriesArray.map((timeseries, index) => {
          const convertedTimeseries = {};
          Object.keys(timeseries).forEach(key => {
            const convertedKey = this.checkAndCapitalize(key);
            convertedTimeseries[convertedKey] = timeseries[key];
          });
          return {
            ...convertedTimeseries,
            deviceId: allDeviceId[index]
          };
        });
        this.latestTelemetry.updateLatestTimeseries(timeseriesWithIds);
      },
    });
  }
}
