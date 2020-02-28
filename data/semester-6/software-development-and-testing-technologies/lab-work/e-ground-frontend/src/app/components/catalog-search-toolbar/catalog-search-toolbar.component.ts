import {Component, OnInit} from '@angular/core';
import {AppState} from '../../store';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {updateCatalogSearchParamsAction} from '../../store/actions/catalog-search-toolbar.actions';
import {selectCatalogSearchParams} from '../../store/selectors/catalog-search-toolbar.selector';
import {searchOffersAction} from '../../store/actions/catalog.actions';

@Component({
  selector: 'app-catalog-search-toolbar',
  templateUrl: './catalog-search-toolbar.component.html',
  styleUrls: ['./catalog-search-toolbar.component.css']
})
export class CatalogSearchToolbarComponent implements OnInit {
  @select(selectCatalogSearchParams)
  searchParams: Observable<string>;

  constructor(private ngRedux: NgRedux<AppState>) {
  }

  ngOnInit() {
  }

  search(name: string) {
    this.ngRedux.dispatch(searchOffersAction(name.trim()));
    this.ngRedux.dispatch(updateCatalogSearchParamsAction(name.trim()));
  }
}
