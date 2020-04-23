import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import { Capacitor, Plugins } from '@capacitor/core';

if (environment.production) {
  enableProdMode();
}

async function bootstrap() {
  try {
    const module = await platformBrowserDynamic().bootstrapModule(AppModule);
    if (Capacitor.isPluginAvailable('SplashScreen')) {
      console.log('hiding splash screen ...');
      await Plugins.SplashScreen.hide();
      console.log('splash screen hidden');
    }
  } catch (e) {
    console.error(e);
  }
}

bootstrap();
