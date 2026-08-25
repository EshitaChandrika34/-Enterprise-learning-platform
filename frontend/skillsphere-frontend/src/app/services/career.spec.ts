import { TestBed } from '@angular/core/testing';

import { Career } from './career';

describe('Career', () => {
  let service: Career;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Career);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
