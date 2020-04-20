import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { GraphQLModule } from './graphql.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule,
    BrowserAnimationsModule,
    FormsModule, HttpClientModule, AppRoutingModule, GraphQLModule, MatToolbarModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
