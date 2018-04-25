import { Component} from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import {Http, Headers} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {catchError} from 'rxjs/operators';
import {TestClass} from './test_json_creater';

const URL = 'http://localhost:8080/AudioServlet';
// const URL = 'https://20180328t090703-dot-audiowavelet.appspot.com/AudioServlet';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})

export class FileUploadComponent {

  constructor(private http: HttpClient) {}
  // constructor(private http: Http) {}
  the_test_json = new TestClass('Bob', 'wav', 4);

  onChange(event: any) {
    const fileList: FileList = event.target.files;

    if (fileList.length > 0) {
      const file: File = fileList[0];
      const new_file_reader = new FileReader();
      const the_buffer = new_file_reader.readAsArrayBuffer(file);

      const formData = new FormData();
      formData.append('upload_file', file, file.name);

      /* const the_headers = new Headers({'Content-Type': 'audio/wav', 'Authorization':
      'Bearer ' + localStorage.getItem('access_token')}); */

      /*const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type':  'multipart/form-data'
        })
      }; */

      /* this.http.post(URL, JSON.stringify(this.the_test_json)).map(res => res.json())
        .catch(error => Observable.throw(error))
        .subscribe(
          data => console.log('success'),
          error => console.log(error)
        ); */

      this.http.post(URL, formData)
        .subscribe(
          res => {
            console.log(res);
          },
          err => {
            console.log(err);
            console.log('Error occured');
          }
        );
    }
  }
}
