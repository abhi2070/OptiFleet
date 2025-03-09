import { 
  Component, 
  Input, 
  OnInit, 
  AfterViewInit, 
  OnChanges, 
  SimpleChanges, 
  ChangeDetectorRef, 
  NgZone, 
  ViewChild, 
  EventEmitter,
  Output
} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GoogleMap, MapMarker } from '@angular/google-maps';
import { BehaviorSubject } from 'rxjs';
import { Position, RouteInfo } from '@app/shared/models/trip.model';

interface TripMarker {
  position: google.maps.LatLngLiteral;
  options?: google.maps.MarkerOptions;
}


@Component({
  selector: 'tb-map-dialog',
  templateUrl: './map-dialog.component.html',
  styleUrls: ['./map-dialog.component.scss']
})
export class MapDialogComponent implements OnInit, AfterViewInit, OnChanges {
  @Input() selectedTrip: any;
  @Input() showCurrentPosition: boolean = false;
  @Input() showRoute: boolean = false;
  @ViewChild('map') map!: GoogleMap;
  @Output() routeInfoUpdated = new EventEmitter<RouteInfo>();
  @Output() markerClicked = new EventEmitter<any>();

  routeInfo: RouteInfo = {
    distance: '',
    duration: '',
    remainingDistance: '',
    remainingDuration: ''
  };

  apiKey ='AIzaSyDoEx2kaGz3PxwbI9T7ccTSg5xjdw8Nw8Q';

