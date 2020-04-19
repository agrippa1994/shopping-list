import { DataService } from '../data';
import { Args, Mutation, Query, Resolver } from '@nestjs/graphql';
import { ShoppingList } from '../data/entities/shopping-list';

@Resolver((of) => ShoppingList)
export class ShoppingListResolver {
  constructor(private readonly dataService: DataService) {}

  @Query((returns) => ShoppingList)
  async shoppingList(@Args('id') id: string): Promise<ShoppingList> {
    return await this.dataService.findShoppingList(id);
  }

  @Mutation((returns) => ShoppingList)
  async createShoppingList(
    @Args({ name: 'name' }) name: string
  ): Promise<ShoppingList> {
    return await this.dataService.createShoppingList({ name });
  }
}
