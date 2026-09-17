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
  forkJoin,
  of
} from 'rxjs';

import {
  catchError
} from 'rxjs/operators';

import {
  HttpClient
} from '@angular/common/http';

import {
  Chart,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
} from 'chart.js';

Chart.register(
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
);

import {
  AuthService
} from '../../services/auth';

import {
  CareerService
} from '../../services/career';

import {
  AiCareerService
} from '../../services/ai-career';


@Component({
  selector: 'app-career',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    RouterLinkActive
  ],

  templateUrl: './career.html',

  styleUrl: './career.css'
})
export class Career implements OnInit {

  // ============================================================
  // CONFIGURATION
  // ============================================================

  private readonly API_URL =
    'http://localhost:8090';

  readonly MINIMUM_APPLICATION_READINESS = 40;


  // ============================================================
  // DEADLINE ALERT
  // ============================================================

  private deadlineAlertShown = false;


  // ============================================================
  // CAREER AI ASSISTANT
  // ============================================================

  chatMessage = '';

  chatReply = '';

  chatLoading = false;

  chatError = '';

  chatMessages: { sender: 'user' | 'assistant'; text: string }[] = [];



  // ============================================================
  // USER
  // ============================================================

  employeeId = 0;

  employeeEmail = '';

  employeeFirstName = '';

  employeeLastName = '';


  // ============================================================
  // CAREER DATA
  // ============================================================

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

  private trainingEffectivenessChart: Chart | null = null;

  private workforceOverviewChart: Chart | null = null;


  // ============================================================
  // CAREER AI ASSISTANT
  // ============================================================

  sendCareerChat(): void {

    const message = this.chatMessage?.trim();

    if (!message || this.chatLoading) {
      return;
    }

    this.chatError = '';
    this.chatLoading = true;

    this.chatMessages.push({
      sender: 'user',
      text: message
    });

    const skills = this.getCurrentEmployeeSkills();

    const missingSkills =
      Array.isArray(this.recommendations?.missingSkills)
        ? this.cleanStringList(this.recommendations.missingSkills)
        : this.getCareerMissingSkills();

    this.aiCareerService
      .sendMessage({
        message: message,
        employeeId: this.employeeId,
        targetRole: this.targetRole !== 'Not Set'
          ? this.targetRole
          : '',
        skills: skills,
        missingSkills: missingSkills
      })
      .subscribe({

        next: (response) => {

          const reply =
            response?.reply ||
            'I could not generate a career suggestion right now.';

          this.chatReply = reply;

          this.chatMessages.push({
            sender: 'assistant',
            text: reply
          });

          this.chatMessage = '';
          this.chatLoading = false;
          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error('Career AI error:', error);

          this.chatError =
            error?.error?.message ||
            'Unable to connect to the Career Assistant. Please try again.';

          this.chatLoading = false;
          this.cdr.detectChanges();
        }
      });
  }


  askCareerQuestion(question: string): void {

    this.chatMessage = question;
    this.sendCareerChat();
  }


  clearCareerChat(): void {

    this.chatMessage = '';
    this.chatReply = '';
    this.chatError = '';
    this.chatMessages = [];
  }


  private getCareerMissingSkills(): string[] {

    const missing =
      this.careerProgress?.missingSkills ||
      this.careerProgress?.skillsMissing ||
      this.recommendations?.missingSkillList ||
      this.recommendations?.skillsMissing;

    return Array.isArray(missing)
      ? this.cleanStringList(missing)
      : [];
  }


  // ============================================================
  // CAREER GOAL FORM
  // ============================================================

  showGoalForm = false;

  selectedTargetRole = '';

  selectedTargetDate = '';

  selectedGoalStatus = 'IN_PROGRESS';

  goalNotes = '';

  savingGoal = false;

  goalMessage = '';

  goalError = '';


  // ============================================================
  // UI
  // ============================================================

  loading = true;

  errorMessage = '';

  applicationMessage = '';

  applicationError = '';

  applyingJobId: number | null = null;


  // ============================================================
  // CONSTRUCTOR
  // ============================================================

  constructor(
    public authService: AuthService,

    private careerService: CareerService,

    private http: HttpClient,

    private cdr: ChangeDetectorRef,

    private aiCareerService: AiCareerService
  ) {}


  // ============================================================
  // INITIALIZE
  // ============================================================

