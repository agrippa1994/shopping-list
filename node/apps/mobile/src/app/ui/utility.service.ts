import { Injectable } from '@angular/core';
import { AlertController, ToastController } from '@ionic/angular';

@Injectable()
export class UtilityService {
  constructor(
    private readonly toastController: ToastController,
    private readonly alertController: AlertController
  ) {}

  public async showToast(message: string) {
    const toast = await this.toastController.create({
      message: message,
      translucent: true,
      duration: 2500,
    });
    return await toast.present();
  }

  public async showConfirmationDialog(options: {
    header: string;
    message: string;
  }): Promise<boolean> {
    let result = false;
    const alert = await this.alertController.create({
      header: options.header,
      message: options.message,
      buttons: [
        {
          text: 'No',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => (result = false),
        },
        {
          text: 'Yes',
          handler: () => (result = true),
        },
      ],
    });

    await alert.present();
    await alert.onDidDismiss();
    return result;
  }
}
