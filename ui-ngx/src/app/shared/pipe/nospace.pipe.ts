

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'nospace'
})
export class NospacePipe implements PipeTransform {

  transform(value: string, args?: any): string {
    return (!value) ? '' : value.replace(/ /g, '');
  }

}
