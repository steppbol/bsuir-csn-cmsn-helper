import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {isLoading} from '../../store/selectors/current-user.selector';
import {NgRedux, select} from '@angular-redux/store';
import {skipWhile, take} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/index';
import {AppState} from '../../store/index';
import {UserService} from '../../services/user.service';
import {ResetPasswordService} from '../../services/reset-password.service';
import {updateRouterState} from '../../store/actions/router.actions';
import {savePassword} from '../../store/actions/reset-password.actions';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  resetPasswordForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  userId: string;
  token: string;

  @select(isLoading)
  isLoading: Observable<boolean>;

  constructor(private fb: FormBuilder,
              private userService: UserService,
              private ngRedux: NgRedux<AppState>,
              private route: ActivatedRoute,
              private resetPasswordService: ResetPasswordService
  ) {
  }

  ngOnInit() {
    // Note: Below 'queryParams' can be replaced with 'params' depending on your requirements
    this.route.queryParams.subscribe(params => {
      this.userId = params['id'];
      this.token = params['token'];
    });


    this.initializeForm();
  }

  private initializeForm() {
    this.resetPasswordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]]
    }, {validator: this.checkPasswords});
  }

  checkPasswords(group: FormGroup) {
    return group.get('password').value === group.get('confirmPassword').value ? null : {notSame: true};
  }


  get password(): FormControl {
    return this.resetPasswordForm.get('password') as FormControl;
  }

  get confirmPassword(): FormControl {
    return this.resetPasswordForm.get('confirmPassword') as FormControl;
  }

  getErrorText(controlName: string): string {
    const control = this.resetPasswordForm.get(controlName) as FormControl;
    return this.getErrorMessage(control);
  }

  onCancelClick() {
  }

  private getErrorMessage(control: FormControl): string {
    let errorMessage = '';
    if (control.errors) {
      if (control.errors['required']) {
        errorMessage = 'Field is required';
      }
      if (control.errors['minlength']) {
        errorMessage = 'Min length - 6 symbols';
      }

    }
    return errorMessage;
  }

  onSubmit() {
    this.submitted = true;

    if (this.resetPasswordForm.invalid) {
      return;
    }
    // this.resetPasswordService.updatePassword(this.userId, this.token, this.resetPasswordForm.get('password').value).subscribe();
    this.ngRedux.dispatch(savePassword({id: this.userId, token: this.token , password: this.resetPasswordForm.get('password').value}));
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => this.ngRedux.dispatch(updateRouterState('/mainpage')));
    this.loading = true;
  }
}
