import {Conversation} from '../../model/Conversation';
import {Reducer} from 'redux';
import {
  FETCH_CONVERSATIONS,
  FETCH_CONVERSATIONS_FAILED,
  FETCH_CONVERSATIONS_SUCCESS,
  GET_CONVERSATION,
  GET_CONVERSATION_FAILED,
  GET_CONVERSATION_SUCCESS
} from '../actions/conversation.action';
import {UPDATE_MESSAGES} from '../actions/message.action';
import {Message} from '../../model/Message';

export interface ConversationsState {
  readonly conversations: Map<string, Conversation>;
  readonly currentConversation: string;
  readonly isLoading: boolean;
}

const INITIAL_STATE = {
  conversations: new Map<string, Conversation>(),
  currentConversation: null,
  isLoading: false
};

export const conversationReducer:
  Reducer<ConversationsState> = (state: ConversationsState = INITIAL_STATE, action): ConversationsState => {
  switch (action.type) {
    case FETCH_CONVERSATIONS:
      return {...state, isLoading: true};
    case FETCH_CONVERSATIONS_SUCCESS:
      return {...state, ...action.payload, isLoading: false};
    case FETCH_CONVERSATIONS_FAILED:
      return {...state, isLoading: false};
    case GET_CONVERSATION:
      return {...state, isLoading: true};
    case GET_CONVERSATION_SUCCESS: {
      const conversations = new Map(state.conversations);
      conversations.set(action.payload.conversation.id, action.payload.conversation);
      return {...state, conversations, currentConversation: action.payload.conversation.id, isLoading: false};
    }
    case GET_CONVERSATION_FAILED:
      return {...state, isLoading: false};
    case UPDATE_MESSAGES: {
      const conversations = new Map(state.conversations);
      const conversation = conversations.get(action.payload.message.conversationId);
      if (conversation != null) {
        const messages: Message[] = [...conversation.conversationMessages];
        messages.unshift(action.payload.message);
        const updatedConversation = {...conversation, conversationMessages: messages};
        conversations.set(conversation.id, updatedConversation);
      }
      return {...state, conversations};
    }
    default:
      return state;
  }
};
