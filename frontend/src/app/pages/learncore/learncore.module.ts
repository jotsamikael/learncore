import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { OverlayModule } from '@angular/cdk/overlay';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from '@ng-select/ng-select';
import {MatMenuModule} from '@angular/material/menu';

import { LearncoreRoutingModule } from './learncore-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FeaturePlaceholderComponent } from './feature-placeholder/feature-placeholder.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
// Load Icon
import { defineElement } from "@lordicon/element";
import lottie from 'lottie-web';
import { allIcons } from 'angular-feather/icons';
import { FeatherModule } from 'angular-feather';
import { NgPipesModule } from 'ngx-pipes';

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    OverlayModule,
    NgSelectModule,
    LearncoreRoutingModule,
    DashboardComponent,
    FeaturePlaceholderComponent,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgbNavModule,
    FeatherModule.pick(allIcons),
    NgPipesModule 


  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class LearncoreModule { 
  constructor() {
    defineElement(lottie.loadAnimation);
  }
}
