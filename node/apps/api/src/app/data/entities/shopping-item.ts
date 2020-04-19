import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { ShoppingList } from './shopping-list';

@Entity()
export class ShoppingItem {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: false, default: false })
  completed: boolean;

  @ManyToOne((type) => ShoppingList, (list) => list.items)
  list: ShoppingList;
}
