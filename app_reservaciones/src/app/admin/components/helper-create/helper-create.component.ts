import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { RoleService } from '../../services/role.service';
import { ServiceService } from '../../services/service.service';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { Role } from '../../../models/Role';
import { Day } from '../../../models/Day';

@Component({
  selector: 'app-helper-create',
  templateUrl: './helper-create.component.html',
  styleUrls: ['./helper-create.component.css'],
})
export class HelperCreateComponent implements OnInit {
  helperForm: FormGroup;
  roles: Role[] = [];
  days: Day[] = [];

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private serviceService: ServiceService,
    private userService: UserService,
    private router: Router
  ) {
    this.helperForm = this.fb.group({
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      nit: ['', Validators.required],
      cui: ['', Validators.required],
      password: ['', Validators.required],
      telefono: ['', Validators.required],
      roles: [[], Validators.required],
      horariosAtencion: this.fb.array([this.createHorarioAtencionFormGroup()]),
    });
  }

  ngOnInit(): void {
    this.loadRoles();
    this.loadDays();
  }

  loadRoles(): void {
    this.roleService.getAllRoles().subscribe({
      next: (response) => {
        this.roles = response;
      },
      error: (error) => {
        console.error('Error al cargar roles:', error);
      },
    });
  }

  loadDays(): void {
    this.serviceService.getDays().subscribe({
      next: (response) => {
        this.days = response;
      },
      error: (error) => {
        console.error('Error al cargar días:', error);
      },
    });
  }

  get horariosAtencion(): FormArray {
    return this.helperForm.get('horariosAtencion') as FormArray;
  }

  addHorarioAtencion(): void {
    this.horariosAtencion.push(this.createHorarioAtencionFormGroup());
  }

  removeHorarioAtencion(index: number): void {
    this.horariosAtencion.removeAt(index);
  }

  createHorarioAtencionFormGroup(): FormGroup {
    return this.fb.group({
      horaInicio: ['', Validators.required],
      horaFinal: ['', Validators.required],
      diaAtencion: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.helperForm.valid) {
      const helperData = {
        usuario: {
          nombres: this.helperForm.get('nombres')?.value,
          apellidos: this.helperForm.get('apellidos')?.value,
          email: this.helperForm.get('email')?.value,
          nit: this.helperForm.get('nit')?.value,
          cui: this.helperForm.get('cui')?.value,
          password: this.helperForm.get('password')?.value,
          telefono: this.helperForm.get('telefono')?.value,
          twoFactorCode: '',
          twoFactorEnabled: false,
        },
        roles: this.helperForm.get('roles')?.value.map((roleId: number) => ({ id: roleId })),
        horarioAtencionUsuarios: this.horariosAtencion.value.map((horario: any) => ({
          horaInicio: horario.horaInicio,
          horaFinal: horario.horaFinal,
          diaAtencion: { id: horario.diaAtencion },
        })),
      };

      this.userService.createHelper(helperData).subscribe({
        next: (response) => {
          console.log('Ayudante creado exitosamente:', response);
          this.router.navigate(['/users']);
        },
        error: (error) => {
          console.error('Error al crear ayudante:', error);
        },
      });
    }
  }
}
