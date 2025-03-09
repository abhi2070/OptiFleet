/* eslint-disable @typescript-eslint/no-inferrable-types */
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { AppState } from '@app/core/public-api';
import { PageComponent } from '@app/shared/public-api';
import { LatestTimeseries } from '@app/shared/services/latest-timeseries';
import { Store } from '@ngrx/store';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import * as XLSX from 'xlsx';
import { ReportFilterComponent } from './report-filter.component';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'tb-equipment-utilization',
  templateUrl: './equipment-utilization.component.html',
  styleUrls: ['./equipment-utilization.component.scss']
})

export class EquipmentUtilizationComponent extends PageComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [];
  dataSource = new MatTableDataSource<any>([]);
  latestTimeseries: any;
  fileName = 'equipment_utilization_report';

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(ReportFilterComponent) reportFilter: ReportFilterComponent;

  reportName: string = 'equipmentMonitoring';

  constructor(
    protected store: Store<AppState>,
    private latestTelemetry: LatestTimeseries,
    private route: ActivatedRoute
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params.deviceId && params.deviceName) {
        setTimeout(() => {
          this.setDeviceFilter(params.deviceId, params.deviceName);
        });
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  setDeviceFilter(deviceId: string, deviceName: string): void {
    if (this.reportFilter) {
      this.reportFilter.reportFilterForm.patchValue({
        reportCategory: '1',
        reportAvailable: deviceId
      });
      this.reportFilter.onCategoryChange({ value: '1' });
      this.reportFilter.update();
    }
  }


  refreshData(): void {
    if (this.reportFilter) { this.reportFilter.update(); }
    else { console.log('Report not available'); }
  }

  showReport(): void {
    this.latestTelemetry.latestTimeseries$.subscribe(
      data => {
        this.latestTimeseries = data;
        this.updateTableData();
      }
    );
  }

  updateTableData(): void {
    if (this.latestTimeseries) {
      if (Array.isArray(this.latestTimeseries) && this.latestTimeseries.length > 0) {
        const allEmpty = this.latestTimeseries.every(item =>
          Object.keys(item).every(key => item[key].length === 0)
        );
        if (allEmpty) {
          this.displayedColumns = [];
          this.dataSource.data = [];
        }
        else {
          this.displayedColumns = Object.keys(this.latestTimeseries[0]);
          this.displayedColumns.sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()));
          this.dataSource.data = this.latestTimeseries.map(item => {
            const newItem: any = {};
            this.displayedColumns.forEach(column => {
              if (column === 'deviceId') {
                newItem[column] = item[column];
              }
              else {
                newItem[column] = item[column] && item[column][0] ? item[column][0].value : null;
              }
            });
            return newItem;
          });
        }
      } else if (typeof this.latestTimeseries === 'object' && Object.keys(this.latestTimeseries).length > 0) {
        this.displayedColumns = Object.keys(this.latestTimeseries);
        this.dataSource.data = this.convertToTableData(this.latestTimeseries);
      } else {
        this.displayedColumns = [];
        this.dataSource.data = [];
      }
    } else {
      this.displayedColumns = [];
      this.dataSource.data = [];
    }
  }

  convertToTableData(data: any): any[] {
    const keys = Object.keys(data);
    const rowCount = Math.max(...keys.map(key => Array.isArray(data[key]) ? data[key].length : 0));
    const tableData = [];

    for (let i = 0; i < rowCount; i++) {
      const row = {};
      keys.forEach(key => {
        row[key] = data[key] && data[key][i] ? data[key][i].value : null;
      });
      tableData.push(row);
    }
    return tableData;
  }

  exportToCSV(): void {
    const data = this.dataSource.data.map(item => {
      const row = {};
      this.displayedColumns.forEach(col => {
        row[col] = item[col] ? item[col].toString() : '';
      });
      return row;
    });

    const options = {
      fieldSeparator: ',',
      quoteStrings: '"',
      decimalseparator: '.',
      showLabels: true,
      showTitle: true,
      title: 'Equipment Utilisation Report',
      useBom: true,
      headers: this.displayedColumns.map(column => column.charAt(0).toUpperCase() + column.slice(1)),
    };

    if (data.length > 0) {
      new ngxCsv(data, this.fileName, options);
    }
    else {
      console.log('No data found');
    }
  }

  exportToExcel(): void {
    const data = this.dataSource.data.map(item => {
      const row = {};
      this.displayedColumns.forEach(col => {
        row[col] = item[col] ? item[col].toString() : '';
      });
      return row;
    });
    if (data.length > 0) {
      const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
      XLSX.utils.sheet_add_aoa(ws, [this.displayedColumns], { origin: 'A1' });
      const wb: XLSX.WorkBook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
      XLSX.writeFile(wb, `${this.fileName}.xlsx`);
    }
    else {
      console.log('No data found');
    }
  }

}
