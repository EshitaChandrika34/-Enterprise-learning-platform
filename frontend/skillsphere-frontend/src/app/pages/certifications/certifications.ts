import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import {
  Certification,
  CertificationService
} from '../../services/certification';

import {
  LearningService,
  Enrollment,
  Course,
  CourseProgress
} from '../../services/learning';

import {
  EmployeeService,
  Employee
} from '../../services/employee';

import {
  AuthService
} from '../../services/auth';


interface CertificationRow {

  enrollmentId: number;

  employeeId: number;

  employeeName: string;

  employeeCode: string;

  department: string;

  designation: string;

  courseId: number;

  courseName: string;

  progressPercentage: number;

  completionStatus: string;

  eligible: boolean;

  certificateId?: number;

  certificateNumber: string;

  issueDate: string;

  certificateStatus: string;

  enrollmentStatus: string;
}


@Component({
  selector: 'app-certifications',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],

  templateUrl: './certifications.html',

  styleUrl: './certifications.css'
})
export class Certifications implements OnInit {

  certifications: Certification[] = [];

  enrollments: Enrollment[] = [];

  courses: Course[] = [];

  progressList: CourseProgress[] = [];

  employees: Employee[] = [];

  certificationRows: CertificationRow[] = [];


  searchText = '';

  statusFilter = 'ALL';


  loading = true;

  errorMessage = '';

  generatingEnrollmentId:
    number | null = null;


  constructor(

    private certificationService:
      CertificationService,

    private learningService:
      LearningService,

    private employeeService:
      EmployeeService,

    public authService:
      AuthService,

    private cdr:
      ChangeDetectorRef

  ) {}


  // ==========================================
  // INITIAL LOAD
  // ==========================================

  ngOnInit(): void {

    console.log(
      'Certifications page started'
    );

    console.log(
      'Logged in user:',
      this.authService.getUser()
    );

    this.loadCertifications();
  }


  // ==========================================
  // LOAD ALL DATA
  // ==========================================

  loadCertifications(): void {

    this.loading = true;

    this.errorMessage = '';

    this.certificationRows = [];

    this.certifications = [];

    this.enrollments = [];

    this.courses = [];

    this.progressList = [];

    this.employees = [];

    this.loadEnrollments();
  }


  // ==========================================
  // LOAD ENROLLMENTS
  // ==========================================

  loadEnrollments(): void {

    this.learningService
      .getEnrollments()
      .subscribe({

        next: (
          data: Enrollment[]
        ) => {

          console.log(
            'Enrollments:',
            data
          );

          this.enrollments =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loadCourses();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Enrollment API error:',
            error
          );

          this.enrollments = [];

          this.errorMessage =
            'Unable to load enrollments.';

          this.loading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // LOAD COURSES
  // ==========================================

  loadCourses(): void {

    this.learningService
      .getCourses()
      .subscribe({

        next: (
          data: Course[]
        ) => {

          console.log(
            'Courses:',
            data
          );

          this.courses =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loadProgress();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Course API error:',
            error
          );

          this.courses = [];

          this.loadProgress();
        }

      });
  }


  // ==========================================
  // LOAD PROGRESS
  // ==========================================

  loadProgress(): void {

    this.learningService
      .getProgress()
      .subscribe({

        next: (
          data: CourseProgress[]
        ) => {

          console.log(
            'Progress:',
            data
          );

          this.progressList =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loadCertificates();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Progress API error:',
            error
          );

          this.progressList = [];

          this.loadCertificates();
        }

      });
  }


  // ==========================================
  // LOAD CERTIFICATES
  // ==========================================

  loadCertificates(): void {

    this.certificationService
      .getCertifications()
      .subscribe({

        next: (
          data: Certification[]
        ) => {

          console.log(
            'Certificates:',
            data
          );

          this.certifications =
            Array.isArray(data)
              ? [...data]
              : [];

          this.loadEmployees();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Certificate API error:',
            error
          );

          this.certifications = [];

          this.loadEmployees();
        }

      });
  }


  // ==========================================
  // LOAD EMPLOYEES
  // ==========================================

