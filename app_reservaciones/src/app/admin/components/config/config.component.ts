import { Component } from '@angular/core';
import { GlobalService } from '../../../core/services/global.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../utils/dialog/dialog.component';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrl: './config.component.css',
})
export class ConfigComponent {
  config: any = {};
  selectedFile: File | null = null;
  constructor(
    private configService: GlobalService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadConfig();
  }

  loadConfig(): void {
    this.configService.getConfig().subscribe({
      next: (response: any) => {
        console.log('Datos del negocio obtenidos de la API:', response);
        this.config = response.data;
        console.log('Objeto Business asignado:', this.config);
      },
      error: () => {
        this.openDialog(
          'Error',
          'Error al cargar los datos del negocio.',
          'red'
        );
      },
    });
  }

  onSubmitNombre(): void {
    if (!this.config.nombre) {
      this.openDialog(
        'Campo vacío',
        'El nombre del sitio no puede estar vacío.',
        'red'
      );
      return;
    }

    this.configService.updateConfig(this.config).subscribe({
      next: () => {
        this.openDialog(
          'Actualización exitosa',
          'Nombre del sitio actualizado con éxito.',
          'green'
        );
      },
      error: () => {
        this.openDialog('Error', 'Error al actualizar el sitio.', 'red');
      },
    });
  }
  // Método para manejar el cambio del archivo
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Asignar el archivo seleccionado
    }
  }

  // Método para manejar el envío del formulario
  onSubmitImagen(): void {
    if (!this.selectedFile) {
      alert('Por favor, selecciona una imagen antes de enviar.');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.configService.updateImage(formData).subscribe({
      next: (response) => {
        console.log('Imagen subida con éxito:', response);
        this.openDialog(
          'Imagen Actualizada',
          'La imagen del sitio fue actualizada, se vera cuando reinicies la ventana',
          'green'
        );
      },
      error: (error) => {
        console.error('Error al subir la imagen:', error);
        this.openDialog('Error al Actualizar', error, 'red');
      },
    });

    console.log('Formulario enviado con la imagen:', this.selectedFile);
  }

  onUpload(event: any) {
    console.log('ARCHIVOS');
    console.log(event);
    console.log(event.files);
  }

  openDialog(
    title: string,
    description: string,
    backgroundColor: 'red' | 'green' | 'gray'
  ): void {
    this.dialog.open(DialogComponent, {
      data: { title, description, backgroundColor },
    });
  }
}
