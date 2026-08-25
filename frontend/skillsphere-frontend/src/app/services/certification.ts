import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface Certification {

  certificateId?: number;

  enrollmentId: number;

  certificateNumber?: string;

  courseName?: string;

  issueDate?: string;

  status?: string;
}


@Injectable({
  providedIn: 'root'
})
export class CertificationService {

  private baseUrl =
    'http://localhost:8084/certificates';


  constructor(
    private http: HttpClient
  ) {}


  // ==========================================
  // GET ALL CERTIFICATES
  // ==========================================

  getCertifications():
    Observable<Certification[]> {

    return this.http.get<Certification[]>(
      this.baseUrl
    );
  }


  // ==========================================
  // GET CERTIFICATE BY ID
  // ==========================================

  getCertificationById(
    id: number
  ): Observable<Certification> {

    return this.http.get<Certification>(
      `${this.baseUrl}/${id}`
    );
  }


  // ==========================================
  // GET BY ENROLLMENT
  // ==========================================

  getByEnrollment(
    enrollmentId: number
  ): Observable<Certification[]> {

    return this.http.get<Certification[]>(
      `${this.baseUrl}/enrollment/${enrollmentId}`
    );
  }


  // ==========================================
  // CHECK ELIGIBILITY
  // ==========================================

  checkEligibility(
    enrollmentId: number
  ): Observable<any> {

    return this.http.get<any>(
      `${this.baseUrl}/eligibility/${enrollmentId}`
    );
  }


  // ==========================================
  // GENERATE CERTIFICATE
  // ==========================================

  generateCertificate(
    enrollmentId: number
  ): Observable<Certification> {

    return this.http.post<Certification>(
      this.baseUrl,
      {
        enrollmentId: enrollmentId
      }
    );
  }


  // ==========================================
  // UPDATE CERTIFICATE
  // ==========================================

  updateCertification(
    id: number,
    certificate: Certification
  ): Observable<Certification> {

    return this.http.put<Certification>(
      `${this.baseUrl}/${id}`,
      certificate
    );
  }


  // ==========================================
  // DELETE CERTIFICATE
  // ==========================================

  deleteCertification(
    id: number
  ): Observable<any> {

    return this.http.delete(
      `${this.baseUrl}/${id}`
    );
  }

}