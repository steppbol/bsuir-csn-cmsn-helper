import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {skipWhile, take} from 'rxjs/internal/operators';
import {DialogResult} from '../../../model/DialogResult';
import {Credential} from '../../../model/Credential';
import {NgRedux, select} from '@angular-redux/store';
import {AppState} from '../../../store';
import {loginUserAction} from '../../../store/actions/current-user.actions';
import {isLoading, selectErrorMessage} from '../../../store/selectors/current-user.selector';
import {Observable} from 'rxjs';
import {NgxPermissionsService} from 'ngx-permissions';
import {closeDialogAction, showDialogAction} from '../../../store/actions/dialogs.actions';
import {EnterEmailComponent} from '../enter-email/enter-email.component';


@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent implements OnInit {

  @select(isLoading)
  isLoading: Observable<boolean>;

  @select(selectErrorMessage)
  error: Observable<string>;

  credentialForm: FormGroup;
  returnUrl: string;

  constructor(
    private formBuilder: FormBuilder,
    private ngRedux: NgRedux<AppState>,
    private route: ActivatedRoute,
    public dialogRef: MatDialogRef<SignInComponent>,
    private permissionsServise: NgxPermissionsService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }


  ngOnInit() {
    this.credentialForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(40)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]]
    });

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  get email(): FormControl {
    return this.credentialForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.credentialForm.get('password') as FormControl;
  }

  getErrorText(controlName: string): string {
    const control = this.credentialForm.get(controlName) as FormControl;
    return this.getErrorMessage(control);
  }

  onCancelClick() {
    this.dialogRef.close(DialogResult.CLOSE);
  }

  private getErrorMessage(control: FormControl): string {
    let errorMessage = '';
    if (control.errors) {
      if (control.errors.required) {
        errorMessage = 'Field is required';
      }
      if (control.errors.minlength) {
        errorMessage = 'Min length - 6 symbols';
      }
    }
    return errorMessage;
  }

  onSubmit() {
    if (this.credentialForm.invalid) {
      return;
    }

    this.ngRedux.dispatch(loginUserAction(this.credentialForm.value as Credential));
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() =>
        this.error.pipe(skipWhile(error => error !== null), take(1)).subscribe(() => this.onCancelClick()));
  }

  forgotPassword() {
    this.ngRedux.dispatch(closeDialogAction());
    this.ngRedux.dispatch(showDialogAction({
      componentType: EnterEmailComponent,
      width: '500px',
      data: null
    }));
  }
}


