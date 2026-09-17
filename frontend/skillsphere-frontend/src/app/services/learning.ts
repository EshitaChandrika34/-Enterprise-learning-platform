import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Course {
  courseId?: number;
  courseName: string;
  description: string;
  trainerName: string;
  duration: number;
  level: string;
}

export interface Enrollment {
  enrollmentId?: number;
  employeeId: number;
  courseId: number;
  enrollmentDate: string;
  status: string;
  progressPercentage?: number;
}

export interface CourseProgress {
  progressId?: number;
  enrollmentId: number;
  progressPercentage: number;
  completionStatus: string;
  lastUpdatedDate: string;
}

export interface LearningCertificate {
  certificateId?: number;
  enrollmentId: number;
  certificateNumber?: string;
  courseName?: string;
  issueDate?: string;
  status?: string;
}

export interface CertificateEligibility {
  enrollmentId: number;
  threshold: number;
  eligible: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class LearningService {

  private baseUrl = 'http://localhost:8084';

  constructor(private http: HttpClient) {}

  // ==========================================
  // GET LOGGED-IN EMPLOYEE ID
  // ==========================================

  private getEmployeeId(): number {

    const storedUser = localStorage.getItem('loggedInUser');

    if (!storedUser) {
      return 0;
    }

    try {

      const user = JSON.parse(storedUser);

      return Number(user.id || user.employeeId || 0);

    } catch {

      return 0;
    }
  }

  // ==========================================
  // COURSES
  // ==========================================

  getCourses(): Observable<Course[]> {

    return this.http.get<Course[]>(
      `${this.baseUrl}/courses`
    );
  }

  addCourse(course: Course): Observable<Course> {

    return this.http.post<Course>(
      `${this.baseUrl}/courses`,
      course
    );
  }

  updateCourse(
    id: number,
    course: Course
  ): Observable<Course> {

    return this.http.put<Course>(
      `${this.baseUrl}/courses/${id}`,
      course
    );
  }

  deleteCourse(id: number): Observable<any> {

    return this.http.delete(
      `${this.baseUrl}/courses/${id}`
    );
  }

  // ==========================================
  // ENROLLMENTS
  // ==========================================

  getEnrollments(
    employeeId?: number
  ): Observable<Enrollment[]> {

    const id =
      employeeId || this.getEmployeeId();

    return this.http.get<Enrollment[]>(
      `${this.baseUrl}/enrollments/employee/${id}`
    );
  }
  getEnrollmentById(enrollmentId: number): Observable<Enrollment> {
  return this.http.get<Enrollment>(
    `${this.baseUrl}/enrollments/${enrollmentId}`
  );
}

  createEnrollment(
    enrollment: Enrollment
  ): Observable<Enrollment> {

    return this.http.post<Enrollment>(
      `${this.baseUrl}/enrollments`,
      enrollment
    );
  }

  // ==========================================
  // UPDATE PROGRESS
  // ==========================================

  updateEnrollmentProgress(
    enrollmentId: number,
    progressPercentage: number
  ): Observable<Enrollment> {

    return this.http.put<Enrollment>(
      `${this.baseUrl}/enrollments/${enrollmentId}/progress`,
      null,
      {
        params: {
          progressPercentage:
            progressPercentage.toString()
        }
      }
    );
  }

  // ==========================================
  // PROGRESS
  // ==========================================

  getProgress(
    employeeId?: number
  ): Observable<CourseProgress[]> {

    return this.getEnrollments(employeeId).pipe(

      map((enrollments: Enrollment[]) => {

        return enrollments.map(
          (enrollment: Enrollment) => {

            const percentage =
              Number(
                enrollment.progressPercentage || 0
              );

            return {

              enrollmentId:
                enrollment.enrollmentId || 0,

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
      })
    );
  }

  updateProgress(
    id: number,
    progress: CourseProgress
  ): Observable<Enrollment> {

    return this.updateEnrollmentProgress(
      id,
      progress.progressPercentage
    );
  }

  // ==========================================
  // CERTIFICATES
  // ==========================================

  getCertificates():
    Observable<LearningCertificate[]> {

    return this.http.get<LearningCertificate[]>(
      `${this.baseUrl}/certificates`
    );
  }

  getCertificatesByEnrollment(
    enrollmentId: number
  ): Observable<LearningCertificate[]> {

    return this.http.get<LearningCertificate[]>(
      `${this.baseUrl}/certificates/enrollment/${enrollmentId}`
    );
  }

  checkCertificateEligibility(
    enrollmentId: number
  ): Observable<CertificateEligibility> {

    return this.http.get<CertificateEligibility>(
      `${this.baseUrl}/certificates/eligibility/${enrollmentId}`
    );
  }

  checkEligibility(
    enrollmentId: number
  ): Observable<CertificateEligibility> {

    return this.http.get<CertificateEligibility>(
      `${this.baseUrl}/certificates/eligibility/${enrollmentId}`
    );
  }

  generateCertificate(
    enrollmentId: number
  ): Observable<LearningCertificate> {

    return this.http.post<LearningCertificate>(
      `${this.baseUrl}/certificates`,
      {
        enrollmentId: enrollmentId
      }
    );
  }
}