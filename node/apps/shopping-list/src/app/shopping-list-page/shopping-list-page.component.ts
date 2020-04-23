import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  AddItemGQL,
  GetShoppingListGQL,
  ListUpdatedGQL,
  ShoppingListFragment,
  UpdateItemGQL,
} from '@node/data-access';
import { EditItemDialogComponent } from './edit-item-dialog.component';
import { EditItemDialogData } from './edit-item-dialog.data';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'node-shopping-list-page',
  templateUrl: './shopping-list-page.component.html',
  styleUrls: ['./shopping-list-page.component.css'],
})
export class ShoppingListPageComponent implements OnInit {
  title: string;
  shoppingList: ShoppingListFragment | undefined;

  constructor(
    private route: ActivatedRoute,
    private readonly getShoppingList: GetShoppingListGQL,
    private readonly dialog: MatDialog,
    private readonly addItem: AddItemGQL,
    private readonly updateItem: UpdateItemGQL,
    private readonly shoppingListUpdated: ListUpdatedGQL
  ) {}

  ngOnInit(): void {
    let subscription = null;

    this.route.params.subscribe(async (params) => {
      try {
        const { errors, data } = await this.getShoppingList
          .fetch({ listId: params['shoppingListId'] })
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
    });
  }

  showEditDialog(item?) {
    const data = new EditItemDialogData();
    if (item) {
      data.name = item.name;
      data.id = item.id;
      data.quantity = item.quantity;
    }
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '250px',
      data,
    });

    dialogRef.afterClosed().subscribe(async (result: EditItemDialogData) => {
      if (!result) {
        return;
      }

      if (result.id) {
        await this.updateItem
          .mutate({
            id: result.id,
            listId: this.shoppingList.id,
            name: result.name,
            quantity: parseInt(result.quantity as any),
          })
          .toPromise();
      } else {
        await this.addItem
          .mutate({
            listId: this.shoppingList.id,
            name: result.name,
            quantity: parseInt(result.quantity as any),
          })
          .toPromise();
      }
      console.log('The dialog was closed', result);
    });
  }
}
