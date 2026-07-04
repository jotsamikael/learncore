import { HttpContextToken } from '@angular/common/http';

export const IS_RETRY_AFTER_REFRESH = new HttpContextToken<boolean>(() => false);
