import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ShoppingListPageModule } from './shopping-list-page';
import { StartPageModule } from './start-page';

const routes: Routes = [
  { path: 'start', loadChildren: () => StartPageModule },
  { path: ':shoppingListId', loadChildren: () => ShoppingListPageModule },
  { path: '**', redirectTo: 'start' },
];

@NgModule({
  imports:[
    RouterModule.forRoot(routes),
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {

}
