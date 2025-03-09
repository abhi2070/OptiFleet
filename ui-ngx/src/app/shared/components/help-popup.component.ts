

import {
  Component,
  ElementRef,
  Input, OnChanges,
  OnDestroy,
  Renderer2, SimpleChanges,
  ViewChild,
  ViewContainerRef,
  ViewEncapsulation
} from '@angular/core';
import { TbPopoverService } from '@shared/components/popover.service';
import { PopoverPlacement } from '@shared/components/popover.models';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { isDefinedAndNotNull } from '@core/utils';
import { coerceBoolean } from '@shared/decorators/coercion';

@Component({
  selector: '[tb-help-popup], [tb-help-popup-content]',
  templateUrl: './help-popup.component.html',
  styleUrls: ['./help-popup.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HelpPopupComponent implements OnChanges, OnDestroy {

  @ViewChild('toggleHelpButton', {read: ElementRef, static: false}) toggleHelpButton: ElementRef;
  @ViewChild('toggleHelpTextButton', {read: ElementRef, static: false}) toggleHelpTextButton: ElementRef;

  @Input('tb-help-popup') helpId: string;

  @Input('tb-help-popup-content') helpContent: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('trigger-text') triggerText: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('trigger-style') triggerStyle: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('tb-help-popup-placement') helpPopupPlacement: PopoverPlacement;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('tb-help-popup-style') helpPopupStyle: { [klass: string]: any } = {};

  popoverVisible = false;
  popoverReady = true;


  @Input()
  @coerceBoolean()
  hintMode = false;

  triggerSafeHtml: SafeHtml = null;
  textMode = false;

  constructor(private viewContainerRef: ViewContainerRef,
              private element: ElementRef<HTMLElement>,
              private sanitizer: DomSanitizer,
              private renderer: Renderer2,
              private popoverService: TbPopoverService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (isDefinedAndNotNull(this.triggerText)) {
      this.triggerSafeHtml = this.sanitizer.bypassSecurityTrustHtml(this.triggerText);
    } else {
      this.triggerSafeHtml = null;
    }
    this.textMode = this.triggerSafeHtml != null;
  }

  toggleHelp() {
    const trigger = this.textMode ? this.toggleHelpTextButton.nativeElement : this.toggleHelpButton.nativeElement;
    this.popoverService.toggleHelpPopover(trigger, this.renderer, this.viewContainerRef,
      this.helpId,
      this.helpContent,
      (visible) => {
        this.popoverVisible = visible;
      }, (ready => {
        this.popoverReady = ready;
      }),
      this.helpPopupPlacement,
      {},
      this.helpPopupStyle);
  }

  ngOnDestroy(): void {
  }

}
