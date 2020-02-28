import {Offer} from '../../model/Offer';
import {Comment} from '../../model/Comment';

export const CREATE_OFFER = 'CREATE_OFFER';
export const CREATE_OFFER_SUCCESS = 'CREATE_OFFER_SUCCESS';
export const CREATE_OFFER_FAILED = 'CREATE_OFFER_FAILED';
export const FETCH_COMMENTS = 'FETCH_COMMENTS';
export const FETCH_COMMENTS_SUCCESS = 'FETCH_COMMENTS_SUCCESS';
export const FETCH_COMMENTS_FAILED = 'FETCH_COMMENTS_FAILED';

export function createOfferAction(offer: Offer) {
  return {
    type: CREATE_OFFER,
    payload: {offer}
  };
}

export function createOfferSuccessAction(offer: Offer) {
  return {
    type: CREATE_OFFER_SUCCESS,
    payload: {offer}
  };
}

export function createOfferFailedAction(errorMessage: string) {
  return {
    type: CREATE_OFFER_FAILED,
    error: true,
    payload: {errorMessage}
  };
}

export function fetchCommentsAction(offerId: string) {
  return {
    type: FETCH_COMMENTS,
    payload: {offerId}
  };
}

export function fetchCommentsSuccessAction(comments: Comment[]) {
  return {
    type: FETCH_COMMENTS_SUCCESS,
    payload: {comments}
  };
}

export function fetchCommentsFailedAction(errorMessage: string) {
  return {
    type: FETCH_COMMENTS_FAILED,
    error: true,
    payload: {errorMessage}
  };
}
