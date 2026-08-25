import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Employee {
  id: number;
  name: string;
  email: string;
  department: string;
  role: string;
  status: string;
}

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './employees.html',
  styleUrl: './employees.css'
})
export class Employees implements OnInit {

  employees: Employee[] = [];

  searchText = '';

  showForm = false;

  isEditing = false;

  editingId: number | null = null;

  employee: Employee = {
    id: 0,
    name: '',
    email: '',
    department: '',
    role: '',
    status: 'Active'
  };


  ngOnInit(): void {
    this.loadEmployees();
  }


  // LOAD EMPLOYEES
  loadEmployees(): void {

    const data = localStorage.getItem('employees');

    if (data) {
      this.employees = JSON.parse(data);
    }

  }


  // SAVE EMPLOYEES
  saveEmployees(): void {

    localStorage.setItem(
      'employees',
      JSON.stringify(this.employees)
    );

  }


  // OPEN ADD FORM
  openAddForm(): void {

    this.showForm = true;

    this.isEditing = false;

    this.editingId = null;

    this.employee = {
      id: 0,
      name: '',
      email: '',
      department: '',
      role: '',
      status: 'Active'
    };

  }


  // SAVE EMPLOYEE
  saveEmployee(): void {

    if (
      !this.employee.name.trim() ||
      !this.employee.email.trim() ||
      !this.employee.department.trim() ||
      !this.employee.role.trim()
    ) {

      alert('Please fill all fields');

      return;

    }


    // EDIT
    if (
      this.isEditing &&
      this.editingId !== null
    ) {

      const index = this.employees.findIndex(
        employee =>
          employee.id === this.editingId
      );

      if (index !== -1) {

        this.employees[index] = {
          ...this.employee,
          id: this.editingId
        };

      }

    }

    // ADD
    else {

      const newEmployee: Employee = {

        id: Date.now(),

        name: this.employee.name,

        email: this.employee.email,

        department: this.employee.department,

        role: this.employee.role,

        status: this.employee.status

      };

      this.employees.push(newEmployee);

    }


    this.saveEmployees();

    this.closeForm();

  }


  // EDIT EMPLOYEE
  editEmployee(employee: Employee): void {

    this.employee = {
      ...employee
    };

    this.isEditing = true;

    this.editingId = employee.id;

    this.showForm = true;

  }


  // DELETE EMPLOYEE
  deleteEmployee(id: number): void {

    const result = confirm(
      'Are you sure you want to delete this employee?'
    );

    if (result) {

      this.employees =
        this.employees.filter(
          employee =>
            employee.id !== id
        );

      this.saveEmployees();

    }

  }


  // CLOSE FORM
  closeForm(): void {

    this.showForm = false;

    this.isEditing = false;

    this.editingId = null;

  }


  // SEARCH
  get filteredEmployees(): Employee[] {

    const search =
      this.searchText
        .toLowerCase()
        .trim();


    if (!search) {

      return this.employees;

    }


    return this.employees.filter(
      employee =>

        employee.name
          .toLowerCase()
          .includes(search)

        ||

        employee.email
          .toLowerCase()
          .includes(search)

        ||

        employee.department
          .toLowerCase()
          .includes(search)

        ||

        employee.role
          .toLowerCase()
          .includes(search)

    );

  }

}