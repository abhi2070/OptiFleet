

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { forkJoin, Observable, of } from 'rxjs';
import { PageData } from '@shared/models/page/page-data';
import { Resource, ResourceInfo, ResourceType } from '@shared/models/resource.models';
import { catchError, mergeMap } from 'rxjs/operators';
import { isNotEmptyStr } from '@core/utils';
import { ResourcesService } from '@core/services/resources.service';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  constructor(
    private http: HttpClient,
    private resourcesService: ResourcesService
  ) {

  }

  public getResources(pageLink: PageLink, resourceType?: ResourceType, config?: RequestConfig): Observable<PageData<ResourceInfo>> {
    let url = `/api/resource${pageLink.toQuery()}`;
    if (isNotEmptyStr(resourceType)) {
      url += `&resourceType=${resourceType}`;
    }
    return this.http.get<PageData<ResourceInfo>>(url, defaultHttpOptionsFromConfig(config));
  }

  public getTenantResources(pageLink: PageLink, config?: RequestConfig): Observable<PageData<ResourceInfo>> {
    return this.http.get<PageData<ResourceInfo>>(`/api/resource/tenant${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  public getResource(resourceId: string, config?: RequestConfig): Observable<Resource> {
    return this.http.get<Resource>(`/api/resource/${resourceId}`, defaultHttpOptionsFromConfig(config));
  }

  public getResourceInfo(resourceId: string, config?: RequestConfig): Observable<ResourceInfo> {
    return this.http.get<Resource>(`/api/resource/info/${resourceId}`, defaultHttpOptionsFromConfig(config));
  }

  public downloadResource(resourceId: string): Observable<any> {
    return this.resourcesService.downloadResource(`/api/resource/${resourceId}/download`);
  }

  public saveResources(resources: Resource[], config?: RequestConfig): Observable<Resource[]> {
    let partSize = 100;
    partSize = resources.length > partSize ? partSize : resources.length;
    const resourceObservables: Observable<Resource>[] = [];
    for (let i = 0; i < partSize; i++) {
      resourceObservables.push(this.saveResource(resources[i], config).pipe(catchError(() => of({} as Resource))));
    }
    return forkJoin(resourceObservables).pipe(
      mergeMap((resource) => {
        resources.splice(0, partSize);
        if (resources.length) {
          return this.saveResources(resources, config);
        } else {
          return of(resource);
        }
      })
    );
  }

  public saveResource(resource: Resource, config?: RequestConfig): Observable<Resource> {
    return this.http.post<Resource>('/api/resource', resource, defaultHttpOptionsFromConfig(config));
  }

  public deleteResource(resourceId: string, config?: RequestConfig) {
    return this.http.delete(`/api/resource/${resourceId}`, defaultHttpOptionsFromConfig(config));
  }

}
