import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PasswordhistorySharedModule } from '../../shared';
import { PasswordhistoryAdminModule } from '../../admin/admin.module';
import {
    PasswordHistoryService,
    PasswordHistoryPopupService,
    PasswordHistoryComponent,
    PasswordHistoryDetailComponent,
    PasswordHistoryDialogComponent,
    PasswordHistoryPopupComponent,
    PasswordHistoryDeletePopupComponent,
    PasswordHistoryDeleteDialogComponent,
    passwordHistoryRoute,
    passwordHistoryPopupRoute,
    PasswordHistoryResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...passwordHistoryRoute,
    ...passwordHistoryPopupRoute,
];

@NgModule({
    imports: [
        PasswordhistorySharedModule,
        PasswordhistoryAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        PasswordHistoryComponent,
        PasswordHistoryDetailComponent,
        PasswordHistoryDialogComponent,
        PasswordHistoryDeleteDialogComponent,
        PasswordHistoryPopupComponent,
        PasswordHistoryDeletePopupComponent,
    ],
    entryComponents: [
        PasswordHistoryComponent,
        PasswordHistoryDialogComponent,
        PasswordHistoryPopupComponent,
        PasswordHistoryDeleteDialogComponent,
        PasswordHistoryDeletePopupComponent,
    ],
    providers: [
        PasswordHistoryService,
        PasswordHistoryPopupService,
        PasswordHistoryResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PasswordhistoryPasswordHistoryModule {}
