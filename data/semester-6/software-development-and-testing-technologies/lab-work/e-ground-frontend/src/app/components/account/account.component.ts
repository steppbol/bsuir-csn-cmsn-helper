import {Component, OnInit} from '@angular/core';
import {AppState} from '../../store';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {skipWhile, take} from 'rxjs/operators';
import {fetchUserAction} from '../../store/actions/account.actions';
import {User} from '../../model/User';
import {selectOfferAction} from '../../store/actions/catalog.actions';
import {ActivatedRoute} from '@angular/router';
import {isLoading, selectCurrentUser} from '../../store/selectors/account.selector';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

  id: string;

  constructor(private ngRedux: NgRedux<AppState>,
              private route: ActivatedRoute) {
  }

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(selectCurrentUser)
  user: Observable<User>;

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.ngRedux.dispatch(fetchUserAction(this.id));

    this.isLoading.pipe(skipWhile(result => result === true), take(1)).subscribe(() =>
      this.user.subscribe(user => {
        if (user !== null && user !== undefined) {
          if (this.ngRedux.getState().currentUserState.currentUser !== null) {
            // this.checkPermissionToEdit(startup);
          }
        }
      }));
  }

}
