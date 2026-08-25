import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  RouterModule
} from '@angular/router';

import {
  forkJoin,
  of
} from 'rxjs';

import {
  catchError
} from 'rxjs/operators';

import {
  DashboardService
} from '../../services/dashboard';

import {
  AuthService
} from '../../services/auth';


@Component({
  selector: 'app-analytics',

  standalone: true,

  imports: [
    CommonModule,
    RouterModule
  ],

  templateUrl: './analytics.html',

  styleUrl: './analytics.css'
})
export class Analytics implements OnInit {


  // ==========================================
  // EXISTING PLATFORM DATA
  // ==========================================

  employees: any[] = [];

  courses: any[] = [];

  skills: any[] = [];

  certifications: any[] = [];


  // ==========================================
  // CAREER / ANALYTICS DATA FROM 8090
  // ==========================================

  dashboardAnalytics: any = null;

  trainingAnalytics: any = null;


  // ==========================================
  // PAGE STATE
  // ==========================================

  loading = true;

  errorMessage = '';


  constructor(

    private dashboardService:
      DashboardService,

    public authService:
      AuthService,

    private cdr:
      ChangeDetectorRef

  ) {}


  // ==========================================
  // INITIAL LOAD
  // ==========================================

  ngOnInit(): void {

    if (
      !this.authService
        .canViewAnalytics()
    ) {

      this.loading = false;

      return;
    }


    this.loadAnalytics();
  }


  // ==========================================
  // LOAD COMPLETE PLATFORM ANALYTICS
  // ==========================================

