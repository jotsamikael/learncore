import { Component, OnInit } from '@angular/core';

import {
  SimpleClassicEditor,
  simpleClassicEditorConfig,
} from '../../../core/editors/simple-classic.editor';

@Component({
    selector: 'app-editors',
    templateUrl: './editors.component.html',
    styleUrls: ['./editors.component.scss'],
    standalone: false
})

/**
 * Editors Component
 */
export class EditorsComponent implements OnInit {

  // bread crumb items
  breadCrumbItems!: Array<{}>;
  public Editor = SimpleClassicEditor;
  public editorConfig = simpleClassicEditorConfig;

  constructor() { }

  ngOnInit(): void {
    /**
    * BreadCrumb
    */
     this.breadCrumbItems = [
      { label: 'Forms' },
      { label: 'Editors', active: true }
    ];
  }

}
