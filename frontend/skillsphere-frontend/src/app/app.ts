import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  ngOnInit(): void {
    const theme = localStorage.getItem('theme') || 'Dark';
    const light = theme === 'Light';
    document.documentElement.classList.toggle('light-theme', light);
    document.body.classList.toggle('light-theme', light);
  }
}
