import { Component } from '@angular/core';
import { CreateShoppingListGQL } from '@node/data-access';
import { AlertController, ModalController, ToastController } from '@ionic/angular';

@Component({
  template: `
    <ion-header translucent="true">
      <ion-toolbar>
        <ion-title>Your Shopping Lists</ion-title>
        <ion-buttons slot="primary">
          <ion-button (click)="addShoppingList()">
            <ion-icon slot="icon-only" name="add"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content padding fullscreen="true">
      <!--ion-button expand="block" routerLink="/dashboard" routerDirection="root">
        Login
      </ion-button-->

      <ion-list>
        <ion-item-sliding *ngFor="let item of shoppingLists">
          <ion-item detail="true" routerLink="/detail/{{ item.id }}" routerDirection="forward">{{ item.name }}</ion-item>
          <ion-item-options side="end">
            <ion-item-option color="danger">
              <ion-icon slot="icon-only" name="trash"></ion-icon>
            </ion-item-option>
          </ion-item-options>
        </ion-item-sliding>
      </ion-list>
    </ion-content>
  `,
  selector: 'node-list-page',
  styles: [``],
})
export class ListPageComponent{

  shoppingLists: Array<{ id: string, name: string }> = [];

  constructor(
    private readonly createShoppingListQuery: CreateShoppingListGQL,
    private alertController: AlertController,
    private toastController: ToastController,
  ) {
    try {
      this.shoppingLists = JSON.parse(localStorage.getItem('lists')) || [];
    } catch(e) {
      console.log(e);
    }

  }


  async addShoppingList() {
    const alert = await this.alertController.create({
      header: 'Add Shopping List!',
      inputs: [
        {
          id: 'name',
          name: 'name',
          type: 'text',
          placeholder: 'name...'
        },
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel');
          }
        }, {
          text: 'Save',
          handler: async (values) => {
            const { name } = values;
            if (!name || name.length === 0) {
              return false;
            }

            await this.createShoppingList(name);
          }
        }
      ]
    });

    await alert.present();
  }

  async createShoppingList(name) {
    try {
      const data = await this.createShoppingListQuery.mutate({ name }).toPromise();
      if (data.errors) {
        throw data.errors;
      }

      const toast = await this.toastController.create({
        message: 'Shopping List created',
        translucent: true,
        duration: 2500,
      });
      await toast.present();

      this.saveShoppingList(data.data.createShoppingList.id, data.data.createShoppingList.name);

    } catch(e) {
      console.error(e);
      const toast = await this.toastController.create({
        message: 'Could not create shopping list',
        translucent: true,
        duration: 2500,
      });
      await toast.present();
    }
  }

  saveShoppingList(id: string, name: string) {
    this.shoppingLists.push({ id: id, name: name });
    localStorage.setItem("lists", JSON.stringify(this.shoppingLists));

  }
}
