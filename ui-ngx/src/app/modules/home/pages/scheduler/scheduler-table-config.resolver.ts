/* eslint-disable max-len */
import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { Observable, catchError, map, mergeMap, of, switchMap, take, tap } from 'rxjs';
import { CellActionDescriptor, DateEntityTableColumn, EntityTableColumn, EntityTableConfig } from '../../models/entity/entities-table-config.models';
import { AppState } from '@app/core/core.state';
import { BroadcastService } from '@app/core/services/broadcast.service';
import { Store, select } from '@ngrx/store';
import { CustomerService } from '@app/core/http/customer.service';
import { EdgeService } from '@app/core/http/edge.service';
import { DialogService } from '@app/core/services/dialog.service';
import { HomeDialogsService } from '../../dialogs/home-dialogs.service';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@app/shared/models/entity-type.models';
import { Authority, Customer, Direction, PageLink } from '@app/shared/public-api';
import { getCurrentAuthUser, selectAuthUser } from '@app/core/auth/auth.selectors';
import { SchedulerComponent } from './scheduler.component';
import { EmailConfiguration, Schedulers, SchedulersInfo } from '@app/shared/models/schedulers.model';
import { SchedulersService } from '@app/core/http/schedulers.service';
import { UpdateDialogComponent, UpdateDialogData } from '@modules/home/dialogs/update-dialog.component';
import { ReportService } from '@app/core/http/report.service';


@Injectable({
  providedIn: 'root',
})
export class SchedulerTableConfigResolver {

  private readonly config: EntityTableConfig<SchedulersInfo> = new EntityTableConfig<SchedulersInfo>();

  private customerId: string;
  authUser: any;
  toggleButton: true;

  constructor(private store: Store<AppState>,
    private broadcast: BroadcastService,
    private schedulersService: SchedulersService,
    private customerService: CustomerService,
    private edgeService: EdgeService,
    private reportService: ReportService,
    private dialogService: DialogService,
    private homeDialogs: HomeDialogsService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private router: Router,
    private dialog: MatDialog) {

    this.config.entityType = EntityType.SCHEDULERS;
    this.config.entityComponent = SchedulerComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.SCHEDULERS);
    this.config.entityResources = entityTypeResources.get(EntityType.SCHEDULERS);

    this.config.addActionDescriptors.push(
      {
        name: this.translate.instant('schedulers.add-scheduler-text'),
        icon: 'add',
        isEnabled: () => true,
        onAction: ($event) => this.config.getTable().addEntity($event)

      }
    );

