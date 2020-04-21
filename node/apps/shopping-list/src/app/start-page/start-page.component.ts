import { Component, OnInit } from '@angular/core';
import {
  CreateShoppingListGQL,
  GetShoppingListGQL,
  GetShoppingListQuery,
} from '../../../../../libs/data-access/src/lib/generated/generated';
import { FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'node-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.scss'],
})
export class StartPageComponent implements OnInit {
  name = new FormControl('', [Validators.required, Validators.minLength(1)]);

  constructor(
    private readonly createShoppingList: CreateShoppingListGQL,
    private router: Router
  ) {}

  ngOnInit(): void {}

  getErrorMessage() {}

  async createEvent() {
    try {
      const shoppingList = await this.createShoppingList
        .mutate({
          name: this.name.value,
        })
        .toPromise();

      await this.router.navigateByUrl(
        '/' + shoppingList.data.createShoppingList.id
      );
    } catch (e) {
      console.error(e);
    }
  }
}
