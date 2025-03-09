import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, mergeMap, take, tap } from 'rxjs/operators';
import {
  DateEntityTableColumn,
  EntityTableColumn,
  EntityTableConfig,
  HeaderActionDescriptor
} from '@home/models/entity/entities-table-config.models';
import { Reports, ReportsInfo } from '@app/shared/models/reports.model';
import { AppState, BroadcastService, CustomerService } from '@app/core/public-api';
import { selectAuthUser } from '@core/auth/auth.selectors';
import { select, Store } from '@ngrx/store';
import { HomeDialogsService } from '../../dialogs/home-dialogs.service';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { Customer } from '@app/shared/models/customer.model';
import { MatDialog } from '@angular/material/dialog';
import { Authority } from '@app/shared/models/authority.enum';
import { BaseData, EntityType, entityTypeResources, entityTypeTranslations, HasId } from '@app/shared/public-api';
import { AddReportDialogComponent } from '../../components/reports/add-report-dialog.component';
import { AddEntityDialogData } from '../../models/entity/entity-component.models';

@Injectable()
export class ReportsTableConfigResolver implements Resolve<EntityTableConfig<ReportsInfo>> {

  private readonly config: EntityTableConfig<ReportsInfo> = new EntityTableConfig<ReportsInfo>();

  constructor(private store: Store<AppState>,
    private broadcast: BroadcastService,
    private customerService: CustomerService,
    private homeDialogs: HomeDialogsService,
    private translate: TranslateService,
    private datePipe: DatePipe,
    private dialog: MatDialog,
    private router: Router) {

    this.config.entityType = EntityType.REPORT;
    this.config.entityComponent = null;
    this.config.entityTabsComponent = null;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.REPORT);
    this.config.entityResources = entityTypeResources.get(EntityType.REPORT);

    this.config.deleteEntityTitle = report =>
      this.translate.instant('report.delete-report-title', { reportTitle: report.title });
    this.config.deleteEntityContent = () => this.translate.instant('report.delete-report-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('report.delete-reports-title', { count });
    this.config.deleteEntitiesContent = () => this.translate.instant('report.delete-reports-text');
    this.config.detailsReadonly = () => (this.config.componentsData.reportScope === 'customer_user');
  }

  resolve(route: ActivatedRouteSnapshot): Observable<EntityTableConfig<ReportsInfo>> {
    const routeParams = route.params;
    this.config.componentsData = {
      reportScope: route.data.reportsType,
      customerId: routeParams.customerId,
    };
    return this.store.pipe(select(selectAuthUser), take(1)).pipe(
      tap((authUser) => {
        if (authUser.authority === Authority.CUSTOMER_USER) {
          this.config.componentsData.reportScope = 'customer_user';
          this.config.componentsData.customerId = authUser.customerId;
        }
      }),
      mergeMap(() =>
        this.config.componentsData.customerId ?
          this.customerService.getCustomer(this.config.componentsData.customerId) : of(null as Customer)
      ),
      map(() => {
        this.config.tableTitle = this.translate.instant('report.reports');
        this.config.columns = this.configureColumns(this.config.componentsData.reportScope);
        this.config.addActionDescriptors = this.configureAddActions(this.config.componentsData.reportScope);
        this.config.addEnabled = !(this.config.componentsData.reportScope === 'customer_user');
        this.config.deleteEnabled = () => this.config.componentsData.reportScope === 'tenant';
        this.config.handleRowClick = (event: Event, entity: ReportsInfo) => {
          if (event) {
            event.stopPropagation();
          }
          return true;
        };
        return this.config;
      })
    );
  }

  configureColumns(reportScope: string): Array<EntityTableColumn<ReportsInfo>> {
    const columns: Array<EntityTableColumn<ReportsInfo>> = [
      new DateEntityTableColumn<ReportsInfo>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<ReportsInfo>('name', 'report.name', '25%'),
      new EntityTableColumn<ReportsInfo>('type', 'report.type', '25%'),
    ];
    if (reportScope === 'tenant') {
      columns.push(
        new EntityTableColumn<ReportsInfo>('assignedToClients', 'report.assigned-to-clients', '25%'),
      );
    }
    return columns;
  }

  configureAddActions(reportScope: string): Array<HeaderActionDescriptor> {
    this.config.addEntity = null;
    const actions: Array<HeaderActionDescriptor> = [];
    if (reportScope === 'tenant') {
      actions.push(
        {
          name: this.translate.instant('report.add'),
          icon: 'add',
          isEnabled: () => true,
          onAction: ($event) => this.addReport($event)
        }
      );
      this.config.addEntity = () => { this.addReport(null); return of(null); };
    }
    return actions;
  }


  addReport($event: Event): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<AddReportDialogComponent, AddEntityDialogData<BaseData<HasId>>,
      Reports>(AddReportDialogComponent, {
        disableClose: true,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
      }).afterClosed().subscribe(() => {
        this.config.updateData();
      });
  }

}
