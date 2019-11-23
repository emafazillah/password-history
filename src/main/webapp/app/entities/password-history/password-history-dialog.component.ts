import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { PasswordHistory } from './password-history.model';
import { PasswordHistoryPopupService } from './password-history-popup.service';
import { PasswordHistoryService } from './password-history.service';
import { User, UserService } from '../../shared';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-password-history-dialog',
    templateUrl: './password-history-dialog.component.html'
})
export class PasswordHistoryDialogComponent implements OnInit {

    passwordHistory: PasswordHistory;
    isSaving: boolean;

    users: User[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private passwordHistoryService: PasswordHistoryService,
        private userService: UserService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.userService.query()
            .subscribe((res: ResponseWrapper) => { this.users = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.passwordHistory.id !== undefined) {
            this.subscribeToSaveResponse(
                this.passwordHistoryService.update(this.passwordHistory));
        } else {
            this.subscribeToSaveResponse(
                this.passwordHistoryService.create(this.passwordHistory));
        }
    }

    private subscribeToSaveResponse(result: Observable<PasswordHistory>) {
        result.subscribe((res: PasswordHistory) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: PasswordHistory) {
        this.eventManager.broadcast({ name: 'passwordHistoryListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.alertService.error(error.message, null, null);
    }

    trackUserById(index: number, item: User) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-password-history-popup',
    template: ''
})
export class PasswordHistoryPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private passwordHistoryPopupService: PasswordHistoryPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.passwordHistoryPopupService
                    .open(PasswordHistoryDialogComponent as Component, params['id']);
            } else {
                this.passwordHistoryPopupService
                    .open(PasswordHistoryDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