    this.config.deleteEntityTitle = scheduler => this.translate.instant('schedulers.delete-scheduler-title', { schedulersName: scheduler.name });
    this.config.deleteEntityContent = () => this.translate.instant('schedulers.delete-scheduler-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('schedulers.delete-schedulers-title', { count });
    this.config.deleteEntitiesContent = () => this.translate.instant('schedulers.delete-schedulers-text');

    this.config.saveEntity = scheduler => this.schedulersService.saveScheduler(scheduler).pipe(
      switchMap(savedScheduler => {
        if (savedScheduler.reportType) {
          const filterOptions = {
            category: savedScheduler.reportCategory,
            available: savedScheduler.reportAvailable
          };
          return this.reportService.generateReport(savedScheduler.reportType, filterOptions).pipe(
            switchMap(reportBlob => this.handleReportBlob(savedScheduler, reportBlob)),
            switchMap(() => this.schedulersService.getSchedulerInfo(savedScheduler.id.id) as Observable<SchedulersInfo>)
          );
        } else {
          return this.schedulersService.getSchedulerInfo(savedScheduler.id.id) as Observable<SchedulersInfo>;
        }
      }),
      catchError(error => of(null))
    );
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<SchedulersInfo>> {
    const routeParams = route.params;
    this.config.componentsData = {
      schedulerScope: route.data.schedulersType || 'tenant',
      schedulerProfileId: null,
      schedulerType: '',
    };


    this.customerId = routeParams.customerId;
    return this.store.pipe(select(selectAuthUser), take(1)).pipe(
      tap((authUser) => {
        this.authUser = authUser;
        if (authUser.authority === Authority.CUSTOMER_USER) {
          if (route.data.schedulersType === 'edge') {
            this.config.componentsData.schedulerScope = 'edge_customer_user';
          } else {
            this.config.componentsData.schedulerScope = 'customer_user';
          }
          this.customerId = authUser.customerId;
        }
      }),
      mergeMap(() =>
        this.customerId ? this.customerService.getCustomer(this.customerId) : of(null as Customer)
      ),
      map((parentCustomer) => {
        if (parentCustomer) {
          if (parentCustomer.additionalInfo && parentCustomer.additionalInfo.isPublic) {
            this.config.tableTitle = this.translate.instant('customer.public-schedulers');
          } else {
            this.config.tableTitle = parentCustomer.title + ': ' + this.translate.instant('schedulers.scheduler');
          }
        } else if (this.config.componentsData.schedulerScope === 'edge') {
          this.edgeService.getEdge(this.config.componentsData.edgeId).subscribe(
            edge => this.config.tableTitle = edge.name + ': ' + this.translate.instant('schedulers.scheduler')
          );
        } else {
          this.config.tableTitle = this.translate.instant('schedulers.scheduler-events');
        }
        this.config.columns = this.configureColumns(this.config.componentsData.schedulerScope);

        this.configureEntityFunctions(this.config.componentsData.schedulerScope);
        this.config.cellActionDescriptors = this.configureCellActions(this.config.componentsData.schedulerScope);
        this.config.entitiesDeleteEnabled = this.config.componentsData.schedulerScope === 'tenant';
        this.config.deleteEnabled = () => this.config.componentsData.schedulerScope === 'tenant';
        this.config.handleRowClick = (event: Event, entity: SchedulersInfo) => {
          if (event) {
            event.stopPropagation();
          }
          const selected = this.config.getTable().dataSource.toggleCurrentEntity(entity);
          return true;
        };

        return this.config;
      })
    );
  }



  handleReportBlob(scheduler: Schedulers, reportBlob: Blob): Observable<any> {
    if (reportBlob) {
      return this.schedulersService.getSchedulerInfo(scheduler.id.id).pipe(
        switchMap((schedulerInfo: SchedulersInfo) => {
          const fileName = this.generateFileName(scheduler, schedulerInfo.reportNamePattern);

          const emailConfig: EmailConfiguration = {
            toAddress: schedulerInfo.toAddress,
            cc: schedulerInfo.cc,
            bcc: schedulerInfo.bcc,
            subject: schedulerInfo.subject || `Report for ${scheduler.name}`,
            body: schedulerInfo.body || `Please find attached the report for ${scheduler.name}.`
          };
          const file = new File([reportBlob], fileName, { type: reportBlob.type });

          return this.schedulersService.sendEmail(emailConfig, [file]);
        }),
        catchError(error => {
          console.error('Error in handleReportBlob:', error);
          return of(null);
        })
      );
    } else {
      return of(null);
    }
  }

  private generateFileName(scheduler: Schedulers, reportNamePattern?: string): string {
    let fileName = scheduler.name;

    if (reportNamePattern) {
      let pattern = reportNamePattern.replace('{schedulerName}', '').trim();

      pattern = pattern.replace(/^[_-]/, '');

      pattern = pattern.replace(/%d\{([^}]+)\}/, (match, dateFormat) => this.formatDate(new Date(), dateFormat));

      if (pattern) {
        fileName += '_' + pattern;
      }
    } else {
      fileName += `_report_${this.formatDate(new Date(), 'yyyy-MM-dd_HH:mm:ss')}`;
    }
    if (!fileName.endsWith(this.getFileExtension(scheduler.reportType))) {
      fileName += `.${this.getFileExtension(scheduler.reportType)}`;
    }

    return fileName;
  }

