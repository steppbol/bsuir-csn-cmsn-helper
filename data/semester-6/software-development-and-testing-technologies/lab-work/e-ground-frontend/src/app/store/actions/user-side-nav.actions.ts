export const SHOW_USER_SIDE_NAV = '[UserSideNav] Show user side navigation bar';
export const HIDE_USER_SIDE_NAV = '[UserSideNav] Hide user side navigation bar';

export function showUserSideNavAction() {
  return {
    type: SHOW_USER_SIDE_NAV
  };
}

export function hideUserSideNavAction() {
  return {
    type: HIDE_USER_SIDE_NAV
  };
}
