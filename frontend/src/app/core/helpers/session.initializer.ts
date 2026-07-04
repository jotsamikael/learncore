import { APP_INITIALIZER, FactoryProvider } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { LearncoreAuthService } from '../services/learncore-auth.service';
import { environment } from '../../../environments/environment';

export function sessionInitializer(auth: LearncoreAuthService): () => Promise<unknown> {
  return () => {
    if (environment.defaultauth !== 'learncore') {
      return Promise.resolve(null);
    }
    return firstValueFrom(auth.restoreSessionIfNeeded()).catch(() => null);
  };
}

export const SESSION_INITIALIZER: FactoryProvider = {
  provide: APP_INITIALIZER,
  useFactory: sessionInitializer,
  deps: [LearncoreAuthService],
  multi: true,
};
