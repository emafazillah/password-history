import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { PasswordhistoryTestModule } from '../../../test.module';
import { PasswordHistoryDeleteDialogComponent } from 'app/entities/password-history/password-history-delete-dialog.component';
import { PasswordHistoryService } from 'app/entities/password-history/password-history.service';

describe('Component Tests', () => {
  describe('PasswordHistory Management Delete Component', () => {
    let comp: PasswordHistoryDeleteDialogComponent;
    let fixture: ComponentFixture<PasswordHistoryDeleteDialogComponent>;
    let service: PasswordHistoryService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [PasswordhistoryTestModule],
        declarations: [PasswordHistoryDeleteDialogComponent]
      })
        .overrideTemplate(PasswordHistoryDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PasswordHistoryDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PasswordHistoryService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
