import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../utils/dialog/dialog.component';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private dialog: MatDialog) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const jwtToken = localStorage.getItem('jwt');
    let clonedRequest = request;

    if (jwtToken) {
      clonedRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
    }

    return next.handle(clonedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.dialog.open(DialogComponent, {
            data: {
              title: 'Acceso denegado',
              description: 'No tienes permiso para realizar esta acción.',
              backgroundColor: 'red',
            },
          });
        } else if (error.status === 401) {
          this.dialog.open(DialogComponent, {
            data: {
              title: 'Sesión expirada',
              description: 'Por favor, inicia sesión nuevamente.',
              backgroundColor: 'red',
            },
          });
        } else {
          this.dialog.open(DialogComponent, {
            data: {
              title: 'Error',
              description: 'Ocurrió un error inesperado. Intenta de nuevo.',
              backgroundColor: 'red',
            },
          });
        }
        return throwError(error);
      })
    );
  }
}
