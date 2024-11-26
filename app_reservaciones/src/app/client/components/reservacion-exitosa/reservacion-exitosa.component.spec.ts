import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservacionExitosaComponent } from './reservacion-exitosa.component';

describe('ReservacionExitosaComponent', () => {
  let component: ReservacionExitosaComponent;
  let fixture: ComponentFixture<ReservacionExitosaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReservacionExitosaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservacionExitosaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
