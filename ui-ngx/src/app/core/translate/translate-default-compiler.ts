

import {
  MESSAGE_FORMAT_CONFIG,
  MessageFormatConfig,
  TranslateMessageFormatCompiler
} from 'ngx-translate-messageformat-compiler';
import { Inject, Injectable, Optional } from '@angular/core';
import { parse } from '@messageformat/parser';

@Injectable({ providedIn: 'root' })
export class TranslateDefaultCompiler extends TranslateMessageFormatCompiler {

  constructor(
    @Optional()
    @Inject(MESSAGE_FORMAT_CONFIG)
      config?: MessageFormatConfig
  ) {
    super(config);
  }

  public compile(value: string, lang: string): (params: any) => string {
    return this.defaultCompile(value, lang);
  }

  public compileTranslations(translations: any, lang: string): any {
    return this.defaultCompile(translations, lang);
  }

  private defaultCompile(src: any, lang: string): any {
    if (typeof src !== 'object') {
      if (this.checkIsPlural(src)) {
        try {
          return super.compile(src, lang.replace('_', '-'));
        } catch (e) {
          console.warn('Failed compile translate:', src, e);
          return src;
        }
      } else {
        return src;
      }
    } else {
      const result = {};
      for (const key of Object.keys(src)) {
        result[key] = this.defaultCompile(src[key], lang);
      }
      return result;
    }
  }

  private checkIsPlural(src: string): boolean {
    let tokens: any[];
    try {
      tokens = parse(src.replace(/\{\{/g, '{').replace(/\}\}/g, '}'),
        {cardinal: [], ordinal: []});
    } catch (e) {
      console.warn(`Failed to parse source: ${src}`);
      console.error(e);
      return false;
    }
    const res = tokens.filter(
      (value) => typeof value !== 'string' && value.type === 'plural'
    );
    return res.length > 0;
  }

}
