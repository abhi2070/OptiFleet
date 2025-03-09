

import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { coerceBoolean } from '@shared/decorators/coercion';

@Component({
  selector: 'tb-error',
  template: `
    <div [@animation]="state" [ngStyle]="{marginTop: noMargin ? '0' : '0.5rem', fontSize: '.75rem'}">
      <mat-error>
        {{message}}
      </mat-error>
    </div>
  `,
  styles: [`
    :host {
        height: 24px;
    }
  `],
  animations: [
    trigger('animation', [
      state('show', style({
        opacity: 1,
        transform: 'translateY(0)'
      })),
      state('hide',   style({
        opacity: 0,
        transform: 'translateY(-1rem)'
      })),
      transition('* <=> *', animate('200ms ease-out'))
    ]),
  ]
})
export class TbErrorComponent {
  errorValue: string;
  state = 'hide';
  message: string;

  constructor(private cd: ChangeDetectorRef) {
  }

  @Input()
  @coerceBoolean()
  noMargin = false;

  @Input()
  set error(value) {
    if (this.errorValue !== value) {
      this.errorValue = value;
      if (value) {
        this.message = value;
      }
      this.state = value ? 'show' : 'hide';
      this.cd.markForCheck();
    }
  }
}
