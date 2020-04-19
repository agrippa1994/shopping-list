import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StartPageComponent } from './start-page.component';
import { RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [StartPageComponent],
  bootstrap: [StartPageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([{
      path: '**', component: StartPageComponent
    }]),
    MatTabsModule,
    MatButtonModule
  ]
})
export class StartPageModule { }
