export enum OrderDeliveryPoint {
  MAIN_ENTRANCE = 'MAIN_ENTRANCE',
  LIBRARY = 'LIBRARY',
  CAFETERIA = 'CAFETERIA',
  SPORTS_CENTER = 'SPORTS_CENTER',
  ADMINISTRATION = 'ADMINISTRATION',
  PARKING = 'PARKING'
}

export const OrderDeliveryPointLabels: Record<OrderDeliveryPoint, string> = {
  [OrderDeliveryPoint.MAIN_ENTRANCE]: 'Entrée principale',
  [OrderDeliveryPoint.LIBRARY]: 'Bibliothèque',
  [OrderDeliveryPoint.CAFETERIA]: 'Cafétéria',
  [OrderDeliveryPoint.SPORTS_CENTER]: 'Centre sportif',
  [OrderDeliveryPoint.ADMINISTRATION]: 'Administration',
  [OrderDeliveryPoint.PARKING]: 'Parking'
};
