import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Driver, DriverInfo, DriverSearchQuery } from '@app/shared/models/driver.model';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { filter, map, Observable } from 'rxjs';
import { PageData, PageLink } from '@app/shared/public-api';

@Injectable({
  providedIn: 'root'
})
export class DriverService {

  constructor(private http: HttpClient) { }


  public getDriverInfo(driverId: string, config?: RequestConfig): Observable<DriverInfo> {
    return this.http.get<DriverInfo>(`/api/driver/info/${driverId}`, defaultHttpOptionsFromConfig(config));
  }

  public getDriverInfos(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<DriverInfo>> {
    return this.http.get<PageData<DriverInfo>>(`/api/tenant/drivers${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  public saveDriver(driver: Driver, config?: RequestConfig): Observable<Driver> {
    return this.http.post<Driver>('/api/driver', driver, defaultHttpOptionsFromConfig(config));
  }

  public deleteDriver(driverId: string, config?: RequestConfig) {
    return this.http.delete(`/api/driver/${driverId}`, defaultHttpOptionsFromConfig(config));
  }

  public findByQuery(query: DriverSearchQuery, config?: RequestConfig): Observable<Array<Driver>> {
    return this.http.post<Array<Driver>>('/api/driver', query, defaultHttpOptionsFromConfig(config));
  }

  public uploadDriverPhotoDocument(driverId: string, file: File, config?: RequestConfig): Observable<Driver> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<Driver>(
      `/api/driver/document/${driverId}`,
      formData,
      {
        headers: new HttpHeaders(),
        reportProgress: true,
        observe: 'response'
      }
    ).pipe(
      filter((response): response is HttpResponse<Driver> => response instanceof HttpResponse),
      map(response => response.body as Driver)
    );
  }

  public uploadDriverLicenseDocument(driverId: string, file: File, config?: RequestConfig): Observable<Driver> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', 'LICENSE');

    return this.http.post<Driver>(
      `/api/driver/document/${driverId}`,
      formData,
      {
        headers: new HttpHeaders(),
        reportProgress: true,
        observe: 'response'
      }
    ).pipe(
      filter((response): response is HttpResponse<Driver> => response instanceof HttpResponse),
      map(response => response.body as Driver)
    );
  }

  public verifyDriver(driverId: string, config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>(`/api/driver/${driverId}/verify`, { ...defaultHttpOptionsFromConfig(config),
    });
  }
}
