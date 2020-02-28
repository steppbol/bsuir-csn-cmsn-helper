import {Component, OnInit} from '@angular/core';
import {NgRedux, select} from '@angular-redux/store';
import {Observable} from 'rxjs';
import {selectCurrentUser} from '../../store/selectors/current-user.selector';
import {User} from '../../model/User';
import {Offer} from '../../model/Offer';
import {AppState} from '../../store';
import {ActivatedRoute} from '@angular/router';
import {skipWhile, take} from 'rxjs/operators';
import {isSelected, selectOffer} from '../../store/selectors/offer.selector';
import {selectOfferAction} from '../../store/actions/catalog.actions';


@Component({
  selector: 'app-offer',
  templateUrl: './offer.component.html',
  styleUrls: ['./offer.component.css']
})
export class OfferComponent implements OnInit {

  id: string;

  @select(isSelected)
  isSelected: Observable<boolean>;

  @select(selectOffer)
  offer: Observable<Offer>;

  @select(selectCurrentUser)
  currentUser: Observable<User>;

  permissionToEdit = false;

  constructor(private ngRedux: NgRedux<AppState>,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.ngRedux.dispatch(selectOfferAction(this.id));

    this.isSelected.pipe(skipWhile(result => result === true), take(1)).subscribe(() =>
      this.offer.subscribe(offer => {
        if (offer !== null && offer !== undefined) {
          if (this.ngRedux.getState().currentUserState.currentUser !== null) {
            // this.checkPermissionToEdit(startup);
          }
        }
      }));
  }

  checkPermissionToEdit(offer: Offer) {
    /*this.permissionToEdit = startup.startupRoles
      .find(value => value.accountId ===
        this.ngRedux.getState().currentUserState.selectCurrentUser.profile.id && value.roleName === 'MODERATOR') !== undefined;*/
  }

  get currentUserAccountId(): string {
    return this.ngRedux.getState().currentUserState.currentUser.id;
  }

  get currentOffer(): Offer {
    return this.ngRedux.getState().offerPageState.offer;
  }

}
