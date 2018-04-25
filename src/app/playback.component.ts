import {Component} from '@angular/core';
// import * as decompress from 'decompress';
import decompresszip = require('decompress-zip');
/*import * as decompresszip from '../../node_modules/decompress-zip/lib/decompress-zip'; */
import pako = require('pako');
import JSZip = require('jszip');
import JSZipUtils = require('jszip-utils');


@Component({
  selector: 'app-playback',
  templateUrl: './playback.component.html',
  styleUrls: ['./playback.component.css']
})

export class PlaybackComponent {
  doPlayback() {
    /*const the_unzipper = decompresszip('https://storage.googleapis.com/audiowavelet.appspot.com/the_file.zip');
    the_unzipper.on('extract', function(log) {
      console.log('Finished Extracting');
    }); */


    /*try {
      const unzip = pako.inflate('https://storage.googleapis.com/audiowavelet.appspot.com/the_file.zip');
    } catch (err) {
      console.log(err);
    } */

    const filePath = 'https://storage.googleapis.com/audiowavelet.appspot.com/the_file.zip';

    const the_zip = new JSZip();

    JSZipUtils.getBinaryContent('https://storage.googleapis.com/audiowavelet.appspot.com/the_file.zip', function(err, data) {
      if (err) {
        throw err; // or handle err
      }

      JSZip.loadAsync(data).then(function (zip) {
        Object.keys(zip.files).forEach(function (filename) {
          zip.files[filename].async('string').then(function (fileData) {
            console.log(fileData);
          });
        });
        console.log('Success');
      });
    });
  }
}
