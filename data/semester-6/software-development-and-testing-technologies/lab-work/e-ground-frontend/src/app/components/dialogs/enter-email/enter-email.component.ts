import {Component, Inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {isLoading, selectErrorMessage} from '../../../store/selectors/current-user.selector';
import {NgRedux, select} from '@angular-redux/store';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {DialogResult} from '../../../model/DialogResult';
import {skipWhile, take} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/index';
import {AppState} from '../../../store';
import {NgxPermissionsService} from 'ngx-permissions';
import {sendResetPasswordEmail} from '../../../store/actions/reset-password.actions';

@Component({
  selector: 'app-enter-email',
  templateUrl: './enter-email.component.html',
  styleUrls: ['./enter-email.component.css']
})
export class EnterEmailComponent implements OnInit {

  @select(isLoading)
  isLoading: Observable<boolean>;


  emailForm: FormGroup;
  returnUrl: string;
  @select(selectErrorMessage)
  error: Observable<string>;
  roleNames: string[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private ngRedux: NgRedux<AppState>,
    private route: ActivatedRoute,
    public dialogRef: MatDialogRef<EnterEmailComponent>,
    private permissionsServise: NgxPermissionsService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }


  ngOnInit() {
    this.emailForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(40)]]
    });
    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  get login(): FormControl {
    return this.emailForm.get('email') as FormControl;
  }

  getErrorText(controlName: string): string {
    const control = this.emailForm.get(controlName) as FormControl;
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
      if (control.errors.email) {
        errorMessage = 'Incorrect email';
      }
    }
    return errorMessage;
  }

  onSubmit() {
    if (this.emailForm.invalid) {
      return;
    }
    console.log(this.emailForm.get('email').value);
    this.ngRedux.dispatch(sendResetPasswordEmail(this.emailForm.get('email').value));
    this.isLoading.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() =>
        this.error.pipe(skipWhile(error => error !== null), take(1)).subscribe(() => this.onCancelClick()));

  }
}
