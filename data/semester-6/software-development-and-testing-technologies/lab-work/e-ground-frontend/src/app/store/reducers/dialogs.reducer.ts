import { Type } from '@angular/core';
import { Reducer } from 'redux';
import { SHOW_DIALOG, CLOSE_DIALOG } from '../actions/dialogs.actions';

export interface DialogProperty {
  readonly componentType: Type<any>;
  readonly width?: string;
  readonly height?: string;
  readonly data?: any;
}

export interface DialogState {
  readonly isDialogOpen: boolean;
  readonly dialogProperty: DialogProperty;
}

const INITIAL_STATE = {
  isDialogOpen: false,
  dialogProperty: {
    componentType: null,
    width: '400px',
    height: '300px',
    data: null
  }
};

export const dialogStateReducer: Reducer<DialogState> = (state: DialogState = INITIAL_STATE, action): DialogState => {
  switch (action.type) {
    case SHOW_DIALOG: {
      return {...state, ...action.payload, isDialogOpen: true};
    }
    case CLOSE_DIALOG: {
      return INITIAL_STATE;
    }
    default: {
      return state;
    }
  }
};
