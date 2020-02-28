import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {User} from "../model/User";
import {selectCurrentUser} from "../store/selectors/current-user.selector";
import {AppState} from "../store";
import {NgRedux, select} from "@angular-redux/store";
import {updateRouterState} from "../store/actions/router.actions";

@Injectable({
  providedIn: 'root'
})
export class AccountEditGuard implements CanActivate {
  @select(selectCurrentUser)
  user: Observable<User>;

  currentUser: User;

  constructor(private ngRedux: NgRedux<AppState>, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // console.log("Id update router state", this.id);
    this.user.subscribe(value => this.currentUser = value);
    if (this.currentUser && (route.paramMap.get('id') === this.currentUser.id)) {
      return true;
    }
    //this.ngRedux.dispatch(updateRouterState('account/' + this.id));
    this.router.navigate(['account/' + route.paramMap.get('id')]);

    return false;
  }
  
}
