import { formatDate } from '@angular/common';
import { Injectable, TemplateRef } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { NgbModal, NgbModalOptions, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SelectionModel } from '@angular/cdk/collections';
import Swal from 'sweetalert2';

export interface ListMeta {
  totalItems: number;
  totalPages: number;
  currentPage: number;
}

export interface PrintTableOptions {
  title: string;
  table: string[][];
  metaLine?: string;
  forPdfHint?: boolean;
}

export interface DetailEditToggleOptions {
  readOnly: boolean;
  snapshot: unknown | null;
  saving: boolean;
  canEdit: boolean;
  form: FormGroup;
  restoreForm: () => void;
  onSubmittedReset?: () => void;
  onErrorClear?: () => void;
}

@Injectable({ providedIn: 'root' })
export class CommonService {

  readonly defaultModalOptions: NgbModalOptions = {
    centered: true,
    size: 'lg',
    backdrop: 'static',
  };

  openModal(modalService: NgbModal, content: TemplateRef<unknown>, options?: NgbModalOptions): NgbModalRef {
    return modalService.open(content, { ...this.defaultModalOptions, ...options });
  }

  openCreateModal(
    modalService: NgbModal,
    content: TemplateRef<unknown>,
    form: FormGroup,
    resetValue: Record<string, unknown>,
    options?: NgbModalOptions
  ): NgbModalRef {
    form.reset(resetValue);
    return this.openModal(modalService, content, options);
  }

  toggleDetailEditMode(options: DetailEditToggleOptions): boolean | null {
    const { snapshot, saving, canEdit, form, readOnly, restoreForm } = options;
    if (!snapshot || saving || !canEdit) {
      return null;
    }
    if (readOnly) {
      form.enable({ emitEvent: false });
      return false;
    }
    options.onSubmittedReset?.();
    options.onErrorClear?.();
    restoreForm();
    return true;
  }

  patchDetailForm(form: FormGroup, values: Record<string, unknown>, readOnly: boolean): void {
    form.reset(values);
    if (readOnly) {
      form.disable({ emitEvent: false });
    } else {
      form.enable({ emitEvent: false });
    }
  }

  getFormArrayValue(form: FormGroup, controlName: string): string[] {
    return (form.get(controlName)?.value as string[] | null) ?? [];
  }

  getFormControl<T>(form: FormGroup, controlName: string): FormControl<T> {
    return form.get(controlName) as FormControl<T>;
  }

  removeFormArrayValue(form: FormGroup, controlName: string, value: string): void {
    const control = form.get(controlName);
    if (!control || control.disabled) {
      return;
    }
    const next = this.getFormArrayValue(form, controlName).filter((item) => item !== value);
    control.setValue(next);
    control.markAsDirty();
  }

  isAllSelected<T>(selection: SelectionModel<T>, rows: T[]): boolean {
    return rows.length > 0 && rows.every((row) => selection.isSelected(row));
  }

  masterToggle<T>(selection: SelectionModel<T>, rows: T[]): void {
    if (this.isAllSelected(selection, rows)) {
      selection.clear();
    } else {
      rows.forEach((row) => selection.select(row));
    }
  }

  shortUuid(uuid?: string): string {
    if (!uuid || uuid.length <= 14) {
      return uuid ?? '—';
    }
    return `${uuid.slice(0, 8)}…${uuid.slice(-6)}`;
  }

  coerceDate(value: unknown): Date | null {
    if (value == null || value === '') {
      return null;
    }
    if (value instanceof Date) {
      return Number.isNaN(value.getTime()) ? null : value;
    }
    const date = new Date(value as string | number);
    return Number.isNaN(date.getTime()) ? null : date;
  }

  formatMedium(value: unknown, empty = '—', locale = 'en-US'): string {
    const date = this.coerceDate(value);
    return date ? formatDate(date, 'medium', locale) : empty;
  }

  summarizeList(items?: string[], maxVisible = 2, empty = '—'): string {
    if (!items?.length) {
      return empty;
    }
    if (items.length <= maxVisible) {
      return items.join(', ');
    }
    return `${items.slice(0, maxVisible).join(', ')} +${items.length - maxVisible}`;
  }

