import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformStaffComponent } from './platform-staff.component';

describe('PlatformStaffComponent', () => {
  let component: PlatformStaffComponent;
  let fixture: ComponentFixture<PlatformStaffComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlatformStaffComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlatformStaffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
