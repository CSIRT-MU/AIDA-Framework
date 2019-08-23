import { Input, Component, Output, EventEmitter } from '@angular/core';
import { Filter } from '../../models/filter.model';

@Component({
  selector: 'widget',
  templateUrl: './widget.component.html',
  styleUrls: ['./widget.component.css']
})
export class WidgetComponent {

  @Input() title: string;
  @Input() type: string;
  @Input() data: string;
  @Input() dataType: string;
  @Input() filter: Filter;

  @Output() filterChange: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }
}
