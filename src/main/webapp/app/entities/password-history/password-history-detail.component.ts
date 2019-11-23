import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { PasswordHistory } from './password-history.model';
import { PasswordHistoryService } from './password-history.service';

@Component({
    selector: 'jhi-password-history-detail',
    templateUrl: './password-history-detail.component.html'
})
export class PasswordHistoryDetailComponent implements OnInit, OnDestroy {

    passwordHistory: PasswordHistory;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private passwordHistoryService: PasswordHistoryService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPasswordHistories();
    }

    load(id) {
        this.passwordHistoryService.find(id).subscribe((passwordHistory) => {
            this.passwordHistory = passwordHistory;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPasswordHistories() {
        this.eventSubscriber = this.eventManager.subscribe(
            'passwordHistoryListModification',
            (response) => this.load(this.passwordHistory.id)
        );
    }
}
