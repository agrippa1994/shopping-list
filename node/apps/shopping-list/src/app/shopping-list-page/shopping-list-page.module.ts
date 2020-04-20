import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShoppingListPageComponent } from './shopping-list-page.component';
import { RouterModule } from '@angular/router';



@NgModule({
  bootstrap: [ShoppingListPageComponent],
  declarations: [ShoppingListPageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '**', component: ShoppingListPageComponent }
    ])
  ]
})
export class ShoppingListPageModule {}
