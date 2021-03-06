import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { ShoppingList } from './shopping-list';
import { Field, Int, ObjectType } from '@nestjs/graphql';

@Entity()
@ObjectType()
export class ShoppingItem {
  @PrimaryGeneratedColumn()
  @Field((type) => Int)
  id: number;

  @Column({ nullable: false, default: false })
  @Field()
  checked: boolean;

  @Column({ nullable: false })
  @Field()
  name: string;

  @Column({ nullable: false })
  @Field()
  quantity: string;

  @ManyToOne((type) => ShoppingList, (list) => list.items, {
    onDelete: 'CASCADE',
    onUpdate: 'CASCADE',
  })
  @Field((type) => ShoppingList)
  list: ShoppingList;
}
