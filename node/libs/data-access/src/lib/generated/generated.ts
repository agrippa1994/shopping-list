import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};



export type ShoppingItem = {
   __typename?: 'ShoppingItem';
  id: Scalars['Int'];
  checked: Scalars['Boolean'];
  name: Scalars['String'];
  quantity: Scalars['String'];
  list: ShoppingList;
};

export type ShoppingList = {
   __typename?: 'ShoppingList';
  id: Scalars['String'];
  name: Scalars['String'];
  items: Array<ShoppingItem>;
};

export type Query = {
   __typename?: 'Query';
  shoppingList: ShoppingList;
};


export type QueryShoppingListArgs = {
  id: Scalars['String'];
};

export type Mutation = {
   __typename?: 'Mutation';
  createShoppingList: ShoppingList;
  addItem: ShoppingItem;
  udpateItem: ShoppingItem;
  deleteItem: ShoppingItem;
};


export type MutationCreateShoppingListArgs = {
  name: Scalars['String'];
};


export type MutationAddItemArgs = {
  quantity: Scalars['String'];
  name: Scalars['String'];
  listId: Scalars['String'];
};


export type MutationUdpateItemArgs = {
  checked?: Maybe<Scalars['Boolean']>;
  quantity?: Maybe<Scalars['String']>;
  name?: Maybe<Scalars['String']>;
  id: Scalars['Int'];
  listId: Scalars['String'];
};


export type MutationDeleteItemArgs = {
  id: Scalars['Int'];
  listId: Scalars['String'];
};

export type Subscription = {
   __typename?: 'Subscription';
  shoppingListUpdated: ShoppingList;
};


export type SubscriptionShoppingListUpdatedArgs = {
  id: Scalars['String'];
};

export type ShoppingItemFragment = (
  { __typename?: 'ShoppingItem' }
  & Pick<ShoppingItem, 'id' | 'checked' | 'name' | 'quantity'>
  & { list: (
    { __typename?: 'ShoppingList' }
    & ShoppingListFragment
  ) }
);

export type ShoppingListFragment = (
  { __typename?: 'ShoppingList' }
  & Pick<ShoppingList, 'id' | 'name'>
  & { items: Array<(
    { __typename?: 'ShoppingItem' }
    & Pick<ShoppingItem, 'id' | 'checked' | 'name' | 'quantity'>
  )> }
);

export type GetShoppingListQueryVariables = {
  listId: Scalars['String'];
};


export type GetShoppingListQuery = (
  { __typename?: 'Query' }
  & { shoppingList: (
    { __typename?: 'ShoppingList' }
    & ShoppingListFragment
  ) }
);

export type CreateShoppingListMutationVariables = {
  name: Scalars['String'];
};


export type CreateShoppingListMutation = (
  { __typename?: 'Mutation' }
  & { createShoppingList: (
    { __typename?: 'ShoppingList' }
    & ShoppingListFragment
  ) }
);

export type AddItemMutationVariables = {
  listId: Scalars['String'];
  name: Scalars['String'];
  quantity: Scalars['String'];
};


export type AddItemMutation = (
  { __typename?: 'Mutation' }
  & { addItem: (
    { __typename?: 'ShoppingItem' }
    & ShoppingItemFragment
  ) }
);

export type UpdateItemMutationVariables = {
  listId: Scalars['String'];
  id: Scalars['Int'];
  checked?: Maybe<Scalars['Boolean']>;
  name?: Maybe<Scalars['String']>;
  quantity?: Maybe<Scalars['String']>;
};


export type UpdateItemMutation = (
  { __typename?: 'Mutation' }
  & { udpateItem: (
    { __typename?: 'ShoppingItem' }
    & ShoppingItemFragment
  ) }
);

export type DeleteItemMutationVariables = {
  listId: Scalars['String'];
  id: Scalars['Int'];
};


export type DeleteItemMutation = (
  { __typename?: 'Mutation' }
  & { deleteItem: (
    { __typename?: 'ShoppingItem' }
    & ShoppingItemFragment
  ) }
);

export type ListUpdatedSubscriptionVariables = {
  listId: Scalars['String'];
};


export type ListUpdatedSubscription = (
  { __typename?: 'Subscription' }
  & { shoppingListUpdated: (
    { __typename?: 'ShoppingList' }
    & ShoppingListFragment
  ) }
);

export const ShoppingListFragmentDoc = gql`
    fragment ShoppingList on ShoppingList {
  id
  name
  items {
    id
    checked
    name
    quantity
  }
}
    `;
export const ShoppingItemFragmentDoc = gql`
    fragment ShoppingItem on ShoppingItem {
  id
  checked
  name
  quantity
  list {
    ...ShoppingList
  }
}
    ${ShoppingListFragmentDoc}`;
export const GetShoppingListDocument = gql`
    query GetShoppingList($listId: String!) {
  shoppingList(id: $listId) {
    ...ShoppingList
  }
}
    ${ShoppingListFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class GetShoppingListGQL extends Apollo.Query<GetShoppingListQuery, GetShoppingListQueryVariables> {
    document = GetShoppingListDocument;
    
  }
export const CreateShoppingListDocument = gql`
    mutation CreateShoppingList($name: String!) {
  createShoppingList(name: $name) {
    ...ShoppingList
  }
}
    ${ShoppingListFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CreateShoppingListGQL extends Apollo.Mutation<CreateShoppingListMutation, CreateShoppingListMutationVariables> {
    document = CreateShoppingListDocument;
    
  }
export const AddItemDocument = gql`
    mutation AddItem($listId: String!, $name: String!, $quantity: String!) {
  addItem(listId: $listId, name: $name, quantity: $quantity) {
    ...ShoppingItem
  }
}
    ${ShoppingItemFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AddItemGQL extends Apollo.Mutation<AddItemMutation, AddItemMutationVariables> {
    document = AddItemDocument;
    
  }
export const UpdateItemDocument = gql`
    mutation UpdateItem($listId: String!, $id: Int!, $checked: Boolean, $name: String, $quantity: String) {
  udpateItem(listId: $listId, id: $id, checked: $checked, name: $name, quantity: $quantity) {
    ...ShoppingItem
  }
}
    ${ShoppingItemFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class UpdateItemGQL extends Apollo.Mutation<UpdateItemMutation, UpdateItemMutationVariables> {
    document = UpdateItemDocument;
    
  }
export const DeleteItemDocument = gql`
    mutation DeleteItem($listId: String!, $id: Int!) {
  deleteItem(listId: $listId, id: $id) {
    ...ShoppingItem
  }
}
    ${ShoppingItemFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class DeleteItemGQL extends Apollo.Mutation<DeleteItemMutation, DeleteItemMutationVariables> {
    document = DeleteItemDocument;
    
  }
export const ListUpdatedDocument = gql`
    subscription ListUpdated($listId: String!) {
  shoppingListUpdated(id: $listId) {
    ...ShoppingList
  }
}
    ${ShoppingListFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ListUpdatedGQL extends Apollo.Subscription<ListUpdatedSubscription, ListUpdatedSubscriptionVariables> {
    document = ListUpdatedDocument;
    
  }