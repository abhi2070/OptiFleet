import { VehicleId } from "./id/vehicle-id";
import { BaseData, EntitySearchDirection, EntitySearchQuery, ExportableEntity, HasTenantId, TenantId } from "./public-api";



export interface Vehicle extends BaseData<VehicleId>,HasTenantId, ExportableEntity<VehicleId>{
    tenantId?: TenantId;
    vehiclenumber: string;
    type: string;
    status: string | null;
    permit: string;
    nextService: number;
    device: string[];
    registrationCertificate?: string;
    insuranceCertificate?: string;
    pucCertificate?: string;
    requiredPermits?: string;


}

export interface VehicleInfo extends Vehicle{

}

export interface VehicleSearchQuery extends EntitySearchQuery{
    VehicleTypes: Array<string>;
}



