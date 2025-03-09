import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { Trip, TripInfo,  } from '@shared/models/trip.model';

@Injectable({
  providedIn: 'root'
})
export class TripService {
  constructor(private http: HttpClient) {}

  public getTrip(tripId: string, config?: RequestConfig): Observable<Trip> {
    return this.http.get<Trip>(`/api/trips/${tripId}`, defaultHttpOptionsFromConfig(config));
  }

  public getTripInfo(tripId: string, config?: RequestConfig): Observable<TripInfo> {
    return this.http.get<TripInfo>(`/api/trip/info/${tripId}`, defaultHttpOptionsFromConfig(config));
  }

  public getTenantTripInfos(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<TripInfo>> {
    return this.http.get<PageData<TripInfo>>(`/api/tenant/trips${pageLink.toQuery()}&type=${type}`, defaultHttpOptionsFromConfig(config));
  }

  public saveTrip(trip: Trip, config?: RequestConfig): Observable<Trip> {
    return this.http.post<Trip>('/api/trip', trip, defaultHttpOptionsFromConfig(config));
  }

  public deleteTrip(tripId: string, config?: RequestConfig) {
    return this.http.delete(`/api/trips/${tripId}`, defaultHttpOptionsFromConfig(config));
  }

}