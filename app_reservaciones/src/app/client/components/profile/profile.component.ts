import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../auth/services/auth.service';
import { UserStorageService } from '../../../storages/user-storage.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

    profileForm: FormGroup

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private userStorage: UserStorageService,
        fb: FormBuilder
    ) {
        this.profileForm = fb.group({
            nombres: ["", Validators.required],
            apellidos: ["", Validators.required],
            email: ["", Validators.required],
            nit: ["", Validators.required],
            cui: ["", Validators.required],
            telefono: ["", Validators.required],
            twoFactorEnabled: ["", Validators.required]
        });

        this.profileForm.disable()
    }


    userData = {id: 0, nombres: '', apellidos: '', email: '', nit: '', cui: '', telefono: '', twoFactorEnabled: false}
    userDataOg = {id: 0, nombres: '', apellidos: '', email: '', nit: '', cui: '', telefono: '', twoFactorEnabled: false}

    editMode = false

    ngOnInit(): void {
        const data = this.userStorage.getUser()!;
        console.log(data)
        this.userData.nombres = data.nombres
        this.userData.apellidos = data.apellidos
        this.userData.email = data.email
        this.userData.nit = data.nit!
        this.userData.cui = data.cui!
        this.userData.telefono = data.telefono!
        this.userData.twoFactorEnabled = data.twoFactorEnabled

        this.userDataOg = JSON.parse(JSON.stringify(this.userData))
    }

    editar(): void {
        this.editMode = true;
        this.profileForm.enable();
    }

    guardar(): void {
        const payload = this.userData;
        const data = this.userStorage.getUser()!;
        payload.id = data.id;
        this.userService.updateUser(payload).subscribe((response) => {
            this.authService.openConfirmationDialog(
                "Actualizacion Exitosa",
                "El usuario se ha actualizado exitosamente!",
                "green"
            )

            const data = this.userStorage.getUser()!;
            data.nombres = payload.nombres
            data.apellidos = payload.apellidos
            data.email = payload.email
            data.nit = payload.nit
            data.cui = payload.cui
            data.telefono = payload.telefono
            data.twoFactorEnabled = payload.twoFactorEnabled
            this.userStorage.setUser(data)

            this.userData.nombres = data.nombres
            this.userData.apellidos = data.apellidos
            this.userData.email = data.email
            this.userData.nit = data.nit!
            this.userData.cui = data.cui!
            this.userData.telefono = data.telefono!
            this.userData.twoFactorEnabled = data.twoFactorEnabled
            this.userDataOg = JSON.parse(JSON.stringify(this.userData))

            this.editMode = false;
            this.profileForm.disable()
        },
        (error) => {
            this.authService.openConfirmationDialog(
                "Error",
                "Los datos ingresados del usuario no son validos, revisalos e intenta de nuevo.",
                "red"
            )
        });
    }

    cancelar(): void {
        this.userData = JSON.parse(JSON.stringify(this.userDataOg))
        this.editMode = false;
        this.profileForm.disable();
    }

    activarTwoFactor(): void {
        if (this.userData.twoFactorEnabled) {
            this.userData.twoFactorEnabled = false
        } else {
            this.userData.twoFactorEnabled = true
        }
    }
}
