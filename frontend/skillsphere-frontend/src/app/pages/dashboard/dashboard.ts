import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { DashboardService } from '../../services/dashboard';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-dashboard',
  standalone: true,

  imports: [
    CommonModule,
    RouterModule
  ],

  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  loggedInUser: any = null;

  employees: any[] = [];
  courses: any[] = [];
  skills: any[] = [];
  certifications: any[] = [];

  dashboardAnalytics: any = null;
  trainingAnalytics: any = null;

  employeesLoading = false;
  coursesLoading = true;
  skillsLoading = true;
  certificationsLoading = true;
  analyticsLoading = false;

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef,
    public authService: AuthService
  ) {}


  // ==========================================
  // INITIAL LOAD
  // ==========================================

  ngOnInit(): void {

    this.loggedInUser =
      this.authService.getUser();

    console.log(
      'Dashboard user:',
      this.loggedInUser
    );

    console.log(
      'Dashboard role:',
      this.authService.getRole()
    );


    // Common services
    this.loadCourses();

    this.loadSkills();

    this.loadCertifications();


    // ADMIN / HR only
    if (
      this.authService.isAdmin()
      ||
      this.authService.isHR()
    ) {

      this.loadEmployees();

      this.loadAnalytics();

    } else {

      this.employees = [];

      this.employeesLoading = false;
    }
  }


  // ==========================================
  // EMPLOYEES
  // ADMIN / HR ONLY
  // ==========================================

  loadEmployees(): void {

    if (
      !this.authService.canViewEmployees()
    ) {

      this.employees = [];

      this.employeesLoading = false;

      return;
    }


    this.employeesLoading = true;


    this.dashboardService
      .getEmployees()
      .subscribe({

        next: (data: any) => {

          this.employees =
            Array.isArray(data)
              ? data
              : [];

          this.employeesLoading = false;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Employees error:',
            error
          );

          this.employees = [];

          this.employeesLoading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // COURSES
  // ==========================================

  loadCourses(): void {

    this.coursesLoading = true;


    this.dashboardService
      .getCourses()
      .subscribe({

        next: (data: any) => {

          this.courses =
            Array.isArray(data)
              ? data
              : [];

          this.coursesLoading = false;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Courses error:',
            error
          );

          this.courses = [];

          this.coursesLoading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // SKILLS
  // ==========================================

  loadSkills(): void {

    this.skillsLoading = true;


    this.dashboardService
      .getSkills()
      .subscribe({

        next: (data: any) => {

          this.skills =
            Array.isArray(data)
              ? data
              : [];

          this.skillsLoading = false;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Skills error:',
            error
          );

          this.skills = [];

          this.skillsLoading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // CERTIFICATIONS
  // ==========================================

  loadCertifications(): void {

    this.certificationsLoading = true;


    this.dashboardService
      .getCertifications()
      .subscribe({

        next: (data: any) => {

          this.certifications =
            Array.isArray(data)
              ? data
              : [];

          this.certificationsLoading = false;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Certificates error:',
            error
          );

          this.certifications = [];

          this.certificationsLoading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // MILESTONE-4 ANALYTICS
  // ADMIN / HR ONLY
  // ==========================================

  loadAnalytics(): void {

    if (
      !this.authService.canViewAnalytics()
    ) {

      return;
    }


    this.analyticsLoading = true;


    this.dashboardService
      .getDashboardAnalytics()
      .subscribe({

        next: (response: any) => {

          this.dashboardAnalytics =
            response?.data
            ??
            response
            ??
            null;

          this.checkAnalyticsLoading();

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Dashboard analytics error:',
            error
          );

          this.dashboardAnalytics = null;

          this.checkAnalyticsLoading();

          this.cdr.detectChanges();
        }

      });


    this.dashboardService
      .getTrainingAnalytics()
      .subscribe({

        next: (response: any) => {

          this.trainingAnalytics =
            response?.data
            ??
            response
            ??
            null;

          this.checkAnalyticsLoading();

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Training analytics error:',
            error
          );

          this.trainingAnalytics = null;

          this.checkAnalyticsLoading();

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // ANALYTICS LOADING
  // ==========================================

  private checkAnalyticsLoading(): void {

    this.analyticsLoading = false;
  }


  // ==========================================
  // TOTAL EMPLOYEES
  // ==========================================

  get totalEmployees(): number {

    return this.employees.length;
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
  // RECENT EMPLOYEES
  // ==========================================

  get recentEmployees(): any[] {

    return this.employees
      .slice(-4)
      .reverse();
  }


  // ==========================================
  // EMPLOYEE DISPLAY NAME
  // ==========================================

  displayName(
    employee: any
  ): string {

    const name =
      `${employee?.firstName || ''} ${employee?.lastName || ''}`
        .trim();

    return name || 'Employee';
  }


  // ==========================================
  // LOGGED USER NAME
  // ==========================================

  get loggedUserName(): string {

    if (!this.loggedInUser) {

      return 'User';
    }


    const name =
      `${
        this.loggedInUser.firstName || ''
      } ${
        this.loggedInUser.lastName || ''
      }`
        .trim();


    return name || 'User';
  }


  // ==========================================
  // INITIALS
  // ==========================================

  initials(): string {

    if (!this.loggedInUser) {

      return 'US';
    }


    const first =
      this.loggedInUser
        .firstName
        ?.charAt(0)
      || '';


    const last =
      this.loggedInUser
        .lastName
        ?.charAt(0)
      || '';


    return (
      `${first}${last}`
        .toUpperCase()
      ||
      'US'
    );
  }


  // ==========================================
  // HR COUNT
  // ==========================================

  get totalHR(): number {

    return this.employees
      .filter(
        employee =>
          (
            employee?.role
            ||
            ''
          )
            .toString()
            .trim()
            .toUpperCase()
          ===
          'HR'
      )
      .length;
  }


  // ==========================================
  // ACTIVE EMPLOYEES
  // ==========================================

  get activeEmployees(): number {

    return this.employees
      .filter(
        employee =>
          (
            employee?.status
            ||
            ''
          )
            .toString()
            .trim()
            .toUpperCase()
          ===
          'ACTIVE'
      )
      .length;
  }


  // ==========================================
  // TRAINING COMPLETION
  // ==========================================

  get trainingCompletion(): number {

    return Number(
      this.trainingAnalytics
        ?.overallCompletionPercentage
      ??
      this.trainingAnalytics
        ?.completionRate
      ??
      0
    );
  }


  // ==========================================
  // AVERAGE TRAINING PROGRESS
  // ==========================================

  get averageTrainingProgress(): number {

    return Number(
      this.trainingAnalytics
        ?.averageTrainingProgressPercentage
      ??
      this.trainingAnalytics
        ?.averageProgress
      ??
      0
    );
  }


  // ==========================================
  // TOTAL ENROLLMENTS
  // ==========================================

  get totalEnrollments(): number {

    return Number(
      this.trainingAnalytics
        ?.totalEnrollments
      ??
      0
    );
  }


  // ==========================================
  // COMPLETED TRAINING
  // ==========================================

  get completedTraining(): number {

    return Number(
      this.trainingAnalytics
        ?.completedEnrollments
      ??
      this.trainingAnalytics
        ?.completedTrainings
      ??
      0
    );
  }

}