  center: google.maps.LatLngLiteral;
  zoom = 13;
  options: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    zoomControl: true,
    scrollwheel: true,
    disableDoubleClickZoom: true,
    maxZoom: 18,
    minZoom: 3,
    mapTypeControl: true,
    streetViewControl: true,
    fullscreenControl: true
  };


  originMarker: TripMarker | null = null;
  destinationMarker: TripMarker | null = null;
  currentPositionMarker: TripMarker | null = null;

  routePath: google.maps.LatLngLiteral[] | null = null;
  routeOptions: google.maps.PolylineOptions = {
    strokeColor: '#0000FF',
    strokeOpacity: 0.7,
    strokeWeight: 3,
    geodesic: true
  };

  private readonly defaultLat = 12.9716;
  private readonly defaultLng = 77.5946;

  private readonly mockCurrentPosition: Position = {
    latitude: 21.1458,
    longitude: 79.0882,
    address: 'Nagpur, Maharashtra, India'
  };

  isLoading = false;
  address = '';
  addressStatus: 'loading' | 'error' | 'not found' | 'success' = 'loading';
  mapError = new BehaviorSubject<string | null>(null);

  private directionsService: google.maps.DirectionsService;
  private geocoder: google.maps.Geocoder;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {
    this.center = { lat: this.defaultLat, lng: this.defaultLng };
    this.directionsService = new google.maps.DirectionsService();
    this.geocoder = new google.maps.Geocoder();
  }

  ngOnInit() {
    try {
      if (this.selectedTrip) {
        
        if (!this.selectedTrip.currentPosition) {
          this.selectedTrip = {
            ...this.selectedTrip,
            currentPosition: {
              latitude: 21.1458,
              longitude: 79.0882,
              address: 'Nagpur, Maharashtra, India'
            }
          };
        }
        
        if (this.selectedTrip.currentPosition) {
          this.getAddress(
            this.selectedTrip.currentPosition.latitude,
            this.selectedTrip.currentPosition.longitude
          );
        }
      } else {
      }
    } catch (error) {
      console.error('Error in ngOnInit:', error);
      this.handleError('Error initializing map', error);
    }
  }
  
  ngAfterViewInit() {
    try {
      if (this.selectedTrip) {
        this.updateMapForTrip();
      }
    } catch (error) {
      this.handleError('Error setting up map', error);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      if (changes['selectedTrip']) {
        if (!changes['selectedTrip'].firstChange) {
          this.updateMapForTrip();
        }
      }
    } catch (error) {
      this.handleError('Error updating map', error);
    }
  }

  onMarkerClick(marker: any) {

    this.markerClicked.emit(marker);
  }

  private async updateMapForTrip(): Promise<void> {
    if (!this.selectedTrip) {
      return;
    }

    this.isLoading = true;
    try {
      const bounds = new google.maps.LatLngBounds();
      
      if (this.selectedTrip.sourcePosition) {
        this.originMarker = this.createMarker(
          this.selectedTrip.sourcePosition,
          'origin',
          '#2196F3', 
          'Origin'
        );
        if (this.originMarker) bounds.extend(this.originMarker.position);
      }

      if (this.selectedTrip.currentPosition) {
        this.currentPositionMarker = this.createMarker(
          this.selectedTrip.currentPosition,
          'current',
          '#2196F3',
          'Current Position'
        );
        if (this.currentPositionMarker) {
          bounds.extend(this.currentPositionMarker.position);
        }
      }

      if (this.selectedTrip.destinationPosition) {
        this.destinationMarker = this.createMarker(
          this.selectedTrip.destinationPosition,
          'destination',
          '#4CAF50',
          'Destination'
        );
        if (this.destinationMarker) bounds.extend(this.destinationMarker.position);
      }

      if (this.showRoute && this.selectedTrip.sourcePosition && this.selectedTrip.destinationPosition) {
        await this.calculateRoute(
          this.selectedTrip.sourcePosition,
          this.selectedTrip.destinationPosition
        );
      }

      if (this.map && !bounds.isEmpty()) {
        const padding = { top: 50, right: 50, bottom: 50, left: 50 };
        this.map.fitBounds(bounds, padding);
      }

      this.center = {
        lat: bounds.getCenter().lat(),
        lng: bounds.getCenter().lng()
      };

    } catch (error) {
      this.handleError('Error updating map markers and route', error);
    } finally {
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  private createMarker(
    position: Position,
    type: 'origin' | 'destination' | 'current',
    color: string,
    title: string
  ): TripMarker | null {
    if (!position || typeof position.latitude !== 'number' || typeof position.longitude !== 'number') {
      return null;
    }
    
    try {
      const markerPosition = { 
        lat: Number(position.latitude), 
        lng: Number(position.longitude) 
      };
  
      let icon: google.maps.Symbol;
      
      switch (type) {
        case 'origin':
          icon = {
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: '#2196F3', 
            fillOpacity: 1,
            strokeColor: '#FFFFFF',
            strokeWeight: 2,
            scale: 10
          };
          break;
          
        case 'destination':
          icon = {
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: '#4CAF50', 
            fillOpacity: 1,
            strokeColor: '#FFFFFF',
            strokeWeight: 2,
            scale: 8
          };
          break;
          
        case 'current':
          icon = {
            path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
            fillColor: '#2196F3',
            fillOpacity: 1,
            strokeColor: '#FFFFFF',
            strokeWeight: 2,
            scale: 6,
            rotation: 0
          };
          break;
      }
  
      const marker: TripMarker = {
        position: markerPosition,
        options: {
          position: markerPosition,
          map: this.map?.googleMap,
          title: title,
          icon: icon,
          animation: google.maps.Animation.DROP
        }
      };
  
      return marker;
    } catch (error) {
      this.handleError(`Error creating ${title} marker`, error);
      return null;
    }
  }


  private decodePolyline(encoded: string): google.maps.LatLngLiteral[] {
    const poly: google.maps.LatLngLiteral[] = [];
    let index = 0;
    const len = encoded.length;
    let lat = 0;
    let lng = 0;

    while (index < len) {
      let b;
      let shift = 0;
      let result = 0;

      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);

      const dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
      lat += dlat;

      shift = 0;
      result = 0;

      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);

      const dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
      lng += dlng;

      poly.push({
        lat: lat / 1e5,
        lng: lng / 1e5
      });
    }

    return poly;
  }

  private async calculateRoute(
    origin: Position,
    destination: Position
  ): Promise<void> {

    if (!origin?.latitude || !origin?.longitude || !destination?.latitude || !destination?.longitude) {
      console.error('Invalid coordinates for route calculation:', { origin, destination });
      return;
    }

    const url = `https://routes.googleapis.com/directions/v2:computeRoutes`;
    const body = {
      origin: {
        location: {
          latLng: {
            latitude: Number(origin.latitude),
            longitude: Number(origin.longitude)
          }
        }
      },
      destination: {
        location: {
          latLng: {
            latitude: Number(destination.latitude),
            longitude: Number(destination.longitude)
          }
        }
      },
      travelMode: 'DRIVE',
      routingPreference: 'TRAFFIC_AWARE',
      computeAlternativeRoutes: false,
      routeModifiers: {
        avoidTolls: false,
        avoidHighways: false,
        avoidFerries: false
      },
      languageCode: 'en-US',
      units: 'METRIC'
    };

    const headers = {
      'Content-Type': 'application/json',
      'X-Goog-Api-Key': this.apiKey,
      'X-Goog-FieldMask': 'routes.polyline.encodedPolyline,routes.duration,routes.distanceMeters,routes.staticDuration'
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();

      if (response?.routes?.[0]) {
        this.ngZone.run(() => {
          const route = response.routes[0];
          
          const totalDistanceKm = (route.distanceMeters / 1000).toFixed(1);
          const totalDurationMin = Math.round(parseInt(route.duration.substring(0, route.duration.length - 1)) / 60);
    
          if (this.selectedTrip?.currentPosition) {
            this.calculateRemainingDistance(
              this.selectedTrip.currentPosition,
              destination
            ).then(remainingInfo => {
              this.routeInfo = {
                distance: totalDistanceKm,
                duration: totalDurationMin.toString(),
                remainingDistance: remainingInfo.distance,
                remainingDuration: remainingInfo.duration
              };
              this.routeInfoUpdated.emit(this.routeInfo);
              this.cdr.detectChanges();
            });
          }

          if (route.polyline?.encodedPolyline) {
            this.routePath = this.decodePolyline(route.polyline.encodedPolyline);
          }
          
          this.cdr.detectChanges();
        });
      }
    } catch (error) {
      this.handleError('Routes API request failed', error);
    }
  }

  getMarkerOptions(marker: TripMarker): google.maps.MarkerOptions {
    let icon: google.maps.Symbol | null = null;
    
    switch(marker) {
      case this.originMarker:
        icon = {
          path: google.maps.SymbolPath.CIRCLE,
          fillColor: '#4CAF50',
          fillOpacity: 1,
          strokeColor: '#fff',
          strokeWeight: 2,
          scale: 8
        };
        break;
      case this.destinationMarker:
        icon = {
          path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
          fillColor: '#F44336',
          fillOpacity: 1,
          strokeColor: '#fff',
          strokeWeight: 2,
          scale: 8
        };
        break;
      case this.currentPositionMarker:
        icon = {
          path: google.maps.SymbolPath.CIRCLE,
          fillColor: '#2196F3',
          fillOpacity: 1,
          strokeColor: '#fff',
          strokeWeight: 2,
          scale: 8
        };
        break;
    }
  
    return {
      icon: icon || undefined,
      animation: google.maps.Animation.DROP,
      draggable: false
    };
  }

  private async calculateRemainingDistance(
    currentPosition: Position,
    destination: Position
  ): Promise<{ distance: string; duration: string }> {
    const url = `https://routes.googleapis.com/directions/v2:computeRoutes`;
    const body = {
      origin: {
        location: {
          latLng: {
            latitude: Number(currentPosition.latitude),
            longitude: Number(currentPosition.longitude)
          }
        }
      },
      destination: {
        location: {
          latLng: {
            latitude: Number(destination.latitude),
            longitude: Number(destination.longitude)
          }
        }
      },
      travelMode: 'DRIVE',
      routingPreference: 'TRAFFIC_AWARE',
      units: 'METRIC'
    };

    const headers = {
      'Content-Type': 'application/json',
      'X-Goog-Api-Key': this.apiKey,
      'X-Goog-FieldMask': 'routes.duration,routes.distanceMeters'
    };

    try {
      const response: any = await this.http.post(url, body, { headers }).toPromise();
      if (response?.routes?.[0]) {
        const route = response.routes[0];
        const distanceKm = (route.distanceMeters / 1000).toFixed(1);
        const durationMin = Math.round(parseInt(route.duration.substring(0, route.duration.length - 1)) / 60);
        return {
          distance: distanceKm,
          duration: durationMin.toString()
        };
      }
    } catch (error) {
      this.handleError('Error calculating remaining distance', error);
    }
    
    return { distance: 'N/A', duration: 'N/A' };
  }


  async getAddress(lat: number, lng: number): Promise<void> {
    this.addressStatus = 'loading';
    try {
      const result = await new Promise<google.maps.GeocoderResult[]>((resolve, reject) => {
        this.geocoder.geocode(
          { location: { lat, lng } },
          (results, status) => {
            if (status === 'OK' && results && results.length > 0) {
              resolve(results);
            } else {
              reject(new Error(`Geocoding failed: ${status}`));
            }
          }
        );
      });

      this.ngZone.run(() => {
        this.address = result[0].formatted_address;
        this.addressStatus = 'success';
        this.cdr.detectChanges();
      });
    } catch (error) {
      this.handleError('Error getting address', error);
      this.addressStatus = 'error';
    }
  }

  private handleError(context: string, error: any): void {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';
    console.error(`${context}:`, { error, errorMessage });
    this.mapError.next(`${context}: ${errorMessage}`);
  }

  isMapReady(): boolean {
    return !!this.map && !this.isLoading;
  }
  resetMap(): void {
    this.originMarker = null;
    this.destinationMarker = null;
    this.currentPositionMarker = null;
    this.routePath = null;
    this.center = { lat: this.defaultLat, lng: this.defaultLng };
    this.zoom = 13;
    this.mapError.next(null);
    this.cdr.detectChanges();
  }
}