// RoleCreationRequest.ts
export interface RoleCreationRequest {
  rol: { nombre: string };
  permisos: { id: number }[]; // Cambiado para reflejar solo el ID
  servicios: { id: number }[]; // Cambiado para reflejar solo el ID
}
