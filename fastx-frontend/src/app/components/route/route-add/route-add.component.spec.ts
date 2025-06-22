import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouteAddComponent } from './route-add.component';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('RouteAddComponent', () => {
  let component: RouteAddComponent;
  let fixture: ComponentFixture<RouteAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RouteAddComponent],
      imports: [
        FormsModule,              //  Needed for template-driven form
        HttpClientTestingModule   //  Fixes HttpClient injection error
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RouteAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
