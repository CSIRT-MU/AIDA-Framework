import { Panel } from '../../models/general-panel.model';
import { BackendService } from '../../services/backend.service';
import { Widget } from '../../models/widget.model';
import { Filter } from '../../models/filter.model';
import { WidgetGroup } from '../../models/widget-group.model';
import { DefaultFilterComponent } from './filters/default-filter/default-filter.component';
import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit, QueryList, ViewChildren, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
    templateUrl: './general-panel.component.html',
    styleUrls: ['./general-panel.component.scss'],
})
export class GeneralPanelComponent implements OnInit, OnDestroy, AfterViewInit {

    @ViewChild('filterComponent') filterComponent: DefaultFilterComponent;
    @ViewChildren('componentElement') componentElements: QueryList<ElementRef>;
    @ViewChildren('widgetElement') widgetElements: QueryList<ElementRef>;

    filter: Filter;
    panel: Panel;
    widgets: Widget[];
    widgetGroups: WidgetGroup[];
    loading: boolean;
    private sub: Subscription;
    private alias: string;
    private containsNewsfeedComponent: boolean;

    constructor(private route: ActivatedRoute, private backend: BackendService) { }

    ngOnInit() {
        const hours = 1;
        this.loading = true;
        this.filter = new Filter();
        this.filter.aggregation = '5m';
        this.filter.setInterval(hours);
        this.containsNewsfeedComponent = false;

        this.sub = this.route.params.subscribe(params => {
            const alias = params['alias'];

            this.backend.getPanel(alias).subscribe(panel => {
                this.panel = panel;

                this.backend.getWidgetGroups(this.panel.id).subscribe(widgetGroups => {
                    this.widgetGroups = widgetGroups;
                    this.loading = false;
                });

                // this.backend.getWidgets(this.panel.id)
                //     .subscribe(widgets => {
                //         this.widgets = widgets;
                //         this.loading = false;
                //     });

                this.handleSubmit(this.filter);
            });
        });
    }

    // private adjustElements() {
    //     console.log(this.containsNewsfeedComponent);
    //     console.log(this.componentElements);
    //     if (this.containsNewsfeedComponent) {
    //         this.componentElements['_results'].forEach(element => {
    //             console.log('-------------------------');
    //             console.log(element.nativeElement);
    //         });
    //     }
    // }

    ngAfterViewInit(): void {
        // let adjsut = false;
        // this.componentElements.changes.subscribe(() => {
        //     this.componentElements.toArray().forEach(el => {
        //         const elName = el.nativeElement.firstChild.getAttribute('ng-reflect-name');
        //         if (elName === 'NEWSFEED') {
        //             el.nativeElement.classList.add('component-newsfeed');
        //             adjsut = true;
        //         }
        //     });
        //     if (adjsut) {
        //         this.componentElements.toArray().forEach(el => {
        //             const elName = el.nativeElement.firstChild.getAttribute('ng-reflect-name');
        //             if (elName !== 'NEWSFEED') {
        //                 console.log('ADD class for adjusment');
        //                 el.nativeElement.classList.add('component-newsfeed-adjusment');
        //             }
        //         });
        //     }
        // });
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    handleSubmit($event) {
        this.loading = true;

        const dateFrom = $event.dateFrom.toISOString();
        const dateTo = $event.dateTo.toISOString();
        const aggregation = $event.aggregation;
        const filter = $event.ip || 'none';

        // if (this.widgets !== undefined) {
        //     this.widgets.forEach(widget => {
        //         this.backend.getWidgetData(widget.id, dateFrom, dateTo, aggregation, filter)
        //             .subscribe(widget => {
        //                 this.loading = false;
        //             });
        //     });
        // }

        this.backend.getWidgetsLegacy(this.panel.id, dateFrom, dateTo, aggregation, filter)
            .subscribe(widgets => {
                this.widgets = widgets;
                this.loading = false;
            });

    }

    filterChanged(ip: string) {
        // console.log('detection', ip);
        this.filter.ip = ip;
        this.filterComponent.setIPFilter(ip);
    }
}
