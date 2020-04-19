import { Column, Entity, OneToMany, PrimaryColumn } from 'typeorm';
import { ShoppingItem } from './shopping-item';
import { Field, ObjectType } from '@nestjs/graphql';

@Entity()
@ObjectType()
export class ShoppingList {
  @PrimaryColumn({ nullable: false, type: 'uuid' })
  @Field()
  id: string;

  @Column({ nullable: false })
  @Field()
  name: string;

  @OneToMany((type) => ShoppingItem, (item) => item.list, {
    cascade: ['insert', 'update', 'remove'],
  })
  @Field((type) => [ShoppingItem])
  items: ShoppingItem[];
}
