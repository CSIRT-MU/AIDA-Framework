import { Pipe, PipeTransform } from '@angular/core';
/*
 * Converts newlines into html breaks
*/
@Pipe({ name: 'newLine' })
export class NewLinePipe implements PipeTransform {
  transform(value: string, args: string[]): any {
    return value.replace(/(?:_)/g, ' _');
  }
}