export type Severity = 'Critical' | 'High' | 'Moderate' | 'Low';
export type EmergencyType = string;
export type HospitalType = 'Government' | 'Private' | string;
export type AmbulanceStatus = string;
export type EmergencyStatus = string;

export interface Hospital {
  id: string;
  name: string;
  type: HospitalType;
  address: string;
  contact: string;
  emergencyDepartment: boolean;
  specializations: string[];
  icu: boolean;
  traumaCenter: boolean;
  cardiology: boolean;
  neurology: boolean;
  orthopedics: boolean;
  maternity: boolean;
  pediatrics: boolean;
  burnTreatment: boolean;
  bloodBank: boolean;
  availability: string;
  distanceKm: number;
  etaMinutes: number;
  status: string;
  availableBeds: number;
  emergencyAvailable: boolean;
  latitude: number;
  longitude: number;
  x: number;
  y: number;
  rating: number;
}

export interface Driver {
  id: string;
  name: string;
  phone: string;
  emergencyContact: string;
  licenseNumber: string;
  verificationStatus: 'Verified' | 'Pending';
  assignedAmbulanceId?: string;
  status: string;
  currentLocation: string;
  completedTrips: number;
  currentDestination?: string;
}

export interface Ambulance {
  id: string;
  registration: string;
  type: string;
  organization: string;
  hospitalAffiliation?: string;
  driverId: string;
  hubId?: string;
  status: string;
  currentLocation: string;
  destination?: string;
  etaMinutes: number;
  equipment: string[];
  oxygen: boolean;
  ventilator: boolean;
  bls: boolean;
  als: boolean;
  latitude: number;
  longitude: number;
  x: number;
  y: number;
  progress?: number;
}

export interface EmergencyCase {
  id: string;
  patientId: string;
  patientName: string;
  age: number;
  gender: 'Male' | 'Female' | 'Other';
  contact: string;
  emergencyContact: string;
  condition: string;
  emergencyType: EmergencyType;
  category: string;
  severity: Severity;
  pickupLocation: string;
  medicalNotes: string;
  status: EmergencyStatus;
  requestTime: string;
  ambulanceId?: string;
  hospitalId?: string;
  etaMinutes: number;
  driverName?: string;
  hospitalName?: string;
  responseMinutes: number;
}

export interface NotificationItem {
  id: string;
  type: 'sos' | 'dispatch' | 'arrival' | 'capacity' | 'delay' | 'reassignment';
  title: string;
  detail: string;
  time: string;
  unread: boolean;
}

export interface AdminUser {
  id: string;
  name: string;
  role: string;
  email: string;
}

export interface Hub {
  id: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
}
