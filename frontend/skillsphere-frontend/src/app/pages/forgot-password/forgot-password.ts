import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})

export class ForgotPassword {

  email: string = '';

  errorMessage: string = '';

  submitted: boolean = false;

  isLoading: boolean = false;

  onSubmit() {

    this.isLoading = true;

    setTimeout(() => {

      this.isLoading = false;

      this.submitted = true;

    },1500);

  }

}