  loadEmployees(): void {

    this.employeeService
      .getEmployees()
      .subscribe({

        next: (
          data: Employee[]
        ) => {

          console.log(
            'Employees:',
            data
          );

          this.employees =
            Array.isArray(data)
              ? [...data]
              : [];

          this.buildCertificationRows();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Employee API error:',
            error
          );

          this.employees = [];

          this.buildCertificationRows();
        }

      });
  }


  // ==========================================
  // BUILD CERTIFICATION ROWS
  // ==========================================

  buildCertificationRows(): void {

    const rows:
      CertificationRow[] = [];

    for (
      const enrollment
      of this.enrollments
    ) {

      if (
        enrollment.enrollmentId == null
      ) {

        continue;
      }


      const employee =
        this.employees.find(
          item => {

            const itemId =
              Number(
                (item as any).id
                ??
                item.employeeId
              );

            return (
              itemId
              ===
              Number(
                enrollment.employeeId
              )
            );
          }
        );


      const course =
        this.courses.find(
          item =>
            Number(
              item.courseId
            )
            ===
            Number(
              enrollment.courseId
            )
        );


      const progress =
        this.progressList.find(
          item =>
            Number(
              item.enrollmentId
            )
            ===
            Number(
              enrollment.enrollmentId
            )
        );


      const certificate =
        this.certifications.find(
          item =>
            Number(
              item.enrollmentId
            )
            ===
            Number(
              enrollment.enrollmentId
            )
        );


      const progressPercentage =
        Number(
          progress
            ?.progressPercentage
          ??
          0
        );


      const employeeName =
        employee
          ? `${
              employee.firstName
              ||
              ''
            } ${
              employee.lastName
              ||
              ''
            }`.trim()
          :
          `Employee ${enrollment.employeeId}`;


      rows.push({

        enrollmentId:
          enrollment.enrollmentId,

        employeeId:
          Number(
            enrollment.employeeId
          ),

        employeeName:
          employeeName,

        employeeCode:
          employee
            ?.employeeCode
          ||
          (employee as any)
            ?.employeeId
          ||
          '-',

        department:
          employee
            ?.department
          ||
          '-',

        designation:
          employee
            ?.designation
          ||
          '-',

        courseId:
          enrollment.courseId,

        courseName:
          course
            ?.courseName
          ||
          certificate
            ?.courseName
          ||
          `Course ${enrollment.courseId}`,

        progressPercentage:
          progressPercentage,

        completionStatus:
          progress
            ?.completionStatus
          ||
          (
            progressPercentage >= 100
              ? 'COMPLETED'
              : 'IN_PROGRESS'
          ),

        eligible:
          progressPercentage >= 80,

        certificateId:
          certificate
            ?.certificateId,

        certificateNumber:
          certificate
            ?.certificateNumber
          ||
          '-',

        issueDate:
          certificate
            ?.issueDate
          ||
          '-',

        certificateStatus:
          certificate
            ?.status
          ||
          (
            certificate
              ?.certificateId
              != null
              ? 'ISSUED'
              : 'NOT GENERATED'
          ),

        enrollmentStatus:
          enrollment.status
          ||
          'ENROLLED'

      });
    }


    console.log(
      'Final Certification Rows:',
      rows
    );

    this.certificationRows =
      rows;

    console.log(
      'Role Rows:',
      this.roleRows
    );

    this.loading = false;

    this.cdr.detectChanges();
  }


  // ==========================================
  // CURRENT LOGGED-IN EMPLOYEE DATABASE ID
  // ==========================================

  get currentEmployeeId():
    number | null {

    const user =
      this.authService
        .getUser();

    if (
      !user
      ||
      user.id == null
    ) {

      return null;
    }

    return Number(
      user.id
    );
  }


  // ==========================================
  // ROLE-BASED ROWS
  // ==========================================

  get roleRows():
    CertificationRow[] {

    // ADMIN
    if (
      this.authService.isAdmin()
    ) {

      return this.certificationRows;
    }


    // HR
    if (
      this.authService.isHR()
    ) {

      return this.certificationRows;
    }


    // EMPLOYEE
    if (
      this.authService.isEmployee()
    ) {

      const user =
        this.authService.getUser();

      console.log(
        'Logged in employee:',
        user
      );

      if (
        !user
        ||
        user.id == null
      ) {

        return [];
      }

      const employeeId =
        Number(
          user.id
        );

      return this.certificationRows.filter(
        row =>
          Number(
            row.employeeId
          )
          ===
          employeeId
      );
    }


    return [];
  }


  // ==========================================
  // SEARCH + STATUS FILTER
  // ==========================================

  get filteredRows():
    CertificationRow[] {

    const search =
      this.searchText
        .trim()
        .toLowerCase();

    return this.roleRows.filter(
      row => {

        const text = `
          ${row.employeeName}
          ${row.employeeCode}
          ${row.department}
          ${row.designation}
          ${row.courseName}
          ${row.certificateNumber}
          ${row.certificateStatus}
          ${row.completionStatus}
          ${row.enrollmentStatus}
        `.toLowerCase();


        const matchesSearch =
          !search
          ||
          text.includes(
            search
          );


        let matchesStatus =
          true;


        if (
          this.statusFilter
          === 'ISSUED'
        ) {

          matchesStatus =
            row.certificateId
            != null;
        }


        if (
          this.statusFilter
          === 'ELIGIBLE'
        ) {

          matchesStatus =
            row.eligible;
        }


        if (
          this.statusFilter
          === 'NOT_ELIGIBLE'
        ) {

          matchesStatus =
            !row.eligible;
        }


        if (
          this.statusFilter
          === 'NOT_GENERATED'
        ) {

          matchesStatus =
            row.certificateId
            == null;
        }


        return (
          matchesSearch
          &&
          matchesStatus
        );
      }
    );
  }


  // ==========================================
  // COUNTS
  // ==========================================

  get totalEnrollmentCount():
    number {

    return this.roleRows.length;
  }


  get eligibleCount():
    number {

    return this.roleRows.filter(
      row =>
        row.eligible
    ).length;
  }


  get issuedCount():
    number {

    return this.roleRows.filter(
      row =>
        row.certificateId
        != null
    ).length;
  }


  // ==========================================
  // GENERATE CERTIFICATE
  // ==========================================

  generateCertificate(
    row: CertificationRow
  ): void {

    if (
      row.progressPercentage
      < 80
    ) {

      alert(
        'Minimum 80% progress is required.'
      );

      return;
    }


    if (
      row.certificateId
      != null
    ) {

      alert(
        'Certificate already generated.'
      );

      return;
    }


    this.generatingEnrollmentId =
      row.enrollmentId;


    this.certificationService
      .generateCertificate(
        row.enrollmentId
      )
      .subscribe({

        next: (
          certificate:
            Certification
        ) => {

          console.log(
            'Certificate generated:',
            certificate
          );

          this.generatingEnrollmentId =
            null;

          alert(
            'Certificate generated successfully.'
          );

          this.loadCertifications();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Generate certificate error:',
            error
          );

          this.generatingEnrollmentId =
            null;

          alert(
            error?.error?.message
            ||
            'Unable to generate certificate.'
          );

          this.cdr.detectChanges();
        }

      });
  }


  // ==========================================
  // DELETE CERTIFICATE
  // ==========================================

  deleteCertificate(
    row: CertificationRow
  ): void {

    if (
      !this.authService
        .canDeleteCertificate()
    ) {

      return;
    }


    if (
      row.certificateId
      == null
    ) {

      return;
    }


    if (
      !confirm(
        'Are you sure you want to delete this certificate?'
      )
    ) {

      return;
    }


    this.certificationService
      .deleteCertification(
        row.certificateId
      )
      .subscribe({

        next: () => {

          this.loadCertifications();
        },

        error: (
          error: any
        ) => {

          console.error(
            'Delete certificate error:',
            error
          );

          alert(
            'Unable to delete certificate.'
          );
        }

      });
  }


  // ==========================================
  // REFRESH
  // ==========================================

  refresh(): void {

    this.loadCertifications();
  }
}