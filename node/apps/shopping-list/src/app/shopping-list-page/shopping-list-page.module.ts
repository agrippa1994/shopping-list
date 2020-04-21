import { NgModule } from '@angular/core';
import { ShoppingListPageComponent } from './shopping-list-page.component';
import { RouterModule } from '@angular/router';
import { CanActivateHandler } from './can-activate-handler';
import { SharedModule } from '../shared.module';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { EditItemDialogComponent } from './edit-item-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';

@NgModule({
  declarations: [ShoppingListPageComponent, EditItemDialogComponent],
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '**',
        component: ShoppingListPageComponent,
        canActivate: [CanActivateHandler],
      },
    ]),
    MatCardModule,
    MatListModule,
    MatDialogModule,
  ],
  providers: [CanActivateHandler],
})
export class ShoppingListPageModule {}
