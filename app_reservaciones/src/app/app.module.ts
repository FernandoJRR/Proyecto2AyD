import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AuthModule } from './auth/auth.module';
//Demas imports de modules principales

import { AppComponent } from './app.component';


@NgModule({
  declarations: [
    AppComponent    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthModule,
    //Demas Modulos principales
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
