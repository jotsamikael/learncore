import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantStaffComponent } from './tenant-staff.component';

describe('TenantStaffComponent', () => {
  let component: TenantStaffComponent;
  let fixture: ComponentFixture<TenantStaffComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantStaffComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantStaffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
