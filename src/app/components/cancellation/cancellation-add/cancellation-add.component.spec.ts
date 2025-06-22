import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancellationAddComponent } from './cancellation-add.component';

describe('CancellationAddComponent', () => {
  let component: CancellationAddComponent;
  let fixture: ComponentFixture<CancellationAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CancellationAddComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancellationAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
