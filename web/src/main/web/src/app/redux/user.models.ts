import {makeTypedFactory, TypedRecord} from 'typed-immutable-record';

interface IUser {
  userId: string,
  phoneNumber: string,
  email: string,
  picture: string
}

export interface IUserRecord extends TypedRecord<IUserRecord>, IUser {
}

export const createUser = makeTypedFactory<IUser, IUserRecord>({
  userId: '',
  phoneNumber: '',
  email: '',
  picture: ''
});
