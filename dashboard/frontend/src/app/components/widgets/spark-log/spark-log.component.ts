import { Component, ViewChild, ElementRef, Renderer2, OnInit } from '@angular/core';
import { url } from './spark-log-cfg'

 /**
  * @author Jakub Kolman <442445@mail.muni.cz>
  */

@Component({
  selector: 'widget-spark-log',
  templateUrl: './spark-log.component.html',
  styleUrls: ['./spark-log.component.scss']
})
export class SparkLogComponent {

  page_URL: string = url;
}
