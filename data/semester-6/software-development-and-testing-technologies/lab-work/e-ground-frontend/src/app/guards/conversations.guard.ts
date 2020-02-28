import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {NgRedux, select} from "@angular-redux/store";
import {selectCurrentUser} from "../store/selectors/current-user.selector";
import {User} from "../model/User";
import {AppState} from "../store";

@Injectable({
  providedIn: 'root'
})
export class ConversationsGuard implements CanActivate {
  @select(selectCurrentUser)
  user: Observable<User>;

  currentUser: User;

  constructor(private ngRedux: NgRedux<AppState>, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.user.subscribe(value => this.currentUser = value);
    if (this.currentUser && (route.paramMap.get('id') === this.currentUser.id)) {
      return true;
    }
    this.router.navigate(['account/' + route.paramMap.get('id')]);

    return false;
  }
}
