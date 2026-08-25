import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Router,
  RouterModule
} from '@angular/router';
import { HttpClient } from '@angular/common/http';

import {
  finalize,
  timeout
} from 'rxjs/operators';


@Component({
  selector: 'app-login',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],

  templateUrl: './login.html',

  styleUrl: './login.css'
})
export class Login {

  email = '';

  password = '';

  showPassword = false;

  isLoading = false;

  errorMessage = '';


  // Milestone-4 authentication backend
  private loginUrl =
    'http://localhost:8090/api/auth/login';


  constructor(
    private router: Router,
    private http: HttpClient
  ) {}


  // ==========================================
  // LOGIN
  // ==========================================

  onLogin(): void {

    this.errorMessage = '';


    const email =
      this.email
        .trim()
        .toLowerCase();


    const password =
      this.password;


    // ========================================
    // EMPTY VALIDATION
    // ========================================

    if (!email || !password) {

      this.errorMessage =
        'Please enter both email and password.';

      return;
    }


    // ========================================
    // EMAIL VALIDATION
    // ========================================

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (!emailPattern.test(email)) {

      this.errorMessage =
        'Please enter a valid email address.';

      return;
    }


    this.isLoading = true;


    // ========================================
    // CALL AUTH BACKEND
    // ========================================

    this.http
      .post<any>(
        this.loginUrl,
        {
          email: email,
          password: password
        }
      )
      .pipe(

        timeout(10000),

        finalize(() => {

          this.isLoading = false;

        })

      )
      .subscribe({

        // ====================================
        // LOGIN SUCCESS
        // ====================================

        next: (response: any) => {

          console.log(
            'LOGIN RESPONSE:',
            response
          );


          if (
            !response ||
            !response.success ||
            !response.data
          ) {

            this.errorMessage =
              'Invalid login response.';

            return;
          }


          const data =
            response.data;


          const token =
            data.token;


          if (!token) {

            this.errorMessage =
              'Authentication token not received.';

            return;
          }


          // ==================================
          // NORMALIZE ROLE
          // ==================================

          const role =
            this.normalizeRole(
              data.role
            );


          if (
            role !== 'ADMIN' &&
            role !== 'HR' &&
            role !== 'EMPLOYEE'
          ) {

            this.errorMessage =
              'Invalid user role.';

            return;
          }


          // ==================================
          // COMMON LOGGED-IN USER
          // ==================================

          const user = {

            id:
              data.id,

            firstName:
              data.firstName || '',

            lastName:
              data.lastName || '',

            email:
              data.email || email,

            employeeId:
              data.employeeId || '',

            employeeCode:
              data.employeeCode ||
              data.employeeId ||
              '',

            phone:
              data.phone || '',

            department:
              data.department || '',

            designation:
              data.designation || '',

            experience:
              data.experience || 0,

            status:
              data.status || 'ACTIVE',

            role:
              role

          };


          // ==================================
          // CLEAR OLD LOGIN DATA
          // ==================================

          localStorage.removeItem(
            'token'
          );

          localStorage.removeItem(
            'loggedInUser'
          );

          localStorage.removeItem(
            'isLoggedIn'
          );

          localStorage.removeItem(
            'userRole'
          );


          // ==================================
          // SAVE NEW LOGIN
          // ==================================

          localStorage.setItem(
            'token',
            token
          );


          localStorage.setItem(
            'loggedInUser',
            JSON.stringify(user)
          );


          localStorage.setItem(
            'isLoggedIn',
            'true'
          );


          localStorage.setItem(
            'userRole',
            role
          );


          console.log(
            'LOGGED IN USER:',
            user
          );


          console.log(
            'ROLE:',
            role
          );


          // ==================================
          // REDIRECT
          // ==================================

          this.router.navigate([
            '/dashboard'
          ]);

        },


        // ====================================
        // LOGIN ERROR
        // ====================================

        error: (error: any) => {

          console.error(
            'LOGIN ERROR:',
            error
          );


          if (
            error?.name ===
            'TimeoutError'
          ) {

            this.errorMessage =
              'Login request timed out.';

          }

          else if (
            error?.status === 401
          ) {

            this.errorMessage =
              'Invalid email or password.';

          }

          else if (
            error?.status === 403
          ) {

            this.errorMessage =
              'You are not authorized to login.';

          }

          else if (
            error?.status === 0
          ) {

            this.errorMessage =
              'Cannot connect to authentication server on port 8090.';

          }

          else {

            this.errorMessage =
              'Unable to sign in. Please try again.';

          }

        }

      });

  }


  // ==========================================
  // NORMALIZE ROLE
  // ==========================================

  private normalizeRole(
    value: any
  ): string {

    const role =
      (value || '')
        .toString()
        .trim()
        .toUpperCase();


    if (
      role === 'ADMIN' ||
      role === 'ADMINISTRATOR' ||
      role === 'ROLE_ADMIN' ||
      role === 'ROLE_ADMINISTRATOR'
    ) {

      return 'ADMIN';
    }


    if (
      role === 'HR' ||
      role === 'ROLE_HR' ||
      role === 'HUMAN RESOURCE' ||
      role === 'HUMAN RESOURCES'
    ) {

      return 'HR';
    }


    if (
      role === 'EMPLOYEE' ||
      role === 'USER' ||
      role === 'ROLE_EMPLOYEE' ||
      role === 'ROLE_USER'
    ) {

      return 'EMPLOYEE';
    }


    return role;
  }


  // ==========================================
  // SHOW / HIDE PASSWORD
  // ==========================================

  togglePassword(): void {

    this.showPassword =
      !this.showPassword;

  }

}