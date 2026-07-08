import { CountryOption } from '../models/country-option';

export function findCountryByName(
  countries: CountryOption[],
  countryName?: string
): CountryOption | null {
  if (!countryName?.trim()) {
    return null;
  }
  const target = countryName.trim().toLowerCase();
  return countries.find((country) => country.countryName.toLowerCase() === target) ?? null;
}

export function splitPhoneNumber(
  fullPhone: string | undefined,
  country: CountryOption | null
): string {
  if (!fullPhone) {
    return '';
  }
  const phoneDigits = fullPhone.replace(/\D/g, '');
  if (!country) {
    return phoneDigits;
  }
  const codeDigits = country.countryCode.replace(/\D/g, '');
  if (codeDigits && phoneDigits.startsWith(codeDigits)) {
    return phoneDigits.slice(codeDigits.length);
  }
  return phoneDigits;
}

export function buildFullPhoneNumber(
  country: CountryOption | null,
  phoneLocal: string | undefined
): string {
  const local = String(phoneLocal ?? '').replace(/\D/g, '');
  const code = country?.countryCode?.replace(/\s/g, '') ?? '';
  return local && code ? `${code}${local}` : '';
}