  private formatDate(date: Date, format: string): string {
    const pad = (num: number) => num.toString().padStart(2, '0');

    return format
      .replace('yyyy', date.getFullYear().toString())
      .replace('MM', pad(date.getMonth() + 1))
      .replace('dd', pad(date.getDate()))
      .replace('HH', pad(date.getHours()))
      .replace('mm', pad(date.getMinutes()))
      .replace('ss', pad(date.getSeconds()));
  }
  private getFileExtension(reportType: string): string {
    switch (reportType.toLowerCase()) {
      case 'csv':
        return 'csv';
      case 'excel':
        return 'xlsx';
      default:
        return 'txt';
    }
  }

  configureColumns(schedulerScope: string): Array<EntityTableColumn<SchedulersInfo>> {
    const columns: Array<EntityTableColumn<SchedulersInfo>> = [
      new DateEntityTableColumn<SchedulersInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<SchedulersInfo>('name', 'schedulers.name', '30%'),
      new EntityTableColumn<SchedulersInfo>('toAddress', 'schedulers.toAddress', '30%'),
    ];
    return columns;
  }


  importSchedulers($event: Event) {
    this.homeDialogs.importEntities(EntityType.SCHEDULERS).subscribe((res) => {
      if (res) {
        this.broadcast.broadcast('schedulersSaved');
        this.config.updateData();
      }
    });
  }

  configureCellActions(schedulerScope: string): Array<CellActionDescriptor<SchedulersInfo>> {
    const actions: Array<CellActionDescriptor<SchedulersInfo>> = [];
    if (schedulerScope === 'tenant') {
      actions.push(
        {
          name: this.translate.instant('Enable/Disable'),
          iconFunction: (entity: SchedulersInfo) => entity.active ? 'toggle_on' : 'mdi:toggle-switch-off-outline',
          isEnabled: () => true,
          onAction: ($event, entity: Schedulers) => {
            if ($event) {
              $event.stopPropagation();
            }
            this.toggleSchedulerStatus(entity);
          }
        },
        {
          name: this.translate.instant('schedulers.edit'),
          icon: 'edit',
          isEnabled: () => true,
          onAction: ($event, entity: SchedulersInfo) => this.updateSchedulerForm($event, entity)
        }
      );
    }
    return actions;
  }

  toggleSchedulerStatus(entity: Schedulers): void {
    const schedulerId: string = entity.id.id;
    entity.active = !entity.active;
    this.schedulersService.toggleSchedulerStatus(schedulerId).subscribe({
      next: (updatedEntity) => {
        entity.active = updatedEntity.active;
      },
      error: (error) => {
        console.error(error);
        entity.active = !entity.active;
      }
    });
  }

  updateSchedulerForm($event: Event, entity: SchedulersInfo): void {

    if ($event) {
      $event.stopPropagation();
    }

    const schedulerId: string = entity.id.id;

    this.schedulersService.getSchedulerInfo(schedulerId).subscribe(
      (schedulerData: Schedulers) => {

        const dialogRef = this.dialog.open<UpdateDialogComponent, UpdateDialogData, boolean>(UpdateDialogComponent, {
          disableClose: false,
          panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
          data: {
            entityIds: [schedulerId],
            entityType: EntityType.SCHEDULERS,
            entity,
            entitiesTableConfig: this.config,
            prefillData: schedulerData
          }
        });

        dialogRef.afterClosed().subscribe((result: boolean) => {

          if (result) {
            this.config.updateData();
          }
        });
      },
      (error) => {
      }
    );
  }


  configureEntityFunctions(schedulerScope: string): void {
    const authUser = getCurrentAuthUser(this.store);
    const entityNameFilter = '';
    const pageLink = new PageLink(10, 0, entityNameFilter, {
      property: 'name',
      direction: Direction.DESC,
    });


    if (authUser.authority === Authority.TENANT_ADMIN) {
      if (schedulerScope === 'tenant') {
        this.config.entitiesFetchFunction = (page) =>
          this.schedulersService.getTenantSchedulerInfos(page);

        this.config.deleteEntity = (id) => this.schedulersService.deleteScheduler(id.id);
      }
    }
    else {
      console.error('Unknown authority:', authUser.authority);
    }
  }
}
