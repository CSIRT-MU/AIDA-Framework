import * as config from './overview-cfg';
import { Component, OnInit, ViewChild, ElementRef, OnDestroy, NgZone, AfterViewChecked, AfterContentInit } from '@angular/core';
import { DataService } from '../../../services/data.service';
import { timer, Subscription } from 'rxjs';


import 'snapsvg-cjs';
declare var Snap: any;

 /**
  * @author Jakub Kolman <442445@mail.muni.cz>
  */

@Component({
  selector: 'widget-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss'],
  preserveWhitespaces: false
})

export class OverviewComponent implements OnInit {

  private serviceTimer;
  private timerSub: Subscription;
  private SVG_path = config.SVG_path;
  private componentInfo;        // data from server containing info about components
  private showInfo = false;
  private componentName: string;
  constructor(private service: DataService, private ngZone: NgZone) {
    (<any>window).onMouseOver = this.displayInfo.bind(this);
    (<any>window).onClick = this.redirect.bind(this);
  }

  /**
   * on intialization calls service to get data and after 1ms displayes color dots that indicate whether
   * the component is active or not. The delay is to make sure that async call to server is finnished.
   * Repeatedly calls service to get refresh date in cycle that is set by @param refreshTimer
   */
  ngOnInit(): void {
    this.serviceTimer = timer(1, config.refreshTimer);
    timer(1000).subscribe(() => this.createBgs());
    this.timerSub = this.serviceTimer.subscribe(() => this.getDataFromService());
  }

  ngOnDestroy() {
    delete (<any>window).onMouseOver;
    delete (<any>window).onClick;
    this.timerSub.unsubscribe();
  }

  /**
   * calls servive to get data from server
   */
  private getDataFromService() {
    return this.service.get('/componentsinfo').subscribe(serverData => {
      this.componentInfo = serverData;
    }, (err) => {
      return console.error(err);
    },
      () => {
        this.changeStatusDots();
      });
  }

  private flatten (obj, prefix = [], current = {}) {
    if (typeof (obj) === 'object' && obj !== null) {
      Object.keys(obj).forEach(key => {
        this.flatten(obj[key], prefix.concat(key), current);
      });
    } else {
      current[prefix.join('.')] = obj;
    }

    return current;
  }

  /**
   * Filling status dots with colour
   * red - stopped
   * green - running
   */
  private changeStatusDots(): void {
    const s = Snap('#schema');
    config.components.forEach(c => {
      s.select(`#${c.name}`).attr('fill',
                             this.flatten(this.componentInfo)[c.infoPath] === 0 ? 'red' : '#4ff701fa');
    });
  }

  /**
   * Adding links to svg elements
   */
  addLinks(): void {
    const s = Snap('#schema');
    config.components.forEach(c => {
      s.select(`#${c.name}IconLink`).setAttributeNS('http://www.w3.org/1999/xlink', 'href', c.link);
    });
  }

  /**
   * uses Snapsvg to create box around selected components in svg image
   * that responds on click and on mouse over
   */
  createBgs() {
    const ids = config.components.map(c => c.svgId).concat(config.additionalSVGIds);
    const s = Snap('#schema');
    ids.forEach(id => {
      const el = s.select(`#${id}`);
      const bb = el.node.getBBox();
      const name = id.replace(/(Icon|cog)/, '');
      if (el.node.localName === 'image') {
        el.attr({
          onmouseover: `top.onMouseOver('${name}', true)`,
          onmouseout: `top.onMouseOver('${name} ', false)`,
          onclick: `top.onClick('${name}')`
        });
      } else {
        const box = el.rect(bb.x - 2, bb.y - 2, bb.width + 4, bb.height + 4);
        box.attr({
          id: `${id}Box`,
          opacity: 0,
          onmouseover: `top.onMouseOver('${name}', true)`,
          onmouseout: `top.onMouseOver('${name} ', false)`,
          onclick: `top.onClick('${name}')`
        });
      }
    });
  }

  /**
   * Displayes description and logs on mouse over
   * @param component component name that is hovered over
   * @param showValue true on mouse over, false on mouse out
   */
  public displayInfo(component, showValue) {
    this.componentName = component;
    this.ngZone.run(() => {
      this.showInfo = showValue;
    });
  }

  /**
   * On click redirects user to different url
   * @param component component name that is clicked on
   */
  redirect(component: string) {
    const link = config.components.filter(c => c.name === component);
    if (link.length > 0) {
      window.location.href = link[0].link;
    }
  }
}
