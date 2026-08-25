import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  finalize
} from 'rxjs/operators';

import {
  EmployeeService
} from '../../services/employee';


@Component({
  selector: 'app-register',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],

  templateUrl: './register.html',

  styleUrl: './register.css'
})
export class Register {


  // ==========================================
  // FORM FIELDS
  // ==========================================

  firstName = '';

  lastName = '';

  email = '';

  phone = '';

  employeeId = '';

  department = '';

  role = 'EMPLOYEE';

  password = '';

  confirmPassword = '';


  // ==========================================
  // PAGE STATE
  // ==========================================

  isLoading = false;

  errorMessage = '';


  constructor(

    private router:
      Router,

    private employeeService:
      EmployeeService

  ) {}


  // ==========================================
  // REGISTER
  // ==========================================

  register(): void {

    this.errorMessage = '';


    // ========================================
    // REQUIRED FIELD VALIDATION
    // ========================================

    if (
      !this.firstName.trim()
      ||
      !this.lastName.trim()
      ||
      !this.email.trim()
      ||
      !this.phone.trim()
      ||
      !this.department.trim()
      ||
      !this.password
      ||
      !this.confirmPassword
    ) {

      this.errorMessage =
        'Please fill all required fields.';

      return;
    }


    // ========================================
    // EMAIL VALIDATION
    // ========================================

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (
      !emailPattern.test(
        this.email
          .trim()
          .toLowerCase()
      )
    ) {

      this.errorMessage =
        'Please enter a valid email address.';

      return;
    }


    // ========================================
    // PASSWORD MATCH
    // ========================================

    if (
      this.password
      !==
      this.confirmPassword
    ) {

      this.errorMessage =
        'Passwords do not match.';

      return;
    }


    // ========================================
    // PASSWORD LENGTH
    // ========================================

    if (
      this.password.length < 8
    ) {

      this.errorMessage =
        'Password must contain at least 8 characters.';

      return;
    }


    // ========================================
    // PASSWORD STRENGTH
    // ========================================

    const passwordPattern =
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;


    if (
      !passwordPattern.test(
        this.password
      )
    ) {

      this.errorMessage =
        'Password must contain uppercase, lowercase, number and special character.';

      return;
    }


    this.isLoading = true;


    // ========================================
    // CREATE LOGIN ACCOUNT IN 8090
    // ========================================

    const registerData = {

      firstName:
        this.firstName.trim(),

      lastName:
        this.lastName.trim(),

      email:
        this.email
          .trim()
          .toLowerCase(),

      password:
        this.password,

      phone:
        this.phone.trim(),

      employeeId:
        this.employeeId.trim()
        || null,

      department:
        this.department.trim(),

      role:
        'EMPLOYEE'

    };


    this.employeeService
      .registerLoginAccount(
        registerData
      )
      .pipe(

        finalize(() => {

          this.isLoading = false;

        })

      )
      .subscribe({


        // ====================================
        // SUCCESS
        // ====================================

        next: (
          response: any
        ) => {

          console.log(
            'Registration successful:',
            response
          );


          alert(
            'Account created successfully. Please login.'
          );


          this.router.navigate([
            '/login'
          ]);

        },


        // ====================================
        // ERROR
        // ====================================

        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'Registration error:',
            error
          );


          if (
            error.status === 0
          ) {

            this.errorMessage =
              'Cannot connect to authentication service on port 8090.';

          }

          else if (
            error.status === 400
          ) {

            this.errorMessage =
              error.error?.message
              ||
              'Unable to create account. Check the entered details.';

          }

          else if (
            error.status === 409
          ) {

            this.errorMessage =
              'An account with this email already exists.';

          }

          else if (
            error.status === 401
          ) {

            this.errorMessage =
              'Authentication failed.';

          }

          else if (
            error.status === 403
          ) {

            this.errorMessage =
              'You are not authorized to create this account.';

          }

          else {

            this.errorMessage =
              error.error?.message
              ||
              'Registration failed. Please try again.';

          }

        }

      });

  }

}