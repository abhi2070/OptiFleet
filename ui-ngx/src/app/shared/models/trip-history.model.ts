import { TripHistoryId } from "./id/trip-history-id";
import { BaseData, HasTenantId, ExportableEntity } from "./public-api";

export interface TripHistory extends BaseData<TripHistoryId>, HasTenantId, ExportableEntity<TripHistoryId> {
  tripId: TripHistoryId;
  tripName: string;
  startDate: Date | string;
  endDate: Date | string;
//   vehicleId: VehicleId;
  vehicleType: string;
//   driverIds: DriverId[];
  status: string;
//   sourcePosition: Position;
//   destinationPosition: Position;
  distanceCovered: number;
  tripDuration: number; // in minutes or hours
}

export interface TripHistoryInfo extends TripHistory {}
