import { NgModule } from '@angular/core';
import { DetailPageComponent } from './detail-page.component';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { SharedModule } from '../../../../../shopping-list/src/app/shared.module';

@NgModule({
  imports: [
    RouterModule.forChild([
      { path: ':id', component: DetailPageComponent }
    ]),
    IonicModule,
    SharedModule
  ],
  declarations: [DetailPageComponent],
})
export class DetailPageModule {}
