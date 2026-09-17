import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  ActivatedRoute,
  RouterModule
} from '@angular/router';

import {
  Certification,
  CertificationService
} from '../../services/certification';

import {
  LearningService,
  Enrollment
} from '../../services/learning';

import {
  Employee,
  EmployeeService
} from '../../services/employee';

import {
  AuthService
} from '../../services/auth';


@Component({
  selector: 'app-certificate-view',

  standalone: true,

  imports: [
    CommonModule,
    RouterModule
  ],

  templateUrl: './certificate-view.html',

  styleUrl: './certificate-view.css'
})
export class CertificateView implements OnInit {

  certificate:
    Certification | null = null;

  enrollment:
    Enrollment | null = null;

  employee:
    Employee | null = null;

  loggedUser: any = null;

  loading = true;

  errorMessage = '';


  constructor(

    private route:
      ActivatedRoute,

    private certificationService:
      CertificationService,

    private learningService:
      LearningService,

    private employeeService:
      EmployeeService,

    public authService:
      AuthService,

    private cdr:
      ChangeDetectorRef

  ) {}


  ngOnInit(): void {

    this.loggedUser =
      this.authService
        .getLoggedInUser();

    console.log(
      'CERTIFICATE LOGGED USER:',
      this.loggedUser
    );

    const idText =
      this.route
        .snapshot
        .paramMap
        .get('id');


    if (!idText) {

      this.loading = false;

      this.errorMessage =
        'Certificate ID not found.';

      return;
    }


    const id =
      Number(idText);


    if (isNaN(id)) {

      this.loading = false;

      this.errorMessage =
        'Invalid certificate ID.';

      return;
    }


    this.loadCertificate(id);
  }


  // ==========================================
  // LOAD CERTIFICATE
  // ==========================================

