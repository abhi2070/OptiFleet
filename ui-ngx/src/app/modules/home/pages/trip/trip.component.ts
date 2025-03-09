import { ChangeDetectorRef, Component, ElementRef, Input, NgZone, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { forkJoin, Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, tap } from 'rxjs/operators';

import { TripDialogComponent } from './trip-dialog/trip-dialog.component';
import { TripService } from '@app/core/http/trip.service';
import { ConfirmDialogComponent } from '@app/shared/components/dialog/confirm-dialog.component';
import { VehicleService } from '@app/core/http/vehicle.service';
import { DriverService } from '@app/core/http/driver.service';
import { Vehicle } from '@app/shared/models/vehicle.model';
import { Driver } from '@app/shared/models/driver.model';
import { AttributeService, DeviceService } from '@app/core/public-api';
import { Device, PageLink, TsValue } from '@app/shared/public-api';
import { Position, RouteInfo, Trip, TripInfo } from '@app/shared/models/trip.model';
import { HttpClient } from '@angular/common/http';

interface TripEvent {
  id: string;
  type: string;
  timestamp: Date;
  coordinates: {
    lat: number;
    lng: number;
  };
  location: {
    address: string;
  };
  driverName: string;
  images?: Array<{
    thumbnailUrl: string;
    url: string;
    description?: string;
  }>;
}
interface MergedDeviceData {
  name: string;
  deviceid: string;
  imei: string;
  createdTime: number;
  distanceTraveled: number;
  internalBattery: number;
  rpm: string;
  speed: number;
  wireSensorData: any;
  latitude: number;
  longitude: number;
  ignition: any;
  sos: any;
  armDisarm: any;
  harshEvent: any;
  batteryStatus: any;
  id?: any;
  address?: string;
  addressStatus?: 'loading' | 'success' | 'error' | 'not found';
}
interface DeviceStatus {
  status: 'on' | 'off' | 'active' | 'inactive' | 'armed' | 'disarmed' | 'unknown';
  label: string;
  timestamp: number | null;
  value?: number;
}

@Component({
  selector: 'tb-trip',
  templateUrl: './trip.component.html',
  styleUrls: ['./trip.component.scss']
})
export class TripComponent implements OnInit {
  selectedTrip: Trip | null = null;
  selectedVehicle: Vehicle | null = null;
  public drivers: Driver[] = [];
public isLoadingVehicle = false;
public isLoadingDrivers = false;
public isLoadingDevices = false;
  deviceList: Device[] = [];
  originalLiveTrips: Trip[] = [];
  originalScheduledTrips: Trip[] = [];
  liveTrips: Trip[] = [];
  scheduledTrips: Trip[] = [];
  isLoading = false;
  searchTerm$ = new Subject<string>();

  displayedColumns: string[] = ['name', 'status', 'sourcePosition', 'destinationPosition', 'actions'];
  dataSource = new MatTableDataSource<Trip>([]);
  filteredTrips: Trip[] = [];
  isLeftPanelCollapsed = true;
  isRightPanelCollapsed = true;
  liveTripCount = 0;
  scheduledTripCount = 0;
  pageLink: PageLink = new PageLink(10, 0);
  routeInfo: RouteInfo;
  mergedDeviceData: MergedDeviceData[] = [];


  tripHistory: TripEvent[] = [
    {
      id: 'TRIP001',
      type: 'DELIVERY',
      timestamp: new Date('2025-01-31T10:30:00'),
      coordinates: {
        lat: 40.7128,
        lng: -74.0060
      },
      location: {
        address: '350 5th Ave, New York, NY 10118'
      },
      driverName: 'John Smith',
      images: [
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Delivery confirmation signature'
        },
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Package at delivery location'
        },
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Building entrance'
        }
      ]
    },
    {
      id: 'TRIP002',
      type: 'PICKUP',
      timestamp: new Date('2025-01-31T14:15:00'),
      coordinates: {
        lat: 40.7589,
        lng: -73.9851
      },
      location: {
        address: '45 Rockefeller Plaza, New York, NY 10111'
      },
      driverName: 'Sarah Johnson',
      images: [
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Package condition at pickup'
        },
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Loading confirmation'
        }
      ]
    },
    {
      id: 'TRIP003',
      type: 'DELAY',
      timestamp: new Date('2025-01-31T16:45:00'),
      coordinates: {
        lat: 40.7527,
        lng: -73.9772
      },
      location: {
        address: '405 Lexington Ave, New York, NY 10174'
      },
      driverName: 'Michael Brown',
      images: [
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Traffic condition'
        },
        {
          thumbnailUrl: '/api/placeholder/100/100',
          url: '/api/placeholder/800/600',
          description: 'Alternate route map'
        }
      ]
    }
  ];
  @Input() isLoadingHistory = false;
  @Input() totalDistance = 0;
  @ViewChild('leftPanelContainer') leftPanelContainer!: ElementRef;
  @ViewChild('rightPanelContainer') rightPanelContainer!: ElementRef;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild('mapDialog') mapDialog: any;
   readonly defaultPosition:  Position = {
    latitude:28.5823,
    longitude: 77.0500,
    address: 'N/A'
  };
  readonly defaultSourcePosition:  Position = {
    latitude:28.7041,
    longitude: 77.1025,
    address: 'N/A'
  };
  readonly defaultDestinationPosition:  Position = {
    latitude:12.9716,
    longitude: 77.5946,
    address: 'N/A'
  };


  constructor(
    private dialog: MatDialog,
    private tripService: TripService,
    private vehicleService: VehicleService,
    private driverService: DriverService,
    private deviceService: DeviceService,
    private attributeService: AttributeService,
    private cdr: ChangeDetectorRef,
    private http: HttpClient,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    this.loadTrips();
    this.searchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTrips(term);
    });
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  toggleLeftPanel(event: Event) {
    event.preventDefault();
    this.isLeftPanelCollapsed = !this.isLeftPanelCollapsed;
  }

  toggleRightPanel(event: Event) {
    event.preventDefault();
    this.isRightPanelCollapsed = !this.isRightPanelCollapsed;
  }
  onRouteInfoUpdated(info: RouteInfo) {
    this.routeInfo = info;
  }

  handleMarkerClick(markerData: any) {
    console.log('Marker clicked:', markerData);

    this.isRightPanelCollapsed = false;
  }

  getEventIcon(type: string): string {
    const icons = {
      'PICKUP': 'local_shipping',
      'DELIVERY': 'done',
      'CHECKPOINT': 'flag',
      'DELAY': 'timer',
      'STATUS_CHANGE': 'swap_horiz',
      'LOCATION_UPDATE': 'location_on'
    };
    return icons[type] || 'info';
  }
  openImageDialog(image: {url: string, description: string}) {
  }


  getEventTypeClass(type: string): string {
    return `event-type-${type.toLowerCase()}`;
  }

  formatEventType(type: string): string {
    return type.replace('_', ' ').toLowerCase()
      .replace(/\b\w/g, l => l.toUpperCase());
  }


  async loadTrips(): Promise<void> {
    try {
      this.isLoading = true;
      const tripsResponse = await this.tripService.getTenantTripInfos(this.pageLink).toPromise();
      
      if (tripsResponse && tripsResponse.data) {
        this.liveTrips = tripsResponse.data
          .filter(trip => trip.status === 'LIVE')
          .map(trip => ({
            ...trip,
            sourcePosition: trip.sourcePosition || { latitude: 12.9716, longitude: 77.5946, address: 'N/A' },
            destinationPosition: trip.destinationPosition || { latitude: 28.7041, longitude: 77.1025, address: 'N/A' }
          }));
        
        this.scheduledTrips = tripsResponse.data
          .filter(trip => trip.status === 'SCHEDULED')
          .map(trip => ({
            ...trip,
            sourcePosition: trip.sourcePosition || { latitude: 28.7041, longitude: 77.1025, address: 'N/A' },
            destinationPosition: trip.destinationPosition || { latitude: 12.9716, longitude: 77.5946, address: 'N/A' }
          }));
        
        this.originalLiveTrips = [...this.liveTrips];
        this.originalScheduledTrips = [...this.scheduledTrips];
        
        this.liveTripCount = this.liveTrips.length;
        this.scheduledTripCount = this.scheduledTrips.length;
        
        this.refreshData();
      }
      
      this.cdr.detectChanges();
    } catch (error) {
      console.error('Error loading trips:', error);
      this.handleError('Failed to load trips. Please try again later.');
    } finally {
      this.isLoading = false;
    }
  }


