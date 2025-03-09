import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Vehicle, VehicleInfo, VehicleSearchQuery } from '@app/shared/models/vehicle.model';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { PageData, PageLink } from '@app/shared/public-api';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  constructor(private http: HttpClient) { }


  public getVehicle(vehicleId: string, config?: RequestConfig): Observable<Vehicle> {
    return this.http.get<Vehicle>(`/api/vehicle/${vehicleId}`, defaultHttpOptionsFromConfig(config));
}


public getVehicleInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<VehicleInfo>> {
    return this.http.get<PageData<VehicleInfo>>(`/api/tenant/vehicle${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
}

public saveVehicle(vehicle: Vehicle, config?: RequestConfig): Observable<Vehicle> {
    return this.http.post<Vehicle>('/api/vehicle', vehicle, defaultHttpOptionsFromConfig(config));
}

public updateVehicle(vehicleId: string, vehicle: Vehicle, config?: RequestConfig): Observable<Vehicle> {
    return this.http.put<Vehicle>(`/api/vehicle/update/${vehicleId}`, vehicle, defaultHttpOptionsFromConfig(config));
}
public deleteVehicle(vehicleId: string, config?: RequestConfig) {
    return this.http.delete(`/api/vehicle/${vehicleId}`, defaultHttpOptionsFromConfig(config));
}

public findByQuery(query: VehicleSearchQuery, config?: RequestConfig): Observable<Array<Vehicle>> {
    return this.http.post<Array<Vehicle>>('/api/vehicles', query, defaultHttpOptionsFromConfig(config));
}

public uploadVehicleDocuments(
  vehicleId: string,
  documents: {
    registrationCertificate?: File | null,
    insuranceCertificate?: File | null,
    pucCertificate?: File | null,
    requiredPermits?: File | null
  },
  config?: RequestConfig
): Observable<Vehicle> {
  const formData = new FormData();

  (Object.keys(documents) as Array<keyof typeof documents>).forEach(key => {
    const file = documents[key];

    if (file instanceof File) {
      formData.append(key, file);
    }
  });
  const headers = new HttpHeaders();
  const options = {
    headers: headers
  };

  return this.http.put<Vehicle>(
    `/api/vehicle/documents/update/${vehicleId}`,
    formData,
    options
  ).pipe(
    tap(response => {
    }),
    catchError(error => {
      return throwError(() => new Error('Document upload failed'));
    })
  );
}

public verifyVehicle(vehicleId: string, config?: RequestConfig): Observable<boolean> {
  return this.http.get<boolean>(`/api/vehicle/${vehicleId}/verify`, { ...defaultHttpOptionsFromConfig(config),
  });
}
}

