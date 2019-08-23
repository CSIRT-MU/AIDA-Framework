import { Component, KeyValueDiffers, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DateAdapter, MatDatepickerInputEvent, MatSnackBar, MatTableDataSource, MAT_DATE_FORMATS } from '@angular/material';
import * as moment from 'moment';
import { Subscription } from 'rxjs';
import { DataService } from './../../../services/data.service';
import { MessengerService } from './../../../services/messenger.service';
import { AppDateAdapter, APP_DATE_FORMATS } from './date.adapter';
import { maxPages, RuleModel, columnMapping, ColumnHeader } from './display-rules-cfg';


/**
 * @author Jakub Kolman <442445@mail.muni.cz>
 */

@Component({
  selector: 'widget-display-rules',
  templateUrl: './display-rules.component.html',
  styleUrls: ['./display-rules.component.scss'],
  providers: [
    {provide: DateAdapter, useClass: AppDateAdapter},
    {provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS}
  ],
  preserveWhitespaces: false
})

export class DisplayRulesComponent implements OnInit, OnDestroy {

  activeDate;
  displayedColumns: Array<ColumnHeader> =
    columnMapping.concat({property: '', label: 'Action'});

  activeRules;
  activeRulesDD;
  dataSourceActiveRules: MatTableDataSource<RuleModel> = new MatTableDataSource([]);
  dataSourceDeafaultDay: MatTableDataSource<RuleModel> = new MatTableDataSource([]);
  isDataLoaded = false;
  differ: any;

  newRuleActive = false;
  newRule: RuleModel = null;

  showDelay = new FormControl(1000);

  // ------- add rule ------
  rulesForm: FormGroup;

  // ------- date picker ----
  dpDate = moment();
  startDate = new Date(moment().toDate());
  date = new FormControl(new Date());
  serializedDate = new FormControl((new Date()).toISOString());
  events: string[] = [];
  dateTableNameValue: string = moment().format('DD/MM/YYYY').toString();

  // -------test----
  tableNameAR = 'Active rules';
  tableNameDD = 'Default day';
  tableDataAR = [];
  tableDataDD = [];
  dataLoadedAR = false;
  dataLoadedDD = false;
  maxTablePages = maxPages;

  subscription: Subscription;
  action: any;

  constructor(private service: DataService,
              private formBuilder: FormBuilder,
              private snackBar: MatSnackBar,
              private messenger: MessengerService) {
    this.subscription = this.messenger.getMessage().subscribe(message => {
      this.action = message;
      if (this.action[1] === 'Activate') {
        this.enableRule(this.action[0]);
      } else {
        this.disableRule(this.action[0]);
      }
    }, (err) => {
      return console.error(err);
    });
  }

