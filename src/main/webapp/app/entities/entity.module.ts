import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'password-history',
        loadChildren: () => import('./password-history/password-history.module').then(m => m.PasswordhistoryPasswordHistoryModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class PasswordhistoryEntityModule {}
