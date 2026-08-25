# SkillSphere Frontend - Modified Version

This version keeps the existing Angular project and backend integrations, while cleaning and improving the frontend.

## Main fixes
- Reworked the Dashboard into a compact dark enterprise UI with orange SkillSphere accents.
- Dashboard counts now come from the backend instead of hard-coded values.
- Added backend-driven recent employees, course snapshot, certification snapshot and skill count.
- Rebuilt Certifications as a searchable/filterable catalog connected to the Certification Service.
- Added Add Certification and Delete Certification actions.
- Corrected Employee Service URL to port 8081.
- Corrected Skill Service URL to port 8082.
- Learning remains on port 8084 and Certification on port 8083.
- Fixed Forgot Password routing/import problems.
- Added missing forgot-password and verify-otp routes.
- Replaced purple accents across active pages with a consistent orange/dark visual theme.
- Removed unused duplicate component/layout folders that could cause confusion.
- Added global scrollbar, font and form normalization styles.

## Backend URLs used
- User / Employee Service: http://localhost:8081/api/employees
- Skill Service: http://localhost:8082/skills
- Certification Service: http://localhost:8083/certifications
- Learning Service: http://localhost:8084/courses

## Run
1. Open this folder in VS Code.
2. Run `npm install`.
3. Run `ng serve` or `npm start`.
4. Start the required Spring Boot services before testing live data.

The project was checked with TypeScript and Angular template compilation after modification.

## Professional navigation update
- Rebuilt the left sidebar on all authenticated pages to use one identical structure.
- Removed emoji, decorative symbols, and inconsistent navigation icons.
- Standardized sidebar typography, spacing, hover state, and orange active-page indicator.
- Updated the sidebar branding to `SkillSphere` with a `LEARNING PLATFORM` subtitle.
- Normalized dark backgrounds, page headings, and primary action buttons across the main modules.
- Verified TypeScript and Angular template compilation with `tsc` and `ngc`.