async onTripSelected(trip: TripInfo): Promise<void> {
  try {
    this.isLoading = true;
    const tripInfo = await this.tripService.getTripInfo(trip.id.id).toPromise();
    
    if (tripInfo) {
      this.selectedTrip = {
        ...tripInfo,
        sourcePosition: tripInfo.sourcePosition || this.defaultSourcePosition,
        destinationPosition: tripInfo.destinationPosition || this.defaultDestinationPosition,
        currentPosition: {
          latitude: 28.5823, 
          longitude: 77.0500,
          address: 'Test Current Position'
        }
      };
      
      await this.loadTripDetails(this.selectedTrip);
    }
  } catch (error) {
    console.error('Error loading trip details:', error);
    this.handleError('Failed to load trip details. Please try again later.');
    this.resetSelection();
  } finally {
    this.isLoading = false;
    this.cdr.detectChanges();
  }
}

  private async loadTripDetails(tripInfo: Trip): Promise<void> {
    this.isLoadingVehicle = true;
    this.isLoadingDrivers = true;
    this.isLoadingDevices = true;
  
    try {
      if (tripInfo.assignedVehicle) {
        try {
          const vehicleResponse = await this.vehicleService.getVehicle(tripInfo.assignedVehicle).toPromise();
          this.selectedVehicle = vehicleResponse;
          if (this.selectedVehicle?.device?.length > 0) {
            const deviceResponses = await Promise.all(
              this.selectedVehicle.device.map(deviceId => 
                this.deviceService.getDevice(deviceId).toPromise()
              )
            );
            this.deviceList = deviceResponses;
            if (this.deviceList.length > 0) {
              this.loadDeviceTelemetry();
            }
          } else {
            this.deviceList = [];
            this.mergedDeviceData = [];
          }
        } catch (error) {
          console.error('Error loading vehicle details:', error);
          this.handleError('Failed to load vehicle details.');
          this.selectedVehicle = null;
          this.deviceList = [];
        } finally {
          this.isLoadingVehicle = false;
          this.isLoadingDevices = false;
        }
      } else {
        this.selectedVehicle = null;
        this.deviceList = [];
        this.isLoadingVehicle = false;
        this.isLoadingDevices = false;
      }
  
      if (tripInfo.assignedDriver?.length > 0) {
        try {
          const driverResponses = await Promise.all(
            tripInfo.assignedDriver.map(driverId =>
              this.driverService.getDriverInfo(driverId).toPromise()
            )
          );
          this.drivers = driverResponses
            .filter(driver => driver !== null)
            .map(driver => ({
              ...driver,
              status: driver.status || 'INACTIVE'
            } as Driver));
  
        } catch (error) {
          console.error('Error loading drivers details:', error);
          this.handleError('Failed to load drivers details.');
          this.drivers = [];
        } finally {
          this.isLoadingDrivers = false;
        }
      } else {
        this.drivers = [];
        this.isLoadingDrivers = false;
      }
  
    } catch (error) {
      console.error('Error in loadTripDetails:', error);
      this.handleError('Failed to load trip details.');
      this.selectedVehicle = null;
      this.deviceList = [];
      this.drivers = [];
    } finally {
      this.isLoadingVehicle = false;
      this.isLoadingDrivers = false;
      this.isLoadingDevices = false;
    }
  }

  private loadDeviceTelemetry(): void {
    const deviceIds = this.deviceList.map(device => device.id.id);
    
    this.fetchAndMergeData(deviceIds, this.deviceList).subscribe(
      (mergedData) => {
        this.mergedDeviceData = mergedData;
        this.cdr.detectChanges();
      },
      (error) => {
        console.error('Error loading device telemetry:', error);
        this.handleError('Failed to load device telemetry data.');
      }
    );
  }

  private fetchAndMergeData(deviceIds: string[], deviceInfos: Device[]): Observable<MergedDeviceData[]> {
    const timeseriesObservables = deviceIds.map(id =>
      this.attributeService.getDeviceTimeseriesLatestById(id).pipe(
        tap(timeseries => console.log('Fetched timeseries for device', id, ':', timeseries))
      )
    );

    return forkJoin(timeseriesObservables).pipe(
      map((timeseriesDataArray) => {
        const devices = deviceInfos.map((deviceInfo, index) => {
          const timeseries = timeseriesDataArray[index];
          const latitude = this.getLatestValueAsNumber(timeseries.latitude);
          const longitude = this.getLatestValueAsNumber(timeseries.longitude);
          
          const device: MergedDeviceData = {
            ...deviceInfo,
            name: deviceInfo.name,
            deviceid: deviceInfo.id.id,
            imei: this.getLatestValueAsString(timeseries.IMEI),
            createdTime: deviceInfo.createdTime,
            distanceTraveled: this.getLatestValueAsNumber(timeseries.odometer),
            internalBattery: this.getLatestValueAsNumber(timeseries.Internal_battery),
            rpm: this.getLatestValueAsString(timeseries.RPM_Data),
            speed: this.getLatestValueAsNumber(timeseries.speed),
            wireSensorData: this.getLatestValue(timeseries.Wire_sensor_data),
            latitude,
            longitude,
            ignition: this.getBinaryStatus(timeseries.IGN, 'ignition'),
            sos: this.getBinaryStatus(timeseries.SOS, 'sos'),
            armDisarm: this.getBinaryStatus(timeseries.ARM_DISARM, 'security'),
            harshEvent: this.getLatestValue(timeseries.HA_HB),
            batteryStatus: this.getLatestValue(timeseries.MP),
            addressStatus: 'loading'
          };
          if (latitude && longitude) {
            this.getAddress(device, latitude, longitude);
          } else {
            device.addressStatus = 'error';
            device.address = 'Invalid coordinates';
          }

          return device;
        });

        return devices;
      })
    );
  }

  private getAddress(device: MergedDeviceData, latitude: number, longitude: number): void {
    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}&accept-language=en`;
    
    this.http.get(url).subscribe(
      (response: any) => {
        this.ngZone.run(() => {
          if (response && response.address) {
            const addressParts = [
              response.address.road,
              response.address.suburb,
              response.address.city_district,
              response.address.city,
              response.address.state,
              response.address.postcode,
              response.address.country
            ].filter(Boolean);
            
            device.address = addressParts.join(', ');
            device.addressStatus = 'success';
          } else {
            device.addressStatus = 'not found';
            device.address = 'Address not found';
          }
          this.cdr.detectChanges();
        });
      },
      (error) => {
        this.ngZone.run(() => {
          device.addressStatus = 'error';
          device.address = 'Error fetching address';
          this.cdr.detectChanges();
        });
      }
    );
  }

  getLatestValue(tsValues: TsValue[] | undefined): any {
    if (tsValues && tsValues.length > 0) {
      const latestValue = tsValues[tsValues.length - 1].value;
      console.log('Latest value:', latestValue);
      return latestValue;
    }
    console.log('No values found');
    return null;
  }
  getLatestValueAsString(tsValues: TsValue[] | undefined): string {
    const value = this.getLatestValue(tsValues);
    return value !== null ? String(value) : 'N/A';
  }
  private getBinaryStatus(tsValues: TsValue[] | undefined, type: 'ignition' | 'sos' | 'security'): DeviceStatus {
    const value = this.getLatestValue(tsValues);
    if (value === null) {
      return {
        status: 'unknown',
        label: 'Unknown',
        timestamp: null
      };
    }

    const numValue = Number(value);
    const timestamp = tsValues && tsValues.length > 0 ? tsValues[tsValues.length - 1].ts : null;

    switch (type) {
      case 'ignition':
        return {
          status: numValue === 1 ? 'on' : 'off',
          label: numValue === 1 ? 'ON' : 'OFF',
          timestamp,
          value: numValue
        };
      case 'sos':
        return {
          status: numValue === 1 ? 'active' : 'inactive',
          label: numValue === 1 ? 'ACTIVE' : 'INACTIVE',
          timestamp,
          value: numValue
        };
      case 'security':
        return {
          status: numValue === 1 ? 'armed' : 'disarmed',
          label: numValue === 1 ? 'ARMED' : 'DISARMED',
          timestamp,
          value: numValue
        };
      default:
        return {
          status: 'unknown',
          label: 'Unknown',
          timestamp: null
        };
    }
  }

  getLatestValueAsNumber(tsValues: TsValue[] | undefined): number {
    const value = this.getLatestValue(tsValues);
    return value !== null ? Number(value) : 0;
  }
  private resetSelection(): void {
    this.selectedTrip = null;
    this.selectedVehicle = null;
    this.drivers =[]; 
    this.deviceList = [];
  }

  async refreshData(): Promise<void> {
    try {
      this.isLoading = true;

      const tripsResponse = await this.tripService.getTenantTripInfos(this.pageLink).toPromise();
      
      if (tripsResponse && tripsResponse.data) {
        this.liveTrips = tripsResponse.data
          .filter(trip => trip.status === 'LIVE')
          .map(trip => ({
            ...trip,
            sourcePosition: trip.sourcePosition || { latitude: 12.9716, longitude: 77.5946, address: 'N/A' },
            destinationPosition: trip.destinationPosition || { latitude: 28.7041, longitude: 77.1025, address: 'N/A' }
          }));
        
        this.scheduledTrips = tripsResponse.data
          .filter(trip => trip.status === 'SCHEDULED')
          .map(trip => ({
            ...trip,
            sourcePosition: trip.sourcePosition || { latitude: 12.9716, longitude: 77.5946, address: 'N/A' },
            destinationPosition: trip.destinationPosition || { latitude: 28.7041, longitude: 77.1025, address: 'N/A' }
          }));
        
        this.originalLiveTrips = [...this.liveTrips];
        this.originalScheduledTrips = [...this.scheduledTrips];
        
        this.liveTripCount = this.liveTrips.length;
        this.scheduledTripCount = this.scheduledTrips.length;

        this.dataSource.data = [...this.liveTrips, ...this.scheduledTrips];

        if (this.selectedTrip) {
          const updatedTripInfo = await this.tripService.getTripInfo(this.selectedTrip.id.id).toPromise();
          
          if (updatedTripInfo) {
            this.selectedTrip = {
              ...updatedTripInfo,
              sourcePosition: updatedTripInfo.sourcePosition || this.defaultSourcePosition,
              destinationPosition: updatedTripInfo.destinationPosition || this.defaultDestinationPosition,
              currentPosition: updatedTripInfo.currentPosition || {
                latitude: 28.5823,
                longitude: 77.0500,
                address: 'Test Current Position'
              }
            };

            await this.loadTripDetails(this.selectedTrip);
          }
        }
      }
      
      this.cdr.detectChanges();
    } catch (error) {
      console.error('Error refreshing data:', error);
      this.handleError('Failed to refresh data. Please try again later.');
    } finally {
      this.isLoading = false;
    }
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(TripDialogComponent, {
      width: '600px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result.status === 'IN_PROGRESS') {
          this.liveTrips = [...this.liveTrips, result];
          this.originalLiveTrips = [...this.liveTrips];
        } else if (result.status === 'SCHEDULED') {
          this.scheduledTrips = [...this.scheduledTrips, result];
          this.originalScheduledTrips = [...this.scheduledTrips];
        }
        this.refreshData();
      }
    });
  }

  searchTrips(query: string): void {
    const searchLower = query.toLowerCase().trim();
  
    this.liveTrips = this.originalLiveTrips.filter(trip =>
      trip.name.toLowerCase().includes(searchLower) ||
      (trip.sourcePosition?.address?.toLowerCase().includes(searchLower) || false) ||
      (trip.destinationPosition?.address?.toLowerCase().includes(searchLower) || false)
    );
  
    this.scheduledTrips = this.originalScheduledTrips.filter(trip =>
      trip.name.toLowerCase().includes(searchLower) ||
      (trip.sourcePosition?.address?.toLowerCase().includes(searchLower) || false) ||
      (trip.destinationPosition?.address?.toLowerCase().includes(searchLower) || false)
    );
  
    this.liveTripCount = this.liveTrips.length;
    this.scheduledTripCount = this.scheduledTrips.length;
    this.refreshData();
  }

  openStopTripDialog(trip: Trip) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Stop Trip',
        message: `Are you sure you want to stop the trip "${trip.name}"?`,
        cancel: 'Cancel',
        ok: 'Stop'
      }
    });

  }

  openStartTripDialog(trip: Trip) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Start Trip',
        message: `Are you sure you want to start the trip "${trip.name}"?`,
        cancel: 'Cancel',
        ok: 'Start'
      }
    });
  }

  private handleError(message: string): void {
    console.error(message);
  }
}