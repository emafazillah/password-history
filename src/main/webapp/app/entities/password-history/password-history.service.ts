import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IPasswordHistory } from 'app/shared/model/password-history.model';

type EntityResponseType = HttpResponse<IPasswordHistory>;
type EntityArrayResponseType = HttpResponse<IPasswordHistory[]>;

@Injectable({ providedIn: 'root' })
export class PasswordHistoryService {
  public resourceUrl = SERVER_API_URL + 'api/password-histories';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/password-histories';

  constructor(protected http: HttpClient) {}

  create(passwordHistory: IPasswordHistory): Observable<EntityResponseType> {
    return this.http.post<IPasswordHistory>(this.resourceUrl, passwordHistory, { observe: 'response' });
  }

  update(passwordHistory: IPasswordHistory): Observable<EntityResponseType> {
    return this.http.put<IPasswordHistory>(this.resourceUrl, passwordHistory, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPasswordHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPasswordHistory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPasswordHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
