import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IPasswordHistory, PasswordHistory } from 'app/shared/model/password-history.model';
import { PasswordHistoryService } from './password-history.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-password-history-update',
  templateUrl: './password-history-update.component.html',
})
export class PasswordHistoryUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    history_no1: [],
    history_no2: [],
    history_no3: [],
    history_no4: [],
    history_no5: [],
    userId: [],
  });

  constructor(
    protected passwordHistoryService: PasswordHistoryService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ passwordHistory }) => {
      this.updateForm(passwordHistory);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(passwordHistory: IPasswordHistory): void {
    this.editForm.patchValue({
      id: passwordHistory.id,
      history_no1: passwordHistory.history_no1,
      history_no2: passwordHistory.history_no2,
      history_no3: passwordHistory.history_no3,
      history_no4: passwordHistory.history_no4,
      history_no5: passwordHistory.history_no5,
      userId: passwordHistory.userId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const passwordHistory = this.createFromForm();
    if (passwordHistory.id !== undefined) {
      this.subscribeToSaveResponse(this.passwordHistoryService.update(passwordHistory));
    } else {
      this.subscribeToSaveResponse(this.passwordHistoryService.create(passwordHistory));
    }
  }

  private createFromForm(): IPasswordHistory {
    return {
      ...new PasswordHistory(),
      id: this.editForm.get(['id'])!.value,
      history_no1: this.editForm.get(['history_no1'])!.value,
      history_no2: this.editForm.get(['history_no2'])!.value,
      history_no3: this.editForm.get(['history_no3'])!.value,
      history_no4: this.editForm.get(['history_no4'])!.value,
      history_no5: this.editForm.get(['history_no5'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPasswordHistory>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
