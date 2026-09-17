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

    this.loadCertificates();
  }


  // ==========================================
  // GET LOGGED-IN EMPLOYEE DATABASE ID
  // ==========================================

  getEmployeeId(): number {

    const storedUser =
      localStorage.getItem('loggedInUser');

    if (!storedUser) {
      return 0;
    }

    try {

      const user =
        JSON.parse(storedUser);

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

        next: (data: Course[]) => {

          this.courses =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loading = false;

          this.cdr.detectChanges();
        },

        error: (error: any) => {

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
  // LOAD EMPLOYEE ENROLLMENTS
  // ==========================================

  loadEnrollments(): void {

    const employeeId =
      this.getEmployeeId();

    if (!employeeId) {

      this.enrollments = [];

      return;
    }

    this.learningService
      .getEnrollments(employeeId)
      .subscribe({

        next: (data: Enrollment[]) => {

          console.log(
            'EMPLOYEE ENROLLMENTS:',
            data
          );

          this.enrollments =
            Array.isArray(data)
              ? [...data]
              : [];

          this.buildProgressList();

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Enrollment loading error:',
            error
          );

          this.enrollments = [];

          this.progressList = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // BUILD PROGRESS FROM ENROLLMENTS
  // ==========================================

  buildProgressList(): void {

    this.progressList =
      this.enrollments
        .filter(
          enrollment =>
            enrollment.enrollmentId != null
        )
        .map(
          enrollment => {

            const percentage =
              Number(
                enrollment.progressPercentage || 0
              );

            return {

              enrollmentId:
                enrollment.enrollmentId!,

              progressPercentage:
                percentage,

              completionStatus:
                enrollment.status === 'COMPLETED'
                  ? 'COMPLETED'
                  : percentage > 0
                    ? 'IN_PROGRESS'
                    : enrollment.status || 'ENROLLED',

              lastUpdatedDate:
                enrollment.enrollmentDate || ''

            };

          }
        );
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

        error: (error: any) => {

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
      (course: Course) => {

        const text = `
          ${course.courseName || ''}
          ${course.description || ''}
          ${course.trainerName || ''}
          ${course.level || ''}
        `.toLowerCase();

        return text.includes(search);
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
  // MY COURSES COUNT
  // ==========================================

  get myCoursesCount(): number {

    const employeeId =
      this.getEmployeeId();

    if (!employeeId) {

      return 0;
    }

    return this.enrollments.filter(
      enrollment =>
        Number(enrollment.employeeId)
        === employeeId
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
      enrollment =>

        Number(enrollment.courseId)
        === Number(courseId)

        &&

        Number(enrollment.employeeId)
        === Number(employeeId)
    );
  }


  // ==========================================
  // IS ENROLLED?
  // ==========================================

  isEnrolled(
    courseId: number | undefined
  ): boolean {

    return (
      this.getEnrollment(courseId)
      !== undefined
    );
  }


  // ==========================================
  // GET COURSE PROGRESS
  // ==========================================

  getCourseProgress(
    courseId: number | undefined
  ): CourseProgress | undefined {

    const enrollment =
      this.getEnrollment(courseId);

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      return undefined;
    }

    const percentage =
      Number(
        enrollment.progressPercentage || 0
      );

    return {

      enrollmentId:
        enrollment.enrollmentId,

      progressPercentage:
        percentage,

      completionStatus:
        enrollment.status === 'COMPLETED'
          ? 'COMPLETED'
          : percentage > 0
            ? 'IN_PROGRESS'
            : enrollment.status || 'ENROLLED',

      lastUpdatedDate:
        enrollment.enrollmentDate || ''

    };
  }


  // ==========================================
  // GET PROGRESS %
  // ==========================================

  getProgress(
    courseId: number | undefined
  ): number {

    const enrollment =
      this.getEnrollment(courseId);

    if (!enrollment) {

      return 0;
    }

    return Number(
      enrollment.progressPercentage || 0
    );
  }


  // ==========================================
  // GET PROGRESS STATUS
  // ==========================================

  getProgressStatus(
    courseId: number | undefined
  ): string {

    const enrollment =
      this.getEnrollment(courseId);

    if (!enrollment) {

      return 'NOT_STARTED';
    }

    const percentage =
      Number(
        enrollment.progressPercentage || 0
      );

    if (
      enrollment.status === 'COMPLETED'
      ||
      percentage >= 100
    ) {

      return 'COMPLETED';
    }

    if (percentage > 0) {

      return 'IN_PROGRESS';
    }

    return enrollment.status || 'ENROLLED';
  }


  // ==========================================
  // GET CERTIFICATE
  // ==========================================

  getCertificate(
    courseId: number | undefined
  ): LearningCertificate | undefined {

    const enrollment =
      this.getEnrollment(courseId);

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      return undefined;
    }

    return this.certificates.find(
      certificate =>

        Number(certificate.enrollmentId)
        ===
        Number(enrollment.enrollmentId)
    );
  }


  // ==========================================
  // HAS CERTIFICATE?
  // ==========================================

  hasCertificate(
    courseId: number | undefined
  ): boolean {

    return (
      this.getCertificate(courseId)
      !== undefined
    );
  }


  // ==========================================
  // CERTIFICATE NUMBER
  // ==========================================

  getCertificateNumber(
    courseId: number | undefined
  ): string {

    return (
      this.getCertificate(courseId)
        ?.certificateNumber
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

    if (!this.isEnrolled(courseId)) {

      return false;
    }

    if (this.hasCertificate(courseId)) {

      return false;
    }

    return (
      this.getProgress(courseId) >= 80
    );
  }


  // ==========================================
  // REMAINING FOR CERTIFICATE
  // ==========================================

  remainingForCertificate(
    courseId: number | undefined
  ): number {

    const progress =
      this.getProgress(courseId);

    if (progress >= 80) {

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
      this.getEnrollment(course.courseId);

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      this.certificateError =
        'Enrollment information not found.';

      return;
    }

    if (
      this.hasCertificate(course.courseId)
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
          result: CertificateEligibility
        ) => {

          if (!result.eligible) {

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
                certificate: LearningCertificate
              ) => {

                this.certificateMessage =
                  `Certificate generated successfully: ${
                    certificate.certificateNumber || ''
                  }`;

                this.loadCertificates();

                this.cdr.detectChanges();
              },

              error: (error: any) => {

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

        error: (error: any) => {

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

    if (course.courseId == null) {

      this.actionError =
        'Course ID not found.';

      return;
    }

    const employeeId =
      this.getEmployeeId();

    if (!employeeId) {

      this.actionError =
        'Logged-in employee ID not found.';

      return;
    }

    if (
      this.isEnrolled(course.courseId)
    ) {

      this.actionMessage =
        'You are already enrolled in this course.';

      return;
    }

    const enrollment: Enrollment = {

      employeeId:
        employeeId,

      courseId:
        course.courseId,

      enrollmentDate:
        new Date()
          .toISOString()
          .split('T')[0],

      status:
        'ENROLLED',

      progressPercentage:
        0
    };

    console.log(
      'CREATING ENROLLMENT:',
      enrollment
    );

    this.learningService
      .createEnrollment(enrollment)
      .subscribe({

        next: (
          createdEnrollment: Enrollment
        ) => {

          console.log(
            'ENROLLMENT CREATED:',
            createdEnrollment
          );

          this.actionMessage =
            `${course.courseName} started successfully.`;

          this.loadEnrollments();

          this.cdr.detectChanges();
        },

        error: (error: any) => {

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

    if (course.courseId == null) {

      return;
    }

    const enrollment =
      this.getEnrollment(course.courseId);

    if (
      !enrollment ||
      enrollment.enrollmentId == null
    ) {

      this.actionError =
        'Enrollment information not found.';

      return;
    }

    let currentProgress =
      Number(
        enrollment.progressPercentage || 0
      );

    let newPercentage =
      currentProgress + 10;

    if (newPercentage > 100) {

      newPercentage = 100;
    }

    this.learningService
      .updateEnrollmentProgress(
        enrollment.enrollmentId,
        newPercentage
      )
      .subscribe({

        next: (
          updatedEnrollment: Enrollment
        ) => {

          console.log(
            'PROGRESS UPDATED:',
            updatedEnrollment
          );

          this.actionMessage =
            newPercentage >= 100
              ? `${course.courseName} completed successfully.`
              : `Course progress updated to ${newPercentage}%.`;

          this.loadEnrollments();

          this.cdr.detectChanges();
        },

        error: (error: any) => {

          console.error(
            'Progress update error:',
            error
          );

          this.actionError =
            error?.error?.message
            ||
            'Unable to update course progress.';

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // ADMIN / HR - ADD COURSE
  // ==========================================

  openAddForm(): void {

    if (
      !this.authService.canManageLearning()
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
      !this.authService.canManageLearning()
    ) {

      return;
    }

    if (
      !this.course.courseName?.trim()
      ||
      !this.course.description?.trim()
      ||
      !this.course.trainerName?.trim()
      ||
      !this.course.level?.trim()
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

          error: (error: any) => {

            console.error(
              'Course update error:',
              error
            );
          }

        });

    } else {

      this.learningService
        .addCourse(this.course)
        .subscribe({

          next: () => {

            this.closeForm();

            this.loadCourses();
          },

          error: (error: any) => {

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
      !this.authService.canManageLearning()
    ) {

      return;
    }

    this.isEditing = true;

    this.editingId =
      course.courseId ?? null;

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
      !this.authService.canManageLearning()
    ) {

      return;
    }

    if (
      !confirm('Delete this course?')
    ) {

      return;
    }

    this.learningService
      .deleteCourse(id)
      .subscribe({

        next: () => {

          this.loadCourses();
        },

        error: (error: any) => {

          console.error(
            'Course delete error:',
            error
          );
        }

      });
  }

}