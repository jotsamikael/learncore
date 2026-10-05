import {
  Autoformat,
  Bold,
  ClassicEditor,
  Essentials,
  Heading,
  Italic,
  Link,
  List,
  Paragraph,
  Table,
  TableCaption,
  TableCellProperties,
  TableColumnResize,
  TableProperties,
  TableToolbar,
  Undo,
  type EditorConfig,
  type PluginConstructor,
} from 'ckeditor5';

import '@isaul32/ckeditor5-math/src/augmentation';
import { AutoformatMath, Math } from '@isaul32/ckeditor5-math';

import katex from 'katex';
import type { Katex } from '@isaul32/ckeditor5-math/src/typings-external';

declare global {
  var katex: Katex | undefined;
}

if (typeof globalThis !== 'undefined' && !globalThis.katex) {
  globalThis.katex = katex;
}

export const QuestionEditor = ClassicEditor;

const tableToolbarItems = [
  'tableColumn',
  'tableRow',
  'mergeTableCells',
  'tableProperties',
  'tableCellProperties',
] as const;

const mathConfig = {
  engine: 'katex' as const,
  outputType: 'span' as const,
  enablePreview: true,
  katexRenderOptions: {
    throwOnError: false,
  },
};

const sharedPlugins: PluginConstructor[] = [
  Essentials,
  Autoformat,
  Bold,
  Italic,
  Link,
  Paragraph,
  Table,
  TableToolbar,
  TableProperties,
  TableCellProperties,
  TableColumnResize,
  Undo,
  Math,
  AutoformatMath,
];

export const questionRichTextEditorConfig: EditorConfig = {
  licenseKey: 'GPL',
  plugins: [
    ...sharedPlugins,
    Heading,
    List,
    TableCaption,
  ],
  toolbar: [
    'heading',
    '|',
    'bold',
    'italic',
    'link',
    'bulletedList',
    'numberedList',
    '|',
    'insertTable',
    'math',
    '|',
    'undo',
    'redo',
  ],
  table: {
    contentToolbar: [...tableToolbarItems],
  },
  math: mathConfig,
};

export const questionOptionEditorConfig: EditorConfig = {
  licenseKey: 'GPL',
  plugins: sharedPlugins,
  toolbar: [
    'bold',
    'italic',
    'link',
    '|',
    'insertTable',
    'math',
    '|',
    'undo',
    'redo',
  ],
  table: {
    contentToolbar: [...tableToolbarItems],
  },
  math: mathConfig,
};

export const questionExplanationEditorConfig: EditorConfig = {
  licenseKey: 'GPL',
  plugins: [
    ...sharedPlugins,
    List,
  ],
  toolbar: [
    'bold',
    'italic',
    'link',
    'bulletedList',
    'numberedList',
    '|',
    'insertTable',
    'math',
    '|',
    'undo',
    'redo',
  ],
  table: {
    contentToolbar: [...tableToolbarItems],
  },
  math: mathConfig,
};
