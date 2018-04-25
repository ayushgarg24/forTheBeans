import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import { FileUploadModule } from 'ng2-file-upload';
import {Http, HttpModule} from '@angular/http';

// URL to which to send File
const URL = 'http://localhost:8080/AudioServlet';
// const URL = 'https://20180316t130218-dot-audiowavelet.appspot.com/AudioServlet';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'app';

  /*public uploader: FileUploader = new FileUploader({url: URL});
  public hasBaseDropZoneOver = false;
  public hasAnotherDropZoneOver = false;

  public fileOverBase(e: any): void {
    this.hasBaseDropZoneOver = e;
  }

  public fileOverAnother(e: any): void {
    this.hasAnotherDropZoneOver = e;
    console.log(this.uploader.progress);
  } */
}
