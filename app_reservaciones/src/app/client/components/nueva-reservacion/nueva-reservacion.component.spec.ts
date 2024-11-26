import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevaReservacionComponent } from './nueva-reservacion.component';

describe('NuevaReservacionComponent', () => {
  let component: NuevaReservacionComponent;
  let fixture: ComponentFixture<NuevaReservacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NuevaReservacionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NuevaReservacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
