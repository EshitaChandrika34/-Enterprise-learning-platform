import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class DashboardService {


  constructor(
    private http: HttpClient
  ) {}


  // ==========================================
  // USER SERVICE
  // ==========================================

  getEmployees(): Observable<any[]> {

    return this.http.get<any[]>(
      'http://localhost:8081/api/employees'
    );

  }


  // ==========================================
  // LEARNING SERVICE
  // ==========================================

  getCourses(): Observable<any[]> {

    return this.http.get<any[]>(
      'http://localhost:8084/courses'
    );

  }


  // ==========================================
  // CERTIFICATION SERVICE
  // ==========================================

  getCertifications(): Observable<any[]> {

    return this.http.get<any[]>(
      'http://localhost:8083/certifications'
    );

  }


  // ==========================================
  // SKILL SERVICE
  // ==========================================

  getSkills(): Observable<any[]> {

    return this.http.get<any[]>(
      'http://localhost:8082/skills'
    );

  }


  // ==========================================
  // MILESTONE-4 ANALYTICS
  // ==========================================

  getDashboardAnalytics(): Observable<any> {

    return this.http.get<any>(
      'http://localhost:8090/api/analytics/dashboard'
    );

  }


  getTrainingAnalytics(): Observable<any> {

    return this.http.get<any>(
      'http://localhost:8090/api/analytics/training'
    );

  }

}