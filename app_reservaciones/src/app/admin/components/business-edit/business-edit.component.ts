import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BusinessService } from '../../services/business.service';
import { Business } from '../../../models/Business';
import { MatSnackBar } from '@angular/material/snack-bar';

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
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const businessId = this.route.snapshot.paramMap.get('id');
    if (businessId) {
      this.loadBusiness(Number(businessId));
    }
  }

  loadBusiness(id: number): void {
    this.businessService.getBusinessById(id).subscribe({
      next: (data: Business) => {
        this.business = data;
      },
      error: () => {
        this.snackBar.open('Error al cargar los datos del negocio', 'Cerrar', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    this.businessService.updateBusiness(this.business).subscribe({
      next: () => {
        this.snackBar.open('Negocio actualizado con éxito', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/admin/business']);
      },
      error: () => {
        this.snackBar.open('Error al actualizar el negocio', 'Cerrar', { duration: 3000 });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/business']);
  }
}
