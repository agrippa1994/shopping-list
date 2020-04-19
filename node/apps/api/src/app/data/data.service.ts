import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ShoppingList } from './entities/shopping-list';
import { Repository } from 'typeorm';
import { v4 } from 'uuid';
import { ShoppingItem } from './entities/shopping-item';
import { EntityNotFoundError } from 'typeorm/error/EntityNotFoundError';

@Injectable()
export class DataService {
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
    quantity: number
  ): Promise<ShoppingItem> {
    const shoppingList = await this.findShoppingList(id);
    const shoppingItem = new ShoppingItem();
    shoppingItem.list = shoppingList;
    shoppingItem.name = item;
    shoppingItem.quantity = quantity;
    shoppingList.items.push(shoppingItem);
    await this.shoppingListRepository.save(shoppingList);
    return shoppingItem;
  }

  async updateItem(
    listId: string,
    id: number,
    name?: string,
    quantity?: number,
    checked?: boolean
  ) {
    const item = await this.findOneItem(listId, id);

    Object.assign(item, {
      name: name === undefined ? item.name : name,
      quantity: quantity === undefined ? item.quantity : quantity,
      checked: checked === undefined ? item.checked : checked,
    });
    await this.shoppingItemRepository.save(item);
    return await this.findOneItem(listId, id);
  }

  async deleteItem(listId: string, id: number): Promise<ShoppingItem> {
    const item = await this.findOneItem(listId, id);
    await this.shoppingItemRepository.delete(item);

    // reload list
    item.list = await this.findOneList(listId);
    return item;
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