  loadCertificate(
    id: number
  ): void {

    this.loading = true;

    this.errorMessage = '';


    this.certificationService
      .getCertificationById(id)
      .subscribe({

        next: (
          data: Certification
        ) => {

          console.log(
            'CERTIFICATE:',
            data
          );

          this.certificate =
            data;


          if (
            data.enrollmentId
            == null
          ) {

            this.loading =
              false;

            this.errorMessage =
              'Enrollment ID is missing from certificate.';

            this.cdr.detectChanges();

            return;
          }


          this.loadEnrollment(
            data.enrollmentId
          );
        },


        error: (
          error: any
        ) => {

          console.error(
            'Certificate loading error:',
            error
          );


          this.loading =
            false;


          if (
            error?.status === 404
          ) {

            this.errorMessage =
              'Certificate not found.';

          } else if (
            error?.status === 0
          ) {

            this.errorMessage =
              'Cannot connect to Certification Service.';

          } else {

            this.errorMessage =
              'Unable to load certificate.';
          }


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD ENROLLMENT
  // ==========================================

  loadEnrollment(
    enrollmentId: number
  ): void {

    /*
     * Milestone-4 Learning Service does not
     * provide GET /enrollments/{id}.
     *
     * Therefore we first get the logged-in
     * employee ID and then request:
     *
     * GET /enrollments/employee/{employeeId}
     */

    const employeeId =
      this.getEmployeeId();


    if (!employeeId) {

      this.loading =
        false;

      this.errorMessage =
        'Employee ID not found for the logged-in user.';

      this.cdr.detectChanges();

      return;
    }


    console.log(
      'Loading enrollments for employee:',
      employeeId
    );


    this.learningService
      .getEnrollments(employeeId)
      .subscribe({

        next: (
          enrollments: Enrollment[]
        ) => {

          console.log(
            'EMPLOYEE ENROLLMENTS:',
            enrollments
          );


          const matchingEnrollment =
            enrollments.find(
              enrollment =>
                Number(enrollment.enrollmentId)
                === Number(enrollmentId)
            );


          if (!matchingEnrollment) {

            console.error(
              'Enrollment not found:',
              enrollmentId
            );

            this.loading =
              false;

            this.errorMessage =
              'Enrollment information not found.';

            this.cdr.detectChanges();

            return;
          }


          console.log(
            'CERTIFICATE ENROLLMENT:',
            matchingEnrollment
          );


          this.enrollment =
            matchingEnrollment;


          /*
           * EMPLOYEE:
           *
           * Use the logged-in user's information.
           *
           * Do not call the old Employee Service
           * with the Learning Service employee ID.
           */

          if (
            this.authService
              .isEmployee()
          ) {

            this.loading =
              false;

            this.cdr.detectChanges();

            return;
          }


          /*
           * ADMIN / HR:
           *
           * Existing Employee Service can be
           * used when employee information is
           * required.
           */

          if (
            matchingEnrollment.employeeId
            != null
          ) {

            this.loadEmployee(
              matchingEnrollment.employeeId
            );

          } else {

            this.loading =
              false;

            this.cdr.detectChanges();
          }
        },


        error: (
          error: any
        ) => {

          console.error(
            'Enrollment loading error:',
            error
          );


          this.loading =
            false;


          if (
            error?.status === 0
          ) {

            this.errorMessage =
              'Cannot connect to Learning Service.';

          } else if (
            error?.status === 404
          ) {

            this.errorMessage =
              'No enrollments found for this employee.';

          } else {

            this.errorMessage =
              'Unable to load enrollment information.';
          }


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // GET LOGGED-IN EMPLOYEE ID
  // ==========================================

  private getEmployeeId(): number {

    if (
      this.loggedUser
    ) {

      const id =
        Number(
          this.loggedUser.id
          ||
          this.loggedUser.employeeId
          ||
          0
        );


      if (id > 0) {

        return id;
      }
    }


    const storedUser =
      localStorage.getItem(
        'loggedInUser'
      );


    if (!storedUser) {

      return 0;
    }


    try {

      const user =
        JSON.parse(
          storedUser
        );


      return Number(
        user.id
        ||
        user.employeeId
        ||
        0
      );

    } catch {

      return 0;
    }
  }


  // ==========================================
  // LOAD OLD EMPLOYEE SERVICE DATA
  // ADMIN / HR ONLY
  // ==========================================

  loadEmployee(
    employeeId:
      number | string
  ): void {

    this.employeeService
      .getEmployeeById(
        employeeId
      )
      .subscribe({

        next: (
          data: Employee
        ) => {

          this.employee =
            data;

          this.loading =
            false;

          this.cdr.detectChanges();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Employee loading error:',
            error
          );


          this.loading =
            false;

          this.errorMessage =
            'Unable to load employee information.';


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // EMPLOYEE NAME
  // ==========================================

  get employeeName(): string {

    if (
      this.authService.isEmployee()
      &&
      this.loggedUser
    ) {

      const name =
        `${
          this.loggedUser.firstName
          || ''
        } ${
          this.loggedUser.lastName
          || ''
        }`
          .trim();

      return (
        name
        ||
        'Employee'
      );
    }


    if (!this.employee) {

      return 'Employee';
    }


    const name =
      `${
        this.employee.firstName
        || ''
      } ${
        this.employee.lastName
        || ''
      }`
        .trim();


    return (
      name
      ||
      'Employee'
    );
  }


  // ==========================================
  // EMPLOYEE CODE
  // ==========================================

  get employeeCode(): string {

    if (
      this.authService.isEmployee()
      &&
      this.loggedUser
    ) {

      return (
        this.loggedUser.employeeId
        ||
        '-'
      );
    }


    return (
      this.employee
        ?.employeeCode
      ||
      '-'
    );
  }


  // ==========================================
  // DEPARTMENT
  // ==========================================

  get department(): string {

    if (
      this.authService.isEmployee()
      &&
      this.loggedUser
    ) {

      return (
        this.loggedUser.department
        ||
        '-'
      );
    }


    return (
      this.employee
        ?.department
      ||
      '-'
    );
  }


  // ==========================================
  // DESIGNATION
  // ==========================================

  get designation(): string {

    if (
      this.authService.isEmployee()
      &&
      this.loggedUser
    ) {

      /*
       * Current Milestone-4 login response
       * does not provide designation.
       * Therefore role is used as fallback.
       */

      return (
        this.loggedUser.designation
        ||
        this.loggedUser.role
        ||
        'Employee'
      );
    }


    return (
      this.employee
        ?.designation
      ||
      '-'
    );
  }


  // ==========================================
  // EMAIL
  // ==========================================

  get employeeEmail(): string {

    if (
      this.authService.isEmployee()
      &&
      this.loggedUser
    ) {

      return (
        this.loggedUser.email
        ||
        '-'
      );
    }


    return (
      this.employee
        ?.email
      ||
      '-'
    );
  }


  // ==========================================
  // FORMAT DATE
  // ==========================================

  formatDate(
    date:
      string | undefined
  ): string {

    if (!date) {

      return '-';
    }


    const value =
      new Date(
        `${date}T00:00:00`
      );


    return value
      .toLocaleDateString(
        'en-US',
        {
          day: 'numeric',
          month: 'long',
          year: 'numeric'
        }
      );
  }


  // ==========================================
  // PRINT
  // ==========================================

  printCertificate(): void {

    window.print();
  }

}