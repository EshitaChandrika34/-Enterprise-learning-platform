import { Routes } from '@angular/router';

import { Landing } from './pages/landing/landing';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Employees } from './pages/employees/employees';
import { Learning } from './pages/learning/learning';
import { Skills } from './pages/skills/skills';
import { Certifications } from './pages/certifications/certifications';
import { Career } from './pages/career/career';
import { Analytics } from './pages/analytics/analytics';
import { Settings } from './pages/settings/settings';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { VerifyOtp } from './pages/verify-otp/verify-otp';
import { CertificateView} from './pages/certificate-view/certificate-view';

export const routes: Routes = [

  {
    path: '',
    component: Landing
  },

  {
    path: 'login',
    component: Login
  },

  {
    path: 'register',
    component: Register
  },

  { path: 'forgot-password', component: ForgotPassword },

  { path: 'verify-otp', component: VerifyOtp },

  {
    path: 'dashboard',
    component: Dashboard
  },

  {
    path: 'employees',
    component: Employees
  },

  {
    path: 'learning',
    component: Learning
  },

  {
    path: 'skills',
    component: Skills
  },

  {
    path: 'certifications',
    component: Certifications
  },
  {
  path: 'certificate/:id',
  component: CertificateView
},

  {
    path: 'career',
    component: Career
  },

  {
    path: 'analytics',
    component: Analytics
  },

  {
    path: 'settings',
    component: Settings
  },

  {
    path: '**',
    redirectTo: ''
  }

];