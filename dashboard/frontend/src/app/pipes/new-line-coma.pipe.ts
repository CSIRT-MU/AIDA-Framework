import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'newLineComa'
})
export class NewLineComaPipe implements PipeTransform {

  transform(value: string, args: string[]): any {
    return value.replace(/(?:,)/g, ', \n');
  }
}
