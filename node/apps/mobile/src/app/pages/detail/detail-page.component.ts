import { Component, OnInit } from '@angular/core';
import { AddItemGQL, GetShoppingListGQL, ListUpdatedGQL, ShoppingListFragment, UpdateItemGQL } from '@node/data-access';
import { ActivatedRoute } from '@angular/router';
import { AlertController, IonCheckbox, ToastController } from '@ionic/angular';

@Component({
  selector: 'node-detail-page',
  template: `
    <ion-header translucent="true">
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button routerLink="/" defaultHref="/" routerDirection="root"></ion-back-button>
        </ion-buttons>
        <ion-title>{{ shoppingList ? shoppingList.name : '' }}</ion-title>
        <ion-buttons slot="primary">
          <ion-button (click)="upsertItem()">
            <ion-icon slot="icon-only" name="add"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content padding fullscreen="true">
      <ion-list *ngIf="shoppingList">
        <ion-item-sliding *ngFor="let item of shoppingList.items">
          <ion-item detail="false">
            <ion-label>
              <h2>{{ item.name }}</h2>
              <p *ngIf="item.quantity">x{{ item.quantity }}</p>
            </ion-label>
            <ion-checkbox slot="start" [ngModel]="item.checked" (ionChange)="setChecked($event, item)"></ion-checkbox>
          </ion-item>

          <ion-item-options side="end">
            <ion-item-option color="warning" (click)="upsertItem(item.id, item.name, item.quantity)">
              <ion-icon slot="icon-only" name="settings"></ion-icon>
            </ion-item-option>
            <ion-item-option color="danger">
              <ion-icon slot="icon-only" name="trash"></ion-icon>
            </ion-item-option>
          </ion-item-options>
        </ion-item-sliding>
      </ion-list>
    </ion-content>
  `,

})
export class DetailPageComponent implements OnInit {

  shoppingList: ShoppingListFragment | undefined = null;

  constructor(private readonly getShoppingListQuery: GetShoppingListGQL,
              private readonly activatedRoute: ActivatedRoute,
              private readonly shoppingListUpdated: ListUpdatedGQL,
              private readonly alertController: AlertController,
              private readonly updateItemMutation: UpdateItemGQL,
              private readonly addItemMutation: AddItemGQL,
              private readonly toastController: ToastController,) {
  }

  ngOnInit(): void {
    let subscription;
    this.activatedRoute.params.subscribe(async params => {
      try {
        const { errors, data } = await this.getShoppingListQuery
          .fetch({ listId: params['id'] })
          .toPromise();
        if (errors) {
          throw errors;
        }

        this.shoppingList = data.shoppingList;

        if (subscription) {
          subscription.unsubscribe();
        }
        subscription = this.shoppingListUpdated
          .subscribe({
            listId: this.shoppingList.id,
          })
          .subscribe(
            (data) => (this.shoppingList = data.data.shoppingListUpdated)
          );
      } catch (e) {}
    })
  }

  async upsertItem(id?: number, name?: string, quantity?: number) {
    const alert = await this.alertController.create({
      header: 'Add Shopping List!',
      inputs: [
        {
          id: 'name',
          name: 'name',
          type: 'text',
          placeholder: 'name...',
          value: name ? name : '',
        },
        {
          id: 'quantity',
          name: 'quantity',
          type: 'number',
          placeholder: 'quantity...',
          value: quantity ? quantity : '0',
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
            const { name, quantity } = values;
            if (!name || name.length === 0) {
              return false;
            }

            await this.upsertShoppingItem(name, parseInt(quantity), id);
          }
        }
      ]
    });

    await alert.present();
  }

  private async upsertShoppingItem(name: string, quantity: number, id?: number) {
    try {
      let result;
      if (id) {
        result = await this.updateItemMutation.mutate({ quantity, name, listId: this.shoppingList.id, id }).toPromise();
      } else {
        result = await this.addItemMutation.mutate({ quantity, name, listId: this.shoppingList.id }).toPromise();
      }
      const { errors, data } = result;
      if (errors) {
        throw errors;
      }

      const toast = await this.toastController.create({
        message: id ? 'Item updated' : 'Item added',
        translucent: true,
        duration: 2500,
      });
      await toast.present();

    } catch(e) {
      console.log(e);
      const toast = await this.toastController.create({
        message: 'Could not create item',
        translucent: true,
        duration: 2500,
      });
      await toast.present();
    }

  }

  async setChecked(event: any, item: any) {
    const { checked } = event.detail;
    if (checked === item.checked) {
      return;
    }
    const { errors } = await this.updateItemMutation.mutate({
      listId: this.shoppingList.id,
      id: item.id,
      checked,
    }).toPromise();

    if (errors) {

    }
  }
}
