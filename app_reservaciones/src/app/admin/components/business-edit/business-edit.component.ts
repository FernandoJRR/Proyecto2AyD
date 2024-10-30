import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BusinessService } from '../../services/business.service';
import { Business } from '../../../models/Business';
import { DialogComponent } from '../../../utils/dialog/dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-business-edit',
  templateUrl: './business-edit.component.html',
  styleUrls: ['./business-edit.component.css']
})
export class BusinessEditComponent implements OnInit {
  business: Business = {
    id: 0,
    nombre: '',
  };

  constructor(
    private businessService: BusinessService,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const businessId = this.route.snapshot.paramMap.get('id');
    if (businessId) {
      console.log('ID del negocio a editar:', businessId);
      this.loadBusiness(Number(businessId));
    }
  }

  loadBusiness(id: number): void {
    this.businessService.getBusinessById(id).subscribe({
      next: (data: Business) => {
        console.log('Datos del negocio obtenidos de la API:', data);
        this.business = data;
        console.log('Objeto Business asignado:', this.business);
      },
      error: () => {
        this.openDialog('Error', 'Error al cargar los datos del negocio.', 'red');
      }
    });
  }

  onSubmit(): void {
    if (!this.business.nombre) {
      this.openDialog('Campo vacío', 'El nombre del negocio no puede estar vacío.', 'red');
      return;
    }

    this.businessService.updateBusiness(this.business).subscribe({
      next: () => {
        this.openDialog('Actualización exitosa', 'Negocio actualizado con éxito.', 'green');
        this.router.navigate(['/admin/business']);
      },
      error: () => {
        this.openDialog('Error', 'Error al actualizar el negocio.', 'red');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/business']);
  }

  openDialog(title: string, description: string, backgroundColor: 'red' | 'green' | 'gray'): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
