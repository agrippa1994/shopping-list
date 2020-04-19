import { Module } from '@nestjs/common';
import { GraphQLModule } from '@nestjs/graphql';
import { DataModule } from '../data';
import { ShoppingListResolver } from './shopping-list.resolver';
import { ShoppingItemResolver } from './shopping-item.resolver';
import { PUB_SUB } from './constants';
import { PubSub } from 'graphql-subscriptions';

@Module({
  imports: [
    GraphQLModule.forRoot({
      autoSchemaFile: 'shopping-list.gql',
      installSubscriptionHandlers: true,
    }),
    DataModule,
  ],
  providers: [
    ShoppingListResolver,
    ShoppingItemResolver,
    {
      provide: PUB_SUB,
      useValue: new PubSub(),
    },
  ],
})
export class GraphqlModule {}
