import { DriverId } from "./id/driver-id";
import { BaseData, EntitySearchQuery, ExportableEntity, HasTenantId, TenantId } from "./public-api";

export interface Driver extends BaseData<DriverId>,HasTenantId,ExportableEntity<DriverId>{
    tenantId: TenantId;
    dateOfBirth: number;
  drivingLicenseNumber: number;
  drivingLicenseValidity: number;
  gender: string;
  name: string;
  serviceStartDate: number;
  status: string | null;
  profilePhoto?: string;
  drivingLicenseDocument?: string;
  phoneNumber: string;
}

export interface DriverInfo extends Driver{

}

export interface DriverSearchQuery extends EntitySearchQuery{
    DriverTypes: Array<string>;
}
