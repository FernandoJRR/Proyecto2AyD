import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelarReservacionComponent } from './cancelar-reservacion.component';

describe('CancelarReservacionComponent', () => {
  let component: CancelarReservacionComponent;
  let fixture: ComponentFixture<CancelarReservacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CancelarReservacionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelarReservacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
