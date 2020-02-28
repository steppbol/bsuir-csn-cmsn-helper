import { DialogProperty } from '../reducers/dialogs.reducer';

export const SHOW_DIALOG = '[Dialogs] Show dialog';
export const CLOSE_DIALOG = '[Dialogs] Close dialog';

export function showDialogAction(dialogProperty: DialogProperty) {
  return {
    type: SHOW_DIALOG,
    payload: {dialogProperty}
  };
}

export function closeDialogAction() {
  return {
    type: CLOSE_DIALOG
  };
}
