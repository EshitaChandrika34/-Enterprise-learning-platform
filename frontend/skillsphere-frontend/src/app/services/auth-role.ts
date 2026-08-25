import { Injectable } from '@angular/core';

export interface LoggedInUser {
  employeeId?: number;
  employeeCode?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthRoleService {

  getLoggedInUser(): LoggedInUser | null {

    const savedUser =
      localStorage.getItem('loggedInUser');

    if (!savedUser) {
      return null;
    }

    try {
      return JSON.parse(savedUser);
    } catch {
      return null;
    }
  }

  isAdmin(): boolean {

    const role =
      this.getLoggedInUser()
        ?.role
        ?.trim()
        .toLowerCase() ?? '';

    return (
      role === 'admin' ||
      role === 'administrator'
    );
  }

  logout(): void {
    localStorage.removeItem('loggedInUser');
  }
}