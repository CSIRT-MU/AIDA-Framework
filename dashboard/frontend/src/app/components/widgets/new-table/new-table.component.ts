import { DataService } from './../../../services/data.service';
import { Component, Input, OnChanges, SimpleChange, EventEmitter, Output, ViewEncapsulation, SecurityContext } from '@angular/core';
import { MessengerService } from '../../../services/messenger.service';
import { DomSanitizer } from '@angular/platform-browser';
import { ColumnHeader } from '../display-rules/display-rules-cfg';

/**
 * Original widget is taken from Security Dashboard
 *
 * it is modified to display some visuals required for widget-display-rules
 * to establish communication between widgets there is added MessengerService
 *
* @author Jakub Kolman <442445@mail.muni.cz> (I just modified the widget)
 */

@Component({
  selector: 'widget-new-table',
  templateUrl: './new-table.component.html',
  styleUrls: ['./new-table.component.scss'],
})
export class NewTableComponent implements OnChanges {

  @Input('title') title: string;
  @Input('data') data: Object[];
  @Input('header') header: ColumnHeader[];
  @Input('trim') trim?: boolean;
  @Input() maxPages;
  @Output() myEvent = new EventEmitter();

  currentPage: number;
  itemsCount: number;

  showPageFooter: boolean;
  filteredData: Object[];
  sortedData: Object[];

  constructor(private _service: MessengerService, private domSanitizer: DomSanitizer) {
    // by default trim is enabled
    this.trim = true;
  }

  /**
   * @param id represnts row
   * @param action specifies whether to enable or disable rule
   */
  functionRule(id, action) {
    this._service.sendMessage(id, action);
  }

  ngOnChanges(changes: { [propKey: string]: SimpleChange }) {
    this.currentPage = 1;
    this.showPageFooter = this.maxPages < this.data.length;
    this.itemsCount = this.data.length;
    this.sortedData = this.data;
    this.setPage(1);
  }

  setPage(pageNumber: number) {
    this.currentPage = pageNumber;
    this.filteredData = this.sortedData.slice(
      (pageNumber - 1) * this.maxPages,
      (pageNumber - 1) * this.maxPages + this.maxPages);
  }

  dynamicSort(property) {
    return (a, b) => {

      let result;

      let itemA = a[property];
      let itemB = b[property];

      itemA = this.domSanitizer.sanitize(SecurityContext.HTML, (itemA == null ? '' : itemA).toString());
      itemB = this.domSanitizer.sanitize(SecurityContext.HTML, (itemB == null ? '' : itemB).toString());

      // THIS IS PURE MADNESS!! WROTE AT 3:00 AM
      // check units
      const units = 'KiB\\/s|MiB\\/s|GiB\\/s|TiB\\/s|PiB\\/s|EiB\\/s|ZiB\\/s|YiB\\/s|K\\/s|M\\/s|G\\/s'
        + '|B\\/s|KiB|MiB|GiB|TiB|PiB|EiB|ZiB|YiB|K|M|G|B|packets\\/s|packets|flows\\/s|flows';
      const unitsOrder = ['B', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB', 'K', 'M', 'G'];
      const unitsOrderPerS = ['B/s', 'KiB/s', 'MiB/s', 'GiB/s', 'TiB/s', 'PiB/s', 'EiB/s', 'ZiB/s', 'YiB/s', 'K/s', 'M/s', 'G/s'];

      let compareUnitsA = false;
      let compareUnitsB = false;
      const unitsRegex = '^(\\d+\\.?\\d{0,9}|\\.\\d{1,9}).*(' + units + ')';
      let itemsA;
      let itemsB;
      if (itemA.toString().match(unitsRegex)) {
        itemsA = itemA.toString().split(' ');
        compareUnitsA = true;
      }
      if (itemB.toString().match(unitsRegex)) {
        itemsB = itemB.toString().split(' ');
        compareUnitsB = true;
      }
      if (compareUnitsA && compareUnitsB) {
        if (itemsA.length > 2 && itemsB.length > 2) {

          const numberA = Number.parseFloat(itemsA[0]);
          const numberB = Number.parseFloat(itemsB[0]);
          const sizeA = unitsOrder.indexOf(itemsA[1]);
          const sizeB = unitsOrder.indexOf(itemsB[1]);

          return sizeA === sizeB ? (numberA < numberB) ? -1 : (numberA > numberB) ? 1 : 0 :
            sizeA > sizeB ? 1 : sizeA < sizeB ? -1 : 0;
        } else {
          const unitAIndex = itemsA[1].indexOf('/s') !== -1 ? unitsOrderPerS.indexOf(itemsA[1]) : unitsOrder.indexOf(itemsA[1]);
          const unitBIndex = itemsB[1].indexOf('/s') !== -1 ? unitsOrderPerS.indexOf(itemsB[1]) : unitsOrder.indexOf(itemsB[1]);
          return unitAIndex === unitBIndex ? (Number.parseFloat(itemsA[0]) < Number.parseFloat(itemsB[0]))
            ? -1 : (Number.parseFloat(itemA[0]) > Number.parseFloat(itemsB[0])) ? 1 : 0 :
            (unitAIndex < unitBIndex) ? -1 : (unitAIndex > unitBIndex) ? 1 : 0;
        }
      }


      // if String compare like String
      if ((typeof itemA === 'string')
        || typeof itemB === 'string') {
        return itemA.localeCompare(itemB);
      }

      // check if Date
      const timeA = Date.parse(itemA.toString().replace(/\./g, '/'));
      const timeB = Date.parse(itemB.toString().replace(/\./g, '/'));
      if (!Number.isNaN(timeA) && !Number.isNaN(timeB)) {
        result = (timeA < timeB) ? -1 : (timeA > timeB) ? 1 : 0;
      } else {
        result = (itemA < itemB) ? -1 : (itemA > itemB) ? 1 : 0;
      }
      return result;
    };

  }

  sortAsc(property: string) {
    this.sortedData = [...this.data].sort(this.dynamicSort(property));
    this.setPage(this.currentPage);
  }

  sortDesc(property: string) {
    this.sortedData = [...this.data].sort(this.dynamicSort(property)).reverse();
    this.setPage(this.currentPage);
  }
}
