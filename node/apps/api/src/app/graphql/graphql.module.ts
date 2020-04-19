import { Module } from '@nestjs/common';
import { GraphQLModule } from '@nestjs/graphql';
import { DataModule } from '../data';
import { ShoppingListResolver } from './shopping-list.resolver';
import { ShoppingItemResolver } from './shopping-item.resolver';

@Module({
  imports: [
    GraphQLModule.forRoot({
      autoSchemaFile: 'shopping-list.gql',
      installSubscriptionHandlers: true,
    }),
    DataModule,
  ],
  providers: [ShoppingListResolver, ShoppingItemResolver],
})
export class GraphqlModule {}
