import { Args, Int, Mutation, Resolver } from '@nestjs/graphql';
import { ShoppingItem } from '../data/entities/shopping-item';
import { DataService } from '../data';

@Resolver((of) => ShoppingItem)
export class ShoppingItemResolver {
  constructor(private readonly dataService: DataService) {}

  @Mutation((returns) => ShoppingItem)
  async addItem(
    @Args('listId') listId: string,
    @Args('name') name: string,
    @Args('quantity', { type: () => Int }) quantity: number
  ): Promise<ShoppingItem> {
    return await this.dataService.addItem(listId, name, quantity);
  }

  @Mutation((returns) => ShoppingItem)
  async udpateItem(
    @Args('listId') listId: string,
    @Args('id', { type: () => Int }) id: number,
    @Args('name', { nullable: true }) name?: string,
    @Args('quantity', { type: () => Int, nullable: true }) quantity?: number,
    @Args('checked', { nullable: true }) checked?: boolean
  ): Promise<ShoppingItem> {
    return await this.dataService.updateItem(
      listId,
      id,
      name,
      quantity,
      checked
    );
  }

  @Mutation((returns) => ShoppingItem)
  async deleteItem(
    @Args('listId') listId: string,
    @Args('id', { type: () => Int }) id: number
  ): Promise<ShoppingItem> {
    return await this.dataService.deleteItem(listId, id);
  }
}
