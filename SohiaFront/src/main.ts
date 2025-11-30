import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { App } from './app/app';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';





bootstrapApplication(App, AppComponent, appConfig, {
  providers: [provideRouter(routes),provideHttpClient()]
}).catch(err => console.error(err));