  loadAnalytics(): void {

    this.loading = true;

    this.errorMessage = '';


    forkJoin({


      // ======================================
      // USER SERVICE - 8081
      // ======================================

      employees:

        this.dashboardService
          .getEmployees()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Employee analytics error:',
                  error
                );

                return of([]);
              }
            )

          ),


      // ======================================
      // LEARNING SERVICE - 8084
      // ======================================

      courses:

        this.dashboardService
          .getCourses()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Course analytics error:',
                  error
                );

                return of([]);
              }
            )

          ),


      // ======================================
      // SKILL SERVICE - 8082
      // ======================================

      skills:

        this.dashboardService
          .getSkills()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Skill analytics error:',
                  error
                );

                return of([]);
              }
            )

          ),


      // ======================================
      // CERTIFICATION SERVICE - 8083
      // ======================================

      certifications:

        this.dashboardService
          .getCertifications()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Certification analytics error:',
                  error
                );

                return of([]);
              }
            )

          ),


      // ======================================
      // CAREER ANALYTICS - 8090
      // ======================================

      dashboard:

        this.dashboardService
          .getDashboardAnalytics()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Career analytics error:',
                  error
                );

                return of(null);
              }
            )

          ),


      // ======================================
      // TRAINING ANALYTICS - 8090
      // ======================================

      training:

        this.dashboardService
          .getTrainingAnalytics()
          .pipe(

            catchError(
              error => {

                console.error(
                  'Training analytics error:',
                  error
                );

                return of(null);
              }
            )

          )


    }).subscribe({


      // ======================================
      // SUCCESS
      // ======================================

      next: result => {


        this.employees =
          this.extractArray(
            result.employees
          );


        this.courses =
          this.extractArray(
            result.courses
          );


        this.skills =
          this.extractArray(
            result.skills
          );


        this.certifications =
          this.extractArray(
            result.certifications
          );


        this.dashboardAnalytics =

          result.dashboard?.data

          ||

          result.dashboard

          ||

          null;


        this.trainingAnalytics =

          result.training?.data

          ||

          result.training

          ||

          null;


        console.log(
          'EMPLOYEES:',
          this.employees
        );


        console.log(
          'COURSES:',
          this.courses
        );


        console.log(
          'SKILLS:',
          this.skills
        );


        console.log(
          'CERTIFICATIONS:',
          this.certifications
        );


        console.log(
          'CAREER ANALYTICS:',
          this.dashboardAnalytics
        );


        console.log(
          'TRAINING ANALYTICS:',
          this.trainingAnalytics
        );


        this.loading = false;


        this.cdr.detectChanges();
      },


      // ======================================
      // ERROR
      // ======================================

      error: error => {

        console.error(
          'Analytics loading error:',
          error
        );


        this.errorMessage =
          'Unable to load analytics.';


        this.loading = false;


        this.cdr.detectChanges();
      }

    });
  }


  // ==========================================
  // EXTRACT ARRAY
  // ==========================================

  private extractArray(
    response: any
  ): any[] {


    if (
      Array.isArray(
        response
      )
    ) {

      return response;
    }


    if (
      Array.isArray(
        response?.data
      )
    ) {

      return response.data;
    }


    return [];
  }


  // ==========================================
  // TOTAL EMPLOYEES
  // ==========================================

  get totalEmployees(): number {

    return this.employees.length;
  }


  // ==========================================
  // ACTIVE EMPLOYEES
  // ==========================================

  get activeEmployees(): number {

    return this.employees.filter(

      employee => {


        const status =

          (
            employee.status
            ||
            'ACTIVE'
          )

            .toString()

            .trim()

            .toUpperCase();


        return (
          status === 'ACTIVE'
        );
      }

    ).length;
  }


  // ==========================================
  // TOTAL COURSES
  // ==========================================

  get totalCourses(): number {

    return this.courses.length;
  }


  // ==========================================
  // TOTAL SKILLS
  // ==========================================

  get totalSkills(): number {

    return this.skills.length;
  }


  // ==========================================
  // TOTAL CERTIFICATES
  // ==========================================

  get totalCertificates(): number {

    return this.certifications.length;
  }


  // ==========================================
  // EMPLOYEE COUNT
  // ==========================================

  get employeeCount(): number {

    return this.employees.filter(

      employee => {


        const role =
          this.normalizeRole(
            employee.role
          );


        return (
          role === 'EMPLOYEE'
        );
      }

    ).length;
  }


  // ==========================================
  // HR COUNT
  // ==========================================

  get hrCount(): number {

    return this.employees.filter(

      employee => {


        const role =
          this.normalizeRole(
            employee.role
          );


        return (
          role === 'HR'
        );
      }

    ).length;
  }


  // ==========================================
  // ADMIN COUNT
  // ==========================================

  get adminCount(): number {

    return this.employees.filter(

      employee => {


        const role =
          this.normalizeRole(
            employee.role
          );


        return (
          role === 'ADMIN'
        );
      }

    ).length;
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
        ''
      )

        .toString()

        .trim()

        .toUpperCase();


    // ======================================
    // ADMIN
    // ======================================

    if (

      role === 'ADMIN'

      ||

      role === 'ADMINISTRATOR'

      ||

      role === 'ROLE_ADMIN'

      ||

      role === 'ROLE_ADMINISTRATOR'

    ) {

      return 'ADMIN';
    }


    // ======================================
    // HR
    // ======================================

    if (

      role === 'HR'

      ||

      role === 'ROLE_HR'

      ||

      role === 'HUMAN RESOURCE'

      ||

      role === 'HUMAN RESOURCES'

    ) {

      return 'HR';
    }


    // ======================================
    // EMPLOYEE
    // ======================================

    if (

      role === 'EMPLOYEE'

      ||

      role === 'USER'

      ||

      role === 'ROLE_EMPLOYEE'

      ||

      role === 'ROLE_USER'

    ) {

      return 'EMPLOYEE';
    }


    return role;
  }


  // ==========================================
  // ISSUED CERTIFICATES
  // ==========================================

  get issuedCertificates(): number {

    return this.certifications.filter(

      certificate => {


        const status =

          (
            certificate.status

            ||

            certificate.certificateStatus

            ||

            ''
          )

            .toString()

            .trim()

            .toUpperCase();


        if (

          status === 'ISSUED'

          ||

          status === 'ACTIVE'

          ||

          status === 'VALID'

        ) {

          return true;
        }


        return (

          certificate.certificateId
          !=
          null

          ||

          certificate.id
          !=
          null

          ||

          !!certificate.certificateNumber

        );
      }

    ).length;
  }


  // ==========================================
  // SKILL ASSIGNMENTS
  // ==========================================

  get skillAssignments(): number {

    return (

      this.dashboardAnalytics

        ?.skillStats

        ?.totalSkillAssignments

      ||

      0

    );
  }


  // ==========================================
  // CAREER PROFILE SKILLS
  // ==========================================

  get careerProfiledSkills(): number {

    return (

      this.dashboardAnalytics

        ?.skillStats

        ?.totalSkills

      ||

      0

    );
  }


  // ==========================================
  // TOTAL TRAINING ENROLLMENTS
  // ==========================================

  get trainingEnrollments(): number {

    return (

      this.trainingAnalytics

        ?.totalEnrollments

      ||

      0

    );
  }


  // ==========================================
  // COMPLETED ENROLLMENTS
  // ==========================================

  get completedEnrollments(): number {

    return (

      this.trainingAnalytics

        ?.completedEnrollments

      ||

      0

    );
  }


  // ==========================================
  // IN PROGRESS
  // ==========================================

  get inProgressEnrollments(): number {

    return (

      this.trainingAnalytics

        ?.inProgressEnrollments

      ||

      0

    );
  }


  // ==========================================
  // NOT STARTED
  // ==========================================

  get notStartedEnrollments(): number {

    return (

      this.trainingAnalytics

        ?.notStartedEnrollments

      ||

      0

    );
  }


  // ==========================================
  // COMPLETION PERCENTAGE
  // ==========================================

  get completionPercentage(): number {

    return (

      this.trainingAnalytics

        ?.overallCompletionPercentage

      ||

      0

    );
  }


  // ==========================================
  // AVERAGE TRAINING PROGRESS
  // ==========================================

  get averageProgress(): number {

    return (

      this.trainingAnalytics

        ?.averageTrainingProgressPercentage

      ||

      0

    );
  }


  // ==========================================
  // TRAINING HOURS
  // ==========================================

  get trainingHours(): number {

    return (

      this.trainingAnalytics

        ?.totalTrainingHoursCompleted

      ||

      0

    );
  }


  // ==========================================
  // PARTICIPATING LEARNERS
  // ==========================================

  get learnerCount(): number {

    return (

      this.trainingAnalytics

        ?.totalLearnersParticipating

      ||

      0

    );
  }


  // ==========================================
  // PARTICIPATION RATE
  // ==========================================

  get participationRate(): number {

    return (

      this.trainingAnalytics

        ?.enterpriseTrainingParticipationRate

      ||

      0

    );
  }


  // ==========================================
  // TRAINING COURSES
  // ==========================================

  get trainingCourses(): number {

    return (

      this.trainingAnalytics

        ?.totalCourses

      ||

      0

    );
  }


  // ==========================================
  // EMPLOYEES BY DEPARTMENT
  // ==========================================

  get employeesByDepartment(): any[] {

    const result: any = {};


    this.employees.forEach(

      employee => {


        const department =

          employee.department

          ||

          'Not Assigned';


        if (
          result[department]
        ) {

          result[department]++;

        } else {

          result[department] = 1;
        }
      }

    );


    return this.objectEntries(
      result
    );
  }


  // ==========================================
  // OBJECT TO ARRAY
  // ==========================================

  objectEntries(
    value: any
  ): any[] {


    if (!value) {

      return [];
    }


    return Object.keys(
      value
    ).map(

      key => ({

        key:
          key,

        value:
          value[key]

      })

    );
  }


  // ==========================================
  // REFRESH
  // ==========================================

  refresh(): void {

    this.loadAnalytics();
  }

}