  exportStamp(): string {
    return new Date().toISOString().slice(0, 10);
  }

  getExportMetaLine(meta: ListMeta | null): string {
    if (!meta) {
      return 'Current table data';
    }
    return `Page ${meta.currentPage} of ${meta.totalPages || 1} · ${meta.totalItems} total · Exported ${new Date().toLocaleString()}`;
  }

  escapeCsvCell(value: string): string {
    const text = String(value ?? '');
    if (/[",\r\n]/.test(text)) {
      return `"${text.replace(/"/g, '""')}"`;
    }
    return text;
  }

  downloadCsv(table: string[][], filename: string, useBom = false): void {
    const lines = table.map((row) => row.map((cell) => this.escapeCsvCell(cell)).join(','));
    const content = (useBom ? '\uFEFF' : '') + lines.join('\r\n');
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    anchor.click();
    URL.revokeObjectURL(url);
  }

  exportCsv(table: string[][], baseFilename: string): void {
    this.downloadCsv(table, `${baseFilename}-${this.exportStamp()}.csv`, false);
  }

  exportExcel(table: string[][], baseFilename: string): void {
    this.downloadCsv(table, `${baseFilename}-${this.exportStamp()}.csv`, true);
  }

  exportSelectedCsv(table: string[][], baseFilename: string): void {
    if (!table.length || table.length <= 1) {
      return;
    }
    this.downloadCsv(table, `${baseFilename}-selected-${this.exportStamp()}.csv`, false);
  }

  escapeHtml(text: string): string {
    return String(text ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  printHtmlTable(title: string, rows: [string, string][]): void {
    const body = rows
      .map(([key, value]) => `<tr><th>${this.escapeHtml(key)}</th><td>${this.escapeHtml(value)}</td></tr>`)
      .join('');
    const html = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${this.escapeHtml(title)}</title>
<style>
body{font-family:system-ui,sans-serif;padding:16px;font-size:13px}
table{border-collapse:collapse;width:100%;max-width:720px}
th,td{border:1px solid #ccc;padding:8px;text-align:left}
th{width:140px;background:#f5f5f5}
h1{font-size:18px;margin:0 0 12px}
</style></head><body>
<h1>${this.escapeHtml(title)}</h1>
<table><tbody>${body}</tbody></table>
</body></html>`;
    this.openPrintWindow(html);
  }

  printDataTable(options: PrintTableOptions): void {
    const { title, table, metaLine, forPdfHint = false } = options;
    const thead = `<tr>${table[0].map((header) => `<th>${this.escapeHtml(header)}</th>`).join('')}</tr>`;
    const tbody = table
      .slice(1)
      .map((row) => `<tr>${row.map((cell) => `<td>${this.escapeHtml(cell)}</td>`).join('')}</tr>`)
      .join('');
    const documentTitle = forPdfHint ? `${title} (save as PDF from the print dialog)` : title;
    const html = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${this.escapeHtml(documentTitle)}</title>
<style>
  body { font-family: system-ui, sans-serif; padding: 16px; }
  h1 { font-size: 18px; margin: 0 0 12px; }
  .meta { color: #666; font-size: 12px; margin-bottom: 12px; }
  table { border-collapse: collapse; width: 100%; font-size: 12px; }
  th, td { border: 1px solid #ccc; padding: 6px 8px; text-align: left; }
  th { background: #f5f5f5; }
</style></head><body>
<h1>${this.escapeHtml(title)}</h1>
<p class="meta">${this.escapeHtml(metaLine ?? 'Current table data')}</p>
<table><thead>${thead}</thead><tbody>${tbody}</tbody></table>
</body></html>`;
    this.openPrintWindow(html);
  }

  async confirmDelete(title: string, text: string): Promise<boolean> {
    const result = await Swal.fire({
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Delete',
    });
    return result.isConfirmed === true;
  }

  showErrorAlert(title: string, message: string): void {
    Swal.fire(title, message, 'error');
  }

  private openPrintWindow(html: string): void {
    const win = window.open('', '_blank');
    if (!win) {
      return;
    }
    win.document.write(html);
    win.document.close();
    win.focus();
    setTimeout(() => {
      win.print();
      setTimeout(() => win.close(), 400);
    }, 100);
  }
}
