import { Component, OnInit, Input } from '@angular/core';
import { BackendService } from '../../../services/backend.service';
import { environment } from '../../../../environments/environment';
import { Attack } from './attack.model';

@Component({
  selector: 'widget-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css'],
  providers: [BackendService]
})
export class TableComponent implements OnInit {

  // @Input() interval: number;
  @Input() aggregation: number;
  @Input() title: string;
  @Input() data: Attack[];
  @Input() maxPages = 10;

  currentPage: number;
  itemsCount: number;

  filteredData: Attack[];
  sortedData: Attack[];

  header: string[] = [
    'Timestamp', 'Source', 'Destination', 'Flows', 'Duration'
  ];

  constructor(private backend: BackendService) { }

  ngOnInit() {
    this.currentPage = 1;
    this.itemsCount = this.data.length;
    this.sortedData = this.data;
    this.setPage(1);
  }

  setPage(pageNumber: number) {
    this.currentPage = pageNumber;
    this.filteredData = this.sortedData.slice(
      (pageNumber - 1) * environment.itemsPerPage,
      (pageNumber - 1) * environment.itemsPerPage + environment.itemsPerPage);
  }

  dynamicSort(property) {
    return function (a, b) {
        const result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
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
