import { TripId } from "./id/trip-id";
import { BaseData, EntitySearchQuery, ExportableEntity, HasTenantId, TenantId } from "./public-api";

export interface Trip extends BaseData<TripId>,HasTenantId,ExportableEntity<TripId> {
    name: string;
  startDate: Date | string;
  endDate: Date | string;
  vehicleType: string;
  assignedVehicle: string;
  assignedDriver: string[];
  status: string;
  sourcePosition: Position;
  destinationPosition: Position;
  currentPosition: Position
}

export interface TripInfo extends Trip{

}

export interface Position {
    address: string;
    latitude: number;
    longitude: number;
  }

  export interface RouteInfo {
    distance: string;
    duration: string;
    remainingDistance: string;
    remainingDuration: string;
  }