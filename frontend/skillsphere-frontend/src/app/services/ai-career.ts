import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CareerAiRequest {
  message: string;
  employeeId?: number;
  targetRole?: string;
  skills?: string[];
  missingSkills?: string[];
}

export interface CareerAiResponse {
  reply: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiCareerService {

  private baseUrl = 'http://localhost:8090/api/ai';

  constructor(private http: HttpClient) {}

  sendMessage(request: CareerAiRequest): Observable<CareerAiResponse> {
    return this.http.post<CareerAiResponse>(
      `${this.baseUrl}/career-chat`,
      request
    );
  }
}