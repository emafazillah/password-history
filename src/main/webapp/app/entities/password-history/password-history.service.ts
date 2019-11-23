import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../../app.constants';

import { PasswordHistory } from './password-history.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class PasswordHistoryService {

    private resourceUrl = SERVER_API_URL + 'api/password-histories';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/password-histories';

    constructor(private http: Http) { }

    create(passwordHistory: PasswordHistory): Observable<PasswordHistory> {
        const copy = this.convert(passwordHistory);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(passwordHistory: PasswordHistory): Observable<PasswordHistory> {
        const copy = this.convert(passwordHistory);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<PasswordHistory> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
            .map((res: any) => this.convertResponse(res));
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(passwordHistory: PasswordHistory): PasswordHistory {
        const copy: PasswordHistory = Object.assign({}, passwordHistory);
        return copy;
    }
}
