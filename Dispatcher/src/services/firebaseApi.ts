import { onValue, ref } from 'firebase/database';
import {
  collection,
  doc,
  onSnapshot,
  setDoc,
  Timestamp,
  serverTimestamp,
} from 'firebase/firestore';
import { db, realtimeDb } from '../firebase';
import type { Ambulance, Driver, EmergencyCase, Hospital, Hub } from '../types';

const text = (value: unknown, fallback = '') => (typeof value === 'string' ? value : fallback);
const num = (value: unknown, fallback = 0) => (typeof value === 'number' ? value : fallback);

const dateLabel = (value: unknown) => {
  if (value instanceof Timestamp) return value.toDate().toLocaleString();
  if (value instanceof Date) return value.toLocaleString();
  if (typeof value === 'string') return value;
  return '';
};

export function subscribeDispatcherData(callback: (data: {
  emergencies: EmergencyCase[];
  ambulances: Ambulance[];
  hospitals: Hospital[];
  drivers: Driver[];
  hubs: Hub[];
}) => void, onError: (error: Error) => void) {
  const state = {
    emergencies: [] as EmergencyCase[],
    ambulances: [] as Ambulance[],
    hospitals: [] as Hospital[],
    drivers: [] as Driver[],
    hubs: [] as Hub[],
  };

  const emit = () => callback({ ...state, hubs: [...state.hubs] });
  const unsubscribers: Array<() => void> = [];

  unsubscribers.push(onSnapshot(collection(db, 'emergencyRequests'), (snapshot) => {
    state.emergencies = snapshot.docs.map((doc) => {
      const d = doc.data();
      return {
        id: doc.id,
        patientId: text(d.patientId),
        patientName: text(d.patientName, 'Patient'),
        age: num(d.age),
        gender: text(d.gender, 'Other') as EmergencyCase['gender'],
        contact: text(d.contact),
        emergencyContact: text(d.emergencyContact),
        condition: text(d.condition),
        emergencyType: text(d.emergencyType, 'Other Emergency') as EmergencyCase['emergencyType'],
        category: text(d.category, 'General Emergency') as EmergencyCase['category'],
        severity: text(d.priority, text(d.severity, 'Moderate')) as EmergencyCase['severity'],
        pickupLocation: text(d.pickupLocation, text(d.address, 'Current location')),
        medicalNotes: text(d.medicalNotes),
        status: text(d.status, 'New') as EmergencyCase['status'],
        requestTime: dateLabel(d.createdAt),
        ambulanceId: text(d.ambulanceId) || undefined,
        hospitalId: text(d.hospitalId) || undefined,
        etaMinutes: num(d.etaMinutes),
        driverName: text(d.driverName) || undefined,
        hospitalName: text(d.hospitalName) || undefined,
        responseMinutes: num(d.responseMinutes),
      };
    });
    emit();
  }, onError));

  unsubscribers.push(onSnapshot(collection(db, 'ambulances'), (snapshot) => {
    state.ambulances = snapshot.docs.map((doc) => {
      const d = doc.data();
      return {
        id: doc.id,
        registration: text(d.vehicleNumber, text(d.registration)),
        type: text(d.type, 'BLS') as Ambulance['type'],
        organization: text(d.organization),
        hospitalAffiliation: text(d.hospitalAffiliation) || undefined,
        driverId: text(d.driverId),
        hubId: text(d.hubId) || undefined,
        status: text(d.status, 'OFFLINE') as Ambulance['status'],
        currentLocation: text(d.currentLocation),
        destination: text(d.destination) || undefined,
        etaMinutes: num(d.etaMinutes),
        equipment: Array.isArray(d.equipment) ? d.equipment.map(String) : [],
        oxygen: Boolean(d.oxygen),
        ventilator: Boolean(d.ventilator),
        bls: Boolean(d.bls),
        als: Boolean(d.als),
        latitude: num(d.latitude, num(d.location?.latitude)),
        longitude: num(d.longitude, num(d.location?.longitude)),
        x: 50,
        y: 50,
      };
    });
    emit();
  }, onError));

  unsubscribers.push(onSnapshot(collection(db, 'hospitals'), (snapshot) => {
    state.hospitals = snapshot.docs.map((doc) => {
      const d = doc.data();
      const availability = d.emergencyAvailable === false ? 'NOT ACCEPTING' : num(d.availableBeds) <= 5 ? 'LIMITED' : 'ACCEPTING';
      return {
        id: doc.id,
        name: text(d.name, 'Hospital'),
        type: text(d.type, 'Private') as Hospital['type'],
        address: text(d.address),
        contact: text(d.contact, text(d.phone)),
        emergencyDepartment: Boolean(d.emergencyDepartment ?? d.emergencyAvailable),
        specializations: Array.isArray(d.specializations) ? d.specializations.map(String) : [],
        icu: Boolean(d.icu),
        traumaCenter: Boolean(d.traumaCapability ?? d.traumaCenter),
        cardiology: Boolean(d.cardiacCapability ?? d.cardiology),
        neurology: Boolean(d.neurology),
        orthopedics: Boolean(d.orthopedics),
        maternity: Boolean(d.maternity),
        pediatrics: Boolean(d.pediatrics),
        burnTreatment: Boolean(d.burnTreatment),
        bloodBank: Boolean(d.bloodBank),
        availability,
        distanceKm: num(d.distanceKm),
        etaMinutes: num(d.etaMinutes),
        status: text(d.emergencyStatus, availability),
        availableBeds: num(d.availableBeds),
        emergencyAvailable: Boolean(d.emergencyAvailable),
        latitude: num(d.latitude),
        longitude: num(d.longitude),
        x: 50,
        y: 50,
        rating: num(d.rating),
      };
    });
    emit();
  }, onError));

  unsubscribers.push(onSnapshot(collection(db, 'hubs'), (snapshot) => {
    state.hubs = snapshot.docs.map((doc) => {
      const d = doc.data();
      return {
        id: doc.id,
        name: text(d.name, doc.id),
        address: text(d.address),
        latitude: num(d.latitude),
        longitude: num(d.longitude),
      };
    });
    emit();
  }, onError));

  unsubscribers.push(onSnapshot(collection(db, 'users'), (snapshot) => {
    state.drivers = [];
    snapshot.docs.forEach((doc) => {
      const d = doc.data();
      if (d.role === 'DRIVER') {
        state.drivers.push({
          id: doc.id,
          name: text(d.name, 'Driver'),
          phone: text(d.phone),
          emergencyContact: text(d.emergencyContact),
          licenseNumber: text(d.licenseNumber),
          verificationStatus: 'Verified',
          assignedAmbulanceId: text(d.ambulanceId) || undefined,
          status: text(d.status, 'Offline') as Driver['status'],
          currentLocation: text(d.currentLocation),
          completedTrips: num(d.completedTrips),
          currentDestination: text(d.currentDestination) || undefined,
        });
      }
    });
    emit();
  }, onError));

  return () => unsubscribers.forEach((unsubscribe) => unsubscribe());
}

