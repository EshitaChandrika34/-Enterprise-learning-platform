import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


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

  private baseUrl =
    'http://localhost:8084';


  constructor(
    private http: HttpClient
  ) {}


  // ==========================================
  // COURSES
  // ==========================================

  getCourses():
    Observable<Course[]> {

    return this.http.get<Course[]>(
      `${this.baseUrl}/courses`
    );
  }


  addCourse(
    course: Course
  ): Observable<Course> {

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


  deleteCourse(
    id: number
  ): Observable<any> {

    return this.http.delete(
      `${this.baseUrl}/courses/${id}`
    );
  }


  // ==========================================
  // ENROLLMENTS
  // ==========================================

  getEnrollments():
    Observable<Enrollment[]> {

    return this.http.get<Enrollment[]>(
      `${this.baseUrl}/enrollments`
    );
  }


  getEnrollmentById(
    id: number
  ): Observable<Enrollment> {

    return this.http.get<Enrollment>(
      `${this.baseUrl}/enrollments/${id}`
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


  updateEnrollment(
    id: number,
    enrollment: Enrollment
  ): Observable<Enrollment> {

    return this.http.put<Enrollment>(
      `${this.baseUrl}/enrollments/${id}`,
      enrollment
    );
  }


  deleteEnrollment(
    id: number
  ): Observable<any> {

    return this.http.delete(
      `${this.baseUrl}/enrollments/${id}`
    );
  }


  // ==========================================
  // PROGRESS
  // ==========================================

  getProgress():
    Observable<CourseProgress[]> {

    return this.http.get<CourseProgress[]>(
      `${this.baseUrl}/progress`
    );
  }


  createProgress(
    progress: CourseProgress
  ): Observable<CourseProgress> {

    return this.http.post<CourseProgress>(
      `${this.baseUrl}/progress`,
      progress
    );
  }


  updateProgress(
    id: number,
    progress: CourseProgress
  ): Observable<CourseProgress> {

    return this.http.put<CourseProgress>(
      `${this.baseUrl}/progress/${id}`,
      progress
    );
  }


  deleteProgress(
    id: number
  ): Observable<any> {

    return this.http.delete(
      `${this.baseUrl}/progress/${id}`
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