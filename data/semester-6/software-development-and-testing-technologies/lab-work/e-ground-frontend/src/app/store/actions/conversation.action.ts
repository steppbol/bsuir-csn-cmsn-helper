import {Conversation} from '../../model/Conversation';

export const GET_CONVERSATION = 'GET_CONVERSATION';
export const GET_CONVERSATION_SUCCESS = 'GET_CONVERSATION_SUCCESS';
export const GET_CONVERSATION_FAILED = 'GET_CONVERSATION_FAILED';
export const FETCH_CONVERSATIONS = 'FETCH_CONVERSATIONS';
export const FETCH_CONVERSATIONS_SUCCESS = 'FETCH_CONVERSATIONS_SUCCESS';
export const FETCH_CONVERSATIONS_FAILED = 'FETCH_CONVERSATIONS_FAILED';

export function getConversationAction(yourId: string, otherId: string) {
  return {
    type: GET_CONVERSATION,
    payload: {yourId, otherId}
  };
}

export function getConversationSuccessAction(conversation: Conversation) {
  return {
    type: GET_CONVERSATION_SUCCESS,
    payload: {conversation}
  };
}

export function getConversationFailedAction(message: string) {
  return {
    type: GET_CONVERSATION_FAILED,
    error: true,
    payload: {message}
  };
}

export function fetchConversationsAction(userId: string) {
  return {
    type: FETCH_CONVERSATIONS,
    payload: {userId}
  };
}

export function fetchConversationsSuccessAction(conversations: Map<string, Conversation>) {
  return {
    type: FETCH_CONVERSATIONS_SUCCESS,
    payload: {conversations}
  };
}

export function fetchConversationsFailedAction(message: string) {
  return {
    type: FETCH_CONVERSATIONS_FAILED,
    error: true,
    payload: {message}
  };
}
