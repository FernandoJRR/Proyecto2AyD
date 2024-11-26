import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceUnitComponent } from './resource-unit.component';

describe('ResourceUnitComponent', () => {
  let component: ResourceUnitComponent;
  let fixture: ComponentFixture<ResourceUnitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResourceUnitComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResourceUnitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
