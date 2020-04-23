import { Component, OnInit } from '@angular/core';
import { CreateShoppingListGQL, GetShoppingListGQL } from '@node/data-access';
import { ActionSheetController, AlertController, ToastController } from '@ionic/angular';
import { UtilityService } from '../../ui';
import { Plugins } from '@capacitor/core';
import { ListPageService } from './list-page.service';

@Component({
  template: `
    <ion-header translucent="true">
      <ion-toolbar>
        <ion-title>Your Shopping Lists</ion-title>
        <ion-buttons slot="primary">
          <ion-button (click)="addOrCreateShoppingList()">
            <ion-icon slot="icon-only" name="add"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content padding fullscreen="true">
      <ion-list>
        <ion-item-sliding *ngFor="let item of shoppingLists" #slidingItem>
          <ion-item
            detail="true"
            routerLink="/detail/{{ item.id }}"
            routerDirection="forward"
            >{{ item.name }}</ion-item
          >
          <ion-item-options side="end">
            <ion-item-option
              color="danger"
              (click)="deleteShoppingList(item.id, slidingItem)"
            >
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
export class ListPageComponent implements OnInit {
  shoppingLists: Array<{ id: string; name: string }> = [];

  constructor(
    private readonly createShoppingListQuery: CreateShoppingListGQL,
    private alertController: AlertController,
    private toastController: ToastController,
    private utilityService: UtilityService,
    private listPageService: ListPageService,
    private actionSheetController: ActionSheetController,
    private getShoppingListQuery: GetShoppingListGQL,
  ) {}

  async ngOnInit() {
    this.listPageService
      .updatedShoppingLists()
      .subscribe((list) => (this.shoppingLists = list));
  }

  async addShoppingList() {
    const alert = await this.alertController.create({
      header: 'Add Shopping List!',
      inputs: [
        {
          id: 'name',
          name: 'name',
          type: 'text',
          placeholder: 'name...',
        },
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel');
          },
        },
        {
          text: 'Save',
          handler: async (values) => {
            const { name } = values;
            if (!name || name.length === 0) {
              return false;
            }

            await this.createShoppingList(name);
          },
        },
      ],
    });

    await alert.present();
  }

  async deleteShoppingList(id, slidingItem) {
    const result = await this.utilityService.showConfirmationDialog({
      header: 'Info',
      message: 'Do you really want to delete this?',
    });
    console.log(result);
    if (result) {
      await this.listPageService.deleteShoppingListEntry({ id });
    }
    slidingItem.close();
  }

  async addOrCreateShoppingList() {
    let action = '';
    const sheet = await this.actionSheetController.create({
      header: 'Shopping List',
      buttons: [
        {
          text: 'Create ....',
          handler: () => {
            action = 'Create';
          }
        },
        {
          text: 'Add Existing ...',
          handler: () => {
            action = 'Add';
          }
        },{
          text: 'Cancel',
          icon: 'close',
          role: 'cancel',
        }
      ],
    });

    await sheet.present();
    await sheet.onDidDismiss();

    switch (action) {
      case 'Add':
        await this.addExistingShoppingList();
        break;
      case 'Create':
        await this.addShoppingList();
        break;
    }
  }

  async addExistingShoppingList() {
    console.log('HI');
    const alert = await this.alertController.create({
      header: 'Add Existing Shopping List',
      inputs: [
        {
          id: 'id',
          name: 'id',
          type: 'text',
          placeholder: 'ID...',
        },
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel');
          },
        },
        {
          text: 'Save',
          handler: async (values) => {

            const { id } = values;
            console.log('X', values);
            if (!id || id.length === 0) {
              return false;
            }


            try {
              const { data } = await this.getShoppingListQuery.fetch({ listId: id }).toPromise();
              await this.listPageService.storeShoppingListEntry(data.shoppingList);
              await this.utilityService.showToast('List added');
              console.log('hi2');
            } catch(e) {
              await this.utilityService.showToast('Could not find list');
              console.error(e);
              console.log('hi');
              return false;
            }

          },
        },
      ],
    });
    await alert.present();
    await alert.onDidDismiss();
  }


  async createShoppingList(name) {
    try {
      const data = await this.createShoppingListQuery
        .mutate({ name })
        .toPromise();
      await this.listPageService.storeShoppingListEntry(
        data.data.createShoppingList
      );
      await this.utilityService.showToast('Shopping List created');
    } catch (e) {
      console.error(e);
      await this.utilityService.showToast('Could not create shopping list');
    }
  }
}
