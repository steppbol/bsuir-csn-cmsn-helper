import {Component, OnInit} from '@angular/core';
import {Offer} from '../../model/Offer';
import {AppState} from '../../store';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {skipWhile, take} from 'rxjs/operators';
import {selectOffers, isLoading} from '../../store/selectors/catalog.selector';
import {fetchOffersAction} from '../../store/actions/catalog.actions';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit {

  constructor(private ngRedux: NgRedux<AppState>) {
  }

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(selectOffers)
  offerList: Observable<Offer[]>;

  ngOnInit() {
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => this.ngRedux.dispatch(fetchOffersAction()));
  }

  selectOffer(id: string) {
  }
}
