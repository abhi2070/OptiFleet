

import {
  Compiler,
  ComponentFactory,
  Inject,
  Injectable,
  Injector,
  ModuleWithComponentFactories,
  Type
} from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { forkJoin, Observable, ReplaySubject, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { IModulesMap } from '@modules/common/modules-map.models';
import { TbResourceId } from '@shared/models/id/tb-resource-id';
import { isObject } from '@core/utils';
import { AuthService } from '@core/auth/auth.service';
import { select, Store } from '@ngrx/store';
import { selectIsAuthenticated } from '@core/auth/auth.selectors';
import { AppState } from '@core/core.state';
import { map, tap } from 'rxjs/operators';

declare const System;

export interface ModulesWithFactories {
  modules: Type<any>[];
  factories: ComponentFactory<any>[];
}

@Injectable({
  providedIn: 'root'
})
export class ResourcesService {

  private loadedJsonResources: { [url: string]: ReplaySubject<any> } = {};
  private loadedResources: { [url: string]: ReplaySubject<void> } = {};
  private loadedModules: { [url: string]: ReplaySubject<Type<any>[]> } = {};
  private loadedModulesAndFactories: { [url: string]: ReplaySubject<ModulesWithFactories> } = {};

  private anchor = this.document.getElementsByTagName('head')[0] || this.document.getElementsByTagName('body')[0];

  constructor(@Inject(DOCUMENT) private readonly document: any,
              protected store: Store<AppState>,
              private compiler: Compiler,
              private http: HttpClient,
              private injector: Injector) {
    this.store.pipe(select(selectIsAuthenticated)).subscribe(() => this.clearModulesCache());
  }

  public loadJsonResource<T>(url: string, postProcess?: (data: T) => T): Observable<T> {
    if (this.loadedJsonResources[url]) {
      return this.loadedJsonResources[url].asObservable();
    }
    const subject = new ReplaySubject<any>();
    this.loadedJsonResources[url] = subject;
    const req$ = (url.endsWith('.raw') || url.endsWith('.svg') ?
      this.http.get(url, {responseType: 'text'}) : this.http.get<T>(url)) as Observable<T>;
    req$.subscribe(
      {
        next: (o) => {
          if (postProcess) {
            o = postProcess(o);
          }
          this.loadedJsonResources[url].next(o);
          this.loadedJsonResources[url].complete();
        },
        error: () => {
          this.loadedJsonResources[url].error(new Error(`Unable to load ${url}`));
          delete this.loadedJsonResources[url];
        }
      }
    );
    return subject.asObservable();
  }

  public loadResource(url: string): Observable<any> {
    if (this.loadedResources[url]) {
      return this.loadedResources[url].asObservable();
    }

    let fileType;
    const match = /[./](css|less|html|htm|js)?(([?#]).*)?$/.exec(url);
    if (match !== null) {
      fileType = match[1];
    }
    if (!fileType) {
      return throwError(() => new Error(`Unable to detect file type from url: ${url}`));
    } else if (fileType !== 'css' && fileType !== 'js') {
      return throwError(() => new Error(`Unsupported file type: ${fileType}`));
    }
    return this.loadResourceByType(fileType, url);
  }

  public downloadResource(downloadUrl: string): Observable<any> {
    return this.http.get(downloadUrl, {
      responseType: 'arraybuffer',
      observe: 'response'
    }).pipe(
      map((response) => {
        const headers = response.headers;
        const filename = headers.get('x-filename');
        const contentType = headers.get('content-type');
        const linkElement = document.createElement('a');
        try {
          const blob = new Blob([response.body], {type: contentType});
          const url = URL.createObjectURL(blob);
          linkElement.setAttribute('href', url);
          linkElement.setAttribute('download', filename);
          const clickEvent = new MouseEvent('click',
            {
              view: window,
              bubbles: true,
              cancelable: false
            }
          );
          linkElement.dispatchEvent(clickEvent);
          return null;
        } catch (e) {
          throw e;
        }
      })
    );
  }

  public loadFactories(resourceId: string | TbResourceId, modulesMap: IModulesMap): Observable<ModulesWithFactories> {
    const url = this.getDownloadUrl(resourceId);
    if (this.loadedModulesAndFactories[url]) {
      return this.loadedModulesAndFactories[url].asObservable();
    }
    modulesMap.init();
    const meta = this.getMetaInfo(resourceId);
    const subject = new ReplaySubject<ModulesWithFactories>();
    this.loadedModulesAndFactories[url] = subject;
    import('@angular/compiler').then(
      () => {
        System.import(url, undefined, meta).then(
          (module) => {
            const modules = this.extractNgModules(module);
            if (modules.length) {
              const tasks: Promise<ModuleWithComponentFactories<any>>[] = [];
              for (const m of modules) {
                tasks.push(this.compiler.compileModuleAndAllComponentsAsync(m));
              }
              forkJoin(tasks).subscribe({
                next: (compiled) => {
                  try {
                    const componentFactories: ComponentFactory<any>[] = [];
                    for (const c of compiled) {
                      c.ngModuleFactory.create(this.injector);
                      componentFactories.push(...c.componentFactories);
                    }
                    const modulesWithFactories: ModulesWithFactories = {
                      modules,
                      factories: componentFactories
                    };
                    this.loadedModulesAndFactories[url].next(modulesWithFactories);
                    this.loadedModulesAndFactories[url].complete();
                  } catch (e) {
                    this.loadedModulesAndFactories[url].error(new Error(`Unable to init module from url: ${url}`));
                  }
                },
                error: (e) => {
                  this.loadedModulesAndFactories[url].error(new Error(`Unable to compile module from url: ${url}`));
                }
              });
            } else {
              this.loadedModulesAndFactories[url].error(new Error(`Module '${url}' doesn't have default export!`));
            }
          },
          (e) => {
            this.loadedModulesAndFactories[url].error(new Error(`Unable to load module from url: ${url}`));
          }
        );
      }
    );
    return subject.asObservable().pipe(
      tap({
        next: () => System.delete(url),
        error: () => {
          delete this.loadedModulesAndFactories[url];
          System.delete(url);
        },
        complete: () => System.delete(url)
      })
    );
  }

  public loadModules(resourceId: string | TbResourceId, modulesMap: IModulesMap): Observable<Type<any>[]> {
    const url = this.getDownloadUrl(resourceId);
    if (this.loadedModules[url]) {
      return this.loadedModules[url].asObservable();
    }
    modulesMap.init();
    const meta = this.getMetaInfo(resourceId);
    const subject = new ReplaySubject<Type<any>[]>();
    this.loadedModules[url] = subject;
    import('@angular/compiler').then(
      () => {
        System.import(url, undefined, meta).then(
          (module) => {
            try {
              let modules;
              try {
                modules = this.extractNgModules(module);
              } catch (e) {
                console.error(e);
              }
              if (modules && modules.length) {
                const tasks: Promise<ModuleWithComponentFactories<any>>[] = [];
                for (const m of modules) {
                  tasks.push(this.compiler.compileModuleAndAllComponentsAsync(m));
                }
                forkJoin(tasks).subscribe((compiled) => {
                    try {
                      for (const c of compiled) {
                        c.ngModuleFactory.create(this.injector);
                      }
                      this.loadedModules[url].next(modules);
                      this.loadedModules[url].complete();
                    } catch (e) {
                      this.loadedModules[url].error(new Error(`Unable to init module from url: ${url}`));
                    }
                  },
                  (e) => {
                    this.loadedModules[url].error(new Error(`Unable to compile module from url: ${url}`));
                  });
              } else {
                this.loadedModules[url].error(new Error(`Module '${url}' doesn't have default export or not NgModule!`));
              }
            } catch (e) {
              this.loadedModules[url].error(new Error(`Unable to load module from url: ${url}`));
            }
          },
          (e) => {
            this.loadedModules[url].error(new Error(`Unable to load module from url: ${url}`));
            console.error(`Unable to load module from url: ${url}`, e);
          }
        );
      }
    );
    return subject.asObservable().pipe(
      tap({
        next: () => System.delete(url),
        error: () => {
          delete this.loadedModulesAndFactories[url];
          System.delete(url);
        },
        complete: () => System.delete(url)
      })
    );
  }

  private extractNgModules(module: any, modules: Type<any>[] = []): Type<any>[] {
    try {
      let potentialModules = [module];
      let currentScanDepth = 0;

      while (potentialModules.length && currentScanDepth < 10) {
        const newPotentialModules = [];
        for (const potentialModule of potentialModules) {
          if (potentialModule && ('ɵmod' in potentialModule)) {
            modules.push(potentialModule);
          } else {
            for (const k of Object.keys(potentialModule)) {
              if (!this.isPrimitive(potentialModule[k])) {
                newPotentialModules.push(potentialModule[k]);
              }
            }
          }
        }
        potentialModules = newPotentialModules;
        currentScanDepth++;
      }
    } catch (e) {
      console.log('Could not load NgModule', e);
    }
    return modules;
  }

  private isPrimitive(test) {
    return test !== Object(test);
  }

  private loadResourceByType(type: 'css' | 'js', url: string): Observable<any> {
    const subject = new ReplaySubject<void>();
    this.loadedResources[url] = subject;
    let el;
    let loaded = false;
    switch (type) {
      case 'js':
        el = this.document.createElement('script');
        el.type = 'text/javascript';
        el.async = false;
        el.src = url;
        break;
      case 'css':
        el = this.document.createElement('link');
        el.type = 'text/css';
        el.rel = 'stylesheet';
        el.href = url;
        break;
    }
    el.onload = el.onreadystatechange = (e) => {
      if (el.readyState && !/^c|loade/.test(el.readyState) || loaded) { return; }
      el.onload = el.onreadystatechange = null;
      loaded = true;
      this.loadedResources[url].next();
      this.loadedResources[url].complete();
    };
    el.onerror = () => {
      this.loadedResources[url].error(new Error(`Unable to load ${url}`));
      delete this.loadedResources[url];
    };
    this.anchor.appendChild(el);
    return subject.asObservable();
  }

  private getDownloadUrl(resourceId: string | TbResourceId): string {
    if (isObject(resourceId)) {
      return `/api/resource/js/${(resourceId as TbResourceId).id}/download`;
    }
    return resourceId as string;
  }

  private getMetaInfo(resourceId: string | TbResourceId): object {
    if (isObject(resourceId)) {
      return {
        additionalHeaders: {
          'X-Authorization': `Bearer ${AuthService.getJwtToken()}`
        }
      };
    }
  }

  private clearModulesCache() {
    this.loadedModules = {};
    this.loadedModulesAndFactories = {};
  }
}
