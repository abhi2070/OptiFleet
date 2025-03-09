

import { AfterViewInit, Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { WidgetContext } from '@home/models/widget-component.models';
import { ContentType } from '@shared/models/constants';
import {
  JsonObjectEditDialogComponent,
  JsonObjectEditDialogData
} from '@shared/components/dialog/json-object-edit-dialog.component';
import { jsonRequired } from '@shared/components/json-object-edit.component';

@Component({
  selector: 'tb-gateway-service-rpc',
  templateUrl: './gateway-service-rpc.component.html',
  styleUrls: ['./gateway-service-rpc.component.scss']
})
export class GatewayServiceRPCComponent implements AfterViewInit {

  @Input()
  ctx: WidgetContext;

  contentTypes = ContentType;

  resultTime: number | null;

  @Input()
  dialogRef: MatDialogRef<any>;

  commandForm: FormGroup;

  isConnector: boolean;

  RPCCommands: Array<string> = [
    'Ping',
    'Stats',
    'Devices',
    'Update',
    'Version',
    'Restart',
    'Reboot'
  ];

  private connectorType: string;

  constructor(private fb: FormBuilder,
              private dialog: MatDialog) {
    this.commandForm = this.fb.group({
      command: [null,[Validators.required]],
      time: [60, [Validators.required, Validators.min(1)]],
      params: ['{}', [jsonRequired]],
      result: [null]
    });
  }

  ngAfterViewInit() {
    this.isConnector = this.ctx.settings.isConnector;
    if (!this.isConnector) {
      this.commandForm.get('command').setValue(this.RPCCommands[0]);
    } else {
      this.connectorType = this.ctx.stateController.getStateParams().connector_rpc.value.type;
    }
  }

  sendCommand() {
    this.resultTime = null;
    const formValues = this.commandForm.value;
    const commandPrefix = this.isConnector ? `${this.connectorType}_` : 'gateway_';
    this.ctx.controlApi.sendTwoWayCommand(commandPrefix+formValues.command.toLowerCase(), formValues.params,formValues.time).subscribe({
      next: resp => {
        this.resultTime  = new Date().getTime();
        this.commandForm.get('result').setValue(JSON.stringify(resp))
      },
      error: error => {
        this.resultTime  = new Date().getTime();
        console.error(error);
        this.commandForm.get('result').setValue(JSON.stringify(error.error));
      }
    });
  }

  openEditJSONDialog($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<JsonObjectEditDialogComponent, JsonObjectEditDialogData, object>(JsonObjectEditDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        jsonValue: JSON.parse(this.commandForm.get('params').value),
        required: true
      }
    }).afterClosed().subscribe(
      (res) => {
        if (res) {
          this.commandForm.get('params').setValue(JSON.stringify(res));
        }
      }
    );
  }
}
