import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';


@Component({
  selector: 'app-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.css'],
})
export class ImageUploadComponent implements OnInit {

  @Output() valueChange = new EventEmitter();
  base64textString: string;
  imageUrl: string;
  isHiddenImage: boolean;
  @Input() defaultImage: string;
  @Input() imageWidth: string;
  @Input() imageHeight: string;
  @Input() onErrorImage: string;

  constructor() {
  }

  ngOnInit() {
    if (this.defaultImage) {
      this.imageUrl = 'https://drive.google.com/thumbnail?id=' + this.defaultImage;
    } else {
      this.imageUrl = this.onErrorImage;
    }
    this.isHiddenImage = false;
  }

  selectFile(event) {
    const file = event.target.files.item(0);
    if (file.type.match('image.*')) {
      this.convertToBinaryString(file);
      this.convertToDataURL(file);
    } else {
      alert('invalid format!');
    }
  }

  convertToBinaryString(file: File) {
    const readerForBinaryString = new FileReader();
    readerForBinaryString.onload = this._handleReaderLoaded.bind(this);
    readerForBinaryString.readAsBinaryString(file);
  }

  convertToDataURL(file: File) {
    this.isHiddenImage = true;
    const readerForDataUrl = new FileReader();
    readerForDataUrl.readAsDataURL(file);
    readerForDataUrl.onload = (event) => {
      this.imageUrl = readerForDataUrl.result.toString();
      this.isHiddenImage = false;
    };
  }

  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.valueChange.emit(this.base64textString);
  }
}
