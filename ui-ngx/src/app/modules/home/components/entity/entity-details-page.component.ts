import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  HostBinding,
  Injector,
  OnDestroy,
  OnInit
} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import { BaseData, HasId } from '@shared/models/base-data';
import { ActivatedRoute, Router } from '@angular/router';
import { UntypedFormGroup } from '@angular/forms';
import { AssetId } from '@shared/models/id/asset-id';
import { TranslateService } from '@ngx-translate/core';
import { deepClone } from '@core/utils';
import { BroadcastService } from '@core/services/broadcast.service';
import { EntityDetailsPanelComponent } from '@home/components/entity/entity-details-panel.component';
import { DialogService } from '@core/services/dialog.service';
import { IEntityDetailsPageComponent } from '@home/models/entity/entity-details-page-component.models';
import { EntityService } from '@app/core/http/entity.service';

@Component({
  selector: 'tb-entity-details-page',
  templateUrl: './entity-details-page.component.html',
  styleUrls: ['./entity-details-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityDetailsPageComponent extends EntityDetailsPanelComponent implements IEntityDetailsPageComponent, OnInit, OnDestroy {

  headerTitle: string;
  headerSubtitle: string;

  isReadOnly = false;

  backNavigationCommands?: any[];

  set entitiesTableConfig(entitiesTableConfig: EntityTableConfig<BaseData<HasId>>) {
    if (this.entitiesTableConfigValue !== entitiesTableConfig) {
      this.entitiesTableConfigValue = entitiesTableConfig;
      if (this.entitiesTableConfigValue) {
        this.entitiesTableConfigValue.setEntityDetailsPage(this);
        this.isEdit = false;
        this.entity = null;
      }
    }
  }

  get entitiesTableConfig(): EntityTableConfig<BaseData<HasId>> {
    return this.entitiesTableConfigValue;
  }

  @HostBinding('class') 'tb-absolute-fill';

  constructor(private route: ActivatedRoute,
              private router: Router,
              protected injector: Injector,
              protected cd: ChangeDetectorRef,
              protected componentFactoryResolver: ComponentFactoryResolver,
              private broadcast: BroadcastService,
              private translate: TranslateService,
              private dialogService: DialogService,
              protected store: Store<AppState>) {
    super(store, injector, cd,componentFactoryResolver);
    this.entitiesTableConfig = this.route.snapshot.data.entitiesTableConfig;
    this.backNavigationCommands = this.route.snapshot.data.backNavigationCommands;
  }

  ngOnInit() {
    this.headerSubtitle = '';
    this.headerSubtitle = this.translate.instant(this.entitiesTableConfig.entityTranslations.details);
    super.init();
    this.entityComponent.isDetailsPage = true;
    this.subscriptions.push(this.entityAction.subscribe((action) => {
      if (action.action === 'delete') {
        this.deleteEntity(action.event, action.entity);
      }
    }));
    this.subscriptions.push(this.route.paramMap.subscribe( paramMap => {
      if (this.entitiesTableConfig) {
        const entityType = this.entitiesTableConfig.entityType;
        const id = paramMap.get('entityId');
        this.currentEntityId = { id, entityType };
        this.reload();
        this.selectedTab = 0;
      }
    }));
  }

  ngOnDestroy() {
    super.ngOnDestroy();
  }

  reload(): void {
    this.reloadEntity().subscribe(() => {
      this.onUpdateEntity();
    });
  }

  onToggleDetailsEditMode() {
    if (this.isEdit) {
      this.entityComponent.entity = this.entity;
      if (this.entityTabsComponent) {
        this.entityTabsComponent.entity = this.entity;
      }
      this.isEdit = !this.isEdit;
    } else {
      this.isEdit = !this.isEdit;
      this.editingEntity = deepClone(this.entity);
      this.entityComponent.entity = this.editingEntity;
      if (this.entityTabsComponent) {
        this.entityTabsComponent.entity = this.editingEntity;
      }
      if (this.entitiesTableConfig.hideDetailsTabsOnEdit) {
        this.selectedTab = 0;
      }
    }
  }

  onApplyDetails() {
    this.saveEntity(false).subscribe((entity) => {
      if (entity) {
        this.onUpdateEntity();
      }
    });
  }

  confirmForm(): UntypedFormGroup {
    return this.detailsForm;
  }

  goBack(): void {
    const commands = this.backNavigationCommands || ['../'];
    this.router.navigate(commands, { relativeTo: this.route });
  }

  private onUpdateEntity() {
    this.broadcast.broadcast('updateBreadcrumb');
    this.isReadOnly = this.entitiesTableConfig.detailsReadonly(this.entity);
    this.headerTitle = this.entitiesTableConfig.entityTitle(this.entity);
  }

  private deleteEntity($event: Event, entity: BaseData<HasId>) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialogService.confirm(
      this.entitiesTableConfig.deleteEntityTitle(entity),
      this.entitiesTableConfig.deleteEntityContent(entity),
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((result) => {
      if (result) {
        this.entitiesTableConfig.deleteEntity(entity.id).subscribe(
          () => {
            this.goBack();
          }
        );
      }
    });
  }
}
