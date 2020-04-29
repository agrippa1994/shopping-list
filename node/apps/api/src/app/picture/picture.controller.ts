import { Controller, Get, Injectable, Param, Res } from '@nestjs/common';
import { PictureService } from './picture.service';
import { Response } from 'express';

@Controller('/pictures')
export class PictureController {
  constructor(private readonly pictureService: PictureService) {}

  @Get('/:q')
  public async getPicture(@Param('q') search, @Res() response: Response) {
    response.sendFile(await this.pictureService.providePicture(search));
  }
}
