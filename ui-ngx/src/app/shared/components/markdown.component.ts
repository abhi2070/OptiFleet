

import {
  ChangeDetectorRef,
  Component,
  ComponentRef,
  ElementRef,
  EventEmitter,
  Inject,
  Injector,
  Input, NgZone,
  OnChanges,
  Output,
  Renderer2,
  SimpleChanges,
  Type,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { HelpService } from '@core/services/help.service';
import { MarkdownService, PrismPlugin } from 'ngx-markdown';
import { DynamicComponentFactoryService } from '@core/services/dynamic-component-factory.service';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { SHARED_MODULE_TOKEN } from '@shared/components/tokens';
import { deepClone, guid, isDefinedAndNotNull } from '@core/utils';
import { Observable, of, ReplaySubject } from 'rxjs';
import { coerceBoolean } from '@shared/decorators/coercion';

let defaultMarkdownStyle;

@Component({
  selector: 'tb-markdown',
  templateUrl: './markdown.component.html',
  styleUrls: ['./markdown.component.scss']
})
export class TbMarkdownComponent implements OnChanges {

  @ViewChild('markdownContainer', {read: ViewContainerRef, static: true}) markdownContainer: ViewContainerRef;
  @ViewChild('fallbackElement', {static: true}) fallbackElement: ElementRef<HTMLElement>;

  @Input() data: string | undefined;

  @Input() context: any;

  @Input() additionalCompileModules: Type<any>[];

  @Input() markdownClass: string | undefined;

  @Input() containerClass: string | undefined;

  @Input() style: { [klass: string]: any } = {};

  @Input() applyDefaultMarkdownStyle = true;

  @Input() additionalStyles: string[];

  @Input()
  get lineNumbers(): boolean { return this.lineNumbersValue; }
  set lineNumbers(value: boolean) { this.lineNumbersValue = coerceBooleanProperty(value); }

  @Input()
  get fallbackToPlainMarkdown(): boolean { return this.fallbackToPlainMarkdownValue; }
  set fallbackToPlainMarkdown(value: boolean) { this.fallbackToPlainMarkdownValue = coerceBooleanProperty(value); }

  @Input()
  @coerceBoolean()
  usePlainMarkdown = false;

  @Output() ready = new EventEmitter<void>();

  private lineNumbersValue = false;
  private fallbackToPlainMarkdownValue = false;

  isMarkdownReady = false;

  error = null;

  private tbMarkdownInstanceComponentRef: ComponentRef<any>;
  private tbMarkdownInstanceComponentType: Type<any>;

  constructor(private help: HelpService,
              private cd: ChangeDetectorRef,
              private zone: NgZone,
              public markdownService: MarkdownService,
              @Inject(SHARED_MODULE_TOKEN) private sharedModule: Type<any>,
              private dynamicComponentFactoryService: DynamicComponentFactoryService,
              private renderer: Renderer2) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (isDefinedAndNotNull(this.data)) {
      this.zone.run(() => this.render(this.data));
    }
  }

  private render(markdown: string) {
    const compiled = this.markdownService.parse(markdown, { decodeHtml: false });
    let markdownClass = 'tb-markdown-view';
    if (this.markdownClass) {
      markdownClass += ` ${this.markdownClass}`;
    }
    let template = `<div [ngStyle]="style" class="${markdownClass}">${compiled}</div>`;
    if (this.containerClass) {
      template = `<div class="${this.containerClass}" style="width: 100%; height: 100%;">${template}</div>`;
    }
    const element: HTMLDivElement = this.renderer.createElement('div');
    element.innerHTML = template;
    this.handlePlugins(element);
    this.markdownService.highlight(element);
    const preElements = element.querySelectorAll('pre');
    const matches = Array.from(template.matchAll(/<pre[\S\s]+?(?=<\/pre>)<\/pre>/g));
    for (let i = 0; i < preElements.length; i++) {
      const preHtml = preElements.item(i).outerHTML.replace('ngnonbindable=""', 'ngNonBindable');
      template = template.replace(matches[i][0], preHtml);
    }
    template = this.sanitizeCurlyBraces(template);
    this.markdownContainer.clear();
    let styles: string[] = [];
    let readyObservable: Observable<void>;
    if (this.applyDefaultMarkdownStyle) {
      if (!defaultMarkdownStyle) {
        defaultMarkdownStyle = deepClone(TbMarkdownComponent['ɵcmp'].styles)[0].replace(/\[_nghost\-%COMP%\]/g, '')
          .replace(/\[_ngcontent\-%COMP%\]/g, '');
      }
      styles.push(defaultMarkdownStyle);
    }
    if (this.additionalStyles) {
      styles = styles.concat(this.additionalStyles);
    }
    if (this.usePlainMarkdown) {
      readyObservable = this.plainMarkdown(template, styles);
      this.cd.detectChanges();
      readyObservable.subscribe(() => {
        this.ready.emit();
      });
    } else {
      const parent = this;
      let compileModules = [this.sharedModule];
      if (this.additionalCompileModules) {
        compileModules = compileModules.concat(this.additionalCompileModules);
      }
      this.dynamicComponentFactoryService.createDynamicComponent(
        class TbMarkdownInstance {
          ngOnDestroy(): void {
            parent.destroyMarkdownInstanceResources();
          }
        },
        template,
        compileModules,
        true, 1, styles
      ).subscribe((componentData) => {
          this.tbMarkdownInstanceComponentType = componentData.componentType;
          const injector: Injector = Injector.create({providers: [], parent: this.markdownContainer.injector});
          try {
            this.tbMarkdownInstanceComponentRef =
              this.markdownContainer.createComponent(this.tbMarkdownInstanceComponentType,
                {index: 0, injector, ngModuleRef: componentData.componentModuleRef});
            if (this.context) {
              for (const propName of Object.keys(this.context)) {
                this.tbMarkdownInstanceComponentRef.instance[propName] = this.context[propName];
              }
            }
            this.tbMarkdownInstanceComponentRef.instance.style = this.style;
            readyObservable = this.handleImages(this.tbMarkdownInstanceComponentRef.location.nativeElement);
            this.cd.detectChanges();
            this.error = null;
          } catch (error) {
            readyObservable = this.handleError(template, error, styles);
            this.cd.detectChanges();
          }
          readyObservable.subscribe(() => {
            this.ready.emit();
          });
        },
        (error) => {
          readyObservable = this.handleError(template, error, styles);
          this.cd.detectChanges();
          readyObservable.subscribe(() => {
            this.ready.emit();
          });
        });
    }
  }

  private handleError(template: string, error, styles?: string[]): Observable<void> {
    this.error = (error ? error + '' : 'Failed to render markdown!').replace(/\n/g, '<br>');
    this.markdownContainer.clear();
    if (this.fallbackToPlainMarkdownValue) {
      return this.plainMarkdown(template, styles);
    } else {
      return of(null);
    }
  }

  private plainMarkdown(template: string, styles?: string[]): Observable<void> {
    const element = this.fallbackElement.nativeElement;
    let styleElement;
    if (styles?.length) {
      const markdownClass = 'tb-markdown-view-' + guid();
      let innerStyle = styles.join('\n');
      innerStyle = innerStyle.replace(/\.tb-markdown-view/g, '.' + markdownClass);
      template = template.replace(/tb-markdown-view/g, markdownClass);
      styleElement = this.renderer.createElement('style');
      styleElement.innerHTML = innerStyle;
    }
    element.innerHTML = template;
    if (styleElement) {
      this.renderer.appendChild(element, styleElement);
    }
    return this.handleImages(element);
  }

  private handlePlugins(element: HTMLElement): void {
    if (this.lineNumbers) {
      this.setPluginClass(element, PrismPlugin.LineNumbers);
    }
  }

  private setPluginClass(element: HTMLElement, plugin: string | string[]): void {
    const preElements = element.querySelectorAll('pre');
    for (let i = 0; i < preElements.length; i++) {
      const classes = plugin instanceof Array ? plugin : [plugin];
      preElements.item(i).classList.add(...classes);
    }
  }

  private handleImages(element: HTMLElement): Observable<void> {
    const imgs = $('img', element);
    if (imgs.length) {
      let totalImages = imgs.length;
      const imagesLoadedSubject = new ReplaySubject<void>();
      imgs.each((index, img) => {
        $(img).one('load error', () => {
          totalImages--;
          if (totalImages === 0) {
            imagesLoadedSubject.next();
            imagesLoadedSubject.complete();
          }
        });
      });
      return imagesLoadedSubject.asObservable();
    } else {
      return of(null);
    }
  }

  private sanitizeCurlyBraces(template: string): string {
    return template.replace(/{/g, '&#123;').replace(/}/g, '&#125;');
  }

  private destroyMarkdownInstanceResources() {
    if (this.tbMarkdownInstanceComponentType) {
      this.dynamicComponentFactoryService.destroyDynamicComponent(this.tbMarkdownInstanceComponentType);
      this.tbMarkdownInstanceComponentType = null;
    }
    this.tbMarkdownInstanceComponentRef = null;
  }
}
