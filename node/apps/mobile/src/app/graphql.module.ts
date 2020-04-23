import { NgModule } from '@angular/core';
import { APOLLO_OPTIONS, ApolloModule } from 'apollo-angular';
import { HttpLink, HttpLinkModule } from 'apollo-angular-link-http';
import { InMemoryCache } from 'apollo-cache-inmemory';
import { DataAccessModule } from '@node/data-access';
import { WebSocketLink } from 'apollo-link-ws';
import { split } from 'apollo-link';
import { getMainDefinition } from 'apollo-utilities';
import { environment } from '../environments/environment';

const uri = environment.graphql; // <-- add the URL of the GraphQL server here
export function createApollo(httpLink: HttpLink) {
  const http = httpLink.create({ uri: environment.graphql.http });
  const ws = new WebSocketLink({
    uri: environment.graphql.ws,
    options: {
      reconnect: true,
    },
  });

  return {
    link: split(
      // split based on operation type
      ({ query }) => {
        // @ts-ignore
        const { kind, operation } = getMainDefinition(query);
        return kind === 'OperationDefinition' && operation === 'subscription';
      },
      ws,
      http
    ),
    cache: new InMemoryCache(),
  };
}

@NgModule({
  imports: [DataAccessModule],
  exports: [ApolloModule, HttpLinkModule, DataAccessModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
