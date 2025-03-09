import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { AppState } from '@app/core/core.state';
import { BaseData, HasId, PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';
import { EntityTableConfig } from '../../models/entity/entities-table-config.models';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'tb-user-group-roles',
  templateUrl: './user-group-roles.component.html',
  styleUrls: ['./user-group-roles.component.scss']
})
export class UserGroupRolesComponent extends PageComponent {

  @Input()
  userRolesTableConfig: EntityTableConfig<BaseData<HasId>>;
  form: FormGroup;
  displayedColumns: string[];
  dataSource = new MatTableDataSource<any>([]);
  textSearchMode = false;

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  addEnabled() {
    return this.userRolesTableConfig.addEnabled;
  }

}
