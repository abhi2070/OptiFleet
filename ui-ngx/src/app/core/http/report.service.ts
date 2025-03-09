import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, catchError, switchMap } from 'rxjs/operators';
import * as XLSX from 'xlsx';

import { AppState, AttributeService, DeviceService, getCurrentAuthUser } from '@app/core/public-api';
import { TimeseriesData, DeviceIds } from '@app/shared/public-api';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  constructor(
    private attributeService: AttributeService,
    private deviceService: DeviceService
  ) {}

  generateReport(reportType: string, filterOptions: any): Observable<Blob | null> {
    return this.fetchDataBasedOnFilter(filterOptions).pipe(
      map(data => {
        if (!data || data.length === 0) {
          return null;
        }
        const tableData = this.prepareTableData(data);
        return this.createReport(tableData, reportType);
      }),
      catchError(error => {
        return of(null);
      })
    );
  }

  fetchDataBasedOnFilter(filterOptions: { category: string; available: string }): Observable<any[]> {
    const { category, available } = filterOptions;

    if (category === '1') { // Device Name
      return this.deviceService.getDeviceInfo(available).pipe(
        switchMap(device => {
          if (!device) {
            return of([]);
          }
          return this.attributeService.getTimeseriesKeyById(device.id.entityType, device.id.id).pipe(
            switchMap(keys => {
              if (!keys || keys.length === 0) {
                return of([]);
              }
              const startTime = device.createdTime;
              const endTime = Date.now();
              return this.attributeService.getAllTimeseries(device.id.entityType, device.id.id, keys, startTime, endTime).pipe(
                map(timeseriesData => {
                  if (!timeseriesData || Object.keys(timeseriesData).length === 0) {
                    return [];
                  }
                  return [{
                    deviceId: device.id.id,
                    deviceType: device.type,
                    ...Object.fromEntries(keys.map(key => [
                      key,
                      timeseriesData[key] && timeseriesData[key].length > 0
                        ? [{ value: timeseriesData[key][0].value, ts: timeseriesData[key][0].ts }]
                        : null
                    ]))
                  }];
                })
              );
            })
          );
        }),
        catchError(error => {
          return of([]);
        })
      );
    } else if (category === '2') { // Device Type
      return this.deviceService.getAllIdByDeviceType(available).pipe(
        switchMap((deviceIds: DeviceIds) => {
          const observables = Object.values(deviceIds).map(deviceId =>
            this.attributeService.getDeviceTimeseriesLatestById(deviceId).pipe(
              map(timeseries => ({
                deviceId,
                deviceType: available,
                ...timeseries
              }))
            )
          );
          return forkJoin(observables);
        }),
        catchError(error => {
          return of([]);
        })
      );
    } else {
      return of([]);
    }
  }

  private prepareTableData(data: any[]): any[] {
    if (!data || !Array.isArray(data) || data.length === 0) {
      return [];
    }

    const columns = ['deviceId', 'deviceType', ...new Set(data.flatMap(item => Object.keys(item).filter(key => key !== 'deviceId' && key !== 'deviceType')))];

    return data.map(item => {
      const newItem: any = {
        deviceId: item.deviceId || 'Unknown',
        deviceType: item.deviceType || 'Unknown'
      };
      columns.forEach(column => {
        if (column !== 'deviceId' && column !== 'deviceType') {
          newItem[column] = item[column] && item[column][0] ? item[column][0].value : null;
        }
      });
      return newItem;
    });
  }
  private createReport(data: any[], type: string): Blob | null {
    if (!data || data.length === 0) {
      return null;
    }

    if (type === 'CSV') {
      return this.createCSV(data);
    } else if (type === 'Excel') {
      return this.createExcel(data);
    }
    return null;
  }

  private createCSV(data: any[]): Blob {
    const headers = Object.keys(data[0]);
    const csvRows = [
      headers.join(','),
      ...data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
    ];
    const csvString = csvRows.join('\n');
    return new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
  }

  private createExcel(data: any[]): Blob {
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    const excelBuffer: any = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    return new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  }

}
