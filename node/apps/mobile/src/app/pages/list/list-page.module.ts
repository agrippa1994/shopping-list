import { NgModule } from '@angular/core';
import { ListPageComponent } from './list-page.component';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    RouterModule.forChild([
      { path: '**', component: ListPageComponent }
    ]),
    IonicModule,
    CommonModule
  ],
  declarations: [ListPageComponent],
  entryComponents: [ListPageComponent]
})
export class ListPageModule {}
