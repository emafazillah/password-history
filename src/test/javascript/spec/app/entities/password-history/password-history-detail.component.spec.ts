/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { PasswordhistoryTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { PasswordHistoryDetailComponent } from '../../../../../../main/webapp/app/entities/password-history/password-history-detail.component';
import { PasswordHistoryService } from '../../../../../../main/webapp/app/entities/password-history/password-history.service';
import { PasswordHistory } from '../../../../../../main/webapp/app/entities/password-history/password-history.model';

describe('Component Tests', () => {

    describe('PasswordHistory Management Detail Component', () => {
        let comp: PasswordHistoryDetailComponent;
        let fixture: ComponentFixture<PasswordHistoryDetailComponent>;
        let service: PasswordHistoryService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [PasswordhistoryTestModule],
                declarations: [PasswordHistoryDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    PasswordHistoryService,
                    JhiEventManager
                ]
            }).overrideTemplate(PasswordHistoryDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PasswordHistoryDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PasswordHistoryService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new PasswordHistory(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.passwordHistory).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
