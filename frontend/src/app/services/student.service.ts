import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private apiUrl = `${environment.apiUrl}/etudiant`;

  constructor(private http: HttpClient) {}

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.apiUrl}/retrieve-all-etudiants`);
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/retrieve-etudiant/${id}`);
  }

  createStudent(student: Student): Observable<Student> {
    return this.http.post<Student>(`${this.apiUrl}/add-etudiant`, student);
  }

  updateStudent(student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/update-etudiant`, student);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/remove-etudiant/${id}`);
  }

  getAllStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.apiUrl}/retrieve-all-etudiants`);
  }

  getStudentsByDepartment(departmentId: number): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.apiUrl}/getEtudiantsByDepartement/${departmentId}`);
  }

  assignStudentToDepartment(studentId: number, departmentId: number): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/affecter-etudiant-departement/${studentId}/${departmentId}`, {});
  }
} 