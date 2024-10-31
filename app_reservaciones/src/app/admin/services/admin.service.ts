import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../utils/dialog/dialog.component';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/permiso/public';

  constructor(private http: HttpClient, private dialog: MatDialog) {}

  // Diálogo de error o confirmación
  openConfirmationDialog(
    title: string,
    description: string,
    backgroundColor: 'gray' | 'red' | 'green'
  ): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
