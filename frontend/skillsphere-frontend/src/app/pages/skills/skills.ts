import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  RouterModule
} from '@angular/router';

import {
  SkillService,
  Skill,
  EmployeeSkill,
  SkillAssignRequest
} from '../../services/skill';

import {
  AuthService
} from '../../services/auth';


@Component({
  selector: 'app-skills',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],

  templateUrl: './skills.html',

  styleUrl: './skills.css'
})
export class Skills implements OnInit {

  // ============================================================
  // DATA
  // ============================================================

  skills: Skill[] = [];

  selectedSkills: Skill[] = [];

  employeeSkills: EmployeeSkill[] = [];

  searchText = '';

  loading = false;

  savingSkill = false;

  totalEmployees = 0;

  advancedSkills = 0;

  averageLevel = 0;

  errorMessage = '';


  // ============================================================
  // CONSTRUCTOR
  // ============================================================

  constructor(
    private skillService: SkillService,

    private cdr: ChangeDetectorRef,

    public authService: AuthService
  ) {}


  // ============================================================
  // PAGE LOAD
  // ============================================================

  ngOnInit(): void {

    /*
     * IMPORTANT:
     *
     * Do NOT load selected skills from a global
     * localStorage key.
     *
     * The backend database is the source of truth.
     */

    this.selectedSkills = [];

    this.employeeSkills = [];

    this.loadSkills();

    this.loadEmployeeSkills();
  }


  // ============================================================
  // LOAD ALL AVAILABLE SKILLS
  // ============================================================

  loadSkills(): void {

    this.loading = true;

    this.errorMessage = '';

    this.skillService
      .getSkills()
      .subscribe({

        next: (data: Skill[]) => {

          console.log(
            'Skills received from backend:',
            data
          );

          this.skills =
            Array.isArray(data)
              ? [...data]
              : [];

          this.calculateStatistics();

          this.loading = false;

          this.cdr.detectChanges();
        },

        error: (error: unknown) => {

          console.error(
            'Skill loading error:',
            error
          );

          this.skills = [];

          this.calculateStatistics();

          this.loading = false;

          this.errorMessage =
            'Unable to load skills from the server.';

          this.cdr.detectChanges();
        }

      });
  }


  // ============================================================
  // LOAD CURRENT EMPLOYEE'S SKILLS
  // ============================================================

  loadEmployeeSkills(): void {

    /*
     * Always clear the previous frontend state first.
     *
     * This is important when a different employee
     * logs into the same browser.
     */
    this.employeeSkills = [];

    this.selectedSkills = [];

    const userId =
      this.getCurrentUserId();


    if (userId === null) {

      console.warn(
        'Current user ID not found.'
      );

      this.cdr.detectChanges();

      return;
    }


    console.log(
      'Loading skills for employee/user ID:',
      userId
    );


    this.skillService
      .getSkillsByEmployee(userId)
      .subscribe({

        next: (data: EmployeeSkill[]) => {

          console.log(
            'Employee skills from backend:',
            data
          );


          /*
           * Backend is the ONLY source of truth.
           *
           * If the backend returns an empty array,
           * the employee has zero selected skills.
           */
          this.employeeSkills =
            Array.isArray(data)
              ? [...data]
              : [];


          /*
           * Always synchronize, even when the
           * employee has ZERO skills.
           *
           * The old code returned early when
           * employeeSkills.length === 0.
           */
          this.syncSelectedSkillsFromBackend();


          this.cdr.detectChanges();
        },


        error: (error: unknown) => {

          console.error(
            'Employee skill loading error:',
            error
          );


          /*
           * If loading fails, do not display
           * another employee's cached skills.
           */
          this.employeeSkills = [];

          this.selectedSkills = [];

          this.cdr.detectChanges();
        }

      });
  }


  // ============================================================
  // SYNC DATABASE SKILLS WITH FRONTEND
  // ============================================================

