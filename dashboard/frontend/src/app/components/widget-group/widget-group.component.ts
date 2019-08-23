import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'widget-group',
  templateUrl: './widget-group.component.html',
  styleUrls: ['./widget-group.component.scss']
})
export class WidgetGroupComponent implements OnInit {

  @Input() name: string;
  @Input() config_json: string;
  @Input() title: string;

  config: object;

  constructor() { }

  ngOnInit() {
    if (this.config_json) {
      this.config = JSON.parse(this.config_json);
    }
  }

}
