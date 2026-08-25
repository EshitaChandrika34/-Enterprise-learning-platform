import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  RouterLink,
  RouterLinkActive
} from '@angular/router';

import {
  forkJoin,
  of
} from 'rxjs';

import {
  catchError
} from 'rxjs/operators';

import {
  AuthService
} from '../../services/auth';

import {
  CareerService
} from '../../services/career';


@Component({
  selector: 'app-career',

  standalone: true,

  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './career.html',

  styleUrl: './career.css'
})
export class Career implements OnInit {

  readonly MINIMUM_APPLICATION_READINESS = 40;

  employeeId = 0;

  careerPaths: any[] = [];

  careerGoals: any[] = [];

  recommendations: any = null;

  careerProgress: any = null;

  promotionCriteria: any[] = [];

  promotionEvaluation: any = null;

  jobs: any[] = [];

  matchedJobs: any[] = [];

  myApplications: any[] = [];

  trainingAnalytics: any = null;

  loading = true;

  errorMessage = '';

  applicationMessage = '';

  applicationError = '';

  applyingJobId:
    number | null = null;


  constructor(
    public authService: AuthService,
    private careerService: CareerService,
    private cdr: ChangeDetectorRef
  ) {}


  // ==========================================
  // PAGE LOAD
  // ==========================================

  ngOnInit(): void {

    const user =
      this.authService
        .getLoggedInUser();


    if (!user || !user.id) {

      this.errorMessage =
        'Logged in user not found.';

      this.loading = false;

      return;
    }


    this.employeeId =
      Number(user.id);


    if (
      this.authService.isEmployee()
    ) {

      this.loadEmployeeCareer();

      return;
    }


    if (
      this.authService.isAdmin()
      ||
      this.authService.isHR()
    ) {

      this.loadCareerManagement();

      return;
    }


    this.errorMessage =
      'You are not authorized to access career information.';

    this.loading = false;
  }


  // ==========================================
  // EMPLOYEE CAREER
  // ==========================================

