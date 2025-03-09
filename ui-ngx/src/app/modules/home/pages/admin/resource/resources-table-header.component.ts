

import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { EntityTableHeaderComponent } from '@home/components/entity/entity-table-header.component';
import { Resource, ResourceInfo, ResourceType, ResourceTypeTranslationMap } from '@shared/models/resource.models';
import { PageLink } from '@shared/models/page/page-link';

@Component({
  selector: 'tb-resources-table-header',
  templateUrl: './resources-table-header.component.html',
  styleUrls: []
})
export class ResourcesTableHeaderComponent extends EntityTableHeaderComponent<Resource, PageLink, ResourceInfo> {

  readonly resourceTypes: ResourceType[] = Object.values(ResourceType);
  readonly resourceTypesTranslationMap = ResourceTypeTranslationMap;

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  resourceTypeChanged(resourceType: ResourceType) {
    this.entitiesTableConfig.componentsData.resourceType = resourceType;
    this.entitiesTableConfig.getTable().resetSortAndFilter(true);
  }
}
