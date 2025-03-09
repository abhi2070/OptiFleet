// import { Injectable } from '@angular/core';
// import {
//   Router, Resolve,
//   RouterStateSnapshot,
//   ActivatedRouteSnapshot
// } from '@angular/router';
// import { EntityTableColumn, EntityTableConfig } from '../../models/entity/entities-table-config.models';
// import { TripHistoryInfo } from '@app/shared/models/trip-history.model';
// import { select, Store } from '@ngrx/store';
// import { AppState } from '@core/core.state';
// import { DatePipe } from '@angular/common';
// import { TranslateService } from '@ngx-translate/core';
// import { BroadcastService } from '@app/core/public-api';
// import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
// import { TripHistoryComponent } from './trip-history.component';
// import { getCurrentAuthUser, selectAuthUser } from '@app/core/auth/auth.selectors';
// import { from, map, mergeMap, Observable, of, switchMap, take, tap } from 'rxjs';

// @Injectable({
//   providedIn: 'root'
// })
// export class TripHistoryTableConfigResolver{
//   private readonly config: EntityTableConfig<TripHistoryInfo> = new EntityTableConfig<TripHistoryInfo>();
//   authUser: any;

//   constructor(
//     private store: Store<AppState>,
//     // private tripHistoryService: TripHistoryService,
//     private translate: TranslateService,
//     private datePipe: DatePipe,
//     private router: Router,
//     private broadcast: BroadcastService
//   ) {
//     // this.config.entityType = EntityType.TRIP_HISTORY;
//     this.config.entityComponent = TripHistoryComponent;
//     this.config.entityTranslations = entityTypeTranslations.get(EntityType.TRIP_HISTORY);
//     this.config.entityResources = entityTypeResources.get(EntityType.TRIP_HISTORY);
//     // this.config.loadEntity = id => this.tripHistoryService.getTripHistoryInfo(id.id);
    
//     this.authUser = getCurrentAuthUser(this.store);

//     this.config.deleteEntityTitle = tripHistory =>
//       this.translate.instant('trip-history.delete-trip-history-title', { tripName: tripHistory.tripName });
//     this.config.deleteEntityContent = () =>
//       this.translate.instant('trip-history.delete-trip-history-text');
//     this.config.deleteEntitiesTitle = count =>
//       this.translate.instant('trip-history.delete-trip-histories-title', { count });
//     this.config.deleteEntitiesContent = () =>
//       this.translate.instant('trip-history.delete-trip-histories-text');

//     // this.config.saveEntity = tripHistory =>
//     //   this.tripHistoryService.saveTripHistory(tripHistory).pipe(
//     //     take(1),
//     //     // map(savedTripHistory => this.tripHistoryService.getTripHistoryInfo(savedTripHistory.id.id))
//     //   );
//   }

//   resolve(route: ActivatedRouteSnapshot) {
//     return this.store.pipe(select(selectAuthUser), take(1), map(() => {
//       this.config.tableTitle = this.translate.instant('trip-history.trip-historys');
//       this.config.columns = this.configureColumns();
//       return this.config;
//     }));
//   }

//   private configureColumns(): Array<EntityTableColumn<TripHistoryInfo>> {
//     return [
//       new EntityTableColumn<TripHistoryInfo>('tripName', 'trip-history.tripName', '10%'),
//       new EntityTableColumn<TripHistoryInfo>('startDate', 'trip-history.startDate', '10%',
//         (item) => this.datePipe.transform(item.startDate, 'dd/MM/yy')),
//       new EntityTableColumn<TripHistoryInfo>('endDate', 'trip-history.endDate', '10%',
//         (item) => this.datePipe.transform(item.endDate, 'dd/MM/yy')),
//       new EntityTableColumn<TripHistoryInfo>('tripDuration', 'trip-history.tripDuration', '10%'),
//       new EntityTableColumn<TripHistoryInfo>('distanceCovered', 'trip-history.distanceCovered', '10%'),
//       new EntityTableColumn<TripHistoryInfo>('vehicleType', 'trip-history.vehicleNumber', '15%'),
//       new EntityTableColumn<TripHistoryInfo>('status', 'trip-history.status', '15%')
      
//     ];
//   }
//   }

