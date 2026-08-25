import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  switchMap
} from 'rxjs/operators';

import {
  EmployeeService,
  Employee
} from '../../services/employee';

import {
  AuthService
} from '../../services/auth';


@Component({
  selector: 'app-employees',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './employees.html',

  styleUrl: './employees.css'
})
export class Employees
  implements OnInit {


  // ==========================================
  // PAGE STATE
  // ==========================================

  searchText = '';

  showForm = false;

  isEditing = false;

  editingId:
    string |
    number |
    null = null;

  loading = false;

  saving = false;

  employees:
    Employee[] = [];


  // ==========================================
  // LOGIN PASSWORD FOR NEW EMPLOYEE
  // ==========================================

  employeePassword = '';


  // ==========================================
  // EMPLOYEE FORM
  // ==========================================

  employee:
    Employee = {

      firstName: '',

      lastName: '',

      email: '',

      phone: '',

      department: '',

      designation: '',

      experience: 0,

      role: 'EMPLOYEE',

      status: 'ACTIVE'
    };


  constructor(

    private employeeService:
      EmployeeService,

    private cdr:
      ChangeDetectorRef,

    public authService:
      AuthService

  ) {}


  // ==========================================
  // INITIAL LOAD
  // ==========================================

  ngOnInit(): void {

    this.loadEmployees();
  }


  // ==========================================
  // LOAD EMPLOYEES
  // ==========================================

  loadEmployees(): void {

    this.loading = true;


    this.employeeService
      .getEmployees()
      .subscribe({

        next: (
          data: Employee[]
        ) => {

          this.employees =
            Array.isArray(data)
              ? [...data]
              : [];


          this.loading = false;

          this.cdr.detectChanges();
        },


        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'Employee loading error:',
            error
          );


          this.employees = [];

          this.loading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // SEARCH
  // ==========================================

  get filteredEmployees():
    Employee[] {

    const search =
      this.searchText
        .trim()
        .toLowerCase();


    if (!search) {

      return this.employees;
    }


    return this.employees
      .filter(
        employee => {

          const text =
            `
            ${employee.firstName || ''}
            ${employee.lastName || ''}
            ${employee.email || ''}
            ${employee.department || ''}
            ${employee.designation || ''}
            ${employee.role || ''}
            ${employee.status || ''}
            `
              .toLowerCase();


          return text.includes(
            search
          );
        }
      );
  }


  // ==========================================
  // OPEN ADD FORM
  // ==========================================

  openAddForm(): void {

    if (
      !this.authService
        .canAddEmployee()
    ) {

      alert(
        'You do not have permission to add employees.'
      );

      return;
    }


    this.isEditing = false;

    this.editingId = null;

    this.employeePassword = '';


    this.employee = {

      firstName: '',

      lastName: '',

      email: '',

      phone: '',

      department: '',

      designation: '',

      experience: 0,

      role: 'EMPLOYEE',

      status: 'ACTIVE'
    };


    this.showForm = true;
  }


  // ==========================================
  // CLOSE FORM
  // ==========================================

  closeForm(): void {

    this.showForm = false;

    this.isEditing = false;

    this.editingId = null;

    this.employeePassword = '';

    this.saving = false;
  }


  // ==========================================
  // SAVE EMPLOYEE
  // ==========================================

  saveEmployee(): void {

    if (this.saving) {

      return;
    }


    // ========================================
    // REQUIRED FIELDS
    // ========================================

    if (
      !this.employee
        .firstName
        ?.trim()
      ||
      !this.employee
        .lastName
        ?.trim()
      ||
      !this.employee
        .email
        ?.trim()
      ||
      !this.employee
        .department
        ?.trim()
    ) {

      alert(
        'Please fill all required fields.'
      );

      return;
    }


    // ========================================
    // EMAIL VALIDATION
    // ========================================

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (
      !emailPattern.test(
        this.employee.email.trim()
      )
    ) {

      alert(
        'Please enter a valid email address.'
      );

      return;
    }


    // ========================================
    // UPDATE EXISTING EMPLOYEE
    // ========================================

    if (
      this.isEditing
      &&
      this.editingId !== null
    ) {

      this.updateEmployee();

      return;
    }


    // ========================================
    // CREATE NEW EMPLOYEE
    // ========================================

    this.createEmployee();
  }


  // ==========================================
  // CREATE EMPLOYEE + LOGIN ACCOUNT
  // ==========================================

  private createEmployee(): void {

    if (
      !this.authService
        .canAddEmployee()
    ) {

      alert(
        'You do not have permission to add employees.'
      );

      return;
    }


    // Password required only while creating
    if (
      !this.employeePassword
    ) {

      alert(
        'Please enter a login password for the employee.'
      );

      return;
    }


    if (
      this.employeePassword.length < 6
    ) {

      alert(
        'Password must contain at least 6 characters.'
      );

      return;
    }


    this.saving = true;


    const employeeData:
      Employee = {

        ...this.employee,

        firstName:
          this.employee
            .firstName
            .trim(),

        lastName:
          this.employee
            .lastName
            .trim(),

        email:
          this.employee
            .email
            .trim()
            .toLowerCase(),

        phone:
          this.employee.phone
            ?.trim()
          || '',

        department:
          this.employee
            .department
            .trim(),

        designation:
          this.employee
            .designation
            ?.trim()
          || '',

        experience:
          Number(
            this.employee.experience
            || 0
          ),

        role:
          this.normalizeRole(
            this.employee.role
          ),

        status:
          (
            this.employee.status
            ||
            'ACTIVE'
          )
            .toString()
            .trim()
            .toUpperCase()

      };


    // ========================================
    // STEP 1:
    // CREATE PROFILE IN USER SERVICE 8081
    //
    // STEP 2:
    // CREATE LOGIN IN AUTH SERVICE 8090
    // ========================================

    this.employeeService
      .addEmployee(
        employeeData
      )
      .pipe(

        switchMap(
          (
            createdEmployee:
              any
          ) => {


            console.log(
              '8081 employee created:',
              createdEmployee
            );


            const employeeId =
              createdEmployee
                ?.employeeId
              ??
              createdEmployee
                ?.id
              ??
              employeeData
                .employeeId
              ??
              '';


            const loginAccount = {

              firstName:
                employeeData
                  .firstName,

              lastName:
                employeeData
                  .lastName,

              email:
                employeeData
                  .email,

              password:
                this.employeePassword,

              phone:
                employeeData.phone
                || '',

              employeeId:
                employeeId
                  .toString(),

              department:
                employeeData
                  .department,

              role:
                this.normalizeRole(
                  employeeData.role
                )

            };


            console.log(
              'Creating 8090 login:',
              {
                ...loginAccount,
                password: '********'
              }
            );


            return this.employeeService
              .registerLoginAccount(
                loginAccount
              );
          }
        )

      )
      .subscribe({


        // ====================================
        // BOTH CREATED
        // ====================================

        next: (
          response: any
        ) => {

          console.log(
            '8090 login account created:',
            response
          );


          this.saving = false;


          alert(
            'Employee created successfully. The employee can now login using their email and password.'
          );


          this.closeForm();

          this.loadEmployees();
        },


        // ====================================
        // CREATION FAILED
        // ====================================

        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'Employee/login creation error:',
            error
          );


          this.saving = false;


          const message =
            error?.error?.message;


          if (message) {

            alert(message);

          } else {

            alert(
              'Unable to create employee login account. Check the browser console and backend.'
            );
          }


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // UPDATE EMPLOYEE
  // ==========================================

  private updateEmployee(): void {

    if (
      !this.authService
        .canEditEmployee()
    ) {

      alert(
        'You do not have permission to edit employees.'
      );

      return;
    }


    if (
      this.editingId === null
    ) {

      return;
    }


    this.saving = true;


    const employeeData:
      Employee = {

        ...this.employee,

        firstName:
          this.employee.firstName
            .trim(),

        lastName:
          this.employee.lastName
            .trim(),

        email:
          this.employee.email
            .trim()
            .toLowerCase(),

        department:
          this.employee.department
            .trim(),

        designation:
          this.employee.designation
            ?.trim()
          || '',

        role:
          this.normalizeRole(
            this.employee.role
          ),

        status:
          (
            this.employee.status
            ||
            'ACTIVE'
          )
            .toString()
            .trim()
            .toUpperCase()

      };


    this.employeeService
      .updateEmployee(
        this.editingId,
        employeeData
      )
      .subscribe({

        next: () => {

          this.saving = false;

          alert(
            'Employee updated successfully.'
          );

          this.closeForm();

          this.loadEmployees();
        },


        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'Employee update error:',
            error
          );


          this.saving = false;


          alert(
            error?.error?.message
            ||
            'Unable to update employee.'
          );


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // EDIT EMPLOYEE
  // ==========================================

  editEmployee(
    employee:
      Employee
  ): void {

    if (
      !this.authService
        .canEditEmployee()
    ) {

      alert(
        'You do not have permission to edit employees.'
      );

      return;
    }


    this.isEditing = true;


    this.editingId =
      employee.employeeId
      ??
      null;


    this.employee = {
      ...employee,

      role:
        this.normalizeRole(
          employee.role
        ),

      status:
        (
          employee.status
          ||
          'ACTIVE'
        )
          .toString()
          .toUpperCase()
    };


    // Don't ask for password during profile edit
    this.employeePassword = '';


    this.showForm = true;
  }


  // ==========================================
  // DELETE
  // ==========================================

  deleteEmployee(
    id:
      string |
      number
  ): void {

    if (
      !this.authService
        .canDeleteEmployee()
    ) {

      alert(
        'Only Admin can delete employees.'
      );

      return;
    }


    const confirmed =
      confirm(
        'Are you sure you want to delete this employee?'
      );


    if (!confirmed) {

      return;
    }


    this.employeeService
      .deleteEmployee(id)
      .subscribe({

        next: () => {

          this.loadEmployees();
        },


        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'Employee deletion error:',
            error
          );


          alert(
            'Unable to delete employee.'
          );
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
      (
        value
        ||
        'EMPLOYEE'
      )
        .toString()
        .trim()
        .toUpperCase();


    if (
      role === 'ADMIN'
      ||
      role === 'ADMINISTRATOR'
    ) {

      return 'ADMIN';
    }


    if (
      role === 'HR'
      ||
      role ===
        'HUMAN RESOURCE'
      ||
      role ===
        'HUMAN RESOURCES'
    ) {

      return 'HR';
    }


    return 'EMPLOYEE';
  }

}