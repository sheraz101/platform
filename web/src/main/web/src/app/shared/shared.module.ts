import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {ToasterModule} from 'angular2-toaster/angular2-toaster';

import {AccordionModule} from 'ngx-bootstrap/accordion';
import {AlertModule} from 'ngx-bootstrap/alert';
import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {ButtonsModule} from 'ngx-bootstrap/buttons';
import {CarouselModule} from 'ngx-bootstrap/carousel';
import {CollapseModule} from 'ngx-bootstrap/collapse';
import {DatepickerModule} from 'ngx-bootstrap/datepicker';
import {ModalModule} from 'ngx-bootstrap/modal';
import {PaginationModule} from 'ngx-bootstrap/pagination';
import {ProgressbarModule} from 'ngx-bootstrap/progressbar';
import {RatingModule} from 'ngx-bootstrap/rating';
import {TabsModule} from 'ngx-bootstrap/tabs';
import {TimepickerModule} from 'ngx-bootstrap/timepicker';
import {TooltipModule} from 'ngx-bootstrap/tooltip';
import {TypeaheadModule} from 'ngx-bootstrap/typeahead';

import {FlotDirective} from './directives/flot/flot.directive';
import {SparklineDirective} from './directives/sparkline/sparkline.directive';
import {EasypiechartDirective} from './directives/easypiechart/easypiechart.directive';
import {ColorsService} from './colors/colors.service';
import {CheckallDirective} from './directives/checkall/checkall.directive';
import {VectormapDirective} from './directives/vectormap/vectormap.directive';
import {NowDirective} from './directives/now/now.directive';
import {ScrollableDirective} from './directives/scrollable/scrollable.directive';
import {JqcloudDirective} from './directives/jqcloud/jqcloud.directive';
import {LocalStorageModule} from 'angular-2-local-storage';
import {FundButtonDirective} from './directives/fund-button/fund-button.directive';
import {AddRequestDirective} from './directives/add-request/add-request.directive';
import {WeiAsNumberPipe, WeiPipe} from './pipes/wei/wei.pipe';
import {Ng2FilterPipeModule} from 'ng2-filter-pipe';

// https://angular.io/styleguide#!#04-10
@NgModule({
  imports     : [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    AccordionModule.forRoot(),
    AlertModule.forRoot(),
    ButtonsModule.forRoot(),
    CollapseModule.forRoot(),
    DatepickerModule.forRoot(),
    BsDropdownModule.forRoot(),
    ModalModule.forRoot(),
    Ng2FilterPipeModule,
    PaginationModule.forRoot(),
    ProgressbarModule.forRoot(),
    RatingModule.forRoot(),
    TabsModule.forRoot(),
    TimepickerModule.forRoot(),
    TooltipModule.forRoot(),
    TypeaheadModule.forRoot(),
    ToasterModule,
    LocalStorageModule.withConfig({
      prefix     : 'FND',
      storageType: 'localStorage'
    })
  ],
  providers   : [
    ColorsService
  ],
  declarations: [
    FlotDirective,
    SparklineDirective,
    EasypiechartDirective,
    CheckallDirective,
    VectormapDirective,
    NowDirective,
    ScrollableDirective,
    JqcloudDirective,
    FundButtonDirective,
    AddRequestDirective,
    WeiPipe,
    WeiAsNumberPipe,
  ],
  exports     : [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    RouterModule,
    AccordionModule,
    AlertModule,
    ButtonsModule,
    CarouselModule,
    CollapseModule,
    DatepickerModule,
    BsDropdownModule,
    ModalModule,
    Ng2FilterPipeModule,
    PaginationModule,
    ProgressbarModule,
    RatingModule,
    TabsModule,
    TimepickerModule,
    TooltipModule,
    TypeaheadModule,
    ToasterModule,
    FlotDirective,
    SparklineDirective,
    EasypiechartDirective,
    CheckallDirective,
    VectormapDirective,
    NowDirective,
    ScrollableDirective,
    JqcloudDirective,
    FundButtonDirective,
    AddRequestDirective,
    WeiPipe,
    WeiAsNumberPipe,
  ]
})

// https://github.com/ocombe/ng2-translate/issues/209
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule
    };
  }
}
