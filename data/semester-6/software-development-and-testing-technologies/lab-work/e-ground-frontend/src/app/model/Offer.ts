export interface Offer {
  id: string;
  name: string;
  category: string;
  price: number;
  description: string;
  sellerId: string;
  image: string;
  compressedImageId: string;
  imageId: string;
}

export const defaultOffer: Offer = {
  id: null,
  name: '',
  category: '',
  price: 0,
  description: '',
  sellerId: null,
  image: null,
  compressedImageId: null,
  imageId: null
};
