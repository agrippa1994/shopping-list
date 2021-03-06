import { Component, OnInit, ViewChild } from '@angular/core';
import {
  AddItemGQL,
  GetShoppingListGQL,
  ListUpdatedGQL,
  ShoppingListFragment,
  UpdateItemGQL,
  DeleteItemGQL,
  ShoppingItem,
} from '@node/data-access';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ActionSheetController,
  AlertController,
  IonItemSliding,
  IonSearchbar,
  ToastController,
} from '@ionic/angular';
import { UtilityService } from '../../ui';

@Component({
  selector: 'node-detail-page',
  template: `
    <ion-header translucent="true">
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button
            routerLink="/"
            defaultHref="/"
            routerDirection="root"
          ></ion-back-button>
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
        <ion-searchbar #ionSearchbar></ion-searchbar>
        <ion-item-sliding
          *ngFor="let item of (shoppingList.items | filter: this.filterHandler.bind(this):ionSearchbar.value:showUncheckedItemsOnly)"
          #slidingItem
        >
          <ion-item detail="false">
            <ion-label>
              <h2>{{ item.name }}</h2>
              <p *ngIf="item.quantity">{{ item.quantity }}</p>
            </ion-label>
            <ion-checkbox
              slot="start"
              [ngModel]="item.checked"
              (ionChange)="setChecked($event, item)"
            ></ion-checkbox>
          </ion-item>

          <ion-item-options side="end">
            <ion-item-option
              color="warning"
              (click)="
                upsertItem(item.id, item.name, item.quantity, slidingItem)
              "
            >
              <ion-icon slot="icon-only" name="settings"></ion-icon>
            </ion-item-option>
            <ion-item-option
              color="danger"
              (click)="deleteShoppingItem(item.id, slidingItem)"
            >
              <ion-icon slot="icon-only" name="trash"></ion-icon>
            </ion-item-option>
          </ion-item-options>
        </ion-item-sliding>
      </ion-list>
    </ion-content>

    <ion-footer>
      <ion-toolbar>
        <ion-buttons>
          <ion-button
            (click)="showUncheckedItemsOnly = !showUncheckedItemsOnly"
          >
            <ion-icon
              slot="icon-only"
              [name]="showUncheckedItemsOnly ? 'filter' : 'filter-outline'"
            ></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-footer>
  `,
})
export class DetailPageComponent implements OnInit {
  shoppingList: ShoppingListFragment | undefined = null;
  showUncheckedItemsOnly = false;

  constructor(
    private readonly getShoppingListQuery: GetShoppingListGQL,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly shoppingListUpdated: ListUpdatedGQL,
    private readonly alertController: AlertController,
    private readonly updateItemMutation: UpdateItemGQL,
    private readonly addItemMutation: AddItemGQL,
    private readonly toastController: ToastController,
    private readonly utilityService: UtilityService,
    private readonly deleteItemMutation: DeleteItemGQL
  ) {}

  ngOnInit(): void {
    let subscription;
    this.activatedRoute.params.subscribe(async (params) => {
      try {
        const { data } = await this.getShoppingListQuery
          .fetch({ listId: params['id'] })
          .toPromise();

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
      } catch (e) {
        await this.utilityService.showToast('Error while loading item');
        await this.router.navigateByUrl('/');
      }
    });
  }

  async upsertItem(
    id?: number,
    name?: string,
    quantity?: number,
    slidingItem?: IonItemSliding
  ) {
    const alert = await this.alertController.create({
      header: 'Add Item',
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
          type: 'text',
          placeholder: 'quantity...',
          value: quantity ? quantity : '1',
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
            const { name, quantity } = values;
            if (!name || name.length === 0) {
              return false;
            }

            await this.upsertShoppingItem(name, quantity, id);
          },
        },
      ],
    });

    await alert.present();
    await alert.onDidDismiss();
    if (slidingItem) {
      await slidingItem.close();
    }
  }

  private async upsertShoppingItem(
    name: string,
    quantity: string,
    id?: number
  ) {
    try {
      if (id) {
        await this.updateItemMutation
          .mutate({ quantity, name, listId: this.shoppingList.id, id })
          .toPromise();
      } else {
        await this.addItemMutation
          .mutate({ quantity, name, listId: this.shoppingList.id })
          .toPromise();
      }
      await this.utilityService.showToast(id ? 'Item updated' : 'Item added');
    } catch (e) {
      console.log(e);
      await this.utilityService.showToast('Could not create or update item');
    }
  }

  private async deleteShoppingItem(id: number, slidingItem: IonItemSliding) {
    try {
      if (
        await this.utilityService.showConfirmationDialog({
          header: 'Warning',
          message: 'Do you want to delete this?',
        })
      ) {
        await this.deleteItemMutation
          .mutate({ id, listId: this.shoppingList.id })
          .toPromise();
        await this.utilityService.showToast('Item deleted');
      }
    } catch (e) {
      await this.utilityService.showToast('Could not delete item');
    } finally {
      await slidingItem.close();
    }
  }

  async setChecked(event: any, item: any) {
    const { checked } = event.detail;
    if (checked === item.checked) {
      return;
    }

    try {
      await this.updateItemMutation
        .mutate({
          listId: this.shoppingList.id,
          id: item.id,
          checked,
        })
        .toPromise();
    } catch (e) {
      await this.utilityService.showToast('Could not check / uncheck item');
    }
  }

  filterHandler(
    searchString: string,
    showUncheckedItemsOnly: boolean,
    item: ShoppingItem
  ) {
    if (item.checked && showUncheckedItemsOnly) {
      return false;
    }
    if (!searchString) {
      return true;
    }
    return item.name.toLowerCase().indexOf(searchString.toLowerCase()) !== -1;
  }
}
