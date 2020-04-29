import {
  HttpService,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import slugify from 'slugify';
import { access, createWriteStream, exists, writeFile, readdir } from 'fs';
import * as path from 'path';

interface PixabayResponse {
  total: number;
  totalHits: number;
  hits: Array<{ id: number; previewURL: string; webformatURL: string }>;
}

@Injectable()
export class PictureService {
  private API_KEY = process.env.API_KEY || '16292063-99b92de8f1d1cd9b44fa84417';
  private DATA_STORE =
    process.env.DATA_STORE || (process.cwd() + '/data/images/');

  constructor(private httpService: HttpService) {}

  async providePicture(search: string): Promise<string> {
    search = search.toLowerCase();
    const filePath = slugify(search);

    const doesAlreadyExist = await new Promise<string | null>(
      (resolve, reject) => {
        readdir(this.DATA_STORE, (err, files) => {
          if (err) {
            return reject(err);
          }

          for (const file of files) {
            const fileWithoutExtension = path
              .basename(file)
              .replace(/\.[^/.]+$/, '');
            if (fileWithoutExtension === filePath) {
              return resolve(this.DATA_STORE + file);
            }
          }
          resolve(null);
        });
      }
    );

    if (doesAlreadyExist) {
      Logger.debug('File already exist, sending it', 'PictureService');
      return doesAlreadyExist;
    }

    Logger.debug(
      'File does not exist locally, loading it from external',
      'PictureService'
    );
    const config = { params: { q: search, key: this.API_KEY } };
    const res = await this.httpService
      .get<PixabayResponse>('/api', config)
      .toPromise();

    if (res.data.hits.length === 0) {
      throw new NotFoundException();
    }

    const bestMatch = res.data.hits[0].previewURL;
    const fileSuffix = path.extname(bestMatch);

    const res2 = await this.httpService
      .get(bestMatch, {
        responseType: 'stream',
      })
      .toPromise();

    const fileNameToServe = this.DATA_STORE + filePath + fileSuffix;
    Logger.debug('Store image to ' + fileNameToServe, 'PictureService');
    const writer = createWriteStream(fileNameToServe);
    res2.data.pipe(writer);

    await new Promise((resolve, reject) => {
      writer.on('finish', resolve);
      writer.on('error', reject);
    });

    return fileNameToServe;
  }
}
