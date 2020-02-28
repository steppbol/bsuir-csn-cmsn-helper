import {Message} from '../../model/Message';

export const UPDATE_MESSAGES = 'UPDATE_MESSAGES';

export function updateMessagesAction(message: Message) {
  return {
    type: UPDATE_MESSAGES,
    payload: {message}
  };
}
