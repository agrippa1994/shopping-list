import { DataService } from '../data';
import { Args, Mutation, Query, Resolver, Subscription } from '@nestjs/graphql';
import { ShoppingList } from '../data/entities/shopping-list';
import { Inject, Logger, OnModuleInit } from '@nestjs/common';
import { PUB_SUB } from './constants';
import { PubSub } from 'graphql-subscriptions';

@Resolver((of) => ShoppingList)
export class ShoppingListResolver implements OnModuleInit {
  constructor(
    @Inject(PUB_SUB) private readonly pubSub: PubSub,
    private readonly dataService: DataService
  ) {}

  onModuleInit(): any {
    this.dataService.shoppingListUpdated.subscribe(
      async (shoppingListUpdated: ShoppingList) => {
        Logger.debug(
          `Updated shopping list with id ${shoppingListUpdated.id}`,
          'ShoppingListResolver'
        );
        await this.pubSub.publish('shoppingListUpdated', {
          shoppingListUpdated,
        });
      }
    );
  }

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

  @Subscription((returns) => ShoppingList, {
    filter: (payload, variables) =>
      payload.shoppingListUpdated.id === variables.id,
  })
  shoppingListUpdated(@Args('id') id: string): AsyncIterator<ShoppingList> {
    return this.pubSub.asyncIterator<ShoppingList>('shoppingListUpdated');
  }
}
