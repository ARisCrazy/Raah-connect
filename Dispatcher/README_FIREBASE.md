# RAAH Connect Dispatcher – Firebase setup

This version removes the hard-coded demo/mock data and connects the dispatcher web app to the shared RAAH Connect Firebase project.

## 1. Install dependencies

```bash
npm install
```

## 2. Register a Web app in the Firebase project

Firebase Console → RAAHconnect project → Project settings → Your apps → Add app → Web.

Copy the Web SDK configuration into `.env.local` using the variables in `.env.example`.

## 3. Enable Authentication

Authentication → Sign-in method → Email/Password.

Create a dispatcher account, then create the matching Firestore document:

`users/{dispatcherAuthUid}`

```text
name: Dispatcher
email: dispatcher@example.com
role: DISPATCHER
createdAt: server timestamp
```

`ADMIN` is also accepted by the current web app.

## 4. Firestore collections read by this app

- `users`
- `ambulances`
- `emergencyRequests`
- `hospitals`

The app does not create demo records. It renders whatever is currently in Firebase.

## 5. Realtime Database

The live tracker reads:

`ambulanceLocations/{ambulanceId}`

Expected location fields include:

```text
latitude
longitude
speed
heading
accuracy
timestamp
```

This matches the driver-side GPS structure used by RAAH Connect.

## 6. Run

```bash
npm run dev
```

Open the Vite URL shown in the terminal and sign in with the Firebase dispatcher account.

## Important

The dispatcher UI is now a Firebase-connected dashboard, but ambulance assignment actions, hospital selection actions, and route calculation are not silently invented here. They should be wired to the shared emergency/assignment workflow once those operations are finalized.
