import { Module } from '@nestjs/common';
import { DataModule } from './data';
import { GraphqlModule } from './graphql';

@Module({
  imports: [DataModule, GraphqlModule],
})
export class AppModule {}
