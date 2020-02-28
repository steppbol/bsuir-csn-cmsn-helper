import { TestBed } from '@angular/core/testing';

import { ConversationService } from './conversation.service';

describe('ConversationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ConversationService = TestBed.get(ConversationService);
    expect(service).toBeTruthy();
  });
});
