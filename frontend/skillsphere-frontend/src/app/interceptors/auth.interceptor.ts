import {
  HttpInterceptorFn
} from '@angular/common/http';


export const authInterceptor:
  HttpInterceptorFn =
  (req, next) => {


    // ==========================================
    // PUBLIC AUTH ENDPOINTS
    // ==========================================

    const isAuthRequest =
      req.url.includes(
        '/api/auth/login'
      )
      ||
      req.url.includes(
        '/api/auth/register'
      );


    if (isAuthRequest) {

      return next(req);
    }


    // ==========================================
    // PROTECTED ENDPOINTS
    // ==========================================

    const token =
      localStorage.getItem(
        'token'
      );


    if (token) {

      const authReq =
        req.clone({

          setHeaders: {

            Authorization:
              `Bearer ${token}`

          }

        });


      return next(authReq);
    }


    return next(req);
  };