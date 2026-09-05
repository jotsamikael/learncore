import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportQuestionComponent } from './import-question.component';

describe('ImportQuestionComponent', () => {
  let component: ImportQuestionComponent;
  let fixture: ComponentFixture<ImportQuestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportQuestionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportQuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
