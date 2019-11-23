export interface IPasswordHistory {
  id?: number;
  history_no1?: string;
  history_no2?: string;
  history_no3?: string;
  history_no4?: string;
  history_no5?: string;
  userLogin?: string;
  userId?: number;
}

export class PasswordHistory implements IPasswordHistory {
  constructor(
    public id?: number,
    public history_no1?: string,
    public history_no2?: string,
    public history_no3?: string,
    public history_no4?: string,
    public history_no5?: string,
    public userLogin?: string,
    public userId?: number
  ) {}
}