export function subscribeAmbulanceLocations(callback: (locations: Record<string, { latitude: number; longitude: number; speed?: number; heading?: number; accuracy?: number; timestamp?: number }>) => void) {
  return onValue(ref(realtimeDb, 'ambulanceLocations'), (snapshot) => {
    callback(snapshot.val() ?? {});
  });
}


const prototypeHospitals = [
  {
    id: 'H001',
    name: 'RAAH Prototype General Hospital',
    type: 'Prototype',
    address: 'Central Pune, Maharashtra',
    contact: 'Prototype record',
    emergencyDepartment: true,
    emergencyAvailable: true,
    emergencyStatus: 'ACCEPTING',
    availableBeds: 24,
    totalBeds: 80,
    rating: 4.2,
    cardiacCapability: true,
    traumaCapability: true,
    icu: true,
    bloodBank: true,
    specializations: ['Emergency Medicine', 'Cardiac Care', 'Trauma'],
    latitude: 18.5204,
    longitude: 73.8567,
  },
  {
    id: 'H002',
    name: 'RAAH Prototype Trauma Centre',
    type: 'Prototype',
    address: 'East Pune, Maharashtra',
    contact: 'Prototype record',
    emergencyDepartment: true,
    emergencyAvailable: true,
    emergencyStatus: 'ACCEPTING',
    availableBeds: 12,
    totalBeds: 60,
    rating: 4.0,
    cardiacCapability: false,
    traumaCapability: true,
    icu: true,
    bloodBank: true,
    specializations: ['Emergency Medicine', 'Trauma', 'Orthopedics'],
    latitude: 18.5362,
    longitude: 73.8958,
  },
  {
    id: 'H003',
    name: 'RAAH Prototype Cardiac Centre',
    type: 'Prototype',
    address: 'South Pune, Maharashtra',
    contact: 'Prototype record',
    emergencyDepartment: true,
    emergencyAvailable: true,
    emergencyStatus: 'LIMITED',
    availableBeds: 4,
    totalBeds: 40,
    rating: 4.4,
    cardiacCapability: true,
    traumaCapability: false,
    icu: true,
    bloodBank: false,
    specializations: ['Cardiology', 'Emergency Medicine', 'ICU'],
    latitude: 18.5018,
    longitude: 73.8636,
  },
  {
    id: 'H004',
    name: 'RAAH Prototype Hospital - Offline',
    type: 'Prototype',
    address: 'West Pune, Maharashtra',
    contact: 'Prototype record',
    emergencyDepartment: true,
    emergencyAvailable: false,
    emergencyStatus: 'NOT_ACCEPTING',
    availableBeds: 0,
    totalBeds: 50,
    rating: 3.8,
    cardiacCapability: true,
    traumaCapability: true,
    icu: true,
    bloodBank: false,
    specializations: ['Emergency Medicine'],
    latitude: 18.5167,
    longitude: 73.8067,
  },
];

export async function seedPrototypeHospitals() {
  await Promise.all(prototypeHospitals.map((hospital) => setDoc(doc(db, 'hospitals', hospital.id), {
    ...hospital,
    updatedAt: serverTimestamp(),
  }, { merge: true })));
}
