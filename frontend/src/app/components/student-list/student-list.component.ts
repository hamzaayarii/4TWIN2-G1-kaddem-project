import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  loading = true;

  constructor(private studentService: StudentService) {}

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.loading = true;
    this.studentService.getAllStudents().subscribe({
      next: (students: Student[]) => {
        this.students = students;
        this.loading = false;
      },
      error: (error: Error) => {
        console.error('Error loading students:', error);
        this.loading = false;
      }
    });
  }

  editStudent(id: number) {
    window.location.href = `/students/edit/${id}`;
  }

  deleteStudent(id: number) {
    if (confirm('Are you sure you want to delete this student?')) {
      this.studentService.deleteStudent(id).subscribe({
        next: () => {
          this.loadStudents();
        },
        error: (error: Error) => {
          console.error('Error deleting student:', error);
        }
      });
    }
  }
}
