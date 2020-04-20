import { Module } from '@nestjs/common';

import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DataModule } from './data';
import { GraphqlModule } from './graphql';

@Module({
  imports: [DataModule, GraphqlModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
