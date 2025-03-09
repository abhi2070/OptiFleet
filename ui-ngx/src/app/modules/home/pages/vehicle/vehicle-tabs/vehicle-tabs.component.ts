import { Component } from '@angular/core';
import { AppState } from '@app/core/public-api';
import { EntityTabsComponent } from '@app/modules/home/components/entity/entity-tabs.component';
import { VehicleInfo } from '@app/shared/models/vehicle.model';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-vehicle-tabs',
  templateUrl: './vehicle-tabs.component.html',
  styleUrls: ['./vehicle-tabs.component.scss']
})
export class VehicleTabsComponent extends EntityTabsComponent<VehicleInfo>  {
   constructor(protected store: Store<AppState>) {
        super(store);
      }

      ngOnInit() {
        super.ngOnInit();
      }

}
