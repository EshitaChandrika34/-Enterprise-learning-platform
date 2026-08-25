import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import {
  LearningService,
  Course,
  Enrollment,
  CourseProgress,
  LearningCertificate,
  CertificateEligibility
} from '../../services/learning';

import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-learning',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],

  templateUrl: './learning.html',
  styleUrl: './learning.css'
})
export class Learning implements OnInit {

  searchText = '';

  loading = false;

  showForm = false;

  isEditing = false;

  editingId: number | null = null;

  courses: Course[] = [];

  enrollments: Enrollment[] = [];

  progressList: CourseProgress[] = [];

  certificates: LearningCertificate[] = [];

  certificateMessage = '';

  certificateError = '';

  actionMessage = '';

  actionError = '';

  course: Course = {
    courseName: '',
    description: '',
    trainerName: '',
    duration: 0,
    level: ''
  };


  constructor(
    private learningService: LearningService,
    private cdr: ChangeDetectorRef,
    public authService: AuthService
  ) {}


  ngOnInit(): void {

    this.loadCourses();

    this.loadEnrollments();

    this.loadProgress();

    this.loadCertificates();
  }


  // ==========================================
  // LOGGED-IN USER DATABASE ID
  // ==========================================

  getEmployeeId(): number {

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

      /*
       * IMPORTANT:
       *
       * id = 3
       *
       * employeeId = EMP-DEV-003
       *
       * Backend enrollment requires numeric
       * database user ID.
       */

      return Number(
        user.id || 0
      );

    } catch {

      return 0;
    }
  }


  // ==========================================
  // LOAD COURSES
  // ==========================================

  loadCourses(): void {

    this.loading = true;

    this.learningService
      .getCourses()
      .subscribe({

        next: (
          data: Course[]
        ) => {

          this.courses =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loading = false;

          this.cdr.detectChanges();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Course loading error:',
            error
          );

          this.courses = [];

          this.loading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD ENROLLMENTS
  // ==========================================

  loadEnrollments(): void {

    this.learningService
      .getEnrollments()
      .subscribe({

        next: (
          data: Enrollment[]
        ) => {

          console.log(
            'ENROLLMENTS:',
            data
          );

          this.enrollments =
            Array.isArray(data)
              ? [...data]
              : [];

          this.cdr.detectChanges();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Enrollment loading error:',
            error
          );

          this.enrollments = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD PROGRESS
  // ==========================================

  loadProgress(): void {

    this.learningService
      .getProgress()
      .subscribe({

        next: (
          data: CourseProgress[]
        ) => {

          console.log(
            'COURSE PROGRESS:',
            data
          );

          this.progressList =
            Array.isArray(data)
              ? [...data]
              : [];

          this.cdr.detectChanges();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Progress loading error:',
            error
          );

          this.progressList = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD CERTIFICATES
  // ==========================================

  loadCertificates(): void {

    this.learningService
      .getCertificates()
      .subscribe({

        next: (
          data: LearningCertificate[]
        ) => {

          this.certificates =
            Array.isArray(data)
              ? [...data]
              : [];

          this.cdr.detectChanges();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Certificate loading error:',
            error
          );

          this.certificates = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // FILTER COURSES
  // ==========================================

  get filteredCourses(): Course[] {

    const search =
      this.searchText
        .trim()
        .toLowerCase();

    if (!search) {

      return this.courses;
    }

    return this.courses.filter(
      (
        course: Course
      ) => {

        const text = `
          ${course.courseName || ''}
          ${course.description || ''}
          ${course.trainerName || ''}
          ${course.level || ''}
        `.toLowerCase();

        return text.includes(
          search
        );
      }
    );
  }


  // ==========================================
  // TOTAL COURSES
  // ==========================================

  get totalCourses(): number {

    return this.courses.length;
  }


  // ==========================================
  // MY COURSES
  // ==========================================

  get myCoursesCount(): number {

    const employeeId =
      this.getEmployeeId();

    if (!employeeId) {

      return 0;
    }

    return this.enrollments.filter(
      (
        enrollment: Enrollment
      ) =>

        Number(
          enrollment.employeeId
        )
        ===
        employeeId

    ).length;
  }


  // ==========================================
  // FIND ENROLLMENT
  // ==========================================

  getEnrollment(
    courseId: number | undefined
  ): Enrollment | undefined {

    if (courseId == null) {

      return undefined;
    }

    const employeeId =
      this.getEmployeeId();

    if (!employeeId) {

      return undefined;
    }

    return this.enrollments.find(
      (
        enrollment: Enrollment
      ) =>

        Number(
          enrollment.courseId
        )
        ===
        Number(courseId)

        &&

        Number(
          enrollment.employeeId
        )
        ===
        Number(employeeId)
    );
  }


  // ==========================================
  // ENROLLED?
  // ==========================================

  isEnrolled(
    courseId: number | undefined
  ): boolean {

    return (
      this.getEnrollment(
        courseId
      ) !== undefined
    );
  }


  // ==========================================
  // GET COURSE PROGRESS RECORD
  // ==========================================

  getCourseProgress(
    courseId: number | undefined
  ): CourseProgress | undefined {

    const enrollment =
      this.getEnrollment(
        courseId
      );

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      return undefined;
    }

    const matchingProgress =
      this.progressList.filter(
        (
          progress: CourseProgress
        ) =>

          Number(
            progress.enrollmentId
          )
          ===
          Number(
            enrollment.enrollmentId
          )
      );

    if (
      matchingProgress.length === 0
    ) {

      return undefined;
    }

    return matchingProgress[
      matchingProgress.length - 1
    ];
  }


  // ==========================================
  // GET PROGRESS %
  // ==========================================

  getProgress(
    courseId: number | undefined
  ): number {

    const progress =
      this.getCourseProgress(
        courseId
      );

    if (!progress) {

      return 0;
    }

    return Number(
      progress.progressPercentage || 0
    );
  }


  // ==========================================
  // GET PROGRESS STATUS
  // ==========================================

  getProgressStatus(
    courseId: number | undefined
  ): string {

    const progress =
      this.getCourseProgress(
        courseId
      );

    if (progress) {

      return (
        progress.completionStatus
        ||
        'IN_PROGRESS'
      );
    }

    const enrollment =
      this.getEnrollment(
        courseId
      );

    if (enrollment) {

      return (
        enrollment.status
        ||
        'ENROLLED'
      );
    }

    return 'NOT_STARTED';
  }


  // ==========================================
  // GET CERTIFICATE
  // ==========================================

  getCertificate(
    courseId: number | undefined
  ): LearningCertificate | undefined {

    const enrollment =
      this.getEnrollment(
        courseId
      );

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      return undefined;
    }

    return this.certificates.find(
      (
        certificate:
          LearningCertificate
      ) =>

        Number(
          certificate.enrollmentId
        )
        ===
        Number(
          enrollment.enrollmentId
        )
    );
  }


  // ==========================================
  // CERTIFICATE EXISTS?
  // ==========================================

  hasCertificate(
    courseId: number | undefined
  ): boolean {

    return (
      this.getCertificate(
        courseId
      ) !== undefined
    );
  }


  // ==========================================
  // CERTIFICATE NUMBER
  // ==========================================

  getCertificateNumber(
    courseId: number | undefined
  ): string {

    return (
      this.getCertificate(
        courseId
      )?.certificateNumber
      ||
      ''
    );
  }


  // ==========================================
  // CERTIFICATE ELIGIBILITY
  // ==========================================

  canGenerateCertificate(
    courseId: number | undefined
  ): boolean {

    if (
      !this.isEnrolled(
        courseId
      )
    ) {

      return false;
    }

    if (
      this.hasCertificate(
        courseId
      )
    ) {

      return false;
    }

    return (
      this.getProgress(
        courseId
      ) >= 80
    );
  }


  // ==========================================
  // CERTIFICATE REMAINING %
  // ==========================================

  remainingForCertificate(
    courseId: number | undefined
  ): number {

    const progress =
      this.getProgress(
        courseId
      );

    if (
      progress >= 80
    ) {

      return 0;
    }

    return 80 - progress;
  }


  // ==========================================
  // GENERATE CERTIFICATE
  // ==========================================

  generateCertificate(
    course: Course
  ): void {

    this.certificateMessage = '';

    this.certificateError = '';

    const enrollment =
      this.getEnrollment(
        course.courseId
      );

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      this.certificateError =
        'Enrollment information not found.';

      return;
    }

    if (
      this.hasCertificate(
        course.courseId
      )
    ) {

      this.certificateError =
        'Certificate has already been generated.';

      return;
    }

    const enrollmentId =
      enrollment.enrollmentId;

    this.learningService
      .checkCertificateEligibility(
        enrollmentId
      )
      .subscribe({

        next: (
          result:
            CertificateEligibility
        ) => {

          if (
            !result.eligible
          ) {

            this.certificateError =
              result.message;

            this.cdr.detectChanges();

            return;
          }

          this.learningService
            .generateCertificate(
              enrollmentId
            )
            .subscribe({

              next: (
                certificate:
                  LearningCertificate
              ) => {

                this.certificateMessage =
                  `Certificate generated successfully: ${
                    certificate.certificateNumber || ''
                  }`;

                this.loadCertificates();

                this.cdr.detectChanges();
              },

              error: (
                error: any
              ) => {

                console.error(
                  'Certificate generation error:',
                  error
                );

                this.certificateError =
                  error?.error?.message
                  ||
                  'Unable to generate certificate.';

                this.cdr.detectChanges();
              }

            });
        },

        error: (
          error: any
        ) => {

          console.error(
            'Eligibility error:',
            error
          );

          this.certificateError =
            error?.error?.message
            ||
            'Unable to check certificate eligibility.';

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // START COURSE
  // ==========================================

  startCourse(
    course: Course
  ): void {

    this.actionMessage = '';

    this.actionError = '';

    console.log(
      'START COURSE CLICKED:',
      course
    );

    if (
      course.courseId == null
    ) {

      this.actionError =
        'Course ID not found.';

      console.error(
        this.actionError
      );

      return;
    }

    const employeeId =
      this.getEmployeeId();

    console.log(
      'LOGGED EMPLOYEE DATABASE ID:',
      employeeId
    );

    if (!employeeId) {

      this.actionError =
        'Logged-in employee ID not found.';

      console.error(
        this.actionError
      );

      return;
    }

    if (
      this.isEnrolled(
        course.courseId
      )
    ) {

      this.actionMessage =
        'You are already enrolled in this course.';

      return;
    }

    const today =
      new Date()
        .toISOString()
        .split('T')[0];

    const enrollment:
      Enrollment = {

      employeeId:
        employeeId,

      courseId:
        course.courseId,

      enrollmentDate:
        today,

      status:
        'ENROLLED'
    };

    console.log(
      'CREATING ENROLLMENT:',
      enrollment
    );

    this.learningService
      .createEnrollment(
        enrollment
      )
      .subscribe({

        next: (
          createdEnrollment:
            Enrollment
        ) => {

          console.log(
            'ENROLLMENT CREATED:',
            createdEnrollment
          );

          this.actionMessage =
            `${course.courseName} started successfully.`;

          if (
            createdEnrollment
              .enrollmentId == null
          ) {

            this.loadEnrollments();

            this.cdr.detectChanges();

            return;
          }

          const progress:
            CourseProgress = {

            enrollmentId:
              createdEnrollment.enrollmentId,

            progressPercentage:
              0,

            completionStatus:
              'IN_PROGRESS',

            lastUpdatedDate:
              today
          };

          console.log(
            'CREATING PROGRESS:',
            progress
          );

          this.learningService
            .createProgress(
              progress
            )
            .subscribe({

              next: (
                createdProgress:
                  CourseProgress
              ) => {

                console.log(
                  'PROGRESS CREATED:',
                  createdProgress
                );

                this.loadEnrollments();

                this.loadProgress();

                this.cdr.detectChanges();
              },

              error: (
                error: any
              ) => {

                console.error(
                  'Progress creation error:',
                  error
                );

                /*
                 * Enrollment was still created,
                 * therefore reload enrollments.
                 */

                this.loadEnrollments();

                this.cdr.detectChanges();
              }

            });
        },

        error: (
          error: any
        ) => {

          console.error(
            'Enrollment error:',
            error
          );

          this.actionError =
            error?.error?.message
            ||
            'Unable to start course.';

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // CONTINUE COURSE
  // ==========================================

  continueCourse(
    course: Course
  ): void {

    this.actionMessage = '';

    this.actionError = '';

    if (
      course.courseId == null
    ) {

      return;
    }

    const enrollment =
      this.getEnrollment(
        course.courseId
      );

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      this.actionError =
        'Enrollment information not found.';

      return;
    }

    const progress =
      this.getCourseProgress(
        course.courseId
      );

    const today =
      new Date()
        .toISOString()
        .split('T')[0];

    /*
     * If enrollment exists but there is
     * no progress record yet, create one.
     */

    if (!progress) {

      const newProgress:
        CourseProgress = {

        enrollmentId:
          enrollment.enrollmentId,

        progressPercentage:
          10,

        completionStatus:
          'IN_PROGRESS',

        lastUpdatedDate:
          today
      };

      this.learningService
        .createProgress(
          newProgress
        )
        .subscribe({

          next: () => {

            this.actionMessage =
              'Course progress updated to 10%.';

            this.loadProgress();

            this.cdr.detectChanges();
          },

          error: (
            error: any
          ) => {

            console.error(
              'Progress creation error:',
              error
            );

            this.actionError =
              error?.error?.message
              ||
              'Unable to update course progress.';

            this.cdr.detectChanges();
          }

        });

      return;
    }

    if (
      progress.progressId == null
    ) {

      return;
    }

    let newPercentage =
      Number(
        progress
          .progressPercentage
        || 0
      ) + 10;

    if (
      newPercentage > 100
    ) {

      newPercentage = 100;
    }

    const updatedProgress:
      CourseProgress = {

      ...progress,

      progressPercentage:
        newPercentage,

      completionStatus:
        newPercentage >= 100
          ? 'COMPLETED'
          : 'IN_PROGRESS',

      lastUpdatedDate:
        today
    };

    this.learningService
      .updateProgress(
        progress.progressId,
        updatedProgress
      )
      .subscribe({

        next: () => {

          this.actionMessage =
            newPercentage >= 100
              ? `${course.courseName} completed successfully.`
              : `Course progress updated to ${newPercentage}%.`;

          this.loadProgress();

          this.cdr.detectChanges();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Progress update error:',
            error
          );

          this.actionError =
            error?.error?.message
            ||
            'Unable to update progress.';

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // ADMIN / HR ADD COURSE
  // ==========================================

  openAddForm(): void {

    if (
      !this.authService
        .canManageLearning()
    ) {

      return;
    }

    this.isEditing = false;

    this.editingId = null;

    this.course = {
      courseName: '',
      description: '',
      trainerName: '',
      duration: 0,
      level: ''
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
  }


  // ==========================================
  // SAVE COURSE
  // ==========================================

  saveCourse(): void {

    if (
      !this.authService
        .canManageLearning()
    ) {

      return;
    }

    if (
      !this.course
        .courseName
        ?.trim()
      ||
      !this.course
        .description
        ?.trim()
      ||
      !this.course
        .trainerName
        ?.trim()
      ||
      !this.course
        .level
        ?.trim()
    ) {

      return;
    }

    if (
      this.isEditing
      &&
      this.editingId !== null
    ) {

      this.learningService
        .updateCourse(
          this.editingId,
          this.course
        )
        .subscribe({

          next: () => {

            this.closeForm();

            this.loadCourses();
          },

          error: (
            error: any
          ) => {

            console.error(
              'Course update error:',
              error
            );
          }

        });

    } else {

      this.learningService
        .addCourse(
          this.course
        )
        .subscribe({

          next: () => {

            this.closeForm();

            this.loadCourses();
          },

          error: (
            error: any
          ) => {

            console.error(
              'Course creation error:',
              error
            );
          }

        });
    }
  }


  // ==========================================
  // EDIT COURSE
  // ==========================================

  editCourse(
    course: Course
  ): void {

    if (
      !this.authService
        .canManageLearning()
    ) {

      return;
    }

    this.isEditing = true;

    this.editingId =
      course.courseId
      ?? null;

    this.course = {
      ...course
    };

    this.showForm = true;
  }


  // ==========================================
  // DELETE COURSE
  // ==========================================

  deleteCourse(
    id: number
  ): void {

    if (
      !this.authService
        .canManageLearning()
    ) {

      return;
    }

    if (
      !confirm(
        'Delete this course?'
      )
    ) {

      return;
    }

    this.learningService
      .deleteCourse(id)
      .subscribe({

        next: () => {

          this.loadCourses();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Course delete error:',
            error
          );
        }

      });
  }

}