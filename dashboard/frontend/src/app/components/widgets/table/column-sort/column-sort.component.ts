import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-column-sort',
  templateUrl: './column-sort.component.html'
})
export class ColumnSortComponent implements OnInit {

  @Input() property: string;
  @Output() sortedAsc: EventEmitter<string> = new EventEmitter<string>();
  @Output() sortedDesc: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
    // console.log('property', this.property);
  }

  sortAsc() {
    this.sortedAsc.emit(this.property);
  }
  sortDesc() {
    this.sortedDesc.emit(this.property);
  }

}
