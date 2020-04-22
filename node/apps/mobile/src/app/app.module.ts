import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { RouterModule, Routes } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { ListPageModule } from './pages/list/list-page.module';
import { GraphQLModule } from './graphql.module';
import { HttpClientModule } from '@angular/common/http';
import { DetailPageModule } from './pages/detail/detail-page.module';

const routes: Routes = [
  { path: 'list', loadChildren: () => ListPageModule },
  { path: 'detail', loadChildren: () => DetailPageModule },
  { path: '**', redirectTo: 'list' },
];

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    GraphQLModule,
    RouterModule.forRoot(routes),
    IonicModule.forRoot({ mode: 'ios' }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
