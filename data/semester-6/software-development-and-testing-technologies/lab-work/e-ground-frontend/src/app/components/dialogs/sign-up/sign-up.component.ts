import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../../services/user.service';
import {first, skipWhile, take} from 'rxjs/internal/operators';
import {DialogResult} from '../../../model/DialogResult';
import {NgRedux, select} from '@angular-redux/store';
import {AppState} from '../../../store';
import {NotifierService} from 'angular-notifier';
import {RegistrationData} from '../../../model/RegistrationData';
import {isLoading, selectErrorMessage} from '../../../store/selectors/user.selector';
import {Observable} from 'rxjs';
import {isLoading as loginIsLoading, selectErrorMessage as selectLoginErrorMessage} from '../../../store/selectors/current-user.selector';
import {loginUserAction} from '../../../store/actions/current-user.actions';
import {Credential} from '../../../model/Credential';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent implements OnInit {

  registerForm: FormGroup;
  registrationData: RegistrationData;
  loading = false;
  submitted = false;
  error = '';

  @select(selectLoginErrorMessage)
  loginError: Observable<string>;

  @select(selectErrorMessage)
  registrateError: Observable<string>;

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(loginIsLoading)
  loginLoading: Observable<boolean>;

  constructor(private fb: FormBuilder,
              public dialogRef: MatDialogRef<SignUpComponent>,
              private userService: UserService,
              private ngRedux: NgRedux<AppState>,
              private notifier: NotifierService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit() {
    this.initializeForm();
  }

  private initializeForm() {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.maxLength(40)]],
      lastName: ['', [Validators.maxLength(40)]],
      age: ['', [Validators.maxLength(3)]],
      phoneNumber: ['', [Validators.minLength(6), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(40)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]]
    }, {validator: this.checkPasswords});
  }

  checkPasswords(group: FormGroup) {
    return group.get('password').value === group.get('confirmPassword').value ? null : {notSame: true};
  }

  get firstName(): FormControl {
    return this.registerForm.get('firstName') as FormControl;
  }

  get lastName(): FormControl {
    return this.registerForm.get('lastName') as FormControl;
  }

  get age(): FormControl {
    return this.registerForm.get('age') as FormControl;
  }

  get phoneNumber(): FormControl {
    return this.registerForm.get('phoneNumber') as FormControl;
  }

  get email(): FormControl {
    return this.registerForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.registerForm.get('password') as FormControl;
  }

  get confirmPassword(): FormControl {
    return this.registerForm.get('confirmPassword') as FormControl;
  }

  getErrorText(controlName: string): string {
    const control = this.registerForm.get(controlName) as FormControl;
    return this.getErrorMessage(control);
  }

  onCancelClick() {
    this.dialogRef.close(DialogResult.CLOSE);
  }

  private createCredentialForm(): Credential {
    return {
      email: this.registerForm.get('email').value,
      password: this.registerForm.get('password').value
    };
  }

  private convertFromFormToUser(): RegistrationData {
    return {
      id: '',
      email: this.registerForm.get('email').value,
      password: this.registerForm.get('password').value,
      firstName: this.registerForm.get('firstName').value as string,
      lastName: this.registerForm.get('lastName').value as string,
      age: this.registerForm.get('age').value as number,
      phoneNumber: this.registerForm.get('phoneNumber').value as string
    };
  }

  private getErrorMessage(control: FormControl): string {
    let errorMessage = '';
    if (control.errors) {
      if (control.errors.required) {
        errorMessage = 'Field is required';
      }
      if (control.errors.email) {
        errorMessage = 'Incorrect email';
      }
      if (control.errors.minlength) {
        errorMessage = 'Min length - 6 symbols';
      }
    }
    return errorMessage;
  }

  login() {
    console.log('I am in login now');
    this.ngRedux.dispatch(loginUserAction(this.createCredentialForm()));
    this.loginLoading.pipe(skipWhile(result => result === true), take(1)).subscribe(() =>
      this.loginError.pipe(skipWhile(error => error !== null), take(1)).subscribe(() => this.onCancelClick()));
  }

  onSubmit() {
    this.submitted = true;

    if (this.registerForm.invalid) {
      return;
    }
    this.loading = true;
    this.userService.register(this.convertFromFormToUser())
      .pipe(first())
      .subscribe(
        () => {
          this.notifier.notify('success', 'Registration successful. Check your email to activate your account!');
          this.onCancelClick();
        },
        error => {
          console.log(error);
          this.error = error;
          this.loading = false;
        });
    // this.ngRedux.dispatch(createUserAction(this.convertFromFormToUser()));
    // this.isLoading.pipe(skipWhile(result => result === true), take(1)).subscribe(() => console.log('asd'));
  }

}


