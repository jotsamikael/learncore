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
  Plugin,
  SpecialCharacters,
  SpecialCharactersEssentials,
  Subscript,
  Superscript,
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

/** Extra superscript / subscript digits in the Mathematical special-characters group. */
class QuestionMathCharacterExtras extends Plugin {
  static get pluginName() {
    return 'QuestionMathCharacterExtras';
  }

  static get requires() {
    return [SpecialCharacters];
  }

  init() {
    const specialCharacters = this.editor.plugins.get('SpecialCharacters');
    specialCharacters.addItems('Mathematical', [
      { character: '⁰', title: 'Superscript zero' },
      { character: '¹', title: 'Superscript one' },
      { character: '²', title: 'Superscript two' },
      { character: '³', title: 'Superscript three' },
      { character: '⁴', title: 'Superscript four' },
      { character: '⁵', title: 'Superscript five' },
      { character: '⁶', title: 'Superscript six' },
      { character: '⁷', title: 'Superscript seven' },
      { character: '⁸', title: 'Superscript eight' },
      { character: '⁹', title: 'Superscript nine' },
      { character: '₀', title: 'Subscript zero' },
      { character: '₁', title: 'Subscript one' },
      { character: '₂', title: 'Subscript two' },
      { character: '₃', title: 'Subscript three' },
      { character: '₄', title: 'Subscript four' },
      { character: '₅', title: 'Subscript five' },
      { character: '₆', title: 'Subscript six' },
      { character: '₇', title: 'Subscript seven' },
      { character: '₈', title: 'Subscript eight' },
      { character: '₉', title: 'Subscript nine' },
    ]);
  }
}

const sharedStyleToolbar = [
  'bold',
  'italic',
  'superscript',
  'subscript',
  'link',
] as const;

const sharedSymbolToolbar = ['specialCharacters', 'math'] as const;

const sharedPlugins: PluginConstructor[] = [
  Essentials,
  Autoformat,
  Bold,
  Italic,
  Superscript,
  Subscript,
  Link,
  Paragraph,
  SpecialCharacters,
  SpecialCharactersEssentials,
  QuestionMathCharacterExtras,
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
    ...sharedStyleToolbar,
    'bulletedList',
    'numberedList',
    '|',
    'insertTable',
    ...sharedSymbolToolbar,
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
    ...sharedStyleToolbar,
    '|',
    'insertTable',
    ...sharedSymbolToolbar,
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
    ...sharedStyleToolbar,
    'bulletedList',
    'numberedList',
    '|',
    'insertTable',
    ...sharedSymbolToolbar,
    '|',
    'undo',
    'redo',
  ],
  table: {
    contentToolbar: [...tableToolbarItems],
  },
  math: mathConfig,
};
