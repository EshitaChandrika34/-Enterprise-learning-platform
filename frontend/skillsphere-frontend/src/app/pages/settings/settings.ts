import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Router,
  RouterModule
} from '@angular/router';

import {
  Employee
} from '../../services/employee';

import {
  AuthService
} from '../../services/auth';


@Component({
  selector: 'app-settings',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],

  templateUrl: './settings.html',

  styleUrl: './settings.css'
})
export class Settings implements OnInit {

  profile:
    Employee | null = null;


  notifications = {

    email:
      true,

    courseUpdates:
      true,

    certificationAlerts:
      true,

    careerUpdates:
      false
  };


  preferences = {

    theme:
      'Dark',

    language:
      'English',

    timezone:
      'IST (UTC+5:30)'
  };


  constructor(

    private router:
      Router,

    public authService:
      AuthService

  ) {}


  // ==========================================
  // INITIAL LOAD
  // ==========================================

  ngOnInit(): void {

    const storedUser =
      localStorage.getItem(
        'loggedInUser'
      );


    if (!storedUser) {

      this.router.navigate([
        '/login'
      ]);

      return;
    }


    try {

      this.profile =
        JSON.parse(
          storedUser
        );

    } catch {

      localStorage.removeItem(
        'loggedInUser'
      );

      this.router.navigate([
        '/login'
      ]);

      return;
    }


    // ==========================================
    // LOAD NOTIFICATIONS
    // ==========================================

    const savedNotifications =
      localStorage.getItem(
        'userNotifications'
      );


    if (
      savedNotifications
    ) {

      try {

        this.notifications =
          JSON.parse(
            savedNotifications
          );

      } catch {

        console.error(
          'Unable to load notification preferences.'
        );
      }
    }


    // ==========================================
    // LOAD PREFERENCES
    // ==========================================

    const savedPreferences =
      localStorage.getItem(
        'userPreferences'
      );


    if (
      savedPreferences
    ) {

      try {

        this.preferences =
          JSON.parse(
            savedPreferences
          );

      } catch {

        console.error(
          'Unable to load application preferences.'
        );
      }
    }


    // ==========================================
    // LOAD THEME
    // ==========================================

    const savedTheme =
      localStorage.getItem(
        'theme'
      );


    if (
      savedTheme
    ) {

      this.preferences.theme =
        savedTheme;
    }


    this.applyTheme();
  }


  // ==========================================
  // INITIALS
  // ==========================================

  initials(): string {

    if (
      !this.profile
    ) {

      return 'US';
    }


    const first =
      this.profile.firstName
        ?.charAt(0)
      ||
      '';


    const last =
      this.profile.lastName
        ?.charAt(0)
      ||
      '';


    return (
      `${first}${last}`
        .toUpperCase()
      ||
      'US'
    );
  }


  // ==========================================
  // PROFILE SUBTITLE
  // ==========================================

  getProfileSubtitle(): string {

    if (
      this.authService.isAdmin()
    ) {

      return 'Administrator';
    }


    if (
      this.authService.isHR()
    ) {

      return 'HR Manager';
    }


    if (
      this.profile?.designation
    ) {

      return this.profile.designation;
    }


    return 'Employee';
  }


  // ==========================================
  // ROLE LABEL
  // ==========================================

  getRoleLabel(): string {

    return this.authService
      .getRoleLabel();
  }


  // ==========================================
  // CHANGE THEME
  // ==========================================

  changeTheme(): void {

    localStorage.setItem(
      'theme',
      this.preferences.theme
    );


    this.applyTheme();
  }


  // ==========================================
  // APPLY THEME
  // ==========================================

  private applyTheme(): void {

    const root =
      document.documentElement;


    if (
      this.preferences.theme
      ===
      'Light'
    ) {

      root.classList.add(
        'light-theme'
      );

    } else {

      root.classList.remove(
        'light-theme'
      );
    }
  }


  // ==========================================
  // SAVE SETTINGS
  // ==========================================

  saveSettings(): void {

    localStorage.setItem(

      'userNotifications',

      JSON.stringify(
        this.notifications
      )
    );


    localStorage.setItem(

      'userPreferences',

      JSON.stringify(
        this.preferences
      )
    );


    localStorage.setItem(
      'theme',
      this.preferences.theme
    );


    this.applyTheme();


    alert(
      'Settings saved successfully.'
    );
  }


  // ==========================================
  // RESET SETTINGS
  // ==========================================

  resetSettings(): void {

    this.notifications = {

      email:
        true,

      courseUpdates:
        true,

      certificationAlerts:
        true,

      careerUpdates:
        false
    };


    this.preferences = {

      theme:
        'Dark',

      language:
        'English',

      timezone:
        'IST (UTC+5:30)'
    };


    localStorage.removeItem(
      'userNotifications'
    );


    localStorage.removeItem(
      'userPreferences'
    );


    localStorage.setItem(
      'theme',
      'Dark'
    );


    this.applyTheme();
  }


  // ==========================================
  // LOGOUT
  // ==========================================

  logout(): void {

    localStorage.removeItem(
      'loggedInUser'
    );


    localStorage.removeItem(
      'isLoggedIn'
    );


    localStorage.removeItem(
      'token'
    );


    localStorage.removeItem(
      'userRole'
    );


    this.router.navigate([
      '/login'
    ]);
  }

}