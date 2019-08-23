import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, Input } from '@angular/core';
import { Panel } from '../../models/general-panel.model';
import { BackendService } from '../../services/backend.service';
import { PanelGroup } from '../../models/panel_group.model';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['sidebar.component.css'],
})
export class SidebarComponent implements OnInit, AfterViewInit {
  panels: Panel[];
  panelGroups: PanelGroup[];

  constructor(private backend: BackendService) { }

  ngAfterViewInit() {}

  ngOnInit() {
    this.backend.getPanels().subscribe(panels => this.panels = panels);
    this.backend.getPanelGroups().subscribe(panelGroups => this.panelGroups = panelGroups);
  }
}
