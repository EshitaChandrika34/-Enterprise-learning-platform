import {
  Component,
  OnInit,
  AfterViewInit,
  OnDestroy,
  ChangeDetectorRef,
  ElementRef,
  ViewChild
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { RouterModule } from '@angular/router';

import {
  Chart,
  registerables
} from 'chart.js';

import { HttpClient } from '@angular/common/http';

import { forkJoin, of } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { AuthService } from '../../services/auth';

import { CareerService } from '../../services/career';


Chart.register(...registerables);


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


export class Analytics
  implements OnInit, AfterViewInit, OnDestroy {


  // ============================================================
  // CONFIGURATION
  // ============================================================

  private readonly API_URL =
    'http://localhost:8090';


  // ============================================================
  // CHART REFERENCES
  // ============================================================

  @ViewChild('trainingChart')
  private trainingChartElement?: ElementRef<HTMLCanvasElement>;

  @ViewChild('workforceChart')
  private workforceChartElement?: ElementRef<HTMLCanvasElement>;

  @ViewChild('careerChart')
  private careerChartElement?: ElementRef<HTMLCanvasElement>;

  @ViewChild('jobsChart')
  private jobsChartElement?: ElementRef<HTMLCanvasElement>;


  // ============================================================
  // CHART INSTANCES
  // ============================================================

  private trainingChart: Chart | null = null;

  private workforceChart: Chart | null = null;

  private careerChart: Chart | null = null;

  private jobsChart: Chart | null = null;


  // ============================================================
  // DATA
  // ============================================================

  analyticsData: any = null;

  careerPathsData: any[] = [];

  promotionCriteriaData: any[] = [];

  jobsData: any[] = [];


  // ============================================================
  // STATE
  // ============================================================

  loading = false;

  errorMessage = '';


  // ============================================================
  // CONSTRUCTOR
  // ============================================================

  constructor(
    private http: HttpClient,

    private cdr: ChangeDetectorRef,

    public authService: AuthService,

    private careerService: CareerService
  ) {}


  // ============================================================
  // INITIALIZATION
  // ============================================================

  ngOnInit(): void {

    this.loadAnalytics();

  }


  // ============================================================
  // VIEW INITIALIZATION
  // ============================================================

  ngAfterViewInit(): void {

    setTimeout(() => {

      this.renderCharts();

    });

  }


  // ============================================================
  // LOAD ALL ANALYTICS DATA
  // ============================================================

  loadAnalytics(): void {

    this.loading = true;

    this.errorMessage = '';


    forkJoin({

      // ------------------------------------------
      // TRAINING ANALYTICS
      // ------------------------------------------

      analytics:

        this.http
          .get<any>(
            `${this.API_URL}/api/analytics/training`
          )
          .pipe(

            catchError(error => {

              console.error(
                'Training analytics error:',
                error
              );

              return of({
                data: null
              });

            })

          ),


      // ------------------------------------------
      // CAREER PATHS
      // ------------------------------------------

      paths:

        this.http
          .get<any>(
            `${this.API_URL}/api/career/paths`
          )
          .pipe(

            catchError(error => {

              console.error(
                'Career paths error:',
                error
              );

              return of({
                data: []
              });

            })

          ),


      // ------------------------------------------
      // PROMOTION CRITERIA
      // ------------------------------------------

      criteria:

        this.careerService
          .getPromotionCriteria()
          .pipe(

            catchError(error => {

              console.error(
                'Promotion criteria error:',
                error
              );

              return of({
                data: []
              });

            })

          ),


      // ------------------------------------------
      // OPEN JOBS
      // ------------------------------------------

      jobs:

        this.careerService
          .getJobs()
          .pipe(

            catchError(error => {

              console.error(
                'Jobs error:',
                error
              );

              return of({
                data: []
              });

            })

          )

    }).subscribe({

      next: (result: any) => {

        console.log(
          'Complete Analytics Data:',
          result
        );


        // ==========================================
        // TRAINING DATA
        // ==========================================

        this.analyticsData =

          result.analytics?.analytics?.data ??

          result.analytics?.data ??

          result.analytics ??

          null;


        // ==========================================
        // CAREER PATHS
        // ==========================================

        this.careerPathsData =

          Array.isArray(
            result.paths?.data
          )

            ? result.paths.data

            : Array.isArray(result.paths)

              ? result.paths

              : [];


        // ==========================================
        // PROMOTION CRITERIA
        // ==========================================

        this.promotionCriteriaData =

          Array.isArray(
            result.criteria?.data
          )

            ? result.criteria.data

            : Array.isArray(result.criteria)

              ? result.criteria

              : [];


        // ==========================================
        // JOBS
        // ==========================================

        const allJobs =

          Array.isArray(result.jobs?.data)

            ? result.jobs.data

            : Array.isArray(result.jobs)

              ? result.jobs

              : [];


        // Only OPEN jobs

        this.jobsData = allJobs.filter(
          (job: any) =>

            (
              job?.status ??
              ''
            )
              .toString()
              .trim()
              .toUpperCase() === 'OPEN'
        );


        // ==========================================
        // FINISHED
        // ==========================================

        this.loading = false;

        this.cdr.detectChanges();


        setTimeout(() => {

          this.renderCharts();

        });

      },


      error: (error) => {

        console.error(
          'Analytics loading error:',
          error
        );


        this.loading = false;

        this.errorMessage =
          'Unable to load analytics data. Please make sure the backend is running.';

        this.cdr.detectChanges();

      }

    });

  }


  // ============================================================
  // TRAINING VALUES
  // ============================================================

  get totalCourses(): number {

    return Number(
      this.analyticsData?.totalCourses ?? 0
    );

  }


  get totalEnrollments(): number {

    return Number(
      this.analyticsData?.totalEnrollments ?? 0
    );

  }


  get completionRate(): number {

    return Number(
      this.analyticsData?.overallCompletionPercentage ?? 0
    );

  }


  get averageProgress(): number {

    return Number(
      this.analyticsData?.averageTrainingProgressPercentage ?? 0
    );

  }


  // ============================================================
  // CAREER VALUES
  // ============================================================

  get careerPaths(): number {

    return this.careerPathsData.length;

  }


  get promotionCriteria(): number {

    return this.promotionCriteriaData.length;

  }


  get openJobs(): number {

    return this.jobsData.length;

  }


  // ============================================================
  // CAREER DATA AVAILABLE?
  // ============================================================

  get hasCareerData(): boolean {

    return (

      this.careerPaths > 0 ||

      this.promotionCriteria > 0 ||

      this.openJobs > 0

    );

  }


  // ============================================================
  // RENDER ALL CHARTS
  // ============================================================

  renderCharts(): void {

    this.destroyCharts();


    if (!this.analyticsData) {

      return;

    }


    this.renderTrainingChart();

    this.renderWorkforceChart();


    if (this.hasCareerData) {

      this.renderCareerChart();

    }


    if (this.openJobs > 0) {

      this.renderJobsChart();

    }

  }


  // ============================================================
  // TRAINING PERFORMANCE
  // ============================================================

  private renderTrainingChart(): void {

    const canvas =
      this.trainingChartElement?.nativeElement;


    if (!canvas) {

      return;

    }


    this.trainingChart =

      new Chart(
        canvas,

        {

          type: 'bar',

          data: {

            labels: [

              'Average Progress',

              'Completion Rate'

            ],

            datasets: [

              {

                label: 'Percentage',

                data: [

                  this.averageProgress,

                  this.completionRate

                ],

                backgroundColor: [

                  '#ff6a00',

                  '#22c55e'

                ],

                borderRadius: 8,

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

                    return `${context.parsed.y}%`;

                  }

                }

              }

            },


            scales: {

              y: {

                beginAtZero: true,

                max: 100,


                ticks: {

                  callback: (value) => {

                    return `${value}%`;

                  }

                },


                grid: {

                  color:
                    'rgba(255,255,255,0.07)'

                }

              },


              x: {

                grid: {

                  display: false

                }

              }

            }

          }

        }

      );

  }


  // ============================================================
  // WORKFORCE
  // ============================================================

  private renderWorkforceChart(): void {

    const canvas =
      this.workforceChartElement?.nativeElement;


    if (!canvas) {

      return;

    }


    this.workforceChart =

      new Chart(
        canvas,

        {

          type: 'bar',

          data: {

            labels: [

              'Courses',

              'Enrollments'

            ],

            datasets: [

              {

                label: 'Count',

                data: [

                  this.totalCourses,

                  this.totalEnrollments

                ],

                backgroundColor: [

                  '#3b82f6',

                  '#ff6a00'

                ],

                borderRadius: 8,

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

              y: {

                beginAtZero: true,

                ticks: {

                  precision: 0

                },

                grid: {

                  color:
                    'rgba(255,255,255,0.07)'

                }

              },


              x: {

                grid: {

                  display: false

                }

              }

            }

          }

        }

      );

  }


  // ============================================================
  // CAREER DEVELOPMENT
  // ============================================================

  private renderCareerChart(): void {

    const canvas =
      this.careerChartElement?.nativeElement;


    if (!canvas) {

      return;

    }


    this.careerChart =

      new Chart(
        canvas,

        {

          type: 'doughnut',

          data: {

            labels: [

              'Career Paths',

              'Promotion Criteria',

              'Open Jobs'

            ],

            datasets: [

              {

                data: [

                  this.careerPaths,

                  this.promotionCriteria,

                  this.openJobs

                ],

                backgroundColor: [

                  '#ff6a00',

                  '#8b5cf6',

                  '#22c55e'

                ],

                borderWidth: 0,

                spacing: 4

              }

            ]

          },


          options: {

            responsive: true,

            maintainAspectRatio: false,

            cutout: '65%',


            plugins: {

              legend: {

                position: 'bottom',

                labels: {

                  color: '#94a3b8',

                  padding: 18,

                  usePointStyle: true

                }

              }

            }

          }

        }

      );

  }


  // ============================================================
  // OPEN JOBS
  // ============================================================

  private renderJobsChart(): void {

    const canvas =
      this.jobsChartElement?.nativeElement;


    if (!canvas || this.openJobs === 0) {

      return;

    }


    this.jobsChart =

      new Chart(
        canvas,

        {

          type: 'bar',

          data: {

            labels: [

              'Open Positions'

            ],

            datasets: [

              {

                label: 'Open Jobs',

                data: [

                  this.openJobs

                ],

                backgroundColor: [

                  '#22c55e'

                ],

                borderRadius: 8,

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

              y: {

                beginAtZero: true,

                ticks: {

                  precision: 0

                },

                grid: {

                  color:
                    'rgba(255,255,255,0.07)'

                }

              },


              x: {

                grid: {

                  display: false

                }

              }

            }

          }

        }

      );

  }


  // ============================================================
  // DESTROY CHARTS
  // ============================================================

  private destroyCharts(): void {

    if (this.trainingChart) {

      this.trainingChart.destroy();

      this.trainingChart = null;

    }


    if (this.workforceChart) {

      this.workforceChart.destroy();

      this.workforceChart = null;

    }


    if (this.careerChart) {

      this.careerChart.destroy();

      this.careerChart = null;

    }


    if (this.jobsChart) {

      this.jobsChart.destroy();

      this.jobsChart = null;

    }

  }


  // ============================================================
  // DESTROY COMPONENT
  // ============================================================

  ngOnDestroy(): void {

    this.destroyCharts();

  }

}