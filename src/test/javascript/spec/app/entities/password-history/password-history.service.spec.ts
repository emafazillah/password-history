import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PasswordHistoryService } from 'app/entities/password-history/password-history.service';
import { IPasswordHistory, PasswordHistory } from 'app/shared/model/password-history.model';

describe('Service Tests', () => {
  describe('PasswordHistory Service', () => {
    let injector: TestBed;
    let service: PasswordHistoryService;
    let httpMock: HttpTestingController;
    let elemDefault: IPasswordHistory;
    let expectedResult: IPasswordHistory | IPasswordHistory[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(PasswordHistoryService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new PasswordHistory(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a PasswordHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new PasswordHistory()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a PasswordHistory', () => {
        const returnedFromService = Object.assign(
          {
            history_no1: 'BBBBBB',
            history_no2: 'BBBBBB',
            history_no3: 'BBBBBB',
            history_no4: 'BBBBBB',
            history_no5: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of PasswordHistory', () => {
        const returnedFromService = Object.assign(
          {
            history_no1: 'BBBBBB',
            history_no2: 'BBBBBB',
            history_no3: 'BBBBBB',
            history_no4: 'BBBBBB',
            history_no5: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a PasswordHistory', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
