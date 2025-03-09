import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { map, Observable, of, switchMap, take, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { select, Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { getCurrentAuthUser, selectAuthUser } from '@app/core/auth/auth.selectors';
import { Direction } from '@shared/models/page/sort-order';

import { CellActionDescriptor, EntityTableColumn, EntityTableConfig, HeaderActionDescriptor } from '@home/models/entity/entities-table-config.models';
import { Vehicle, VehicleInfo } from '@app/shared/models/vehicle.model';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
import { VehicleComponent } from './vehicle.component';
import { VehicleService } from '@core/http/vehicle.service';
import { BroadcastService } from '@app/core/public-api';
import { VehicleTabsComponent } from './vehicle-tabs/vehicle-tabs.component';
import { VehicleDetailComponent } from './vehicle-detail/vehicle-detail.component';
import { AddEntityDialogData, EntityAction } from '../../models/entity/entity-component.models';
import { Authority, PageLink } from '@app/shared/public-api';

@Injectable({
  providedIn: 'root'
})
export class VehicleTableConfigResolver {
  private readonly config: EntityTableConfig<VehicleInfo> = new EntityTableConfig<VehicleInfo>();
  authUser: any;

  constructor(
    private store: Store<AppState>,
    private vehicleService: VehicleService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private router: Router,
    private broadcast: BroadcastService,
    private dialog: MatDialog
  ) {
    this.config.entityType = EntityType.VEHICLE;
    this.config.entityComponent = VehicleDetailComponent;
    this.config.entityTabsComponent = VehicleTabsComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.VEHICLE);
    this.config.entityResources = entityTypeResources.get(EntityType.VEHICLE);

    this.config.loadEntity = id => this.vehicleService.getVehicle(id.id);
    this.authUser = getCurrentAuthUser(this.store);

    this.config.deleteEntityTitle = vehicle =>
      this.translate.instant('vehicle.delete-vehicle-title', { vehiclenumber: vehicle.vehiclenumber });
    this.config.deleteEntityContent = () =>
      this.translate.instant('vehicle.delete-vehicle-text');
    this.config.deleteEntitiesTitle = count =>
      this.translate.instant('vehicle.delete-vehicles-title', { count });
    this.config.deleteEntitiesContent = () =>
      this.translate.instant('vehicle.delete-vehicles-text');

    this.config.saveEntity = vehicle =>
      this.vehicleService.saveVehicle(vehicle).pipe(
        tap(() => {
          this.broadcast.broadcast('vehicleSaved');
        }),
        switchMap((savedVehicle) => this.vehicleService.getVehicle(savedVehicle.id.id))
      );

    this.config.onEntityAction = action => this.onVehicleAction(action, this.config);
    this.config.detailsReadonly = () => (
      this.config.componentsData.vehicleScope === 'customer_user' ||
      this.config.componentsData.vehicleScope === 'edge_customer_user'
    );
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<VehicleInfo>> {
    const routeParams = route.params;
    this.config.componentsData = {
      vehicleScope: route.data.vehicleType,
      vehicleType: '',
      edgeId: routeParams.edgeId
    };

    return this.store.pipe(select(selectAuthUser), take(1), map(() => {
      this.config.tableTitle = this.translate.instant('vehicle.vehicles');
      this.config.columns = this.configureColumns();
      this.configureEntityFunctions(this.config.componentsData.vehicleScope);
      this.config.cellActionDescriptors = this.configureCellActions(this.config.componentsData.vehicleScope);
      this.config.addActionDescriptors = this.configureAddActions(this.config.componentsData.vehicleScope);
      this.config.entitiesDeleteEnabled = this.config.componentsData.vehicleScope === 'tenant';
      this.config.deleteEnabled = () => this.config.componentsData.vehicleScope === 'tenant';
      return this.config;
    }));
  }

  private configureColumns(): Array<EntityTableColumn<VehicleInfo>> {
    return [
      new EntityTableColumn<VehicleInfo>('createdTime', 'common.created-time', '15%',
        (item) => this.datePipe.transform(item.createdTime)),
      new EntityTableColumn<VehicleInfo>('vehiclenumber', 'vehicle.vehiclenumber', '20%'),
      new EntityTableColumn<VehicleInfo>('type', 'vehicle.type', '15%'),
      new EntityTableColumn<VehicleInfo>('status', 'vehicle.status', '15%',
        (entity) => {
          let translateKey = '';
          let backgroundColor = '';
          let textColor = '';

          switch (entity.status) {
            case 'AVAILABLE':
              translateKey = 'vehicle.available';
              backgroundColor = 'rgba(25, 128, 56, 0.08)';
              textColor = '#198038';
              break;
            case 'ON_TRIP':
              translateKey = 'vehicle.onTrip';
              backgroundColor = 'rgba(141, 141, 141, 0.08)';
              textColor = '#FFA000';
              break;
            case 'MAINTENANCE':
              translateKey = 'vehicle.maintenance';
              backgroundColor = 'rgba(255, 193, 7, 0.08)';
              textColor = '#FFA000';
              break;
            default:
              translateKey = 'driver.verificationPending';
              backgroundColor = 'rgba(141, 141, 141, 0.08)';
              textColor = '#666666';
          }

          return `<div class="status" style="border-radius: 16px;
                      height: 32px;
                      line-height: 32px;
                      padding: 0 12px;
                      width: fit-content;
                      background-color: ${backgroundColor};
                      color: ${textColor}">
                      ${this.translate.instant(translateKey)}
                  </div>`;
        }),
    ];
  }

  configureEntityFunctions(vehicleScope: string): void {
    const authUser = getCurrentAuthUser(this.store);
    const entityNumberFilter = '';
    const pageLink = new PageLink(10, 0, entityNumberFilter, { property: 'vehiclenumber', direction: Direction.DESC });

    if (authUser.authority === Authority.TENANT_ADMIN) {
      if (vehicleScope === 'tenant') {
        this.config.entitiesFetchFunction = pageLink =>
          this.vehicleService.getVehicleInfos(pageLink);
        this.config.deleteEntity = id => this.vehicleService.deleteVehicle(id.id);
      }
    } else {
      console.error('Unknown authority:', authUser.authority);
    }
  }

   configureCellActions(vehicleScope: string): Array<CellActionDescriptor<Vehicle>> {
      const actions: Array<CellActionDescriptor<Vehicle>> = [];
      if (vehicleScope === 'tenant') {
        actions.push(
          {
            name: this.translate.instant('vehicle.more'),
            icon: 'info',
            isEnabled: () => true,
            onAction: null
          }
        );
      }
      return actions;
    }

  configureAddActions(vehicleScope: string): Array<HeaderActionDescriptor> {
    const actions: Array<HeaderActionDescriptor> = [];

    if (vehicleScope === 'tenant') {
      actions.push({
        name: this.translate.instant('vehicle.add-vehicle-text'),
        icon: 'add',
        isEnabled: () => true,
        onAction: ($event) => {
          this.addVehicle($event);
        }
      });
      this.config.addEntity = () => {
        this.addVehicle(null);
        return of(null);
      };
    } else {
      this.config.addEntity = null;
    }
    return actions;
  }

  addVehicle($event: Event): void {
    if ($event) {
      $event.stopPropagation();
    }

    try {
      const dialogRef = this.dialog.open<VehicleComponent, AddEntityDialogData<Vehicle>, Vehicle>(
        VehicleComponent,
        {
          disableClose: true,
          panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
          data: {
            entitiesTableConfig: this.config
          }
        }
      );

      dialogRef.afterClosed().subscribe(
        (result) => {
          if (result) {
            this.config.updateData();
          }
        }
      );
    } catch (error) {
      console.error('Error details:', {
        dialog: this.dialog,
        VehicleComponent: VehicleComponent
      });
    }
  }

  onVehicleAction(action: EntityAction<VehicleInfo>, config: EntityTableConfig<VehicleInfo>): boolean {
    switch (action.action) {
      case 'open':
        if (action.entity) {
          this.openVehicle(action.event, action.entity, config);
          return true;
        }
        break;
    }
    return false;
  }

  private openVehicle($event: Event, vehicle: VehicleInfo, config: EntityTableConfig<VehicleInfo>) {
    if ($event) {
      $event.stopPropagation();
    }
    const url = this.router.createUrlTree([vehicle.id.id], { relativeTo: config.getActivatedRoute() });
    this.router.navigateByUrl(url);
  }
}
