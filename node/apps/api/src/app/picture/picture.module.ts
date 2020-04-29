import { HttpModule, Module } from '@nestjs/common';
import { PictureService } from './picture.service';
import { PictureController } from './picture.controller';

@Module({
  imports: [
    HttpModule.register({
      baseURL: 'https://pixabay.com/',
      params: {
        key: '16292063-99b92de8f1d1cd9b44fa84417',
      },
    }),
  ],
  providers: [PictureService],
  controllers: [PictureController],
})
export class PictureModule {}
