const SLUG_PATTERN = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;
const MAX_SLUG_LENGTH = 80;

export function slugifyText(value: string): string {
  const slug = value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/-{2,}/g, '-')
    .replace(/^-+|-+$/g, '');

  if (!slug) {
    return '';
  }

  return slug.length > MAX_SLUG_LENGTH ? slug.slice(0, MAX_SLUG_LENGTH).replace(/-+$/g, '') : slug;
}

export function isValidSlug(value: string): boolean {
  return SLUG_PATTERN.test(value) && value.length <= MAX_SLUG_LENGTH;
}
