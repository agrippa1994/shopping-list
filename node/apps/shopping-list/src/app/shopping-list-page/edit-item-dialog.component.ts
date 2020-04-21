import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EditItemDialogData } from './edit-item-dialog.data';

@Component({
  selector: 'node-edit-item-dialog',
  templateUrl: 'edit-item-dialog.component.html',
})
export class EditItemDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<EditItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditItemDialogData
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
