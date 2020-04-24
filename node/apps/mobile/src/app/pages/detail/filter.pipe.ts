import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
  pure: true,
})
export class FilterPipe implements PipeTransform {
  transform(value: any, callback: (...args) => boolean, ...args: any[]): any {
    return value.filter(callback.bind(null, ...args));
  }
}
