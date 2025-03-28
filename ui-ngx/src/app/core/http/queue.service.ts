

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { Observable } from 'rxjs';
import { QueueInfo, ServiceType } from '@shared/models/queue.models';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';

@Injectable({
  providedIn: 'root'
})
export class QueueService {

  constructor(
    private http: HttpClient
  ) { }

  public getQueueById(queueId: string, config?: RequestConfig): Observable<QueueInfo> {
    return this.http.get<QueueInfo>(`/api/queues/${queueId}`, defaultHttpOptionsFromConfig(config));
  }

  public getQueueByName(queueName: string, config?: RequestConfig): Observable<QueueInfo> {
    return this.http.get<QueueInfo>(`/api/queues/name/${queueName}`, defaultHttpOptionsFromConfig(config));
  }

  public getTenantQueuesByServiceType(pageLink: PageLink,
                                      serviceType: ServiceType,
                                      config?: RequestConfig): Observable<PageData<QueueInfo>> {
    return this.http.get<PageData<QueueInfo>>(`/api/queues${pageLink.toQuery()}&serviceType=${serviceType}`,
      defaultHttpOptionsFromConfig(config));
  }

  public saveQueue(queue: QueueInfo, serviceType: ServiceType, config?: RequestConfig): Observable<QueueInfo> {
    return this.http.post<QueueInfo>(`/api/queues?serviceType=${serviceType}`, queue, defaultHttpOptionsFromConfig(config));
  }

  public deleteQueue(queueId: string) {
    return this.http.delete(`/api/queues/${queueId}`);
  }
}
