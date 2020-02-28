import { TestBed } from '@angular/core/testing';

import { ChatServerService } from './chat-server.service';

describe('ChatServerService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChatServerService = TestBed.get(ChatServerService);
    expect(service).toBeTruthy();
  });
});
