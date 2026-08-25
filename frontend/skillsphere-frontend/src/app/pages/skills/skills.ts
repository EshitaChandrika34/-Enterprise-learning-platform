import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import {
  SkillService,
  Skill
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

  skills: Skill[] = [];

  searchText: string = '';

  loading: boolean = false;

  totalEmployees: number = 0;

  advancedSkills: number = 0;

  averageLevel: number = 0;


  constructor(
    private skillService: SkillService,
    private cdr: ChangeDetectorRef,
    public authService: AuthService
  ) {}


  ngOnInit(): void {

    this.loadSkills();
  }


  // ==============================
  // LOAD SKILLS
  // ==============================

  loadSkills(): void {

    this.loading = true;

    this.skillService
      .getSkills()
      .subscribe({

        next: (data: Skill[]) => {

          console.log(
            'Skills received:',
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


        error: (error) => {

          console.error(
            'Skill loading error:',
            error
          );

          this.skills = [];

          this.calculateStatistics();

          this.loading = false;

          this.cdr.detectChanges();
        }

      });
  }


  // ==============================
  // FILTER
  // ==============================

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
          ${skill.category || ''}
          ${skill.description || ''}
        `.toLowerCase();

        return text.includes(search);
      }
    );
  }


  // ==============================
  // TOTAL SKILLS
  // ==============================

  get totalSkills(): number {

    return this.skills.length;
  }


  // ==============================
  // STATISTICS
  // ==============================

  calculateStatistics(): void {

    this.totalEmployees =
      this.skills.reduce(
        (total, skill) =>
          total + (skill.employees || 0),
        0
      );


    this.advancedSkills =
      this.skills.filter(
        skill =>
          (skill.level || 0) >= 4
      ).length;


    if (this.skills.length === 0) {

      this.averageLevel = 0;

      return;
    }


    const totalLevel =
      this.skills.reduce(
        (total, skill) =>
          total + (skill.level || 0),
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


  // ==============================
  // EMPLOYEE SELECT SKILL
  // ==============================

  selectSkill(skill: Skill): void {

    if (!this.authService.isEmployee()) {

      return;
    }


    const stored =
      localStorage.getItem(
        'selectedSkills'
      );


    let selected: Skill[] = [];


    if (stored) {

      try {

        selected =
          JSON.parse(stored);

      } catch {

        selected = [];
      }
    }


    const alreadySelected =
      selected.some(
        item =>
          item.skillId ===
          skill.skillId
      );


    if (alreadySelected) {

      alert(
        'This skill is already selected.'
      );

      return;
    }


    selected.push(skill);


    localStorage.setItem(
      'selectedSkills',
      JSON.stringify(selected)
    );


    alert(
      'Skill selected successfully.'
    );
  }


  // ==============================
  // CHECK SELECTED
  // ==============================

  isSkillSelected(
    skill: Skill
  ): boolean {

    const stored =
      localStorage.getItem(
        'selectedSkills'
      );


    if (!stored) {

      return false;
    }


    try {

      const selected: Skill[] =
        JSON.parse(stored);


      return selected.some(
        item =>
          item.skillId ===
          skill.skillId
      );

    } catch {

      return false;
    }
  }


  // ==============================
  // ADD SKILL
  // ADMIN / HR
  // ==============================

  addSkill(): void {

    if (
      !this.authService
        .canManageSkills()
    ) {

      return;
    }


    const skill: Skill = {

      skillName: 'Angular',

      category: 'TECHNICAL',

      description:
        'Frontend development',

      level: 1,

      employees: 0
    };


    this.skillService
      .addSkill(skill)
      .subscribe({

        next: () => {

          this.loadSkills();
        },

        error: error => {

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


  // ==============================
  // DELETE
  // ==============================

  deleteSkill(
    id: string
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
      .deleteSkill(id)
      .subscribe({

        next: () => {

          this.loadSkills();
        },

        error: error => {

          console.error(
            'Delete skill error:',
            error
          );
        }

      });
  }


  // ==============================
  // LEVEL TEXT
  // ==============================

  getLevelText(
    level: number
  ): string {

    if (level === 1) {
      return 'Beginner';
    }

    if (level === 2) {
      return 'Basic';
    }

    if (level === 3) {
      return 'Intermediate';
    }

    if (level === 4) {
      return 'Advanced';
    }

    if (level === 5) {
      return 'Expert';
    }

    return 'Not Rated';
  }


  // ==============================
  // INCREASE LEVEL
  // ==============================

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
      (skill.level || 0) < 5
    ) {

      skill.level =
        (skill.level || 0) + 1;
    }
  }


  // ==============================
  // DECREASE LEVEL
  // ==============================

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
      (skill.level || 0) > 1
    ) {

      skill.level =
        (skill.level || 0) - 1;
    }
  }


  // ==============================
  // EDIT
  // ==============================

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
}