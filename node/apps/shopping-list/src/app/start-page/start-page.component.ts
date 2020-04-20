import { Component, OnInit } from '@angular/core';
import { GetShoppingListGQL, GetShoppingListQuery } from '../../../../../libs/data-access/src/lib/generated/generated';

@Component({
  selector: 'node-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.css']
})
export class StartPageComponent implements OnInit {

  constructor(private readonly x: GetShoppingListGQL) {
    x.fetch({ listId: '601b438a-b0fd-4ab1-ba15-c9ea147daf93'}).subscribe({
      next: (d) => { console.log('next', d) },
      error: () => { console.log('error') }
    });
  }

  ngOnInit(): void {
  }

}
