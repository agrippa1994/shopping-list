import { NgModule } from '@angular/core';
import { StartPageComponent } from './start-page.component';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared.module';
import { MatCardModule } from '@angular/material/card';

@NgModule({
  declarations: [StartPageComponent],
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '**',
        component: StartPageComponent,
      },
    ]),
    MatCardModule,
  ],
})
export class StartPageModule {}
