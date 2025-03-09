import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { BulkImportRequest, BulkImportResult } from '@shared/import-export/import-export.models';
import { EmailConfiguration, Schedulers, SchedulersInfo, SchedulersSearchQuery } from '@app/shared/models/schedulers.model';

@Injectable({
  providedIn: 'root'
})
export class SchedulersService {
  constructor(private http: HttpClient) { }


  public getScheduler(schedulerId: string, config?: RequestConfig): Observable<Schedulers> {
    return this.http.get<Schedulers>(`/api/schedulers/${schedulerId}`, defaultHttpOptionsFromConfig(config));
  }

  public getSchedulerInfo(schedulerId: string, config?: RequestConfig): Observable<SchedulersInfo> {
    return this.http.get<SchedulersInfo>(`/api/schedulers/info/${schedulerId}`, defaultHttpOptionsFromConfig(config));
  }

  public getTenantSchedulerInfos(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<SchedulersInfo>> {
    return this.http.get<PageData<SchedulersInfo>>(`/api/tenant/scheduler${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  public saveScheduler(scheduler: Schedulers, config?: RequestConfig): Observable<Schedulers> {
    return this.http.post<Schedulers>('/api/scheduler', scheduler, defaultHttpOptionsFromConfig(config));
  }

  public deleteScheduler(schedulerId: string, config?: RequestConfig) {
    return this.http.delete(`/api/schedulers/${schedulerId}`, defaultHttpOptionsFromConfig(config));
  }

  public findByQuery(query: SchedulersSearchQuery, config?: RequestConfig): Observable<Array<Schedulers>> {
    return this.http.post<Array<Schedulers>>('/api/schedulers', query, defaultHttpOptionsFromConfig(config));
  }

  public bulkImportSchedulers(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/schedulers/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }
  public toggleSchedulerStatus(schedulerId: string,config?: RequestConfig): Observable<Schedulers> {
    return this.http.put<Schedulers>(`/api/schedulers/${schedulerId}/status`, null, defaultHttpOptionsFromConfig(config));
}
public sendEmail(emailConfig: EmailConfiguration, attachments?: File[], start?: number, repeatSchedule: string = 'NONE', endTime?: number, config?: RequestConfig): Observable<EmailConfiguration> {
  const formData = new FormData();
  formData.append('emailConfig', JSON.stringify(emailConfig));

  if (attachments) {
    attachments.forEach((file, index) => {
      formData.append('attachments', file, file.name);
    });
  }

  const headers = new HttpHeaders();
  headers.append('Content-Type', 'multipart/form-data');

  const options = {
    ...defaultHttpOptionsFromConfig(config),
    headers: headers
  };

  return this.http.post<EmailConfiguration>('/api/sendEmail/scheduler', formData, options);
}

}