  syncSelectedSkillsFromBackend(): void {

    /*
     * IMPORTANT:
     *
     * We completely rebuild selectedSkills
     * from the current employee's database records.
     *
     * Therefore:
     *
     * Employee A -> A's skills
     * Employee B -> B's skills
     * New Employee -> []
     */

    this.selectedSkills =
      this.employeeSkills.map(
        employeeSkill => {

          return {

            skillId:
              employeeSkill.skillId,

            id:
              employeeSkill.skillId,

            skillName:
              employeeSkill.skillName ?? '',

            name:
              employeeSkill.skillName ?? '',

            category:
              employeeSkill.skillCategory ?? '',

            description:
              '',

            level:
              this.convertProficiencyToLevel(
                employeeSkill.proficiencyLevel
              ),

            employees:
              0,

            employeeCount:
              0

          };

        }
      );


    console.log(
      'Current employee selected skills:',
      this.selectedSkills
    );
  }


  // ============================================================
  // FILTER SKILLS
  // ============================================================

  get filteredSkills(): Skill[] {

    const search =
      this.searchText
        .trim()
        .toLowerCase();


    if (!search) {

      return this.skills;
    }


    return this.skills.filter(
      skill => {

        const text = `
          ${skill.skillName || ''}
          ${skill.name || ''}
          ${skill.category || ''}
          ${skill.description || ''}
        `.toLowerCase();


        return text.includes(search);
      }
    );
  }


  // ============================================================
  // TOTAL SKILLS
  // ============================================================

  get totalSkills(): number {

    return this.skills.length;
  }


  // ============================================================
  // NUMBER OF SELECTED SKILLS
  // ============================================================

  get selectedSkillCount(): number {

    /*
     * Only backend employee-skill records count.
     *
     * Do NOT fall back to localStorage.
     */
    return this.employeeSkills.length;
  }


  // ============================================================
  // STATISTICS
  // ============================================================

  calculateStatistics(): void {

    this.totalEmployees =
      this.skills.reduce(
        (
          total: number,
          skill: Skill
        ) =>
          total
          +
          (
            skill.employeeCount ??
            skill.employees ??
            0
          ),
        0
      );


    this.advancedSkills =
      this.skills.filter(
        skill =>
          (skill.level ?? 0) >= 4
      ).length;


    if (
      this.skills.length === 0
    ) {

      this.averageLevel = 0;

      return;
    }


    const totalLevel =
      this.skills.reduce(
        (
          total: number,
          skill: Skill
        ) =>
          total
          +
          (
            skill.level ??
            0
          ),
        0
      );


    this.averageLevel =
      Number(
        (
          totalLevel /
          this.skills.length
        ).toFixed(1)
      );
  }


  // ============================================================
  // EMPLOYEE SELECT SKILL
  // ============================================================

