import { UPDATE_LOCATION } from '@angular-redux/router';

export function updateRouterState(url: string) {
  return {
    type: UPDATE_LOCATION,
    payload: url
  };
}
