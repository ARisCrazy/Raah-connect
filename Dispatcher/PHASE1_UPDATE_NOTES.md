# RAAH Connect Dispatcher – Phase 1 Update

## Completed in this phase
- Removed the large Dispatcher Workflow strip from the dashboard.
- Replaced the decorative “Firebase live →” control with a compact Live indicator.
- Kept the existing Firebase live emergency/fleet/hospital/hub subscriptions.
- Redesigned the Hospitals page as an operational hospital-coordination view.
- Added hospital counts for Total, Accepting, Limited Capacity and Not Accepting.
- Added hospital search and status filtering.
- Added capacity, emergency status, capabilities, rating and location fields to the operational table.
- Added a “Create prototype hospitals” action that creates four clearly labelled prototype records in Firestore under `hospitals/H001`–`H004`.
- Prototype hospital records are explicitly demo/testing data and are not presented as real hospital capacity information.
- Kept Mappls integration out of this phase as requested. Distance/ETA remain blank until routing is integrated.
- Kept the existing Hubs collection model aligned with the current Firebase structure: `name`, `address`, `latitude`, `longitude`.
- Removed the previous Hubs UI dependence on unconfirmed `status`/`contact` fields.
- Removed the `.env.local` file from this distributable package so Firebase credentials are not bundled.

## Prototype hospital document structure
Each seeded hospital contains:
- name
- type
- address
- contact (prototype label)
- emergencyDepartment
- emergencyAvailable
- emergencyStatus
- availableBeds
- totalBeds
- rating
- cardiacCapability
- traumaCapability
- icu
- bloodBank
- specializations
- latitude
- longitude
- updatedAt

## Next phases
1. Dispatcher emergency assignment/driver acceptance integration.
2. Hospital web dashboard.
3. Driver app updates for assignment acceptance, BUSY state and live GPS publishing.
4. Patient app updates for hospital selection and live ambulance tracking.
5. Map/routing provider integration (Mappls remains deferred until credential setup is resolved).
6. Two-stage route display: ambulance → patient, then ambulance → hospital.
