import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { BusinessService } from '../../services/business.service';
import { Business } from '../../../models/Business';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-business',
  templateUrl: './business.component.html',
  styleUrls: ['./business.component.css'],
})
export class BusinessComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'acciones'];
  dataSource: MatTableDataSource<Business> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private businessService: BusinessService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadBusinesses();
  }

  loadBusinesses(): void {
    this.businessService.getAllBusinesses().subscribe({
      next: (businesses) => {
        console.log('Negocios obtenidos:', businesses); // Verifica la respuesta aquí
        this.dataSource.data = businesses;
      },
      error: () => {
        this.snackBar.open('Error al cargar los negocios', 'Cerrar', {
          duration: 3000,
        });
      },
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createBusiness(): void {
    this.router.navigate(['/admin/business-create']);
  }

  editBusiness(negocio: Business): void {
    this.router.navigate(['/admin/business-edit', negocio.id]);
  }

  deleteBusiness(id: number): void {
    const confirmDialog = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirmar eliminación',
        description: '¿Estás seguro de que deseas eliminar este negocio?',
        backgroundColor: 'gray',
      },
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.businessService.deleteBusiness(id).subscribe({
          next: () => {
            this.loadBusinesses();
            this.snackBar.open('Negocio eliminado con éxito', 'Cerrar', {
              duration: 3000,
            });
          },
          error: () => {
            this.snackBar.open('Error al eliminar el negocio', 'Cerrar', {
              duration: 3000,
            });
          },
        });
      }
    });
  }
}
