import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Service } from '../models/Service';

@Injectable({
  providedIn: 'root'
})
export class ServiceStorageService {
  private servicesSubject = new BehaviorSubject<Service[]>([]);
  services$ = this.servicesSubject.asObservable();

  setServices(services: Service[]): void {
    this.servicesSubject.next(services);
    localStorage.setItem('services', JSON.stringify(services));
  }

  getServices(): Service[] {
    const services = localStorage.getItem('services');
    return services ? JSON.parse(services) : this.servicesSubject.getValue();
  }

  clearServices(): void {
    this.servicesSubject.next([]);
    localStorage.removeItem('services');
  }
}
