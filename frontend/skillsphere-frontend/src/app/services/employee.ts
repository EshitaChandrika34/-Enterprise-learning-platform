import {
  Injectable
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';


export interface Employee {

  employeeId?:
    string | number;

  employeeCode?:
    string;

  firstName:
    string;

  lastName:
    string;

  email:
    string;

  phone?:
    string;

  department:
    string;

  designation:
    string;

  experience?:
    number;

  role:
    string;

  status:
    string;
}


@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private employeeUrl =
    'http://localhost:8081/api/employees';

  private registerUrl =
    'http://localhost:8090/api/auth/register';


  constructor(
    private http: HttpClient
  ) {}


  // ==========================================
  // GET ALL EMPLOYEES
  // ==========================================

  getEmployees():
    Observable<Employee[]> {

    return this.http.get<Employee[]>(
      this.employeeUrl
    );
  }


  // ==========================================
  // GET EMPLOYEE
  // ==========================================

  getEmployeeById(
    id: string | number
  ): Observable<Employee> {

    return this.http.get<Employee>(
      `${this.employeeUrl}/${id}`
    );
  }


  // ==========================================
  // GET EMPLOYEE BY EMAIL
  // ==========================================

  getEmployeeByEmail(
    email: string
  ): Observable<Employee> {

    return this.http.get<Employee>(
      `${this.employeeUrl}/email/${encodeURIComponent(email)}`
    );
  }


  // ==========================================
  // CREATE EMPLOYEE PROFILE - 8081
  // ==========================================

  addEmployee(
    employee: Employee
  ): Observable<any> {

    return this.http.post<any>(
      this.employeeUrl,
      employee
    );
  }


  // ==========================================
  // CREATE LOGIN ACCOUNT - 8090
  // ==========================================

  registerLoginAccount(
    data: any
  ): Observable<any> {

    return this.http.post<any>(
      this.registerUrl,
      data
    );
  }


  // ==========================================
  // UPDATE EMPLOYEE
  // ==========================================

  updateEmployee(
    id: string | number,
    employee: Employee
  ): Observable<any> {

    return this.http.put<any>(
      `${this.employeeUrl}/${id}`,
      employee
    );
  }


  // ==========================================
  // DELETE EMPLOYEE
  // ==========================================

  deleteEmployee(
    id: string | number
  ): Observable<any> {

    return this.http.delete<any>(
      `${this.employeeUrl}/${id}`
    );
  }

}