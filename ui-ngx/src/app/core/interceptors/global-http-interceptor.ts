

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { Inject, Injectable } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Constants } from '@shared/models/constants';
import { InterceptorHttpParams } from './interceptor-http-params';
import { catchError, delay, finalize, mergeMap, switchMap } from 'rxjs/operators';
import { of, throwError } from 'rxjs';
import { InterceptorConfig } from './interceptor-config';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ActionLoadFinish, ActionLoadStart } from './load.actions';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { parseHttpErrorMessage } from '@core/utils';

const tmpHeaders = {};

@Injectable()
export class GlobalHttpInterceptor implements HttpInterceptor {

  private AUTH_SCHEME = 'Bearer ';
  private AUTH_HEADER_NAME = 'X-Authorization';

  private internalUrlPrefixes = [
    '/api/auth/token',
    '/api/rpc'
  ];

  private activeRequests = 0;

  constructor(@Inject(Store) private store: Store<AppState>,
              @Inject(DialogService) private dialogService: DialogService,
              @Inject(TranslateService) private translate: TranslateService,
              @Inject(AuthService) private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.startsWith('/api/')) {
      const config = this.getInterceptorConfig(req);
      this.updateLoadingState(config, true);
      let observable$: Observable<HttpEvent<any>>;
      if (this.isTokenBasedAuthEntryPoint(req.url)) {
        if (!AuthService.getJwtToken() && !this.authService.refreshTokenPending()) {
          observable$ = this.handleResponseError(req, next, new HttpErrorResponse({error: {message: 'Unauthorized!'}, status: 401}));
        } else if (!AuthService.isJwtTokenValid()) {
          observable$ = this.handleResponseError(req, next, new HttpErrorResponse({error: {refreshTokenPending: true}}));
        } else {
          observable$ = this.jwtIntercept(req, next);
        }
      } else {
        observable$ = this.handleRequest(req, next);
      }
      return observable$.pipe(
        finalize(() => {
          if (req.url.startsWith('/api/')) {
            this.updateLoadingState(config, false);
          }
        })
      );
    } else {
      return next.handle(req);
    }
  }

  private jwtIntercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const newReq = this.updateAuthorizationHeader(req);
    if (newReq) {
      return this.handleRequest(newReq, next);
    } else {
      return throwError(() => new Error('Could not get JWT token from store.'));
    }
  }

  private handleRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err) => {
        const errorResponse = err as HttpErrorResponse;
        return this.handleResponseError(req, next, errorResponse);
      }));
  }

  private handleResponseError(req: HttpRequest<any>, next: HttpHandler, errorResponse: HttpErrorResponse): Observable<HttpEvent<any>> {
    const config = this.getInterceptorConfig(req);
    let unhandled = false;
    const ignoreErrors = config.ignoreErrors;
    const resendRequest = config.resendRequest;
    const errorCode = errorResponse.error ? errorResponse.error.errorCode : null;
    if (errorResponse.error && errorResponse.error.refreshTokenPending ||
      errorResponse.status === 401 && req.url !== Constants.entryPoints.tokenRefresh) {
      if (errorResponse.error && errorResponse.error.refreshTokenPending ||
          errorCode && errorCode === Constants.serverErrorCode.jwtTokenExpired) {
          return this.refreshTokenAndRetry(req, next);
      } else if (errorCode !== Constants.serverErrorCode.credentialsExpired) {
        unhandled = true;
      }
    } else if (errorResponse.status === 429) {
      if (resendRequest) {
        return this.retryRequest(req, next);
      }
    } else if (errorResponse.status === 403) {
      if (!ignoreErrors) {
        this.dialogService.forbidden();
      }
    } else if (errorResponse.status === 0 || errorResponse.status === -1) {
        this.showError('Unable to connect');
    } else if (!(req.url.startsWith('/api/rpc') || req.url.startsWith('/api/plugins/rpc'))) {
      if (errorResponse.status === 404) {
        if (!ignoreErrors) {
          this.showError(req.method + ': ' + req.url + '<br/>' +
            errorResponse.status + ': ' + errorResponse.statusText);
        }
      } else {
        unhandled = true;
      }
    }

    if (unhandled && !ignoreErrors) {
      const errorMessageWithTimeout = parseHttpErrorMessage(errorResponse, this.translate, req.responseType);
      this.showError(errorMessageWithTimeout.message, errorMessageWithTimeout.timeout);
    }
    return throwError(() => errorResponse);
  }

  private retryRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const thisTimeout =  1000 + Math.random() * 3000;
    return of(null).pipe(
      delay(thisTimeout),
      mergeMap(() => this.jwtIntercept(req, next)
    ));
  }

  private refreshTokenAndRetry(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.refreshJwtToken().pipe(
      catchError((err: Error) => {
        this.authService.logout(true, true);
        const message = err ? err.message : 'Unauthorized!';
        return this.handleResponseError(req, next, new HttpErrorResponse({error: {message, timeout: 200}, status: 401}));
      }),
      switchMap(() => this.jwtIntercept(req, next)),
    );
  }

  private updateAuthorizationHeader(req: HttpRequest<any>): HttpRequest<any> {
    const jwtToken = AuthService.getJwtToken();
    if (jwtToken) {
      tmpHeaders[this.AUTH_HEADER_NAME] = `${this.AUTH_SCHEME}${jwtToken}`;
      req = req.clone({
        setHeaders: tmpHeaders
      });
      return req;
    } else {
      return null;
    }
  }

  private isInternalUrlPrefix(url: string): boolean {
    for (const index in this.internalUrlPrefixes) {
      if (url.startsWith(this.internalUrlPrefixes[index])) {
        return true;
      }
    }
    return false;
  }

  private isTokenBasedAuthEntryPoint(url: string): boolean {
    return  url.startsWith('/api/') &&
      !url.startsWith(Constants.entryPoints.login) &&
      !url.startsWith(Constants.entryPoints.tokenRefresh) &&
      !url.startsWith(Constants.entryPoints.nonTokenBased);
  }

  private updateLoadingState(config: InterceptorConfig, isLoading: boolean) {
    if (!config.ignoreLoading) {
      if (isLoading) {
        this.activeRequests++;
      } else {
        this.activeRequests--;
      }
      if (this.activeRequests === 1 && isLoading) {
        this.store.dispatch(new ActionLoadStart());
      } else if (this.activeRequests === 0) {
        this.store.dispatch(new ActionLoadFinish());
      }
    }
  }

  private getInterceptorConfig(req: HttpRequest<any>): InterceptorConfig {
    let config: InterceptorConfig;
    if (req.params && req.params instanceof InterceptorHttpParams) {
      config = (req.params as InterceptorHttpParams).interceptorConfig;
    } else {
      config = new InterceptorConfig(false, false);
    }
    if (this.isInternalUrlPrefix(req.url)) {
      config.ignoreLoading = true;
    }
    return config;
  }

  private showError(error: string, timeout: number = 0) {
    setTimeout(() => {
      this.store.dispatch(new ActionNotificationShow({message: error, type: 'error'}));
    }, timeout);
  }
}
