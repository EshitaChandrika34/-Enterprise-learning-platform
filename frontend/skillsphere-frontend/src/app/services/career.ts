import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class CareerService {

  private baseUrl =
    'http://localhost:8090/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}


  // ==========================================
  // CAREER PATHS
  // ==========================================

  getCareerPaths() {

    return this.http.get<any>(
      `${this.baseUrl}/career/paths`
    );
  }


  // ==========================================
  // CAREER GOALS
  // ==========================================

  getCareerGoals(
    employeeId: number
  ) {

    if (
      this.authService.isEmployee()
    ) {

      return this.http.get<any>(
        `${this.baseUrl}/career/goals`
      );
    }

    return this.http.get<any>(
      `${this.baseUrl}/career/goals/employee/${employeeId}`
    );
  }


  // ==========================================
  // RECOMMENDATIONS
  // ==========================================

  getRecommendations(
    employeeId: number
  ) {

    if (
      this.authService.isEmployee()
    ) {

      return this.http.get<any>(
        `${this.baseUrl}/career/recommendations`
      );
    }

    return this.http.get<any>(
      `${this.baseUrl}/career/recommendations/${employeeId}`
    );
  }


  // ==========================================
  // CAREER PROGRESS
  // ==========================================

  getCareerProgress(
    employeeId: number
  ) {

    if (
      this.authService.isEmployee()
    ) {

      return this.http.get<any>(
        `${this.baseUrl}/career/progress`
      );
    }

    return this.http.get<any>(
      `${this.baseUrl}/career/progress/employee/${employeeId}`
    );
  }


  // ==========================================
  // PROMOTION CRITERIA
  // ==========================================

  getPromotionCriteria() {

    return this.http.get<any>(
      `${this.baseUrl}/promotion-criteria`
    );
  }


  // ==========================================
  // PROMOTION EVALUATION
  // ==========================================

  getPromotionEvaluation(
    employeeId: number,
    criteriaId: number
  ) {

    if (
      this.authService.isEmployee()
    ) {

      return this.http.get<any>(
        `${this.baseUrl}/promotion-criteria/my-evaluation/${criteriaId}`
      );
    }

    return this.http.get<any>(
      `${this.baseUrl}/promotion-criteria/evaluate/${employeeId}/${criteriaId}`
    );
  }


  // ==========================================
  // ALL JOBS
  // ==========================================

  getJobs() {

    return this.http.get<any>(
      `${this.baseUrl}/jobs`
    );
  }


  // ==========================================
  // MATCHED JOBS
  // ==========================================

  getMatchedJobs(
    employeeId: number
  ) {

    if (
      this.authService.isEmployee()
    ) {

      return this.http.get<any>(
        `${this.baseUrl}/jobs/matched`
      );
    }

    return this.http.get<any>(
      `${this.baseUrl}/jobs/matched/employee/${employeeId}`
    );
  }


  // ==========================================
  // APPLY FOR JOB
  // ==========================================

  applyForJob(
    jobPostingId: number
  ) {

    return this.http.post<any>(
      `${this.baseUrl}/jobs/apply`,
      {
        jobPostingId:
          jobPostingId
      }
    );
  }


  // ==========================================
  // MY JOB APPLICATIONS
  // ==========================================

  getMyApplications() {

    return this.http.get<any>(
      `${this.baseUrl}/jobs/my-applications`
    );
  }


  // ==========================================
  // WITHDRAW APPLICATION
  // ==========================================

  withdrawApplication(
    applicationId: number
  ) {

    return this.http.delete<any>(
      `${this.baseUrl}/jobs/applications/${applicationId}`
    );
  }


  // ==========================================
  // TRAINING ANALYTICS
  // ==========================================

  getTrainingAnalytics() {

    return this.http.get<any>(
      `${this.baseUrl}/analytics/training`
    );
  }


  // ==========================================
  // DASHBOARD ANALYTICS
  // ==========================================

  getDashboardAnalytics() {

    return this.http.get<any>(
      `${this.baseUrl}/analytics/dashboard`
    );
  }
  
}