import { Module } from '@nestjs/common';
import { DataModule } from './data';
import { GraphqlModule } from './graphql';
import { PictureModule } from './picture';

@Module({
  imports: [DataModule, GraphqlModule, PictureModule],
})
export class AppModule {}
