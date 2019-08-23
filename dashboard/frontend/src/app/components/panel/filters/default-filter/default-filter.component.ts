import { Component, OnInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { Filter } from '../../../../models/filter.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-default-filter',
  templateUrl: './default-filter.component.html',
  styleUrls: [
    './default-filter.component.scss'
  ]
})
export class DefaultFilterComponent implements OnInit {

  @Output() intervalChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output() aggregationChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output() dateFromChanged: EventEmitter<Date> = new EventEmitter<Date>();
  @Output() dateToChanged: EventEmitter<Date> = new EventEmitter<Date>();
  @Output() ipChanged: EventEmitter<string> = new EventEmitter<string>();
  @Output() submitted: EventEmitter<Filter> = new EventEmitter<Filter>();
  @Output() filterChange: EventEmitter<string> = new EventEmitter<string>();

  @Input() filter: Filter;

  @ViewChild('ipInput') ipInput: ElementRef;

  title = 'Filter';

  ngOnInit() {
    // trigger filter submittion on load
    // this.onSubmit();
  }

  changeInterval(input: HTMLSelectElement) {
    const val = parseInt(input.value, 10);
    this.filter.setInterval(val);
  }

  changeAggregation(input) {
    const val = input.srcElement.value;
    this.aggregationChanged.emit(val);
    this.filter.aggregation = val;
  }
  changeDateFrom(input) {
    const date = new Date(input);
    this.dateFromChanged.emit(date);
    this.filter.dateFrom = date;
  }
  changeDateTo(input) {
    const date = new Date(input);
    this.dateToChanged.emit(date);
    this.filter.dateTo = date;
  }
  changeIPFilter(value) {
    // console.log('input', input);
    // const val = input.srcElement.value;
    this.setIPFilter(value);
  }
  setIPFilter(ip: string) {
    // TODO this is quite hacky solution, dispatchEvent() should be replaced with proper data binding
    this.ipChanged.emit(ip);
    this.filter.ip = ip;
    this.ipInput.nativeElement.dispatchEvent(new Event('blur'));
  }

  onSubmit() {
    this.submitted.emit(this.filter);
  }

  filterChanged(ip: string) {
    // console.log('widget', ip);
    this.filterChange.emit(ip);
  }
}
