import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

/*
 * ============================================================
 * SKILL MODEL
 * ============================================================
 */
export interface Skill {

  skillId?: string | number;

  skillName: string;

  category: string;

  description: string;

  level?: number;

  employees?: number;

  /*
   * Backend fields
   */
  id?: number;

  name?: string;

  employeeCount?: number;

  createdAt?: string;

  updatedAt?: string;
}


/*
 * ============================================================
 * EMPLOYEE SKILL MODEL
 * ============================================================
 */
export interface EmployeeSkill {

  id?: number;

  userId: number;

  employeeName?: string;

  employeeId?: string;

  skillId: number;

  skillName?: string;

  skillCategory?: string;

  proficiencyLevel:
    | 'BEGINNER'
    | 'INTERMEDIATE'
    | 'ADVANCED'
    | 'EXPERT';

  verified: boolean;

  yearsOfExperience?: number;

  assignedDate?: string;
}


/*
 * ============================================================
 * ASSIGN SKILL REQUEST
 * ============================================================
 *
 * This exactly matches the backend SkillAssignRequest.
 */
export interface SkillAssignRequest {

  userId: number;

  skillId: number;

  proficiencyLevel:
    | 'BEGINNER'
    | 'INTERMEDIATE'
    | 'ADVANCED'
    | 'EXPERT';

  verified: boolean;

  yearsOfExperience?: number;
}


/*
 * ============================================================
 * BACKEND API RESPONSE
 * ============================================================
 */
interface ApiResponse<T> {

  success: boolean;

  message: string;

  data: T;

  timestamp?: string;
}


/*
 * ============================================================
 * SERVICE
 * ============================================================
 */
@Injectable({
  providedIn: 'root'
})
export class SkillService {

  /*
   * IMPORTANT:
   *
   * We are using the SAME 8090 backend database
   * that CareerServiceImpl reads from.
   */
  private apiUrl =
    'http://localhost:8090/api/skills';


  constructor(
    private http: HttpClient
  ) {}


  /*
   * ==========================================================
   * GET ALL SKILLS
   * ==========================================================
   *
   * Backend:
   *
   * GET /api/skills
   *
   * Backend returns:
   *
   * {
   *   success: true,
   *   message: "...",
   *   data: [...]
   * }
   *
   * We return only data[] to the component.
   */
  getSkills(): Observable<Skill[]> {

    return this.http
      .get<ApiResponse<any[]>>(
        this.apiUrl
      )
      .pipe(

        map(response => {

          const skills =
            Array.isArray(response?.data)
              ? response.data
              : [];

          return skills.map(
            skill => this.normalizeSkill(skill)
          );

        })

      );
  }


  /*
   * ==========================================================
   * GET SINGLE SKILL
   * ==========================================================
   *
   * GET /api/skills/{id}
   */
  getSkillById(
    id: string | number
  ): Observable<Skill> {

    return this.http
      .get<ApiResponse<any>>(
        `${this.apiUrl}/${id}`
      )
      .pipe(

        map(response =>
          this.normalizeSkill(
            response.data
          )
        )

      );
  }


  /*
   * ==========================================================
   * ADD SKILL
   * ==========================================================
   *
   * POST /api/skills
   *
   * Used by ADMIN / MANAGER.
   */
  addSkill(
    skill: Skill
  ): Observable<Skill> {

    const request = {

      name:
        skill.name ??
        skill.skillName,

      description:
        skill.description ?? '',

      category:
        skill.category ?? ''

    };

    return this.http
      .post<ApiResponse<any>>(
        this.apiUrl,
        request
      )
      .pipe(

        map(response =>
          this.normalizeSkill(
            response.data
          )
        )

      );
  }


  /*
   * ==========================================================
   * UPDATE SKILL
   * ==========================================================
   *
   * PUT /api/skills/{id}
   */
  updateSkill(
    id: string | number,
    skill: Skill
  ): Observable<Skill> {

    const request = {

      name:
        skill.name ??
        skill.skillName,

      description:
        skill.description ?? '',

      category:
        skill.category ?? ''

    };

    return this.http
      .put<ApiResponse<any>>(
        `${this.apiUrl}/${id}`,
        request
      )
      .pipe(

        map(response =>
          this.normalizeSkill(
            response.data
          )
        )

      );
  }


  /*
   * ==========================================================
   * DELETE SKILL
   * ==========================================================
   *
   * DELETE /api/skills/{id}
   */
  deleteSkill(
    id: string | number
  ): Observable<any> {

    return this.http
      .delete<ApiResponse<any>>(
        `${this.apiUrl}/${id}`
      );
  }


  /*
   * ==========================================================
   * GET CURRENT EMPLOYEE'S SKILLS
   * ==========================================================
   *
   * GET /api/skills/employee/{userId}
   */
  getSkillsByEmployee(
    userId: number
  ): Observable<EmployeeSkill[]> {

    return this.http
      .get<ApiResponse<any[]>>(
        `${this.apiUrl}/employee/${userId}`
      )
      .pipe(

        map(response => {

          return Array.isArray(response?.data)
            ? response.data
            : [];

        })

      );
  }


  /*
   * ==========================================================
   * ASSIGN SKILL TO EMPLOYEE
   * ==========================================================
   *
   * POST /api/skills/assign
   *
   * This creates/updates the EmployeeSkill record.
   *
   * CareerServiceImpl reads that same EmployeeSkill record,
   * so Career Progress can now react to selected skills.
   */
  assignSkillToEmployee(
    request: SkillAssignRequest
  ): Observable<EmployeeSkill> {

    return this.http
      .post<ApiResponse<any>>(
        `${this.apiUrl}/assign`,
        request
      )
      .pipe(

        map(response =>
          response.data
        )

      );
  }


  /*
   * ==========================================================
   * REMOVE SKILL FROM EMPLOYEE
   * ==========================================================
   *
   * DELETE /api/skills/employee/{userId}/{skillId}
   */
  removeSkillFromEmployee(
    userId: number,
    skillId: number
  ): Observable<any> {

    return this.http
      .delete<ApiResponse<any>>(
        `${this.apiUrl}/employee/${userId}/${skillId}`
      );
  }


  /*
   * ==========================================================
   * NORMALIZE BACKEND SKILL
   * ==========================================================
   *
   * Backend:
   *
   * id
   * name
   * description
   * category
   * employeeCount
   *
   * Frontend originally used:
   *
   * skillId
   * skillName
   * employees
   *
   * We support both so the existing UI is less likely
   * to break.
   */
  private normalizeSkill(
    skill: any
  ): Skill {

    return {

      id:
        skill?.id,

      skillId:
        skill?.id,

      name:
        skill?.name ?? '',

      skillName:
        skill?.name ?? '',

      description:
        skill?.description ?? '',

      category:
        skill?.category ?? '',

      employeeCount:
        skill?.employeeCount ?? 0,

      employees:
        skill?.employeeCount ?? 0,

      level:
        skill?.level ?? 0,

      createdAt:
        skill?.createdAt,

      updatedAt:
        skill?.updatedAt

    };

  }

}