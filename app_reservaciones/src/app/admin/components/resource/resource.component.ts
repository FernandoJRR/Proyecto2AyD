import { Component, OnInit, ViewChild } from '@angular/core';
import { ResourceService } from '../../services/resource.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { Resource } from '../../../models/Resource';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-resource',
  templateUrl: './resource.component.html',
  styleUrls: ['./resource.component.css']
})
export class ResourceComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nombre', 'actions'];
  dataSource: MatTableDataSource<Resource> = new MatTableDataSource();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  constructor(
    private resourceService: ResourceService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe({
      next: (resources) => {
        console.log('Recursos obtenidos:', resources); // Verifica la respuesta aquí
        this.dataSource.data = resources;
      },
      error: () => {
        this.snackBar.open('Error al cargar los recursos', 'Cerrar', {
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

  createResource(): void {
    
  }

  editResource(id: number): void {
    this.router.navigate(['/admin/resources/edit', id]);
  }

  deleteResource(id: number): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: "Confirmar eliminación",
        description: "¿Estás seguro de que deseas eliminar este recurso?",
        backgroundColor: 'gray',
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.resourceService.deleteResource(id).subscribe({
          next: () => this.loadResources(),
          error: () => this.showErrorDialog("Error al eliminar el recurso")
        });
      }
    });
  }

  private showErrorDialog(message: string): void {
    this.dialog.open(DialogComponent, {
      data: {
        title: "Error",
        description: message
      }
    });
  }
}
