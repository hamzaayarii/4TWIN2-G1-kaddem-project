import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';
import { Option } from '../../models/option.model';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class StudentFormComponent implements OnInit {
  student: Student = {
    prenomE: '',
    nomE: '',
    op: undefined
  };
  isEditing = false;
  options = Object.values(Option);

  constructor(
    private studentService: StudentService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEditing = true;
      this.loadStudent(id);
    }
  }

  loadStudent(id: number) {
    this.studentService.getStudent(id).subscribe({
      next: (student) => this.student = student,
      error: (error: Error) => console.error('Error loading student:', error)
    });
  }

  onSubmit() {
    if (this.isEditing) {
      this.studentService.updateStudent(this.student).subscribe({
        next: () => this.router.navigate(['/students']),
        error: (error: Error) => console.error('Error updating student:', error)
      });
    } else {
      this.studentService.createStudent(this.student).subscribe({
        next: () => this.router.navigate(['/students']),
        error: (error: Error) => console.error('Error creating student:', error)
      });
    }
  }

  goBack() {
    this.router.navigate(['/students']);
  }
}
