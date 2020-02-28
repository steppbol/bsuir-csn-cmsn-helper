import {Injectable} from '@angular/core';
import {CatalogService} from '../../services/catalog.service';
import {NgRedux} from '@angular-redux/store';
import {AppState} from '../index';
import {ActionsObservable} from 'redux-observable';
import {AnyAction} from 'redux';
import {catchError, map, switchMap} from 'rxjs/operators';
import {
  FETCH_OFFERS,
  fetchOffersFailedAction,
  fetchOffersSuccessAction,
  SEARCH_OFFERS,
  searchOffersFailedAction,
  searchOffersSuccessAction
} from '../actions/catalog.actions';
import {TransformService} from '../../utils/transform.service';
import {of} from 'rxjs';
import {NotifierService} from 'angular-notifier';


@Injectable()
export class CatalogEpic {
  constructor(private catalogService: CatalogService,
              private ngRedux: NgRedux<AppState>,
              private notifierService: NotifierService) {
  }

  fetchOffers$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(FETCH_OFFERS).pipe(
      switchMap(({}) => {
        return this.catalogService.getCatalogList().pipe(
           map(offers => fetchOffersSuccessAction(TransformService.transformToMap(offers))),
            catchError(error => {
              this.notifierService.notify('error', 'Error while fetch offers');
              return of(fetchOffersFailedAction(error));
            })
        );
      })
    );
  }

  searchOffers$ = (action$: ActionsObservable<AnyAction>) => {
    return action$.ofType(SEARCH_OFFERS).pipe(
      switchMap(({payload}) => {
        return this.catalogService
          .searchInCatalog(payload.firstName)
          .pipe(
            map((offers) => searchOffersSuccessAction(TransformService.transformToMap(offers))),
            catchError(error => {
              this.notifierService.notify('error', 'Error while search offers');
              return of(searchOffersFailedAction(error));
            })
          );
      })
    );
  }
}
