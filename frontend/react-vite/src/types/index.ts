import React from 'react';

// To make some changes
export interface UserData {
  username: string;
  email?: string;
  [key: string]: string | undefined;
}

export interface UserFormData {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  birthDate: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER'| '';
  address: {
    street: string;
    city: string;
    stateOrProvince: string;
    postalCode: string;
    country: string;
  };
  profilePictureUrl: string;
  professionalSummary: string;
}

export interface DrawerItem {
  id: string;
  text: string;
  icon: React.ReactNode;
  tooltip: string;
  onClick?: () => void;
}