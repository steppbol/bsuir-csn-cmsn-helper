import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/internal/Subscription';
import {Observable} from 'rxjs';
import {isLoading, selectAccountForEdit, selectCurrentUser} from '../../store/selectors/account.selector';
import {fetchUserAction, updateAccountAction} from '../../store/actions/account.actions';
import {NgRedux, select} from '@angular-redux/store';
import {User} from '../../model/User';
import {skipWhile, take} from 'rxjs/internal/operators';
import {ActivatedRoute} from '@angular/router';
import {AppState} from '../../store';
import {AccountService} from '../../services/account.service';
import {updateRouterState} from '../../store/actions/router.actions';
import {Location} from '@angular/common';

@Component({
  selector: 'app-account-edit',
  templateUrl: './account-edit.component.html',
  styleUrls: ['./account-edit.component.css']
})
export class AccountEditComponent implements OnInit, OnDestroy {
  userId: string;
  accountForm: FormGroup;

  subscription: Subscription;

  updatedUser: User;
  isCompareDateError: boolean;

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(selectCurrentUser)
  user: Observable<User>;

  constructor(private accountService: AccountService,
              private route: ActivatedRoute,
              private ngRedux: NgRedux<AppState>,
              private formBuilder: FormBuilder,
              private location: Location,) {
  }

  ngOnInit() {
    this.isCompareDateError = false;
    this.userId = this.route.snapshot.paramMap.get('id');
    this.ngRedux.dispatch(fetchUserAction(this.userId));
    // this.ngRedux.dispatch(selectAccount(this.userId));
    this.isLoading.pipe(skipWhile(result => result), take(1))
      .subscribe(() => this.ngRedux.select(state => selectAccountForEdit(state))
        .subscribe(user => {
          this.updatedUser = user;
          this.initializeForm(user);
        }));
  }

  private initializeForm(user: User) {
    this.accountForm = this.formBuilder.group({
        firstName: [user.firstName, [Validators.required, Validators.maxLength(35), Validators.pattern(/^[A-z0-9]*$/)]],
        lastName: [user.lastName, [Validators.maxLength(35), Validators.pattern(/^[A-z0-9]*$/)]],
        age: [user.age],
        phoneNumber: [user.phoneNumber],
        image: ['']
      }
    );
  }

  updateAccount(form: FormGroup): User {
    this.updatedUser.firstName = form.getRawValue().firstName;
    this.updatedUser.lastName = form.getRawValue().lastName;
    this.updatedUser.age = form.getRawValue().age;
    this.updatedUser.phoneNumber = form.getRawValue().phoneNumber;
    this.updatedUser.image = form.getRawValue().image;
    return this.updatedUser;
  }

  updateUser(form) {
    this.ngRedux.dispatch(updateAccountAction({...this.updateAccount(form)}));
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => this.ngRedux.dispatch(updateRouterState('/account/' + this.userId)));
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  goBack() {
    this.location.back();
  }

  getImageAsString(base64textString: string) {
    this.accountForm.controls.image.setValue(base64textString);
  }
}

