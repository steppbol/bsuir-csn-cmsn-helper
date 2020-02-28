import { TestBed, async, inject } from '@angular/core/testing';

import { ConversationsGuard } from './conversations.guard';

describe('ConversationsGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ConversationsGuard]
    });
  });

  it('should ...', inject([ConversationsGuard], (guard: ConversationsGuard) => {
    expect(guard).toBeTruthy();
  }));
});
