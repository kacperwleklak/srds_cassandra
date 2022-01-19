export interface Seat {
  date: string,
  theater: number,
  row: number,
  number: number,
  taken: boolean,
  basePrice: number,
  discount: string,
  movieTitle: string,

  selected: boolean | undefined;
}
