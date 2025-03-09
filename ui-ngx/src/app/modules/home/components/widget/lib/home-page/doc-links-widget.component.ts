

import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { Authority } from '@shared/models/authority.enum';
import { map, Subscription } from 'rxjs';
import { DocumentationLink, DocumentationLinks } from '@shared/models/user-settings.models';
import { UserSettingsService } from '@core/http/user-settings.service';
import { getCurrentAuthUser } from '@core/auth/auth.selectors';
import { WidgetContext } from '@home/models/widget-component.models';
import { MatDialog } from '@angular/material/dialog';
import { AddDocLinkDialogComponent } from '@home/components/widget/lib/home-page/add-doc-link-dialog.component';
import {
  EditLinksDialogComponent,
  EditLinksDialogData
} from '@home/components/widget/lib/home-page/edit-links-dialog.component';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { MediaBreakpoints } from '@shared/models/constants';

const defaultDocLinksMap = new Map<Authority, DocumentationLinks>(
  [
    [Authority.SYS_ADMIN, {
      links: [
        {
          icon: 'rocket',
          name: 'Getting started',
          link: 'https://optifleet.com'
        },
        {
          icon: 'title',
          name: 'Tenant profiles',
          link: 'https://optifleet.com'
        },
        {
          icon: 'insert_chart',
          name: 'API',
          link: 'https://optifleet.com'
        },
        {
          icon: 'now_widgets',
          name: 'Widgets Library',
          link: 'https://optifleet.com'
        }
      ]
    }],
    [Authority.TENANT_ADMIN, {
      links: [
        {
          icon: 'rocket',
          name: 'Getting started',
          link: 'https://optifleet.com'
        },
        {
          icon: 'settings_ethernet',
          name: 'Rule engine',
          link: 'https://optifleet.com'
        },
        {
          icon: 'insert_chart',
          name: 'API',
          link: 'https://optifleet.com'
        },
        {
          icon: 'devices',
          name: 'Device profiles',
          link: 'https://optifleet.com'
        }
      ]
    }],
    [Authority.CUSTOMER_USER, {
      links: [
        {
          icon: 'rocket',
          name: 'Getting started',
          link: 'https://optifleet.com'
        }
      ]
    }]
  ]
);

interface DocLinksWidgetSettings {
  columns: number;
}

@Component({
  selector: 'tb-doc-links-widget',
  templateUrl: './doc-links-widget.component.html',
  styleUrls: ['./home-page-widget.scss', './links-widget.component.scss']
})
export class DocLinksWidgetComponent extends PageComponent implements OnInit, OnDestroy {

  @Input()
  ctx: WidgetContext;

  settings: DocLinksWidgetSettings;
  columns: number;
  rowHeight = '55px';
  gutterSize = '12px';

  documentationLinks: DocumentationLinks;
  authUser = getCurrentAuthUser(this.store);

  private observeBreakpointSubscription: Subscription;

  constructor(protected store: Store<AppState>,
              private cd: ChangeDetectorRef,
              private userSettingsService: UserSettingsService,
              private dialog: MatDialog,
              private breakpointObserver: BreakpointObserver) {
    super(store);
  }

  ngOnInit() {
    this.settings = this.ctx.settings;
    this.columns = this.settings.columns || 3;
    const isMdLg = this.breakpointObserver.isMatched(MediaBreakpoints['md-lg']);
    this.rowHeight = isMdLg ? '18px' : '55px';
    this.gutterSize = isMdLg ? '8px' : '12px';
    this.observeBreakpointSubscription = this.breakpointObserver
      .observe(MediaBreakpoints['md-lg'])
      .subscribe((state: BreakpointState) => {
          if (state.matches) {
            this.rowHeight = '18px';
            this.gutterSize = '8px';
          } else {
            this.rowHeight = '55px';
            this.gutterSize = '12px';
          }
          this.cd.markForCheck();
        }
    );
    this.loadDocLinks();
  }

  ngOnDestroy() {
    if (this.observeBreakpointSubscription) {
      this.observeBreakpointSubscription.unsubscribe();
    }
    super.ngOnDestroy();
  }

  private loadDocLinks() {
    this.userSettingsService.getDocumentationLinks().pipe(
      map((documentationLinks) => {
        if (!documentationLinks || !documentationLinks.links) {
          return defaultDocLinksMap.get(this.authUser.authority);
        } else {
          return documentationLinks;
        }
      })
    ).subscribe(
      (documentationLinks) => {
        this.documentationLinks = documentationLinks;
        this.cd.markForCheck();
      }
    );
  }

  edit() {
    this.dialog.open<EditLinksDialogComponent, EditLinksDialogData,
      boolean>(EditLinksDialogComponent, {
      disableClose: true,
      autoFocus: false,
      data: {
        mode: 'docs',
        links: this.documentationLinks
      },
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe(
      (result) => {
        if (result) {
          this.loadDocLinks();
        }
      });
  }

  addLink() {
    this.dialog.open<AddDocLinkDialogComponent, any,
      DocumentationLink>(AddDocLinkDialogComponent, {
      disableClose: true,
      autoFocus: false,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe(
      (docLink) => {
        if (docLink) {
          this.documentationLinks.links.push(docLink);
          this.cd.markForCheck();
          this.userSettingsService.updateDocumentationLinks(this.documentationLinks).subscribe();
        }
    });
  }
}
