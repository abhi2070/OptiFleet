import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { from, map, mergeMap, Observable, of, switchMap, take, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { select, Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { getCurrentAuthUser, selectAuthUser } from '@app/core/auth/auth.selectors';
import { Direction } from '@shared/models/page/sort-order';

import { CellActionDescriptor, EntityTableColumn, EntityTableConfig, HeaderActionDescriptor } from '@home/models/entity/entities-table-config.models';
import { Driver, DriverInfo } from '@app/shared/models/driver.model';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
import { DriverComponent } from './driver.component';
import { DriverService } from '@app/core/http/driver.service';
import { BroadcastService } from '@app/core/public-api';
import { DriverTabsComponent } from './driver-tabs/driver-tabs.component';
import { DriverDetailComponent } from './driver-detail/driver-detail.component';
import { AddEntityDialogData, EntityAction } from '../../models/entity/entity-component.models';
import { Authority, BaseData, HasId, PageLink } from '@app/shared/public-api';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class DriversTableConfigResolver {
  private readonly config: EntityTableConfig<DriverInfo> = new EntityTableConfig<DriverInfo>();
  authUser: any;

  constructor(
    private store: Store<AppState>,
    private driverService: DriverService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private router: Router,
    private broadcast: BroadcastService,
    private dialog: MatDialog,
    private sanitizer: DomSanitizer,
  ) {
    this.config.entityType = EntityType.DRIVER;
    this.config.entityComponent = DriverDetailComponent;
    this.config.entityTabsComponent = DriverTabsComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.DRIVER);
    this.config.entityResources = entityTypeResources.get(EntityType.DRIVER);
    this.config.loadEntity = id => this.driverService.getDriverInfo(id.id);


    this.authUser = getCurrentAuthUser(this.store);

    this.config.deleteEntityTitle = driver =>
      this.translate.instant('driver.delete-driver-title', { driverName: driver.name });
    this.config.deleteEntityContent = () =>
      this.translate.instant('driver.delete-driver-text');
    this.config.deleteEntitiesTitle = count =>
      this.translate.instant('driver.delete-drivers-title', { count });
    this.config.deleteEntitiesContent = () =>
      this.translate.instant('driver.delete-drivers-text');

    this.config.saveEntity = driver =>
      this.driverService.saveDriver(driver).pipe(
        tap(() => {
          this.broadcast.broadcast('driverSaved');
        }),
        switchMap((savedDriver) => this.driverService.getDriverInfo(savedDriver.id.id))
      );

    this.config.onEntityAction = action => this.onDriverAction(action, this.config);
    this.config.detailsReadonly = () => (
      this.config.componentsData.driverScope === 'customer_user' ||
      this.config.componentsData.driverScope === 'edge_customer_user'
    );
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<DriverInfo>> {
    const routeParams = route.params;
    this.config.componentsData = {
      driverScope: route.data.driverType,
      driverType: '',
      edgeId: routeParams.edgeId
    };

    return this.store.pipe(select(selectAuthUser), take(1), map(() => {
      this.config.tableTitle = this.translate.instant('driver.drivers');
      this.config.columns = this.configureColumns();
      this.configureEntityFunctions(this.config.componentsData.driverScope);
      this.config.cellActionDescriptors = this.configureCellActions(this.config.componentsData.driverScope);
      this.config.addActionDescriptors = this.configureAddActions(this.config.componentsData.driverScope);
      this.config.entitiesDeleteEnabled = this.config.componentsData.driverScope === 'tenant';
      this.config.deleteEnabled = () => this.config.componentsData.driverScope === 'tenant';
      return this.config;
    }));
  }

  private configureColumns(): Array<EntityTableColumn<DriverInfo>> {
    return [
      new EntityTableColumn<DriverInfo>('name', 'driver.name', '15%'),
      new EntityTableColumn<DriverInfo>('status', 'driver.status', '18%',
        (entity) => {
          let translateKey = '';
          let backgroundColor = '';
          let textColor = '';
          let showInfoIcon = false;
  
          switch (entity.status) {
            case 'AVAILABLE':
              translateKey = 'driver.available';
              backgroundColor = 'rgba(25, 128, 56, 0.08)';
              textColor = '#198038';
              break;
            case 'DISCONTINUED':
              translateKey = 'driver.discontinued';
              backgroundColor = 'rgba(141, 141, 141, 0.08)';
              textColor = '#666666';
              break;
            case 'ON_TRIP':
              translateKey = 'driver.onTrip';
              backgroundColor = 'rgba(255, 193, 7, 0.08)';
              textColor = '#FFA000';
              break;
            case 'ON_LEAVE':
              translateKey = 'driver.leave';
              backgroundColor = 'rgba(209, 39, 48, 0.08)';
              textColor = '#d12730';
              break;
            default:
              translateKey = 'driver.verificationPending';
              backgroundColor = 'rgba(241, 241, 241, 1)';
              textColor = '#666666';
              showInfoIcon = true;
          }
  
          return `
            <div style="display: flex; align-items: center;">
              <div style="border-radius: 16px;
                  height: 32px;
                  line-height: 32px;
                  padding: 0 12px;
                  width: fit-content;
                  background-color: ${backgroundColor};
                  color: ${textColor};">
                  ${this.translate.instant(translateKey)}
              </div>
            </div>`
        }),
        new EntityTableColumn<DriverInfo>('phoneNumber', 'driver.phoneNumber', '15%',
          (entity) => {
            const whatsappLink = `https://wa.me/${entity.phoneNumber}`;
            return `
              <div style="display: flex; align-items: center; gap: 8px;">
                <span>${entity.phoneNumber}</span>
                <a href="${whatsappLink}" target="_blank" style="text-decoration: none;">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="#25D366">
                    <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413Z"/>
                  </svg>
                </a>
              </div>`;
          }),
      new EntityTableColumn<DriverInfo>('drivingLicenseValidity', 'driver.drivingLicenseValidity', '20%',
        (item) => {
          const today = new Date();
          const validityDate = new Date(item.drivingLicenseValidity);
          const daysUntilExpiry = Math.ceil((validityDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
          
          let backgroundColor = '';
          let textColor = '';
          let warningIcon = '';
          let statusText = '';
          
          if (daysUntilExpiry < 0) {
            backgroundColor = 'rgba(209, 39, 48, 0.08)';
            textColor = '#d12730';
            warningIcon = `
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#d12730" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px;">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>`;
            statusText = '(Expired)';
          } else if (daysUntilExpiry <= 30) {
            backgroundColor = 'rgba(255, 193, 7, 0.08)';
            textColor = '#FFA000';
            warningIcon = `
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#FFA000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px;">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>`;
            statusText = '(Expires soon)';
          } else {
            backgroundColor = 'rgba(25, 128, 56, 0.08)';
            textColor = '#198038';
          }
      
          return `
            <div style="display: flex; align-items: center;">
              <div style="
                border-radius: 16px;
                height: 32px;
                line-height: 32px;
                padding: 0 12px;
                width: fit-content;
                white-space: nowrap;
                background-color: ${backgroundColor};
                color: ${textColor};
                display: flex;
                align-items: center;">
                ${warningIcon}
                ${this.datePipe.transform(item.drivingLicenseValidity, 'dd/MM/yy')} ${statusText}
              </div>
            </div>`;
        })
      ];
  }

  configureEntityFunctions(driverScope: string): void {
    const authUser = getCurrentAuthUser(this.store);
    const entityNameFilter = '';
    const pageLink = new PageLink(10, 0, entityNameFilter, { property: 'name', direction: Direction.DESC });

    if (authUser.authority === Authority.TENANT_ADMIN) {
      if (driverScope === 'tenant') {
        this.config.entitiesFetchFunction = pageLink =>
          this.driverService.getDriverInfos(pageLink);
        this.config.deleteEntity = id => this.driverService.deleteDriver(id.id);
      }
    } else {
      console.error('Unknown authority:', authUser.authority);
    }
  }

  configureAddActions(driverScope: string): Array<HeaderActionDescriptor> {

    const actions: Array<HeaderActionDescriptor> = [];

    if (driverScope === 'tenant') {
      actions.push({
        name: this.translate.instant('driver.add-driver-text'),
        icon: 'add',
        isEnabled: () => true,
        onAction: ($event) => {
          this.addDriver($event);
        }
      });
      this.config.addEntity = () => {
        this.addDriver(null);
        return of(null);
      };
    } else {
      this.config.addEntity = null;
    }
    return actions;
  }

  addDriver($event: Event): void {

    if ($event) {
      $event.stopPropagation();
    }

    try {
      const dialogRef = this.dialog.open<DriverComponent, AddEntityDialogData<Driver>, Driver>(
        DriverComponent,
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
        },
      );
    } catch (error) {
      console.error('Error details:', {
        dialog: this.dialog,
        DriverComponent: DriverComponent
      });
    }
  }

  onDriverAction(action: EntityAction<DriverInfo>, config: EntityTableConfig<DriverInfo>): boolean {
    switch (action.action) {
      case 'open':
        if (action.entity) {
          this.openDriver(action.event, action.entity, config);
          return true;
        }
        break;
    }
    return false;
  }

     configureCellActions(driverScope: string): Array<CellActionDescriptor<Driver>> {
        const actions: Array<CellActionDescriptor<Driver>> = [];
        if (driverScope === 'tenant') {
          actions.push(
            {
              name: this.translate.instant('driver.more'),
              icon: 'info',
              isEnabled: () => true,
              onAction: null
            }
          );
        }
        return actions;
      }
  

  public openDriver($event: Event, driver: DriverInfo, config: EntityTableConfig<DriverInfo>) {
    if ($event) {
      $event.stopPropagation();
    }
    const url = this.router.createUrlTree([driver.id.id], { relativeTo: config.getActivatedRoute() });
    this.router.navigateByUrl(url);
  }

}
