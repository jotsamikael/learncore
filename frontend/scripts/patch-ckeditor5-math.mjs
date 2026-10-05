import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const mathRoot = path.join(
  __dirname,
  '..',
  'node_modules',
  '@isaul32',
  'ckeditor5-math',
  'src'
);

const MATH_TOOLBAR_ICON =
  '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 15.44 10.78" height="40.74" width="58.35"><path d="M8.15 0c-.06 0-.1.02-.11.03a.12.12 0 0 0-.02.01 6.81 6.81 0 0 0-2.32 4.9v.9a6.82 6.82 0 0 0 2.32 4.9.12.12 0 0 0 .02 0c.02.02.06.04.11.04.07 0 .12-.03.16-.07a.22.22 0 0 0 0-.32.12.12 0 0 0-.02-.02A4.4 4.4 0 0 1 7 8.44a7.62 7.62 0 0 1-.5-2.6v-.9c0-.82.19-1.76.5-2.6A4.4 4.4 0 0 1 8.3.42.12.12 0 0 0 8.3.39a.22.22 0 0 0 .08-.16.22.22 0 0 0-.07-.16.22.22 0 0 0-.16-.07z"/></svg>';

const CKEDITOR5_SUBPATH =
  /import\s+\{([^}]+)\}\s+from\s+'ckeditor5\/src\/[^']+';?\n?/g;

function rewriteCkeditorImports(content) {
  const symbols = new Set();
  let match;

  while ((match = CKEDITOR5_SUBPATH.exec(content)) !== null) {
    for (const part of match[1].split(',')) {
      const name = part.trim();
      if (name) {
        symbols.add(name);
      }
    }
  }

  if (symbols.size === 0) {
    return content;
  }

  const mergedImport = `import { ${[...symbols].join(', ')} } from 'ckeditor5';\n`;
  return content.replace(CKEDITOR5_SUBPATH, '').replace(/^/, mergedImport);
}

function walk(dir, onFile) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      walk(full, onFile);
    } else if (entry.name.endsWith('.js')) {
      onFile(full);
    }
  }
}

if (!fs.existsSync(mathRoot)) {
  console.warn('patch-ckeditor5-math: package not installed, skipping.');
  process.exit(0);
}

let patchedFiles = 0;

walk(mathRoot, (filePath) => {
  let content = fs.readFileSync(filePath, 'utf8');
  const original = content;

  content = rewriteCkeditorImports(content);

  if (filePath.endsWith(`${path.sep}mathui.js`)) {
    content = content.replace(
      /import mathIcon from '\.\.\/theme\/icons\/math\.svg';\n|const mathIcon = .*;\n/,
      `const mathIcon = ${JSON.stringify(MATH_TOOLBAR_ICON)};\n`
    );
  }

  if (filePath.endsWith(`${path.sep}mainformview.js`)) {
    content = content.replace(
      /import '\.\.\/\.\.\/theme\/mathform\.css';\n/,
      ''
    );
  }

  if (filePath.endsWith(`${path.sep}autoformatmath.js`)) {
    content = content.replace(
      /import blockAutoformatEditing from '@ckeditor\/ckeditor5-autoformat\/src\/blockautoformatediting';\r?\n/,
      ''
    );
    content = content.replace(
      /import \{([^}]+)\} from 'ckeditor5';/,
      (match, symbols) => {
        if (symbols.includes('blockAutoformatEditing')) {
          return match;
        }
        return `import {${symbols.trim()}, blockAutoformatEditing } from 'ckeditor5';`;
      }
    );
  }

  if (content !== original) {
    fs.writeFileSync(filePath, content, 'utf8');
    patchedFiles += 1;
  }
});

console.log(`patch-ckeditor5-math: updated ${patchedFiles} file(s).`);