  ngOnInit(): void {

    const user =
      this.authService.getLoggedInUser();

    console.log(
      'Logged in user:',
      user
    );


    if (!user) {

      this.errorMessage =
        'Logged in user not found. Please login again.';

      this.loading = false;

      return;
    }


    this.employeeId =
      Number(
        user.id
      );

    this.employeeEmail =
      user.email ?? '';

    this.employeeFirstName =
      user.firstName ?? '';

    this.employeeLastName =
      user.lastName ?? '';


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


  // ============================================================
  // EMPLOYEE CAREER LOAD
  // ============================================================

  loadEmployeeCareer(): void {

    this.loading = true;

    this.errorMessage = '';

    forkJoin({

      paths:
        this.http
          .get<any>(
            `${this.API_URL}/api/career/paths`
          )
          .pipe(
            catchError(
              error => {

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
        this.http
          .get<any>(
            `${this.API_URL}/api/career/goals`
          )
          .pipe(
            catchError(
              error => {

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
        this.http
          .get<any>(
            `${this.API_URL}/api/career/recommendations`
          )
          .pipe(
            catchError(
              error => {

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
        this.http
          .get<any>(
            `${this.API_URL}/api/career/progress`
          )
          .pipe(
            catchError(
              error => {

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
              error => {

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
              error => {

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
              error => {

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
              error => {

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

      next: (
        result: any
      ) => {

        console.log(
          'Career API result:',
          result
        );


        this.careerPaths =
          Array.isArray(
            result.paths?.data
          )
            ? result.paths.data
            : [];


        this.careerGoals =
          Array.isArray(
            result.goals?.data
          )
            ? result.goals.data
            : [];


        this.recommendations =
          result.recommendations?.data
          ??
          null;


        this.careerProgress =
          result.progress?.data
          ??
          null;


        this.promotionCriteria =
          Array.isArray(
            result.criteria?.data
          )
            ? result.criteria.data
            : [];


        this.jobs =
          Array.isArray(
            result.jobs?.data
          )
            ? result.jobs.data
            : [];


        this.matchedJobs =
          Array.isArray(
            result.matchedJobs?.data
          )
            ? result.matchedJobs.data
            : [];


        this.myApplications =
          Array.isArray(
            result.applications?.data
          )
            ? result.applications.data
            : [];


        // Load goal values into the form
        this.loadGoalIntoForm();


        // Check whether the career goal target date has passed
        this.checkDeadlineAlert();


        if (
          this.promotionCriteria.length > 0
        ) {

          const criteriaId =
            Number(
              this.promotionCriteria[0]?.id
            );


          if (
            criteriaId
          ) {

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


      error: (
        error
      ) => {

        console.error(
          'Career loading error:',
          error
        );

        this.errorMessage =
          'Unable to load career information.';

        this.loading = false;

        this.cdr.detectChanges();

        setTimeout(() => {
          this.renderAnalyticsCharts();
        }, 0);
      }

    });
  }


  // ============================================================
  // ADMIN / HR
  // ============================================================

  loadCareerManagement(): void {

    this.loading = true;

    forkJoin({

      paths:
        this.http
          .get<any>(
            `${this.API_URL}/api/career/paths`
          )
          .pipe(
            catchError(
              () =>
                of({
                  data: []
                })
            )
          ),


      criteria:
        this.careerService
          .getPromotionCriteria()
          .pipe(
            catchError(
              () =>
                of({
                  data: []
                })
            )
          ),


      jobs:
        this.careerService
          .getJobs()
          .pipe(
            catchError(
              () =>
                of({
                  data: []
                })
            )
          ),


      analytics:
        this.http
          .get<any>(
            `${this.API_URL}/api/analytics/training`
          )
          .pipe(
            catchError(
              () =>
                of({
                  data: null
                })
            )
          )

    }).subscribe({

      next: (
        result: any
      ) => {

        this.careerPaths =
          result.paths?.data
          ||
          [];


        this.promotionCriteria =
          result.criteria?.data
          ||
          [];


        this.jobs =
          result.jobs?.data
          ||
          [];


        this.trainingAnalytics =
          result.analytics?.data
          ||
          null;


        this.loading = false;

        this.cdr.detectChanges();

        setTimeout(() => {
          this.renderAnalyticsCharts();
        }, 0);
      },


      error: (
        error
      ) => {

        console.error(
          'Career management error:',
          error
        );

        this.errorMessage =
          'Unable to load career management information.';

        this.loading = false;

        this.cdr.detectChanges();

        setTimeout(() => {
          this.renderAnalyticsCharts();
        }, 0);
      }

    });
  }


  // ============================================================
  // CAREER GOAL FORM
  // ============================================================

  loadGoalIntoForm(): void {

    if (
      this.careerGoals.length === 0
    ) {

      this.selectedTargetRole = '';

      this.selectedTargetDate = '';

      this.selectedGoalStatus =
        'IN_PROGRESS';

      this.goalNotes = '';

      return;
    }


    const goal =
      this.careerGoals[0];


    this.selectedTargetRole =
      goal?.targetRole
      ??
      '';


    this.selectedTargetDate =
      this.toDateInputValue(
        goal?.targetDate
      );


    this.selectedGoalStatus =
      goal?.status
      ??
      'IN_PROGRESS';


    this.goalNotes =
      goal?.notes
      ??
      '';
  }


  openGoalForm(): void {

    this.goalMessage = '';

    this.goalError = '';

    this.loadGoalIntoForm();

    this.showGoalForm = true;

    this.cdr.detectChanges();
  }


  closeGoalForm(): void {

    if (
      this.savingGoal
    ) {

      return;
    }


    this.showGoalForm = false;

    this.goalMessage = '';

    this.goalError = '';

    this.cdr.detectChanges();
  }


  saveCareerGoal(): void {

    this.goalMessage = '';

    this.goalError = '';


    if (
      !this.selectedTargetRole
      ||
      !this.selectedTargetRole.trim()
    ) {

      this.goalError =
        'Please select a target role.';

      return;
    }


    if (
      !this.selectedTargetDate
    ) {

      this.goalError =
        'Please select a target date.';

      return;
    }


    this.savingGoal = true;


    const request = {

      targetRole:
        this.selectedTargetRole.trim(),

      targetDate:
        this.selectedTargetDate,

      status:
        this.selectedGoalStatus
        ||
        'IN_PROGRESS',

      notes:
        this.goalNotes?.trim()
        ||
        ''
    };


    console.log(
      'Saving career goal:',
      request
    );


    const existingGoal =
      this.careerGoals.length > 0
        ? this.careerGoals[0]
        : null;


    const request$ =
      existingGoal?.id

        ? this.http.put<any>(
            `${this.API_URL}/api/career/goals/${existingGoal.id}`,
            request
          )

        : this.http.post<any>(
            `${this.API_URL}/api/career/goals`,
            request
          );


    request$
      .subscribe({

        next: (
          response: any
        ) => {

          console.log(
            'Career goal saved:',
            response
          );


          this.savingGoal = false;

          this.goalMessage =
            response?.message
            ||
            'Career goal saved successfully.';


          this.showGoalForm = false;


          /*
           * Reload from the database.
           *
           * This updates:
           * - Target Role
           * - Skills Matched
           * - Skill Progress
           * - Career Progress
           * - Missing Skills
           */
          this.loadEmployeeCareer();


          this.cdr.detectChanges();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Career goal save error:',
            error
          );


          this.savingGoal = false;


          this.goalError =
            error?.error?.message
            ||
            'Unable to save career goal.';


          this.cdr.detectChanges();
        }

      });
  }


  // ============================================================
  // SELECT CAREER PATH
  // ============================================================

  selectCareerPath(
    path: any
  ): void {

    this.selectedTargetRole =
      path?.targetRole
      ??
      '';


    if (
      !this.selectedTargetDate
    ) {

      const date =
        new Date();

      date.setFullYear(
        date.getFullYear() + 1
      );

      this.selectedTargetDate =
        date
          .toISOString()
          .split('T')[0];
    }


    this.selectedGoalStatus =
      'IN_PROGRESS';


    this.goalMessage = '';

    this.goalError = '';

    this.showGoalForm = true;

    this.cdr.detectChanges();

    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }


  // ============================================================
  // PROMOTION
  // ============================================================

  loadPromotionEvaluation(
    criteriaId: number
  ): void {

    this.careerService
      .getPromotionEvaluation(
        this.employeeId,
        criteriaId
      )
      .subscribe({

        next: (
          response: any
        ) => {

          this.promotionEvaluation =
            response?.data
            ??
            null;

          this.loading = false;

          this.cdr.detectChanges();
        },


        error: (
          error: any
        ) => {

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


  // ============================================================
  // CAREER STATE
  // ============================================================

  get hasCareerStarted(): boolean {

    return (
      this.careerGoals.length > 0
    );
  }


  get employeeName(): string {

    const user =
      this.authService.getLoggedInUser();


    const storedName =
      `${user?.firstName || ''} ${user?.lastName || ''}`
        .trim();


    return (
      this.careerProgress?.employeeName
      ||
      storedName
      ||
      'Employee'
    );
  }


  get targetRole(): string {

    return (
      this.careerProgress?.targetRole
      ||
      this.careerGoals[0]?.targetRole
      ||
      'Not Set'
    );
  }


  get goalStatus(): string {

    return (
      this.careerGoals[0]?.status
      ||
      this.careerProgress?.goalStatus
      ||
      'Not Set'
    );
  }


  get targetDate(): string {

    return (
      this.careerGoals[0]?.targetDate
      ||
      this.careerProgress?.targetDate
      ||
      'Not Set'
    );
  }


  // ============================================================
  // PROGRESS VALUES
  // ============================================================

  get overallProgress(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.overallProgressPercentage
        ??
        0
      )
    );
  }


  get skillProgress(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.skillProgressPercentage
        ??
        0
      )
    );
  }


  // ============================================================
  // COURSE / LEARNING PROGRESS
  // ============================================================

  get courseProgress(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.courseProgressPercentage
        ??
        0
      )
    );
  }


  get courseContribution(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.courseContributionScore
        ??
        0
      )
    );
  }


  // ============================================================
  // CERTIFICATION PROGRESS
  // ============================================================

  get certificationProgress(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.certificationProgressPercentage
        ??
        0
      )
    );
  }


  get certificationContribution(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.certificationContributionScore
        ??
        0
      )
    );
  }


  // ============================================================
  // EXPERIENCE PROGRESS
  // ============================================================

  get experienceProgress(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.experienceProgressPercentage
        ??
        0
      )
    );
  }


  get experienceContribution(): number {

    return this.roundValue(
      Number(
        this.careerProgress
          ?.experienceContributionScore
        ??
        0
      )
    );
  }


  // ============================================================
  // SKILLS
  // ============================================================

  get matchedSkillCount(): number {

    return Number(
      this.careerProgress
        ?.skillsMatchedCount
      ??
      0
    );
  }


  get totalRequiredSkillCount(): number {

    return Number(
      this.careerProgress
        ?.skillsTotalCount
      ??
      0
    );
  }


  get readinessStage(): string {

    return (
      this.careerProgress
        ?.readinessStage
      ||
      'EARLY_STAGE'
    );
  }


  // ============================================================
  // CAREER DEADLINE
  // ============================================================

  get isTargetDateExpired(): boolean {

    if (
      !this.targetDate
      ||
      this.targetDate === 'Not Set'
    ) {

      return false;
    }


    /*
     * If the goal is already completed,
     * do not show an expired deadline alert.
     */
    if (
      String(
        this.goalStatus
      )
        .trim()
        .toUpperCase()
      ===
      'COMPLETED'
    ) {

      return false;
    }


    const target =
      new Date(
        this.targetDate
      );


    const today =
      new Date();


    if (
      Number.isNaN(
        target.getTime()
      )
    ) {

      return false;
    }


    target.setHours(
      0,
      0,
      0,
      0
    );


    today.setHours(
      0,
      0,
      0,
      0
    );


    return (
      target.getTime()
      <
      today.getTime()
    );
  }


  get daysUntilTargetDate(): number {

    if (
      !this.targetDate
      ||
      this.targetDate === 'Not Set'
    ) {

      return 0;
    }


    const target =
      new Date(
        this.targetDate
      );


    const today =
      new Date();


    if (
      Number.isNaN(
        target.getTime()
      )
    ) {

      return 0;
    }


    target.setHours(
      0,
      0,
      0,
      0
    );


    today.setHours(
      0,
      0,
      0,
      0
    );


    const difference =
      target.getTime()
      -
      today.getTime();


    return Math.ceil(
      difference /
      (
        1000 *
        60 *
        60 *
        24
      )
    );
  }


  private checkDeadlineAlert(): void {

    if (
      !this.isTargetDateExpired
      ||
      this.deadlineAlertShown
    ) {

      return;
    }


    this.deadlineAlertShown = true;


    setTimeout(
      () => {

        window.alert(

          'ADMIN ALERT\n\n' +

          'Your career goal target date has passed.\n\n' +

          'Target Date: ' +
          this.formatDate(
            this.targetDate
          ) +

          '\n\n' +

          'Current Career Readiness: ' +
          this.overallProgress +
          '%' +

          '\n\n' +

          'Please complete your pending learning, certifications and experience requirements.'

        );

      },

      500
    );
  }


  // ============================================================
  // CAREER ROADMAP
  // ============================================================

  get careerRoadmapSteps(): any[] {

    const skillsCompleted =
      this.totalRequiredSkillCount > 0
      &&
      this.matchedSkillCount >=
      this.totalRequiredSkillCount;


    /*
     * IMPORTANT:
     *
     * Backend uses:
     * courseProgressPercentage
     *
     * NOT:
     * learningProgressPercentage
     */
    const learningProgress =
      this.courseProgress;


    const learningContribution =
      this.courseContribution;


    const certificationProgress =
      this.certificationProgress;


    const certificationContribution =
      this.certificationContribution;


    const experienceProgress =
      this.experienceProgress;


    const experienceContribution =
      this.experienceContribution;


    return [

      {
        number: 1,

        title:
          'Choose Target Role',

        description:
          'Select the career role you want to achieve.',

        completed:
          this.careerGoals.length > 0,

        active:
          this.careerGoals.length === 0
      },


      {
        number: 2,

        title:
          'Build Required Skills',

        description:
          `${this.matchedSkillCount} of ${this.totalRequiredSkillCount} required skills matched.`,

        completed:
          skillsCompleted,

        active:
          !skillsCompleted
      },


      {
        number: 3,

        title:
          'Complete Learning',

        description:
          `Course progress: ${learningProgress}%. Contribution: ${learningContribution} points.`,

        completed:
          learningProgress >= 100,

        active:
          skillsCompleted &&
          learningProgress < 100
      },


      {
        number: 4,

        title:
          'Complete Certifications',

        description:
          `Certification progress: ${certificationProgress}%. Contribution: ${certificationContribution} points.`,

        completed:
          certificationProgress >= 100,

        active:
          skillsCompleted &&
          learningProgress >= 100 &&
          certificationProgress < 100
      },


      {
        number: 5,

        title:
          'Meet Experience Requirement',

        description:
          `Experience progress: ${experienceProgress}%. Contribution: ${experienceContribution} points.`,

        completed:
          experienceProgress >= 100,

        active:
          skillsCompleted &&
          experienceProgress < 100
      },


      {
        number: 6,

        title:
          'Apply for Internal Job',

        description:
          'Apply when the job requirements and readiness threshold are satisfied.',

        completed:
          this.overallProgress >=
          this.MINIMUM_APPLICATION_READINESS,

        active:
          this.overallProgress >=
          this.MINIMUM_APPLICATION_READINESS
      }

    ];
  }


  // ============================================================
  // JOB REQUIRED SKILLS
  // ============================================================

  getJobRequiredSkills(
    job: any
  ): string[] {

    if (
      !job
    ) {

      return [];
    }


    if (
      Array.isArray(
        job.requiredSkillList
      )
    ) {

      return this.cleanStringList(
        job.requiredSkillList
      );
    }


    if (
      Array.isArray(
        job.requiredSkills
      )
    ) {

      return this.cleanStringList(
        job.requiredSkills
      );
    }


    if (
      typeof job.requiredSkills ===
      'string'
    ) {

      return this.cleanStringList(
        job.requiredSkills
          .split(',')
      );
    }


    if (
      Array.isArray(
        job.skillDetails
      )
    ) {

      return this.cleanStringList(
        job.skillDetails.map(
          (detail: any) =>
            detail?.skillName
        )
      );
    }


    return [];
  }


  // ============================================================
  // EMPLOYEE CURRENT SKILLS
  // ============================================================

  getCurrentEmployeeSkills(): string[] {

    const current =
      this.recommendations?.currentSkills;


    if (
      !Array.isArray(
        current
      )
    ) {

      return [];
    }


    return this.cleanStringList(
      current
    );
  }


  // ============================================================
  // CHECK JOB SKILL
  // ============================================================

  hasRequiredJobSkill(
    skill: string
  ): boolean {

    const employeeSkills =
      this.getCurrentEmployeeSkills()
        .map(
          value =>
            this.normalizeSkillName(
              value
            )
        );


    return employeeSkills.includes(
      this.normalizeSkillName(
        skill
      )
    );
  }


  // ============================================================
  // MISSING JOB SKILLS
  // ============================================================

  getMissingJobSkills(
    job: any
  ): string[] {

    return this.getJobRequiredSkills(
      job
    ).filter(
      skill =>
        !this.hasRequiredJobSkill(
          skill
        )
    );
  }


  // ============================================================
  // JOB SKILLS COMPLETED
  // ============================================================

  hasAllRequiredJobSkills(
    job: any
  ): boolean {

    const required =
      this.getJobRequiredSkills(
        job
      );


    if (
      required.length === 0
    ) {

      return true;
    }


    return (
      this.getMissingJobSkills(
        job
      ).length === 0
    );
  }


  // ============================================================
  // JOB READINESS
  // ============================================================

  getJobReadinessMessage(
    job: any
  ): string {

    const missing =
      this.getMissingJobSkills(
        job
      );


    if (
      missing.length > 0
    ) {

      return (
        `Learn these skills first: ${missing.join(', ')}.`
      );
    }


    const jobMatch =
      Number(
        job?.overallMatchScore
        ??
        job?.matchScore
        ??
        0
      );


    if (
      jobMatch <
      this.MINIMUM_APPLICATION_READINESS
    ) {

      return (
        `Current match is ${jobMatch}%. Improve your profile before applying.`
      );
    }


    return (
      'You meet the listed skill requirements for this job.'
    );
  }


  // ============================================================
  // CAN APPLY
  // ============================================================

  canApplyForJob(
    job: any
  ): boolean {

    if (
      !this.authService.isEmployee()
    ) {

      return false;
    }


    const missingSkills =
      this.getMissingJobSkills(
        job
      );


    const matchScore =
      Number(
        job?.overallMatchScore
        ??
        job?.matchScore
        ??
        0
      );


    const careerReadiness =
      Number(
        this.careerProgress
          ?.overallProgressPercentage
        ??
        0
      );


    const skillReadiness =
      Number(
        this.careerProgress
          ?.skillProgressPercentage
        ??
        0
      );


    /*
     * A job can be applied for when:
     *
     * 1. required job skills are satisfied
     *
     * AND
     *
     * 2. the profile reaches the minimum readiness
     * OR the job match reaches the minimum score.
     */

    if (
      missingSkills.length > 0
    ) {

      return false;
    }


    return (
      careerReadiness >=
        this.MINIMUM_APPLICATION_READINESS
      ||
      skillReadiness >=
        this.MINIMUM_APPLICATION_READINESS
      ||
      matchScore >=
        this.MINIMUM_APPLICATION_READINESS
    );
  }


  // ============================================================
  // APPLICATION REQUIREMENT
  // ============================================================

  get applicationRequirementMessage(): string {

    return (
      'Meet the required skills and improve your profile readiness before applying.'
    );
  }


  // ============================================================
  // FIND APPLICATION JOB ID
  // ============================================================

  private getApplicationJobId(
    application: any
  ): number | null {

    const possibleIds = [

      application?.jobPostingId,

      application?.jobId,

      application?.jobPosting?.id,

      application?.jobPosting?.jobId,

      application?.job?.id,

      application?.job?.jobId

    ];


    for (
      const value
      of possibleIds
    ) {

      const id =
        Number(
          value
        );


      if (
        Number.isFinite(id)
        &&
        id > 0
      ) {

        return id;
      }
    }


    return null;
  }


  // ============================================================
  // IS APPLIED
  // ============================================================

  isApplied(
    jobPostingId: number
  ): boolean {

    return this.myApplications.some(
      application => {

        const id =
          this.getApplicationJobId(
            application
          );


        return (
          id !== null
          &&
          id ===
          Number(
            jobPostingId
          )
        );
      }
    );
  }


  // ============================================================
  // GET APPLICATION
  // ============================================================

  getApplication(
    jobPostingId: number
  ): any {

    return this.myApplications.find(
      application => {

        const id =
          this.getApplicationJobId(
            application
          );


        return (
          id !== null
          &&
          id ===
          Number(
            jobPostingId
          )
        );
      }
    );
  }


  // ============================================================
  // APPLICATION STATUS
  // ============================================================

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
      application?.applicationStatus
      ||
      'APPLIED'
    );
  }


  // ============================================================
  // APPLY FOR JOB
  // ============================================================

  applyForJob(
    job: any
  ): void {

    this.applicationMessage = '';

    this.applicationError = '';


    if (
      !this.authService.isEmployee()
    ) {

      this.applicationError =
        'Only employees can apply for internal jobs.';

      return;
    }


    if (
      !this.canApplyForJob(
        job
      )
    ) {

      this.applicationError =
        this.getJobReadinessMessage(
          job
        );

      return;
    }


    const jobId =
      Number(
        job?.id
        ??
        job?.jobId
      );


    if (
      !Number.isFinite(
        jobId
      )
      ||
      jobId <= 0
    ) {

      this.applicationError =
        'Job ID not found.';

      return;
    }


    if (
      this.isApplied(
        jobId
      )
    ) {

      this.applicationMessage =
        'You have already applied for this job.';

      return;
    }


    this.applyingJobId =
      jobId;


    /*
     * Use the existing CareerService application method.
     * This keeps the request format consistent with the backend.
     */
    this.careerService
      .applyForJob(
        jobId
      )
      .subscribe({

        next: (
          response: any
        ) => {

          console.log(
            'Application submitted:',
            response
          );


          this.applyingJobId =
            null;


          this.applicationMessage =
            response?.message
            ||
            'Job application submitted successfully.';


          /*
           * Reload ALL career data.
           *
           * This refreshes:
           *
           * - Matched jobs
           * - Application status
           * - Job match
           * - Career data
           */
          this.loadEmployeeCareer();


          this.cdr.detectChanges();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Apply job error:',
            error
          );


          this.applyingJobId =
            null;


          this.applicationError =
            error?.error?.message
            ||
            error?.error?.data?.message
            ||
            'Unable to apply for this job.';


          this.cdr.detectChanges();
        }

      });
  }


  // ============================================================
  // LOAD APPLICATIONS
  // ============================================================

  loadMyApplications(): void {

    if (
      !this.authService.isEmployee()
    ) {

      return;
    }


    this.careerService
      .getMyApplications()
      .subscribe({

        next: (
          response: any
        ) => {

          this.myApplications =
            Array.isArray(
              response?.data
            )
              ? response.data
              : [];

          this.cdr.detectChanges();
        },


        error: (
          error
        ) => {

          console.error(
            'Applications error:',
            error
          );

          this.myApplications = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ============================================================
  // WITHDRAW
  // ============================================================

  withdrawApplication(
    application: any
  ): void {

    if (
      !this.authService.isEmployee()
    ) {

      return;
    }


    if (
      !application?.id
    ) {

      return;
    }


    if (
      !confirm(
        'Withdraw this job application?'
      )
    ) {

      return;
    }


    this.careerService
      .withdrawApplication(
        application.id
      )
      .subscribe({

        next: (
          response: any
        ) => {

          this.applicationMessage =
            response?.message
            ||
            'Job application withdrawn successfully.';


          this.applicationError = '';


          this.loadEmployeeCareer();

          this.cdr.detectChanges();
        },


        error: (
          error
        ) => {

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


  // ============================================================
  // ANALYTICS CHARTS
  // ============================================================

  private renderAnalyticsCharts(): void {

    if (!this.trainingAnalytics) {
      return;
    }

    const trainingCanvas =
      document.getElementById('trainingEffectivenessChart') as HTMLCanvasElement | null;

    const workforceCanvas =
      document.getElementById('workforceOverviewChart') as HTMLCanvasElement | null;

    if (!trainingCanvas || !workforceCanvas) {
      return;
    }

    if (this.trainingEffectivenessChart) {
      this.trainingEffectivenessChart.destroy();
    }

    if (this.workforceOverviewChart) {
      this.workforceOverviewChart.destroy();
    }

    this.trainingEffectivenessChart = new Chart(
      trainingCanvas,
      {
        type: 'bar',
        data: {
          labels: [
            'Enrollments',
            'Avg Progress',
            'Completion Rate'
          ],
          datasets: [
            {
              label: 'Training Effectiveness',
              data: [
                this.totalEnrollments,
                this.averageProgress,
                this.completionRate
              ],
              backgroundColor: [
                '#ff6a00',
                '#3b82f6',
                '#22c55e'
              ],
              borderRadius: 7,
              borderSkipped: false
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const label = context.label || '';
                  const value = context.parsed.y ?? 0;
                  return label === 'Enrollments'
                    ? ` ${value}`
                    : ` ${value}%`;
                }
              }
            }
          },
          scales: {
            x: {
              grid: {
                display: false
              },
              ticks: {
                color: '#8ca9c9'
              }
            },
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(140, 169, 201, 0.12)'
              },
              ticks: {
                color: '#8ca9c9'
              }
            }
          }
        }
      }
    );

    this.workforceOverviewChart = new Chart(
      workforceCanvas,
      {
        type: 'bar',
        data: {
          labels: [
            'Courses',
            'Career Paths',
            'Promotion Criteria',
            'Open Jobs'
          ],
          datasets: [
            {
              label: 'Workforce Overview',
              data: [
                this.totalTrainingCourses,
                this.totalCareerPaths,
                this.totalPromotionCriteria,
                this.totalOpenJobs
              ],
              backgroundColor: [
                '#8b5cf6',
                '#06b6d4',
                '#f59e0b',
                '#22c55e'
              ],
              borderRadius: 7,
              borderSkipped: false
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            x: {
              grid: {
                display: false
              },
              ticks: {
                color: '#8ca9c9'
              }
            },
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0,
                color: '#8ca9c9'
              },
              grid: {
                color: 'rgba(140, 169, 201, 0.12)'
              }
            }
          }
        }
      }
    );
  }


  // ============================================================
  // ADMIN COUNTS
  // ============================================================

  get totalCareerPaths(): number {

    return this.careerPaths.length;
  }


  get totalPromotionCriteria(): number {

    return this.promotionCriteria.length;
  }


  get totalOpenJobs(): number {

    return this.jobs.filter(
      job =>
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
    ).length;
  }


  get totalTrainingCourses(): number {

    return Number(
      this.trainingAnalytics
        ?.totalCourses
      ??
      0
    );
  }


  get totalEnrollments(): number {

    return Number(
      this.trainingAnalytics
        ?.totalEnrollments
      ??
      0
    );
  }


  get completionRate(): number {

    return Number(
      this.trainingAnalytics
        ?.overallCompletionPercentage
      ??
      0
    );
  }


  get averageProgress(): number {

    return Number(
      this.trainingAnalytics
        ?.averageTrainingProgressPercentage
      ??
      0
    );
  }


  // ============================================================
  // HELPERS
  // ============================================================

  private cleanStringList(
    values: any[]
  ): string[] {

    return values
      .map(
        value =>
          String(
            value
          )
            .trim()
      )
      .filter(
        value =>
          value.length > 0
      );
  }


  private normalizeSkillName(
    skill: string
  ): string {

    return skill
      .trim()
      .toLowerCase()
      .replace(
        /[-_]/g,
        ' '
      )
      .replace(
        /\s+/g,
        ' '
      );
  }


  private roundValue(
    value: number
  ): number {

    if (
      !Number.isFinite(
        value
      )
    ) {

      return 0;
    }


    return Number(
      value.toFixed(
        1
      )
    );
  }


  // ============================================================
  // FORMAT DATE
  // ============================================================

  formatDate(
    value: any
  ): string {

    if (
      !value
      ||
      value === 'Not Set'
    ) {

      return 'Not Set';
    }


    const date =
      new Date(
        value
      );


    if (
      Number.isNaN(
        date.getTime()
      )
    ) {

      return String(
        value
      ).substring(
        0,
        10
      );
    }


    return date.toLocaleDateString(
      'en-IN',
      {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }
    );
  }


  // ============================================================
  // DATE INPUT HELPER
  // ============================================================

  private toDateInputValue(
    value: any
  ): string {

    if (
      !value
    ) {

      return '';
    }


    const date =
      new Date(
        value
      );


    if (
      Number.isNaN(
        date.getTime()
      )
    ) {

      return String(
        value
      ).substring(
        0,
        10
      );
    }


    return date
      .toISOString()
      .split('T')[0];
  }

}