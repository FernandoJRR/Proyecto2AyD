import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceUnitsCreateComponent } from './resource-units-create.component';

describe('ResourceUnitsCreateComponent', () => {
  let component: ResourceUnitsCreateComponent;
  let fixture: ComponentFixture<ResourceUnitsCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResourceUnitsCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResourceUnitsCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
