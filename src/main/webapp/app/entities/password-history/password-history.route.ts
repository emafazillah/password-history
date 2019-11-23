import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { PasswordHistory } from 'app/shared/model/password-history.model';
import { PasswordHistoryService } from './password-history.service';
import { PasswordHistoryComponent } from './password-history.component';
import { PasswordHistoryDetailComponent } from './password-history-detail.component';
import { PasswordHistoryUpdateComponent } from './password-history-update.component';
import { IPasswordHistory } from 'app/shared/model/password-history.model';

@Injectable({ providedIn: 'root' })
export class PasswordHistoryResolve implements Resolve<IPasswordHistory> {
  constructor(private service: PasswordHistoryService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPasswordHistory> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((passwordHistory: HttpResponse<PasswordHistory>) => passwordHistory.body));
    }
    return of(new PasswordHistory());
  }
}

export const passwordHistoryRoute: Routes = [
  {
    path: '',
    component: PasswordHistoryComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'PasswordHistories'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PasswordHistoryDetailComponent,
    resolve: {
      passwordHistory: PasswordHistoryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PasswordHistories'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PasswordHistoryUpdateComponent,
    resolve: {
      passwordHistory: PasswordHistoryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PasswordHistories'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PasswordHistoryUpdateComponent,
    resolve: {
      passwordHistory: PasswordHistoryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PasswordHistories'
    },
    canActivate: [UserRouteAccessService]
  }
];
