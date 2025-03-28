import { Injectable } from '@angular/core';
import { Resolve, Router } from '@angular/router';
import {
  checkBoxCell,
  DateEntityTableColumn,
  EntityTableColumn,
  EntityTableConfig,
  HeaderActionDescriptor
} from '@home/models/entity/entities-table-config.models';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe } from '@angular/common';
import { EntityType, entityTypeResources, entityTypeTranslations } from '@shared/models/entity-type.models';
import { EntityAction } from '@home/models/entity/entity-component.models';
import { DialogService } from '@core/services/dialog.service';
import { MatDialog } from '@angular/material/dialog';
import { ImportExportService } from '@shared/import-export/import-export.service';
import { HomeDialogsService } from '@home/dialogs/home-dialogs.service';
import { AssetProfile, TB_SERVICE_QUEUE } from '@shared/models/asset.models';
import { AssetProfileService } from '@core/http/asset-profile.service';
import { AssetProfileComponent } from '@home/components/profile/asset-profile.component';
import { AssetProfileTabsComponent } from './asset-profile-tabs.component';

@Injectable()
export class AssetProfilesTableConfigResolver implements Resolve<EntityTableConfig<AssetProfile>> {

  private readonly config: EntityTableConfig<AssetProfile> = new EntityTableConfig<AssetProfile>();

  constructor(private assetProfileService: AssetProfileService,
              private importExport: ImportExportService,
              private homeDialogs: HomeDialogsService,
              private translate: TranslateService,
              private datePipe: DatePipe,
              private dialogService: DialogService,
              private router: Router,
              private dialog: MatDialog) {

    this.config.entityType = EntityType.ASSET_PROFILE;
    this.config.entityComponent = AssetProfileComponent;
    this.config.entityTabsComponent = AssetProfileTabsComponent;
    this.config.entityTranslations = entityTypeTranslations.get(EntityType.ASSET_PROFILE);
    this.config.entityResources = entityTypeResources.get(EntityType.ASSET_PROFILE);

    this.config.hideDetailsTabsOnEdit = false;

    this.config.columns.push(
      new DateEntityTableColumn<AssetProfile>('createdTime', 'common.created-time', this.datePipe, '150px'),
      new EntityTableColumn<AssetProfile>('name', 'asset-profile.name', '50%'),
      new EntityTableColumn<AssetProfile>('description', 'asset-profile.description', '50%'),
      new EntityTableColumn<AssetProfile>('isDefault', 'asset-profile.default', '60px',
        entity => checkBoxCell(entity.default))
    );

    this.config.cellActionDescriptors.push(
      {
        name: this.translate.instant('asset-profile.export'),
        icon: 'file_download',
        isEnabled: () => true,
        onAction: ($event, entity) => this.exportAssetProfile($event, entity)
      },
      {
        name: this.translate.instant('asset-profile.set-default'),
        icon: 'flag',
        isEnabled: (assetProfile) => !assetProfile.default && TB_SERVICE_QUEUE !== assetProfile.name,
        onAction: ($event, entity) => this.setDefaultAssetProfile($event, entity)
      }
    );

    this.config.deleteEntityTitle = assetProfile => this.translate.instant('asset-profile.delete-asset-profile-title',
      { assetProfileName: assetProfile.name });
    this.config.deleteEntityContent = () => this.translate.instant('asset-profile.delete-asset-profile-text');
    this.config.deleteEntitiesTitle = count => this.translate.instant('asset-profile.delete-asset-profiles-title', {count});
    this.config.deleteEntitiesContent = () => this.translate.instant('asset-profile.delete-asset-profiles-text');

    this.config.entitiesFetchFunction = pageLink => this.assetProfileService.getAssetProfiles(pageLink);
    this.config.loadEntity = id => this.assetProfileService.getAssetProfile(id.id);
    this.config.saveEntity = assetProfile => this.assetProfileService.saveAssetProfile(assetProfile);
    this.config.deleteEntity = id => this.assetProfileService.deleteAssetProfile(id.id);
    this.config.onEntityAction = action => this.onAssetProfileAction(action);
    this.config.deleteEnabled = (assetProfile) => assetProfile && !assetProfile.default && TB_SERVICE_QUEUE !== assetProfile.name;
    this.config.entitySelectionEnabled = (assetProfile) => assetProfile && !assetProfile.default && TB_SERVICE_QUEUE !== assetProfile.name;
    this.config.detailsReadonly = (assetProfile) => assetProfile && TB_SERVICE_QUEUE === assetProfile.name;
    this.config.addActionDescriptors = this.configureAddActions();
  }

  resolve(): EntityTableConfig<AssetProfile> {
    this.config.tableTitle = this.translate.instant('asset-profile.asset-profiles');

    return this.config;
  }

  configureAddActions(): Array<HeaderActionDescriptor> {
    const actions: Array<HeaderActionDescriptor> = [];
    actions.push(
      {
        name: this.translate.instant('asset-profile.create-asset-profile'),
        icon: 'insert_drive_file',
        isEnabled: () => true,
        onAction: ($event) => this.config.getTable().addEntity($event)
      },
      {
        name: this.translate.instant('asset-profile.import'),
        icon: 'file_upload',
        isEnabled: () => true,
        onAction: ($event) => this.importAssetProfile($event)
      }
    );
    return actions;
  }

  setDefaultAssetProfile($event: Event, assetProfile: AssetProfile) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialogService.confirm(
      this.translate.instant('asset-profile.set-default-asset-profile-title', {assetProfileName: assetProfile.name}),
      this.translate.instant('asset-profile.set-default-asset-profile-text'),
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
        if (res) {
          this.assetProfileService.setDefaultAssetProfile(assetProfile.id.id).subscribe(
            () => {
              this.config.updateData();
            }
          );
        }
      }
    );
  }

  private openAssetProfile($event: Event, assetProfile: AssetProfile) {
    if ($event) {
      $event.stopPropagation();
    }
    const url = this.router.createUrlTree(['profiles', 'assetProfiles', assetProfile.id.id]);
    this.router.navigateByUrl(url);
  }

  importAssetProfile($event: Event) {
    this.importExport.importAssetProfile().subscribe(
      (assetProfile) => {
        if (assetProfile) {
          this.config.updateData();
        }
      }
    );
  }

  exportAssetProfile($event: Event, assetProfile: AssetProfile) {
    if ($event) {
      $event.stopPropagation();
    }
    this.importExport.exportAssetProfile(assetProfile.id.id);
  }

  onAssetProfileAction(action: EntityAction<AssetProfile>): boolean {
    switch (action.action) {
      case 'open':
        this.openAssetProfile(action.event, action.entity);
        return true;
      case 'setDefault':
        this.setDefaultAssetProfile(action.event, action.entity);
        return true;
      case 'export':
        this.exportAssetProfile(action.event, action.entity);
        return true;
    }
    return false;
  }

}
