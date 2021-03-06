import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ShoppingList } from './entities/shopping-list';
import { Repository } from 'typeorm';
import { v4 } from 'uuid';
import { ShoppingItem } from './entities/shopping-item';
import { EntityNotFoundError } from 'typeorm/error/EntityNotFoundError';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class DataService {
  private readonly updatedShoppingListItems: Subject<
    ShoppingList
  > = new Subject<ShoppingList>();

  constructor(
    @InjectRepository(ShoppingList)
    private readonly shoppingListRepository: Repository<ShoppingList>,
    @InjectRepository(ShoppingItem)
    private readonly shoppingItemRepository: Repository<ShoppingItem>
  ) {}
  async createShoppingList(data: { name: string }): Promise<ShoppingList> {
    const shoppingList = new ShoppingList();
    shoppingList.id = v4();
    shoppingList.items = [];
    shoppingList.name = data.name;
    return this.shoppingListRepository.save(shoppingList);
  }

  async findShoppingList(id: string): Promise<ShoppingList> {
    return await this.findOneList(id);
  }

  async addItem(
    id: string,
    item: string,
    quantity: string
  ): Promise<ShoppingItem> {
    const shoppingList = await this.findShoppingList(id);
    const shoppingItem = new ShoppingItem();
    shoppingItem.list = shoppingList;
    shoppingItem.name = item;
    shoppingItem.quantity = quantity;
    shoppingList.items.push(shoppingItem);
    await this.shoppingListRepository.save(shoppingList);
    this.updatedShoppingListItems.next(shoppingList);
    return shoppingItem;
  }

  async updateItem(
    listId: string,
    id: number,
    name?: string,
    quantity?: string,
    checked?: boolean
  ) {
    let item = await this.findOneItem(listId, id);

    Object.assign(item, {
      name: name === undefined ? item.name : name,
      quantity: quantity === undefined ? item.quantity : quantity,
      checked: checked === undefined ? item.checked : checked,
    });
    await this.shoppingItemRepository.save(item);
    item = await this.findOneItem(listId, id);
    this.updatedShoppingListItems.next(item.list);
    return item;
  }

  async deleteItem(listId: string, id: number): Promise<ShoppingItem> {
    const item = await this.findOneItem(listId, id);
    await this.shoppingItemRepository.delete(item);

    // reload list
    item.list = await this.findOneList(listId);
    this.updatedShoppingListItems.next(item.list);
    return item;
  }

  get shoppingListUpdated(): Observable<ShoppingList> {
    return this.updatedShoppingListItems;
  }

  private async findOneList(listId: string): Promise<ShoppingList> {
    const list = await this.shoppingListRepository
      .createQueryBuilder('list')
      .leftJoinAndSelect('list.items', 'items')
      .where('list.id = :listId', { listId })
      .getOne();

    if (!list) {
      throw new EntityNotFoundError(ShoppingList, listId);
    }

    return list;
  }

  private async findOneItem(listId: string, id: number): Promise<ShoppingItem> {
    const itemQuery = await this.shoppingItemRepository
      .createQueryBuilder('item')
      .leftJoinAndSelect('item.list', 'list')
      .leftJoinAndSelect('list.items', 'subItems')
      .where('list.id = :listId', { listId })
      .andWhere('item.id = :id', { id });

    const item = await itemQuery.getOne();
    if (!item) {
      throw new EntityNotFoundError(ShoppingItem, id);
    }
    return item;
  }
}
