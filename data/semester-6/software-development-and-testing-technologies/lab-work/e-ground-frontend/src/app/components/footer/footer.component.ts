import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  navLinks = [{
    path: '/about',
    label: 'About',
    isActive: true
  }];

  constructor() {
  }

  ngOnInit() {
  }

}
