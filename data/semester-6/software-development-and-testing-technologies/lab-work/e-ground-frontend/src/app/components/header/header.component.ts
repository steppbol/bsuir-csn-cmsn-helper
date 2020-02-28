import {NgxPermissionsService} from 'ngx-permissions';
import {Component, OnInit} from '@angular/core';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {User} from '../../model/User';
import {AppState} from '../../store';
import {selectCurrentUser} from '../../store/selectors/current-user.selector';
import {SignInComponent} from '../dialogs/sign-in/sign-in.component';
import {logoutUserAction} from '../../store/actions/current-user.actions';
import {updateRouterState} from '../../store/actions/router.actions';
import {SignUpComponent} from '../dialogs/sign-up/sign-up.component';
import {showDialogAction} from '../../store/actions/dialogs.actions';
import {hideUserSideNavAction} from '../../store/actions/user-side-nav.actions';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  ls = localStorage;

  @select(selectCurrentUser)
  currentUser: Observable<User>;

  navLinks = [{
    path: '/catalog',
    label: 'Catalog',
    isActive: true
  }];

  constructor(private translate: TranslateService,
              private ngRedux: NgRedux<AppState>,
              private permissionsServive: NgxPermissionsService) {
    translate.setDefaultLang('en');
  }

  switchLanguage() {
    this.translate.use(this.translate.currentLang === "ru" ? "en" : "ru");
  }

  ngOnInit() {
    //this.currentLanguage.subscribe(result => this.translate.use(result === "ru" ? "en" : "ru"));
    /* this.currentUser.pipe(skipWhile(result => result === true), take(1))
       .subscribe(() =>
         this.error.pipe(skipWhile(error => error !== null), take(1)).subscribe(() => this.onCancelClick()));*/
  }

  openSingIn(): void {
    this.ngRedux.dispatch(showDialogAction({
      componentType: SignInComponent,
      width: '500px',
      data: null
    }));

  }

  openSingUp(): void {
    this.ngRedux.dispatch(showDialogAction({
      componentType: SignUpComponent,
      width: '500px',
      data: null
    }));
  }

  logout() {
    this.ls.clear();
    this.ngRedux.dispatch(hideUserSideNavAction());
    this.ngRedux.dispatch(logoutUserAction());
    this.ngRedux.dispatch(updateRouterState('/main'));
    this.permissionsServive.flushPermissions();
  }

}
