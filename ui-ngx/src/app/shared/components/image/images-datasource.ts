

import { CollectionViewer, DataSource, SelectionModel } from '@angular/cdk/collections';
import { ImageResourceInfo } from '@shared/models/resource.models';
import { BehaviorSubject, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { emptyPageData, PageData } from '@shared/models/page/page-data';
import { ImageService } from '@core/http/image.service';
import { EntityBooleanFunction } from '@home/models/entity/entities-table-config.models';
import { PageLink } from '@shared/models/page/page-link';
import { catchError, map, take, tap } from 'rxjs/operators';

export class ImagesDatasource implements DataSource<ImageResourceInfo> {
  private entitiesSubject: Subject<ImageResourceInfo[]>;
  private readonly pageDataSubject: Subject<PageData<ImageResourceInfo>>;

  public pageData$: Observable<PageData<ImageResourceInfo>>;

  public selection = new SelectionModel<ImageResourceInfo>(true, []);

  public dataLoading = true;

  constructor(private imageService: ImageService,
              private images: ImageResourceInfo[],
              private selectionEnabledFunction: EntityBooleanFunction<ImageResourceInfo>) {
    if (this.images && this.images.length) {
      this.entitiesSubject = new BehaviorSubject<ImageResourceInfo[]>(this.images);
    } else {
      this.entitiesSubject = new BehaviorSubject<ImageResourceInfo[]>([]);
      this.pageDataSubject = new BehaviorSubject<PageData<ImageResourceInfo>>(emptyPageData<ImageResourceInfo>());
      this.pageData$ = this.pageDataSubject.asObservable();
    }
  }

  connect(collectionViewer: CollectionViewer):
    Observable<ImageResourceInfo[] | ReadonlyArray<ImageResourceInfo>> {
    return this.entitiesSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.entitiesSubject.complete();
    if (this.pageDataSubject) {
      this.pageDataSubject.complete();
    }
  }

  reset() {
    this.entitiesSubject.next([]);
    if (this.pageDataSubject) {
      this.pageDataSubject.next(emptyPageData<ImageResourceInfo>());
    }
  }

  loadEntities(pageLink: PageLink, includeSystemImages = false): Observable<PageData<ImageResourceInfo>> {
    this.dataLoading = true;
    const result = new ReplaySubject<PageData<ImageResourceInfo>>();
    this.fetchEntities(pageLink, includeSystemImages).pipe(
      tap(() => {
        this.selection.clear();
      }),
      catchError(() => of(emptyPageData<ImageResourceInfo>())),
    ).subscribe(
      (pageData) => {
        this.entitiesSubject.next(pageData.data);
        this.pageDataSubject.next(pageData);
        result.next(pageData);
        this.dataLoading = false;
      }
    );
    return result;
  }

  fetchEntities(pageLink: PageLink, includeSystemImages = false): Observable<PageData<ImageResourceInfo>> {
    return this.imageService.getImages(pageLink, includeSystemImages);
  }

  isAllSelected(): Observable<boolean> {
    const numSelected = this.selection.selected.length;
    return this.entitiesSubject.pipe(
      map((entities) => numSelected === entities.length)
    );
  }

  isEmpty(): Observable<boolean> {
    return this.entitiesSubject.pipe(
      map((entities) => !entities.length)
    );
  }

  total(): Observable<number> {
    return this.pageDataSubject.pipe(
      map((pageData) => pageData.totalElements)
    );
  }

  masterToggle() {
    this.entitiesSubject.pipe(
      tap((entities) => {
        const numSelected = this.selection.selected.length;
        if (numSelected === this.selectableEntitiesCount(entities)) {
          this.selection.clear();
        } else {
          entities.forEach(row => {
            if (this.selectionEnabledFunction(row)) {
              this.selection.select(row);
            }
          });
        }
      }),
      take(1)
    ).subscribe();
  }

  private selectableEntitiesCount(entities: Array<ImageResourceInfo>): number {
    return entities.filter((entity) => this.selectionEnabledFunction(entity)).length;
  }
}