  loadEmployeeCareer(): void {

    this.loading = true;

    this.errorMessage = '';


    forkJoin({

      paths:
        this.careerService
          .getCareerPaths()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Career paths error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      goals:
        this.careerService
          .getCareerGoals(
            this.employeeId
          )
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Career goals error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      recommendations:
        this.careerService
          .getRecommendations(
            this.employeeId
          )
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Recommendations error:',
                  error
                );

                return of({
                  data: null
                });
              }
            )
          ),


      progress:
        this.careerService
          .getCareerProgress(
            this.employeeId
          )
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Career progress error:',
                  error
                );

                return of({
                  data: null
                });
              }
            )
          ),


      criteria:
        this.careerService
          .getPromotionCriteria()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Promotion criteria error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      jobs:
        this.careerService
          .getJobs()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Jobs error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      matchedJobs:
        this.careerService
          .getMatchedJobs(
            this.employeeId
          )
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Matched jobs error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      applications:
        this.careerService
          .getMyApplications()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Applications error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          )

    }).subscribe({

      next: (result: any) => {

        this.careerPaths =
          result.paths?.data || [];

        this.careerGoals =
          result.goals?.data || [];

        this.recommendations =
          result.recommendations?.data
          || null;

        this.careerProgress =
          result.progress?.data
          || null;

        this.promotionCriteria =
          result.criteria?.data
          || [];

        this.jobs =
          result.jobs?.data
          || [];

        this.matchedJobs =
          result.matchedJobs?.data
          || [];

        this.myApplications =
          result.applications?.data
          || [];


        if (
          this.promotionCriteria.length > 0
        ) {

          const criteriaId =
            Number(
              this.promotionCriteria[0]?.id
            );


          if (criteriaId) {

            this.loadPromotionEvaluation(
              criteriaId
            );

          } else {

            this.loading = false;

            this.cdr.detectChanges();
          }

        } else {

          this.loading = false;

          this.cdr.detectChanges();
        }

      },


      error: (error: any) => {

        console.error(
          'Career loading error:',
          error
        );

        this.errorMessage =
          'Unable to load career information.';

        this.loading = false;

        this.cdr.detectChanges();
      }

    });
  }


  // ==========================================
  // ADMIN / HR CAREER MANAGEMENT
  // ==========================================

  loadCareerManagement(): void {

    this.loading = true;

    this.errorMessage = '';


    forkJoin({

      paths:
        this.careerService
          .getCareerPaths()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Career paths error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      criteria:
        this.careerService
          .getPromotionCriteria()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Promotion criteria error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      jobs:
        this.careerService
          .getJobs()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Jobs error:',
                  error
                );

                return of({
                  data: []
                });
              }
            )
          ),


      analytics:
        this.careerService
          .getTrainingAnalytics()
          .pipe(
            catchError(
              (error: any) => {

                console.error(
                  'Training analytics error:',
                  error
                );

                return of({
                  data: null
                });
              }
            )
          )

    }).subscribe({

      next: (result: any) => {

        this.careerPaths =
          result.paths?.data
          || [];

        this.promotionCriteria =
          result.criteria?.data
          || [];

        this.jobs =
          result.jobs?.data
          || [];

        this.trainingAnalytics =
          result.analytics?.data
          || null;

        this.loading = false;

        this.cdr.detectChanges();
      },


      error: (error: any) => {

        console.error(
          'Career management error:',
          error
        );

        this.errorMessage =
          'Unable to load career management information.';

        this.loading = false;

        this.cdr.detectChanges();
      }

    });
  }


  // ==========================================
  // PROMOTION EVALUATION
  // ==========================================

  loadPromotionEvaluation(
    criteriaId: number
  ): void {

    this.careerService
      .getPromotionEvaluation(
        this.employeeId,
        criteriaId
      )
      .subscribe({

        next: (response: any) => {

          this.promotionEvaluation =
            response?.data
            || null;

          this.loading = false;

          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Promotion evaluation error:',
            error
          );

          this.promotionEvaluation =
            null;

          this.loading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // NEW EMPLOYEE STATE
  // ==========================================

  get hasCareerStarted(): boolean {

    const overall =
      Number(
        this.careerProgress
          ?.overallProgressPercentage
        || 0
      );


    const skillProgress =
      Number(
        this.careerProgress
          ?.skillProgressPercentage
        || 0
      );


    const matchedSkills =
      Number(
        this.careerProgress
          ?.skillsMatchedCount
        || 0
      );


    const currentSkills =
      Number(
        this.recommendations
          ?.currentSkills
          ?.length
        || 0
      );


    return (
      overall > 0
      ||
      skillProgress > 0
      ||
      matchedSkills > 0
      ||
      currentSkills > 0
      ||
      this.careerGoals.length > 0
    );
  }


  // ==========================================
  // EMPLOYEE NAME
  // ==========================================

  get employeeName(): string {

    const user =
      this.authService
        .getLoggedInUser();


    const storedName =
      `${user?.firstName || ''} ${user?.lastName || ''}`
        .trim();


    return (
      this.careerProgress
        ?.employeeName
      ||
      storedName
      ||
      'Employee'
    );
  }


  // ==========================================
  // GOAL STATUS
  // ==========================================

  get goalStatus(): string {

    if (
      this.careerGoals.length === 0
    ) {

      return 'Not Set';
    }


    return (
      this.careerGoals[0]
        ?.status
      ||
      'Not Set'
    );
  }


  // ==========================================
  // TARGET DATE
  // ==========================================

  get targetDate(): string {

    if (
      this.careerGoals.length === 0
    ) {

      return 'Not Set';
    }


    return (
      this.careerGoals[0]
        ?.targetDate
      ||
      'Not Set'
    );
  }


  // ==========================================
  // CAN APPLY FOR JOB
  // ==========================================

  canApplyForJob(
    job: any
  ): boolean {

    if (
      !this.authService
        .isEmployee()
    ) {

      return false;
    }


    const careerReadiness =
      Number(
        this.careerProgress
          ?.overallProgressPercentage
        || 0
      );


    const skillReadiness =
      Number(
        this.careerProgress
          ?.skillProgressPercentage
        || 0
      );


    const matchedSkills =
      Number(
        this.careerProgress
          ?.skillsMatchedCount
        || 0
      );


    const matchScore =
      Number(
        job?.overallMatchScore
        ||
        job?.matchScore
        ||
        0
      );


    if (
      matchedSkills <= 0
    ) {

      return false;
    }


    return (
      careerReadiness
      >=
      this.MINIMUM_APPLICATION_READINESS

      ||

      skillReadiness
      >=
      this.MINIMUM_APPLICATION_READINESS

      ||

      matchScore
      >=
      this.MINIMUM_APPLICATION_READINESS
    );
  }


  // ==========================================
  // APPLICATION REQUIREMENT
  // ==========================================

  get applicationRequirementMessage():
    string {

    const matchedSkills =
      Number(
        this.careerProgress
          ?.skillsMatchedCount
        || 0
      );


    if (
      matchedSkills <= 0
    ) {

      return 'Add skills to your profile before applying for jobs.';
    }


    return (
      'Improve your career or skill readiness to '
      +
      this.MINIMUM_APPLICATION_READINESS
      +
      '% before applying.'
    );
  }


  // ==========================================
  // CHECK APPLICATION
  // ==========================================

  isApplied(
    jobPostingId: number
  ): boolean {

    return this.myApplications
      .some(
        (application: any) => {

          const id =
            Number(
              application
                ?.jobPostingId
              ??
              application
                ?.jobId
            );


          return (
            id
            ===
            Number(jobPostingId)
          );
        }
      );
  }


  // ==========================================
  // GET APPLICATION
  // ==========================================

  getApplication(
    jobPostingId: number
  ): any {

    return this.myApplications
      .find(
        (application: any) => {

          const id =
            Number(
              application
                ?.jobPostingId
              ??
              application
                ?.jobId
            );


          return (
            id
            ===
            Number(jobPostingId)
          );
        }
      );
  }


  // ==========================================
  // APPLICATION STATUS
  // ==========================================

  getApplicationStatus(
    jobPostingId: number
  ): string {

    const application =
      this.getApplication(
        jobPostingId
      );


    return (
      application?.status
      ||
      'APPLIED'
    );
  }


  // ==========================================
  // APPLY FOR JOB
  // ==========================================

  applyForJob(
    job: any
  ): void {

    this.applicationMessage = '';

    this.applicationError = '';


    if (
      !this.authService
        .isEmployee()
    ) {

      this.applicationError =
        'Only employees can apply for internal jobs.';

      return;
    }


    if (
      !this.canApplyForJob(job)
    ) {

      this.applicationError =
        this.applicationRequirementMessage;

      return;
    }


    const jobId =
      Number(
        job?.id
        ??
        job?.jobId
      );


    if (!jobId) {

      this.applicationError =
        'Job ID not found.';

      return;
    }


    if (
      this.isApplied(jobId)
    ) {

      this.applicationMessage =
        'You have already applied for this job.';

      return;
    }


    this.applyingJobId =
      jobId;


    this.careerService
      .applyForJob(jobId)
      .subscribe({

        next: (response: any) => {

          this.applyingJobId =
            null;


          this.applicationMessage =
            response?.message
            ||
            'Job application submitted successfully.';


          this.loadMyApplications();

          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Apply job error:',
            error
          );


          this.applyingJobId =
            null;


          this.applicationError =
            error?.error?.message
            ||
            'Unable to apply for this job.';


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD MY APPLICATIONS
  // ==========================================

  loadMyApplications(): void {

    if (
      !this.authService
        .isEmployee()
    ) {

      return;
    }


    this.careerService
      .getMyApplications()
      .subscribe({

        next: (response: any) => {

          this.myApplications =
            response?.data
            || [];

          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Applications error:',
            error
          );

          this.myApplications = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // WITHDRAW APPLICATION
  // ==========================================

  withdrawApplication(
    application: any
  ): void {

    if (
      !this.authService
        .isEmployee()
    ) {

      return;
    }


    if (
      !application?.id
    ) {

      return;
    }


    const confirmed =
      confirm(
        'Withdraw this job application?'
      );


    if (!confirmed) {

      return;
    }


    this.careerService
      .withdrawApplication(
        application.id
      )
      .subscribe({

        next: (response: any) => {

          this.applicationMessage =
            response?.message
            ||
            'Job application withdrawn successfully.';


          this.applicationError = '';


          this.loadMyApplications();

          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Withdraw application error:',
            error
          );


          this.applicationError =
            error?.error?.message
            ||
            'Unable to withdraw application.';


          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // ADMIN / HR COUNTS
  // ==========================================

  get totalCareerPaths(): number {

    return this.careerPaths.length;
  }


  get totalPromotionCriteria(): number {

    return this.promotionCriteria.length;
  }


  get totalOpenJobs(): number {

    return this.jobs
      .filter(
        (job: any) =>
          (
            job?.status
            ||
            ''
          )
            .toString()
            .trim()
            .toUpperCase()
          ===
          'OPEN'
      )
      .length;
  }


  get totalTrainingCourses(): number {

    return Number(
      this.trainingAnalytics
        ?.totalCourses
      ||
      0
    );
  }


  get totalEnrollments(): number {

    return Number(
      this.trainingAnalytics
        ?.totalEnrollments
      ||
      0
    );
  }


  get completionRate(): number {

    return Number(
      this.trainingAnalytics
        ?.overallCompletionPercentage
      ||
      0
    );
  }


  get averageProgress(): number {

    return Number(
      this.trainingAnalytics
        ?.averageTrainingProgressPercentage
      ||
      0
    );
  }

}