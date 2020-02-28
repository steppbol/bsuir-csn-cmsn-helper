import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {AppState} from '../../store';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {skipWhile, take} from 'rxjs/operators';
import {updateRouterState} from '../../store/actions/router.actions';
import {createOfferAction} from '../../store/actions/offer.actions';
import {Offer} from '../../model/Offer';
import {ActivatedRoute} from '@angular/router';
import {isSelected} from '../../store/selectors/offer.selector';

@Component({
  selector: 'app-offer-edit',
  templateUrl: './offer-edit.component.html',
  styleUrls: ['./offer-edit.component.css']
})
export class OfferEditComponent implements OnInit {

  offerForm: FormGroup;

  @select(isSelected)
  isSelected: Observable<boolean>;

  constructor(private ngRedux: NgRedux<AppState>,
              private route: ActivatedRoute,
              private fb: FormBuilder
  ) {
  }

  ngOnInit() {
    this.offerForm = this.fb.group({
        name: ['', Validators.required],
        category: ['', Validators.required],
        description: ['', Validators.required],
        price: ['', Validators.pattern('[1-9][0-9]{0,4}')],
        image: ['']
      }
    );
  }

  private initializeForm(offer: Offer) {
    this.offerForm = this.fb.group({
        name: [offer.name, Validators.required],
        category: [offer.category, Validators.required],
        description: ['', Validators.required],
        price: [offer.price, Validators.pattern('[1-9][0-9]{0,4}')],
        image: ['']
      }
    );
  }

  createOffer() {
    this.ngRedux.dispatch(createOfferAction({
      ...this.offerForm.value,
      sellerId: this.ngRedux.getState().currentUserState.currentUser.id
    }));
    this.isSelected.pipe(skipWhile(result => result === true), take(1))
      .subscribe(() => {
        this.ngRedux.dispatch(updateRouterState('/catalog'));
      });
  }

  get price(): FormControl {
    return this.offerForm.get('price') as FormControl;
  }

  get description(): FormControl {
    return this.offerForm.get('description') as FormControl;
  }

  get name(): FormControl {
    return this.offerForm.get('name') as FormControl;
  }

  get category(): FormControl {
    return this.offerForm.get('category') as FormControl;
  }

  getImageAsString(base64textString: string) {
    this.offerForm.controls.image.setValue(base64textString);
  }
}
