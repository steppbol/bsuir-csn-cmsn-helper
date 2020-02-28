import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {NgRedux, select} from "@angular-redux/store";
import {AppState} from "../store";
import {selectOffer} from "../store/selectors/offer.selector";
import {Offer} from "../model/Offer";
import {selectCurrentUser} from "../store/selectors/current-user.selector";
import {User} from "../model/User";

@Injectable({
  providedIn: 'root'
})
export class OfferEditGuard implements CanActivate {
  @select(selectOffer)
  offer: Observable<Offer>;

  @select(selectCurrentUser)
  user: Observable<User>;

  currentUser: User;

  currentOffer: Offer;

  constructor(private ngRedux: NgRedux<AppState>, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // console.log("Id update router state", this.id);
    this.offer.subscribe(value => this.currentOffer = value);
    this.user.subscribe(value => this.currentUser = value);
    //if (this.currentOffer && (this.currentUser.id === this.currentOffer.userId)) {
    if (this.currentOffer) {
      return true;
    }
    //this.ngRedux.dispatch(updateRouterState('account/' + this.id));
    this.router.navigate(['offer/' + route.paramMap.get('id')]);

    return false;
  }
  
}
