

import { Inject, Injectable, Type } from '@angular/core';
import { Observable } from 'rxjs';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DynamicComponentFactoryService } from '@core/services/dynamic-component-factory.service';
import { CommonModule } from '@angular/common';
import { mergeMap, tap } from 'rxjs/operators';
import { CustomDialogComponent } from './custom-dialog.component';
import {
  CustomDialogContainerComponent,
  CustomDialogContainerData
} from '@home/components/widget/dialog/custom-dialog-container.component';
import { SHARED_MODULE_TOKEN } from '@shared/components/tokens';
import {
  HOME_COMPONENTS_MODULE_TOKEN,
  SHARED_HOME_COMPONENTS_MODULE_TOKEN,
  WIDGET_COMPONENTS_MODULE_TOKEN
} from '@home/components/tokens';

@Injectable()
export class CustomDialogService {

  private customModules: Array<Type<any>>;

  constructor(
    private dynamicComponentFactoryService: DynamicComponentFactoryService,
    @Inject(SHARED_MODULE_TOKEN) private sharedModule: Type<any>,
    @Inject(SHARED_HOME_COMPONENTS_MODULE_TOKEN) private sharedHomeComponentsModule: Type<any>,
    @Inject(HOME_COMPONENTS_MODULE_TOKEN) private homeComponentsModule: Type<any>,
    @Inject(WIDGET_COMPONENTS_MODULE_TOKEN) private widgetComponentsModule: Type<any>,
    public dialog: MatDialog
  ) {
  }

  setAdditionalModules(modules: Array<Type<any>>) {
    this.customModules = modules;
  }

  customDialog(template: string, controller: (instance: CustomDialogComponent) => void, data?: any,
               config?: MatDialogConfig): Observable<any> {
    const modules = [this.sharedModule, CommonModule, this.sharedHomeComponentsModule, this.homeComponentsModule,
      this.widgetComponentsModule];
    if (Array.isArray(this.customModules)) {
      modules.push(...this.customModules);
    }
    return this.dynamicComponentFactoryService.createDynamicComponent(
      class CustomDialogComponentInstance extends CustomDialogComponent {}, template, modules).pipe(
      mergeMap((componentData) => {
          const dialogData: CustomDialogContainerData = {
            controller,
            customComponentType: componentData.componentType,
            customComponentModuleRef: componentData.componentModuleRef,
            data
          };
          let dialogConfig: MatDialogConfig = {
            disableClose: true,
            panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
            data: dialogData
          };
          if (config) {
            dialogConfig = {...dialogConfig, ...config};
          }
          return this.dialog.open<CustomDialogContainerComponent, CustomDialogContainerData, any>(
            CustomDialogContainerComponent,
            dialogConfig).afterClosed().pipe(
            tap(() => {
              this.dynamicComponentFactoryService.destroyDynamicComponent(componentData.componentType);
            })
          );
        }
      ));
  }

}

