import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app.routing.module';  // Asegúrate de importar el módulo de enrutamiento

@NgModule({
  declarations: [
    AppComponent,
    // Declara aquí cualquier otro componente que utilices
  ],
  imports: [
    BrowserModule,
    AppRoutingModule  // Importa el módulo de enrutamiento
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
