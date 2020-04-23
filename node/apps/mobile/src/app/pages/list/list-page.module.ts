import { NgModule } from '@angular/core';
import { ListPageComponent } from './list-page.component';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { ListPageService } from './list-page.service';
import { UtilityModule } from '../../ui';

@NgModule({
  imports: [
    RouterModule.forChild([{ path: '**', component: ListPageComponent }]),
    IonicModule,
    CommonModule,
    UtilityModule,
  ],
  declarations: [ListPageComponent],
  entryComponents: [ListPageComponent],
  providers: [ListPageService],
})
export class ListPageModule {}
