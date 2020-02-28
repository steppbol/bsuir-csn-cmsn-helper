import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {SignInComponent} from "../components/dialogs/sign-in/sign-in.component";
import {showDialogAction} from "../store/actions/dialogs.actions";
import {updateRouterState} from "../store/actions/router.actions";
import {AppState} from "../store";
import {NgRedux} from "@angular-redux/store";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private ngRedux: NgRedux<AppState>) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.ngRedux.getState().currentUserState.currentUser !== null) {
      return true;
    }
    this.ngRedux.dispatch(updateRouterState('/main-page'));
    this.ngRedux.dispatch(showDialogAction({
      componentType: SignInComponent,
      width: '500px',
      data: null
    }));


    return false;
  }
  
}
