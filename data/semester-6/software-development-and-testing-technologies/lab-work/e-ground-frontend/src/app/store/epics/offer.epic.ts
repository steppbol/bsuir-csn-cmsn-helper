import {Injectable} from '@angular/core';
import {CatalogService} from '../../services/catalog.service';
import {NgRedux} from '@angular-redux/store';
import {AppState} from '../index';
import {ActionsObservable} from 'redux-observable';
import {AnyAction} from 'redux';
import {catchError, map, switchMap} from 'rxjs/operators';
import {
  CREATE_OFFER,
  createOfferFailedAction,
  createOfferSuccessAction,
  FETCH_COMMENTS,
  fetchCommentsFailedAction,
  fetchCommentsSuccessAction
} from '../actions/offer.actions';
import {of} from 'rxjs';
import {SELECT_OFFER, selectOfferFailedAction, selectOfferSuccessAction} from '../actions/catalog.actions';
import {OfferService} from '../../services/offer.service';
import {defaultOffer} from '../../model/Offer';
import {NotifierService} from 'angular-notifier';

@Injectable()
export class OfferEpic {
  constructor(private catalogService: CatalogService,
              private ngRedux: NgRedux<AppState>,
              private offerService: OfferService,
              private notifierService: NotifierService) {
  }

  createOffer$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(CREATE_OFFER).pipe(
      switchMap(({payload}) => {
        return this.catalogService
          .createOffer(payload.offer)
          .pipe(
            map(offer => {
              return createOfferSuccessAction(offer);
            }),
            catchError(error => {
              this.notifierService.notify('error', 'Create offer failed');
              return of(createOfferFailedAction(error));
            })
          );
      })
    );
  };

  selectOffer$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(SELECT_OFFER).pipe(
      switchMap(({payload}) => {
        return payload.offerId !== null ?
          this.offerService
            .getOfferById(payload.offerId)
            .pipe(
              map(offer => selectOfferSuccessAction(offer)),
              catchError(error => {
                this.notifierService.notify('error', 'Select offer failed');
                return of(selectOfferFailedAction(error));
              })
            )
          : of(defaultOffer)
            .pipe(
              map(offer => selectOfferSuccessAction(offer)),
              catchError(error => {
                this.notifierService.notify('error', 'Select offer failed');
                return of(selectOfferFailedAction(error));
              })
            );
      })
    );
  };

  fetchComments$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(FETCH_COMMENTS).pipe(
      switchMap(({payload}) => {
          return this.offerService
            .getOfferComments(payload.offerId)
            .pipe(
              map(comments => fetchCommentsSuccessAction(comments)),
              catchError(error => of(fetchCommentsFailedAction(error)))
            );
        }
      )
    );
  };
}

