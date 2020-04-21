import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { GetShoppingListGQL } from '@node/data-access';

@Injectable()
export class CanActivateHandler implements CanActivate {
  constructor(
    private getShoppingList: GetShoppingListGQL,
    private router: Router
  ) {}

  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    const { shoppingListId } = route.params;
    if (!shoppingListId) {
      await this.router.navigateByUrl('/');
      return false;
    }

    try {
      const result = await this.getShoppingList
        .fetch({ listId: shoppingListId })
        .toPromise();
      if (result.errors) {
        throw result.errors;
      }

      return result.data.shoppingList.id === shoppingListId;
    } catch (e) {
      await this.router.navigateByUrl('/');
      return false;
    }
  }
}
