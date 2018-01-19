import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { PasswordHistory } from './password-history.model';
import { PasswordHistoryPopupService } from './password-history-popup.service';
import { PasswordHistoryService } from './password-history.service';

@Component({
    selector: 'jhi-password-history-delete-dialog',
    templateUrl: './password-history-delete-dialog.component.html'
})
export class PasswordHistoryDeleteDialogComponent {

    passwordHistory: PasswordHistory;

    constructor(
        private passwordHistoryService: PasswordHistoryService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.passwordHistoryService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'passwordHistoryListModification',
                content: 'Deleted an passwordHistory'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-password-history-delete-popup',
    template: ''
})
export class PasswordHistoryDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private passwordHistoryPopupService: PasswordHistoryPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.passwordHistoryPopupService
                .open(PasswordHistoryDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
