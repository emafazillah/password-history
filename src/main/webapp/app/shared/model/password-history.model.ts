export interface IPasswordHistory {
  id?: number;
  historyNo1?: string;
  historyNo2?: string;
  historyNo3?: string;
  historyNo4?: string;
  historyNo5?: string;
  userLogin?: string;
  userId?: number;
}

export class PasswordHistory implements IPasswordHistory {
  constructor(
    public id?: number,
    public historyNo1?: string,
    public historyNo2?: string,
    public historyNo3?: string,
    public historyNo4?: string,
    public historyNo5?: string,
    public userLogin?: string,
    public userId?: number
  ) {}
}
