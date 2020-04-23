import { NgModule } from '@angular/core';
import { DetailPageComponent } from './detail-page.component';
import { RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { SharedModule } from '../../../../../shopping-list/src/app/shared.module';
import { UtilityModule } from '../../ui';

@NgModule({
  imports: [
    RouterModule.forChild([{ path: ':id', component: DetailPageComponent }]),
    IonicModule,
    SharedModule,
    UtilityModule,
  ],
  declarations: [DetailPageComponent],
})
export class DetailPageModule {}
