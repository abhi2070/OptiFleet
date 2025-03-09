/* eslint-disable @typescript-eslint/ban-types */
import { Component, Inject, Renderer2, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppState } from '@app/core/public-api';
import { PageComponent } from '@app/shared/public-api';
import { Store } from '@ngrx/store';

@Component({
  selector: 'tb-threed-dynamic-menu-dialog',
  templateUrl: './threed-dynamic-menu-dialog.component.html',
  styleUrls: ['./../widget-settings.scss']
})
export class ThreedDynamicMenuDialogComponent extends PageComponent implements OnInit, AfterViewInit {
  @ViewChild('container', { static: true }) container: ElementRef;

  constructor(
    protected store: Store<AppState>,
    private renderer: Renderer2,
    public dialogRef: MatDialogRef<ThreedDynamicMenuDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      html: string;
      css: string;
      js: { fnc: Function; args: any[] };
    }) {
    super(store);
  }

  ngOnInit() {
    this.addStyle(this.data?.css);
  }

  ngAfterViewInit(): void {
    this.container.nativeElement.innerHTML = this.data.html;
    this.data.js.fnc(...this.data.js.args);
  }

  addStyle(css?: string) {
    if (!css) { return; }

    const style = this.renderer.createElement('style');
    style.type = 'text/css';
    style.appendChild(this.renderer.createText(css));
    this.renderer.appendChild(document.head, style);
  }

  addScript(js?: string) {
    if (!js) { return; }

    const script = this.renderer.createElement('script');
    script.type = 'text/javascript';
    script.appendChild(this.renderer.createText(js));
    this.renderer.appendChild(document.head, script);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
