import { BaseEntity } from './../../shared';

export class PasswordHistory implements BaseEntity {
    constructor(
        public id?: number,
        public history_no1?: string,
        public history_no2?: string,
        public history_no3?: string,
        public history_no4?: string,
        public history_no5?: string,
        public userId?: number,
    ) {
    }
}