  /**
   * On initialization calls server to get datasource for rules of today and all active rules
   * Initializes rulesForm and adds one empty row for adding new rule
   */
  ngOnInit(): void {
    // get all active rules
    this.getActiveRules();
    this.getRulesForDay(moment().toLocaleString());

    this.rulesForm = this.formBuilder.group({
      rules: this.formBuilder.array([])
    });
    this.addRuleRow();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getActiveRules() {
    this.service.get('/db/activerules').subscribe(serverData => {     // for REST api
      this.activeRules = serverData;
    }, (err) => {
      return console.error(err);
    }, () => {
      this.dataSourceActiveRules = new MatTableDataSource<RuleModel>(this.activeRules);
      this.tableDataAR = this.dataSourceToArray(this.dataSourceActiveRules);
      this.dataLoadedAR = true;
    });
  }

  /**
   * Calls server to get datasource of rules for selected date
   * Changes default date table
   * @param date value of date
   */
  getRulesForDay(date: string) {
    return this.service.get('/db/rules/' + moment(date).format('YYYY-MM-DD').toString())
      .subscribe(serverData => {      // rest api from sabu
        this.activeRulesDD = serverData;
      }, (err) => {
        return console.error(err);
      },
                 () => {
                   this.activeDate = date;
                   this.dataSourceDeafaultDay = new MatTableDataSource<RuleModel>(this.activeRulesDD);
                   this.tableDataDD = this.dataSourceToArray(this.dataSourceDeafaultDay);
                   this.dataLoadedDD = true;
                 });
  }

  dataSourceToArray(ds: MatTableDataSource<RuleModel>) {
    const tableData = [];
    ds.data.forEach(ruleds => {
      const rule = [];
      columnMapping.forEach(c => rule.push(ruleds[c.property]));
      (ruleds.active === 0) ? rule.push('Activate') : rule.push('Deactivate');
      tableData.push(rule);
    });
    return tableData;
  }

  /**
   * Entering value to datepicker and hitting enter to change datesource of default date
   * @param type value of event type fired
   * the only type that is allowed that can fire event is change,
   * input type is disabled for unneccessary big amount of calls to server
   * @param event value of input entered to datepicker
   */
  addEvent(type: string, event: MatDatepickerInputEvent<Date>) {
    this.events.push(`${type}: ${event.value}`);
    this.dateTableNameValue = moment(event.value.toString()).format('DD/MM/YYYY');
    this.getRulesForDay(event.value.toString());
  }

  /**
   * Filters dates of datepicker
   * Allows only dates of today and before to be clickable
   */
  myFilter = (d: Date): boolean => {
    return moment(d).isBefore(moment());
  }

  /**
   * Post request to server to activate rule
   * and refreshes datasources of tables to visualy match data on backend
   * @param rule
   */
  enableRule(rule) {
    rule = this.toRuleForm(rule);
    this.service.post('/db/activerules/' + rule.id + '/', rule)
      .subscribe(
        () => {},
        (error: Error) => {
          console.log(error);
          throw error;
        }, () => {
          this.getActiveRules();
          this.getRulesForDay(this.activeDate);
        }
      );
  }

  /**
   * Post request to server to deactivate rule
   * and refreshes datasources of tables to visualy match data on backend
   * @param rule
   */
  disableRule(rule) {
    rule = this.toRuleForm(rule);
    this.service.post('/db/inactiverules/' + rule.id + '/', rule)
      .subscribe(
        () => {},
        (error: Error) => {
          console.log(error);
          throw error;
        }, () => {
          this.getActiveRules();
          this.getRulesForDay(this.activeDate);
        }
      );
  }

  /**
   * Adds empty row to rulesForm for adding new Rule
   */
  addRuleRow() {
    (<FormArray>this.rulesForm.get('rules')).push(this.formBuilder.group({
      l_rule_address: '',
      l_rule_action: '',
      l_rule_port: 0,
      r_rule_address: '',
      r_rule_action: '',
      r_rule_port: 0
    }));
  }

  /**
   * Validates input of new rule,
   * if input is invalid, function warns user in form of snackbar
   * in case input form is valid calls function to create new rule
   * @param inputComment comment value of new rule
   * @param inputActive active value of new rule
   */
  onFormSubmit(inputComment: HTMLInputElement, inputActive: HTMLInputElement) {
    if (this.rulesForm.value.rules.every(e => Object.keys(e).every(k => e[k] !== 0 && e[k].trim() !== ''))) {
      this.createRule(inputComment, inputActive);
    } else {
      this.snackBar.open('Rule has incorrect format at least one input field on both sides must be filled', 'Ok', {duration: 5000});
    }
  }

  /**
   * Formats input of new rule to rule section of RuleModel model
   */
  ruleToString(): string {
    const left = this.rulesForm.value.rules.map(r => `${r.l_rule_address}_${r.l_rule_action}_${r.l_rule_port}`).join(',');
    const right = this.rulesForm.value.rules.map(r => `${r.r_rule_address}_${r.r_rule_action}_${r.r_rule_port}`).join(',');
    return `${left} ==> ${right}`;
  }

  get rules() {
    return (<FormArray>this.rulesForm.get('rules')).controls;
  }

  /**
   * removes row at index i from rulesForm
   * @param i
   */
  removeRuleRow(i) {
    (<FormArray>this.rulesForm.get('rules')).removeAt(i);
  }

  /**
   * creates new rule and sends calls service to make post request of new rule
   * @param inputComment comment value of new rule
   * @param inputActive active value of new rule
   */
  createRule(inputComment: HTMLInputElement, inputActive: HTMLInputElement) {
    const activeValue = inputActive.checked ? 1 : 0;
    const newRule: RuleModel = {
      comment: inputComment.value,
      confidence: null,
      algorithm: null,
      database: null,
      support: null,
      rule: (this.ruleToString()),
      number_of_sequences: null,
      active: activeValue.toString(),
      inserted: moment().format('DD/MM/YYYY HH:MM:SS').toString(),
      id: null,
    };
    this.rulesForm = this.formBuilder.group({
      rules: this.formBuilder.array([])
    });
    this.addRuleRow();
    inputComment.value = '';
    inputActive.checked = false;

    this.service.post('/db/addrules', newRule)
      .subscribe(
        response => {
          this.activeRules.push(newRule);
        },
        (error: Error) => {
          console.log(error);
          throw error;
        }, () => {
          this.getActiveRules();
          this.getRulesForDay(this.activeDate);
        }
      );
  }

  /**
   * Converts data obtained from table-widget to rule form
   * @param preRule array of strings containing data of a rule displayed in table
   */
  toRuleForm(preRule: string[]): RuleModel {
    const indexed = {};
    for (let i = 0; i < columnMapping.length; i++) {
      Object.assign(indexed, {[columnMapping[i].property]: i});
    }
    return {
      id                  : preRule[indexed['id']],
      comment             : preRule[indexed['comment']],
      algorithm           : preRule[indexed['algorithm']],
      database            : preRule[indexed['database']],
      rule                : preRule[indexed['rule']],
      support             : preRule[indexed['support']],
      number_of_sequences : preRule[indexed['number_of_sequences']],
      confidence          : preRule[indexed['confidence']],
      active              : preRule[indexed['active']],
      inserted            : preRule[indexed['inserted']]
    } as RuleModel;
  }

}
