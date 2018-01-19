import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { PasswordHistoryComponent } from './password-history.component';
import { PasswordHistoryDetailComponent } from './password-history-detail.component';
import { PasswordHistoryPopupComponent } from './password-history-dialog.component';
import { PasswordHistoryDeletePopupComponent } from './password-history-delete-dialog.component';

@Injectable()
export class PasswordHistoryResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const passwordHistoryRoute: Routes = [
    {
        path: 'password-history',
        component: PasswordHistoryComponent,
        resolve: {
            'pagingParams': PasswordHistoryResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PasswordHistories'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'password-history/:id',
        component: PasswordHistoryDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PasswordHistories'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const passwordHistoryPopupRoute: Routes = [
    {
        path: 'password-history-new',
        component: PasswordHistoryPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PasswordHistories'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'password-history/:id/edit',
        component: PasswordHistoryPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PasswordHistories'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'password-history/:id/delete',
        component: PasswordHistoryDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'PasswordHistories'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
