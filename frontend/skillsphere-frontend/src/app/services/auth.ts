import { Injectable } from '@angular/core';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class AuthService {


  constructor(
    private router: Router
  ) {}


  // ==========================================
  // GET LOGGED IN USER
  // ==========================================

  getUser(): any {

    const storedUser =
      localStorage.getItem(
        'loggedInUser'
      );


    if (!storedUser) {

      return null;

    }


    try {

      return JSON.parse(
        storedUser
      );

    }

    catch {

      return null;

    }

  }


  // ==========================================
  // ALIAS
  // ==========================================

  getLoggedInUser(): any {

    return this.getUser();

  }


  // ==========================================
  // TOKEN
  // ==========================================

  getToken(): string {

    return (
      localStorage.getItem(
        'token'
      ) || ''
    );

  }


  // ==========================================
  // LOGGED IN?
  // ==========================================

  isLoggedIn(): boolean {

    const token =
      this.getToken();


    const user =
      this.getUser();


    return !!token && !!user;

  }


  // ==========================================
  // GET ROLE
  // ==========================================

  getRole(): string {

    const user =
      this.getUser();


    if (!user) {

      return '';

    }


    return this.normalizeRole(
      user.role
    );

  }


  // ==========================================
  // NORMALIZE ROLE
  // ==========================================

  private normalizeRole(
    value: any
  ): string {

    const role =
      (value || '')
        .toString()
        .trim()
        .toUpperCase();


    if (
      role === 'ADMIN' ||
      role === 'ADMINISTRATOR' ||
      role === 'ROLE_ADMIN' ||
      role === 'ROLE_ADMINISTRATOR'
    ) {

      return 'ADMIN';

    }


    if (
      role === 'HR' ||
      role === 'ROLE_HR' ||
      role === 'HUMAN RESOURCE' ||
      role === 'HUMAN RESOURCES'
    ) {

      return 'HR';

    }


    if (
      role === 'EMPLOYEE' ||
      role === 'USER' ||
      role === 'ROLE_EMPLOYEE' ||
      role === 'ROLE_USER'
    ) {

      return 'EMPLOYEE';

    }


    return role;

  }


  // ==========================================
  // ROLE LABEL
  // ==========================================

  getRoleLabel(): string {

    const role =
      this.getRole();


    if (role === 'ADMIN') {

      return 'Administrator';

    }


    if (role === 'HR') {

      return 'HR';

    }


    if (role === 'EMPLOYEE') {

      return 'Employee';

    }


    return 'User';

  }


  // ==========================================
  // ADMIN
  // ==========================================

  isAdmin(): boolean {

    return (
      this.getRole() === 'ADMIN'
    );

  }


  // ==========================================
  // HR
  // ==========================================

  isHR(): boolean {

    return (
      this.getRole() === 'HR'
    );

  }


  // ==========================================
  // EMPLOYEE
  // ==========================================

  isEmployee(): boolean {

    return (
      this.getRole() === 'EMPLOYEE'
    );

  }


  // ==========================================
  // EMPLOYEE PERMISSIONS
  // ==========================================

  canViewEmployees(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canManageEmployees(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canCreateEmployee(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canAddEmployee(): boolean {

    return this.canCreateEmployee();

  }


  canEditEmployee(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canDeleteEmployee(): boolean {

    return this.isAdmin();

  }


  // ==========================================
  // LEARNING PERMISSIONS
  // ==========================================

  canManageLearning(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canCreateCourse(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canAddCourse(): boolean {

    return this.canCreateCourse();

  }


  canEditCourse(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canDeleteCourse(): boolean {

    return this.isAdmin();

  }


  canStartCourse(): boolean {

    return this.isEmployee();

  }


  // ==========================================
  // SKILL PERMISSIONS
  // ==========================================

  canManageSkills(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canAddSkill(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canEditSkill(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canDeleteSkill(): boolean {

    return this.isAdmin();

  }


  canSelectSkill(): boolean {

    return this.isEmployee();

  }


  // ==========================================
  // CERTIFICATION PERMISSIONS
  // ==========================================

  canViewCertifications(): boolean {

    return this.isLoggedIn();

  }


  canManageCertifications(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  canGenerateCertificate(): boolean {

    return (
      this.isAdmin() ||
      this.isHR() ||
      this.isEmployee()
    );

  }


  canDeleteCertificate(): boolean {

    return this.isAdmin();

  }


  // ==========================================
  // CAREER
  // ==========================================

  canViewCareer(): boolean {

    return this.isLoggedIn();

  }


  canViewPersonalCareer(): boolean {

    return this.isEmployee();

  }


  canManageCareer(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  // ==========================================
  // ANALYTICS
  // ==========================================

  canViewAnalytics(): boolean {

    return (
      this.isAdmin() ||
      this.isHR()
    );

  }


  // ==========================================
  // SETTINGS
  // ==========================================

  canViewSettings(): boolean {

    return this.isLoggedIn();

  }


  // ==========================================
  // LOGOUT
  // ==========================================

  logout(): void {

    localStorage.removeItem(
      'token'
    );


    localStorage.removeItem(
      'loggedInUser'
    );


    localStorage.removeItem(
      'isLoggedIn'
    );


    localStorage.removeItem(
      'userRole'
    );


    localStorage.removeItem(
      'selectedSkills'
    );


    this.router.navigate([
      '/login'
    ]);

  }

}