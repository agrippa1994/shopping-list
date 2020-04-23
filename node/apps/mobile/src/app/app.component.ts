import { Component } from '@angular/core';

@Component({
  selector: 'node-app-root',
  template: `
    <ion-app>
      <ion-router-outlet></ion-router-outlet>
    </ion-app>
  `,

  styles: [
    `
      html,
      body {
        background-color: black;
      }
    `,
  ],
})
export class AppComponent {
  title = 'mobile';
}
