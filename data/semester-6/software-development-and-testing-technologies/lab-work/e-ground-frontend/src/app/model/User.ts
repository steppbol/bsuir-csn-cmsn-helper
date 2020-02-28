export class User {
  id: string;
  firstName: string;
  lastName: string;
  age: number;
  phoneNumber: string;
  password?: string;
  email?: string;
  image: string;
  compressedImageId: string;
  imageId: string;
}

export const defaultUser: User = {
  id: null,
  firstName: '',
  lastName: '',
  age: 0,
  phoneNumber: '',
  password: '',
  email: '',
  compressedImageId: null,
  imageId: null,
  image: null
};