  selectSkill(
    skill: Skill
  ): void {

    /*
     * Only employees can select their own skills.
     */
    if (
      !this.authService.isEmployee()
    ) {

      return;
    }


    /*
     * Check ONLY current employee's backend
     * skill records.
     */
    if (
      this.isSkillSelected(skill)
    ) {

      alert(
        'This skill is already selected.'
      );

      return;
    }


    const userId =
      this.getCurrentUserId();


    if (
      userId === null
    ) {

      alert(
        'Unable to identify the logged-in employee. Please login again.'
      );

      return;
    }


    const skillId =
      Number(
        skill.skillId ??
        skill.id
      );


    if (
      !Number.isFinite(skillId)
    ) {

      alert(
        'Invalid skill ID.'
      );

      return;
    }


    const request:
      SkillAssignRequest = {

        userId:

          userId,

        skillId:

          skillId,

        proficiencyLevel:

          'INTERMEDIATE',

        verified:

          false,

        yearsOfExperience:

          0

      };


    this.savingSkill = true;


    console.log(
      'Assigning skill:',
      request
    );


    this.skillService
      .assignSkillToEmployee(
        request
      )
      .subscribe({

        next: (
          employeeSkill:
            EmployeeSkill
        ) => {

          console.log(
            'Skill assigned successfully:',
            employeeSkill
          );


          /*
           * Add the new backend record.
           */
          this.employeeSkills =
            [
              ...this.employeeSkills,
              employeeSkill
            ];


          /*
           * Rebuild selected skills from
           * backend data.
           */
          this.syncSelectedSkillsFromBackend();


          this.savingSkill = false;


          alert(
            'Skill selected successfully.'
          );


          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Skill assignment error:',
            error
          );


          this.savingSkill = false;


          if (
            error?.status === 400
          ) {

            alert(
              error?.error?.message
              ??
              'This skill could not be assigned.'
            );

          } else if (
            error?.status === 401
          ) {

            alert(
              'Your login session has expired. Please login again.'
            );

          } else if (
            error?.status === 403
          ) {

            alert(
              'You do not have permission to assign this skill.'
            );

          } else {

            alert(
              'Unable to save the skill. Check the backend.'
            );
          }
        }

      });
  }


  // ============================================================
  // CHECK WHETHER CURRENT EMPLOYEE SELECTED SKILL
  // ============================================================

  isSkillSelected(
    skill: Skill
  ): boolean {

    const skillId =
      Number(
        skill.skillId ??
        skill.id
      );


    /*
     * IMPORTANT:
     *
     * Only check employeeSkills.
     *
     * Do NOT check localStorage.
     */
    return this.employeeSkills.some(
      employeeSkill =>
        Number(
          employeeSkill.skillId
        ) === skillId
    );
  }


  // ============================================================
  // REMOVE SELECTED SKILL
  // ============================================================

  removeSkill(
    skill: Skill
  ): void {

    if (
      !this.authService.isEmployee()
    ) {

      return;
    }


    const userId =
      this.getCurrentUserId();


    const skillId =
      Number(
        skill.skillId ??
        skill.id
      );


    if (
      userId === null
    ) {

      alert(
        'Unable to identify the logged-in employee.'
      );

      return;
    }


    if (
      !Number.isFinite(skillId)
    ) {

      alert(
        'Invalid skill ID.'
      );

      return;
    }


    if (
      !confirm(
        `Remove ${
          skill.skillName ||
          skill.name ||
          'this skill'
        }?`
      )
    ) {

      return;
    }


    this.skillService
      .removeSkillFromEmployee(
        userId,
        skillId
      )
      .subscribe({

        next: () => {

          /*
           * Remove ONLY from the current
           * employee's backend state.
           */
          this.employeeSkills =
            this.employeeSkills.filter(
              employeeSkill =>
                Number(
                  employeeSkill.skillId
                ) !== skillId
            );


          /*
           * Rebuild selected skills.
           */
          this.syncSelectedSkillsFromBackend();


          alert(
            'Skill removed successfully.'
          );


          this.cdr.detectChanges();
        },


        error: (error: any) => {

          console.error(
            'Skill removal error:',
            error
          );


          alert(
            'Unable to remove skill.'
          );
        }

      });
  }


  // ============================================================
  // ADD SKILL
  // ADMIN / HR
  // ============================================================

  addSkill(): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    const skill: Skill = {

      skillName:
        'Angular',

      category:
        'TECHNICAL',

      description:
        'Frontend development',

      level:
        1,

      employees:
        0
    };


    this.skillService
      .addSkill(
        skill
      )
      .subscribe({

        next: (
          createdSkill:
            Skill
        ) => {

          console.log(
            'Skill added:',
            createdSkill
          );

          this.loadSkills();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Add skill error:',
            error
          );


          alert(
            'Unable to add skill.'
          );
        }

      });
  }


  // ============================================================
  // DELETE SKILL
  // ============================================================

  deleteSkill(
    id: string | number
  ): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    if (
      !confirm(
        'Delete this skill?'
      )
    ) {

      return;
    }


    this.skillService
      .deleteSkill(
        id
      )
      .subscribe({

        next: () => {

          this.loadSkills();
        },


        error: (
          error: any
        ) => {

          console.error(
            'Delete skill error:',
            error
          );


          alert(
            'Unable to delete skill.'
          );
        }

      });
  }


  // ============================================================
  // LEVEL TEXT
  // ============================================================

  getLevelText(
    level: number
  ): string {

    switch (level) {

      case 1:
        return 'Beginner';

      case 2:
        return 'Basic';

      case 3:
        return 'Intermediate';

      case 4:
        return 'Advanced';

      case 5:
        return 'Expert';

      default:
        return 'Not Rated';
    }
  }


  // ============================================================
  // INCREASE LEVEL
  // ============================================================

  increaseLevel(
    skill: Skill
  ): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    if (
      (skill.level ?? 0) < 5
    ) {

      skill.level =
        (skill.level ?? 0) + 1;

      this.calculateStatistics();
    }
  }


  // ============================================================
  // DECREASE LEVEL
  // ============================================================

  decreaseLevel(
    skill: Skill
  ): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    if (
      (skill.level ?? 0) > 1
    ) {

      skill.level =
        (skill.level ?? 0) - 1;

      this.calculateStatistics();
    }
  }


  // ============================================================
  // EDIT
  // ============================================================

  editSkill(
    skill: Skill
  ): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    console.log(
      'Edit skill:',
      skill
    );
  }


  // ============================================================
  // CONVERT PROFICIENCY TO DISPLAY LEVEL
  // ============================================================

  private convertProficiencyToLevel(
    level:
      | 'BEGINNER'
      | 'INTERMEDIATE'
      | 'ADVANCED'
      | 'EXPERT'
      | undefined
  ): number {

    switch (level) {

      case 'BEGINNER':
        return 1;

      case 'INTERMEDIATE':
        return 3;

      case 'ADVANCED':
        return 4;

      case 'EXPERT':
        return 5;

      default:
        return 1;
    }
  }


  // ============================================================
  // GET CURRENT USER ID
  // ============================================================

  private getCurrentUserId():
    number | null {

    const possibleUserKeys = [
      'currentUser',
      'user',
      'loggedInUser',
      'authUser'
    ];


    for (
      const key
      of possibleUserKeys
    ) {

      const stored =
        localStorage.getItem(
          key
        );


      if (
        !stored
      ) {

        continue;
      }


      try {

        const parsed =
          JSON.parse(
            stored
          );


        const id =
          this.extractNumericUserId(
            parsed
          );


        if (
          id !== null
        ) {

          return id;
        }

      } catch {
        /*
         * Ignore invalid JSON.
         */
      }
    }


    /*
     * Direct localStorage values.
     */
    const directKeys = [
      'userId',
      'currentUserId',
      'employeeId'
    ];


    for (
      const key
      of directKeys
    ) {

      const value =
        localStorage.getItem(
          key
        );


      if (
        value !== null
      ) {

        const id =
          Number(
            value
          );


        if (
          Number.isFinite(id)
          &&
          id > 0
        ) {

          return id;
        }
      }
    }


    /*
     * JWT token.
     */
    const tokenKeys = [
      'token',
      'accessToken',
      'authToken',
      'jwt'
    ];


    for (
      const key
      of tokenKeys
    ) {

      const token =
        localStorage.getItem(
          key
        );


      if (
        !token
      ) {

        continue;
      }


      const id =
        this.getUserIdFromJwt(
          token
        );


      if (
        id !== null
      ) {

        return id;
      }
    }


    return null;
  }


  // ============================================================
  // EXTRACT ID FROM USER OBJECT
  // ============================================================

  private extractNumericUserId(
    user: any
  ):
    number | null {

    if (
      !user
    ) {

      return null;
    }


    const possibleIds = [
      user.id,
      user.userId,
      user.employeeId
    ];


    for (
      const value
      of possibleIds
    ) {

      const numberValue =
        Number(
          value
        );


      if (
        Number.isFinite(
          numberValue
        )
        &&
        numberValue > 0
      ) {

        return numberValue;
      }
    }


    return null;
  }


  // ============================================================
  // EXTRACT ID FROM JWT
  // ============================================================

  private getUserIdFromJwt(
    token: string
  ):
    number | null {

    try {

      const parts =
        token.split('.');


      if (
        parts.length !== 3
      ) {

        return null;
      }


      const payload =
        parts[1]
          .replace(/-/g, '+')
          .replace(/_/g, '/');


      const decoded =
        JSON.parse(
          atob(
            payload
          )
        );


      return this.extractNumericUserId(
        decoded
      );

    } catch (
      error
    ) {

      console.warn(
        'Unable to decode authentication token.',
        error
      );

      return null;
    }
  }

}