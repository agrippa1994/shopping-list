import { Injectable, OnInit } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { defer, Observable, ReplaySubject, Subject } from 'rxjs';

export interface ShoppingListEntry {
  id: string;
  name: string;
}
export type ShoppingLists = Array<ShoppingListEntry>;

@Injectable()
export class ListPageService {
  private stream = new ReplaySubject<ShoppingLists>();

  constructor() {
    this.getShoppingListEntries().then((data) => {
      this.stream.next(data);
    });
  }

  async deleteShoppingListEntry(entry: Pick<ShoppingListEntry, 'id'>) {
    let entries = await this.getShoppingListEntries();
    entries = entries.filter((list) => list.id !== entry.id);
    await Plugins.Storage.set({ key: 'lists', value: JSON.stringify(entries) });
    this.stream.next(await this.getShoppingListEntries());
  }

  async storeShoppingListEntry(entry: ShoppingListEntry) {
    const shoppingLists = await this.getShoppingListEntries();
    shoppingLists.push(entry);
    await Plugins.Storage.set({
      key: 'lists',
      value: JSON.stringify(shoppingLists),
    });
    this.stream.next(await this.getShoppingListEntries());
  }

  private async getShoppingListEntries() {
    let lists = [];
    try {
      lists =
        JSON.parse((await Plugins.Storage.get({ key: 'lists' })).value) || [];
    } catch (e) {
      console.log(e);
    }
    return lists;
  }

  updatedShoppingLists(): Observable<ShoppingLists> {
    return this.stream;
  }
}
