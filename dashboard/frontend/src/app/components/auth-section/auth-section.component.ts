import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-auth-section',
  templateUrl: './auth-section.component.html',
  styleUrls: ['./auth-section.component.scss'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        left: '0'
      })),
      state('out', style({
        left: '-300px'
      })),
      transition('in => out', animate('200ms ease-out')),
      transition('out => in', animate('200ms ease-out'))
    ])
  ]
})

export class AuthSectionComponent implements OnInit {
  sidebarState = 'in';
  @ViewChild('mainWrapper') mainWrapper: ElementRef;

  constructor() { }

  ngOnInit() {
  }

  toggleSidebar() {
    this.mainWrapper.nativeElement.classList.toggle('pl-0');
    this.mainWrapper.nativeElement.classList.toggle('pl-250px');
    this.sidebarState = this.sidebarState === 'out' ? 'in' : 'out';
  }
}
