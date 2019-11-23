import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPasswordHistory } from 'app/shared/model/password-history.model';

@Component({
  selector: 'jhi-password-history-detail',
  templateUrl: './password-history-detail.component.html'
})
export class PasswordHistoryDetailComponent implements OnInit {
  passwordHistory: IPasswordHistory;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ passwordHistory }) => {
      this.passwordHistory = passwordHistory;
    });
  }

  previousState() {
    window.history.back();
  }
}
