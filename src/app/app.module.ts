import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FileUploadModule } from 'ng2-file-upload';

import {HttpClientModule} from '@angular/common/http';
import {HttpModule} from '@angular/http';

import { AppComponent } from './app.component';
import { FileUploadComponent} from './fileuploader';
import { PlaybackComponent } from './playback.component';

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent,
    PlaybackComponent
  ],
  imports: [
    BrowserModule,
    FileUploadModule,
    HttpClientModule,
    HttpModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
