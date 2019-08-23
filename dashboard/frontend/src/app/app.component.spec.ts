import { TestBed, async } from '@angular/core/testing';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { APP_BASE_HREF } from '@angular/common';

import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { WidgetComponent } from './components/widget/widget.component';

import { TableComponent } from './components/widgets/table/table.component';
// import { ChartComponent } from './components/widgets/chart/chart.component';
import { FilterComponent } from './components/widgets/filter/filter.component';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        WidgetComponent,
        TableComponent,
        // ChartComponent,
        FilterComponent
      ],
      imports: [
        [
          BrowserModule,
          FormsModule,
          RouterModule.forRoot([])
        ]
      ],
      providers: [{provide: APP_BASE_HREF, useValue: '/'}]
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
  it(`should have as title 'app'`, async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('app');
  }));
});
