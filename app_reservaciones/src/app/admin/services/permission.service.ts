import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Permission } from '../../models/Permission';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../utils/dialog/dialog.component';

@Injectable({
  providedIn: 'root',
})
export class PermissionService {
  private apiUrl = 'http://localhost:8080/api/permiso/public';

  constructor(private http: HttpClient, private dialog: MatDialog) {}

  // Función para obtener todos los permisos
  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<{ data: Permission[] }>(`${this.apiUrl}/getPermisos`).pipe(
      map(response => response.data)
    );
  }

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
