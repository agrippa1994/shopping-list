import { Module } from '@nestjs/common';
import { DataService } from './data.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ShoppingItem } from './entities/shopping-item';
import { ShoppingList } from './entities/shopping-list';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      logging: true,
      synchronize: true,
      type: 'sqlite',
      database: 'shopping-list.db',
      entities: [ShoppingItem, ShoppingList],
    }),
    TypeOrmModule.forFeature([ShoppingItem, ShoppingList]),
  ],
  providers: [DataService],
  exports: [DataService],
})
export class DataModule {}
