# RAAH Connect Dispatcher – UI Update Notes

## Applied
- Removed Patients from dispatcher navigation and routes.
- Removed Reports from dispatcher navigation and routes.
- Combined ambulance + driver information into **Fleet & Crew**.
- Added **Hubs** section backed by Firestore collection `hubs`.
- Simplified the dispatcher navigation by removing duplicate Live Operations and Notifications tabs; the dashboard remains the main live operations view.
- Simplified the dashboard layout and removed the separate analytics chart/system-state blocks.
- Added a compact dispatcher workflow strip: Receive → Prioritize → Assign → Monitor → Complete.
- Added a control-room snapshot for critical cases, open hospitals, and configured hubs.
- Applied a restrained red/white visual theme with neutral gray surfaces.
- Added fleet search and hospital search.
- Fleet view joins ambulance records with driver records using ambulance `driverId` and driver `assignedAmbulanceId`.
- Ambulance records support an optional `hubId` field for hub association.

## Hub data model assumed by the UI
Firestore collection: `hubs`

Suggested fields:
- `name` (string)
- `address` (string)
- `status` (string, e.g. OPEN / CLOSED)
- `contact` or `phone` (string)
- `latitude` (number, optional)
- `longitude` (number, optional)

Ambulance documents may include:
- `hubId` (string, optional)

The UI is read-only for hubs in this update; it does not create or edit hub documents.

## Intentionally not added yet
- Dispatcher-side assignment/reassignment write actions.
- Automatic hospital selection writes.
- Map routing calculations.
- Hub create/edit forms.

These require confirmation of the exact Firebase fields and write permissions before adding them.

## Validation
A TypeScript/TSX syntax transpilation check passed for the source files. Full `npm run build` could not be completed in the working environment because the uploaded project did not contain installed `node_modules` and package installation could not complete from the available package cache.
