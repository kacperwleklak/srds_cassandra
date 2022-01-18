import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Show} from "./show";
import {Seat} from "./seat";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  selectedShow: Show | undefined;
  shows: Show[] | undefined;
  seats: Seat[][] | undefined;
  selectedSeats: Seat[] = [];
  selectedSeatsPrice: number = 0.00;


  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.getShows();
  }

  getShows() {
    this.http
      .get<Show[]>("http://localhost:4200/api/shows")
      .subscribe(data => {
        this.shows = data;
      });
  }

  getSeats() {
    let getSeatsUrl: string = "http://localhost:4200/api/tickets?date="
      + this.selectedShow?.date
      + "&theater="
      + this.selectedShow?.theater;
    this.http
      .get<Seat[]>(getSeatsUrl)
      .subscribe(data => {
        this.setupSeats(data)
      });
  }

  setupSeats(seats: Seat[]) {
    let seatsSorted: Seat[] = seats.sort(this.seatsComparator)
    let twoDimensionSeats: Seat[][] = [];
    let currentRowNumber: number = seats[0].row;
    let currentRow: Seat[] = [];
    seatsSorted.forEach(seat => {
      if (currentRowNumber > seat.row) {
        twoDimensionSeats.push(currentRow);
        currentRowNumber = seat.row;
        currentRow = [];
      }
      currentRow.push(seat);
    });
    twoDimensionSeats.push(currentRow)
    this.seats = twoDimensionSeats;
    console.log(this.seats);
    this.flushSeats();
  }


  onSelectionChange() {
    this.getSeats();
  }

  selectSeat(seat: Seat) {
    if (seat.taken) {
      return;
    }
    if (seat.selected) {
      let index = this.selectedSeats.indexOf(seat);
      if (index > -1) {
        this.selectedSeats.splice(index, 1);
      }
      seat.selected = false;
    } else {
      seat.selected = true;
      this.selectedSeats.push(seat);
    }
    this.selectedSeatsPrice = this.getCurrentPrice();
  }

  getCurrentPrice() {
    let sum = 0.00;
    this.selectedSeats.forEach(seat => sum += seat.basePrice);
    return sum;
  }

  resetAll() {
    this.getShows()
    this.selectedShow = undefined;
    this.seats = undefined;
    this.flushSeats();
  }

  flushSeats() {
    this.selectedSeats = [];
    this.selectedSeatsPrice = 0.00;
  }

  sendBuyRequest() {
    this.selectedSeats.map(seat => {
      delete seat.selected;
    })
    this.http.put<any>('http://localhost:4200/api/tickets/bulk', this.selectedSeats)
      .subscribe(data => {
        alert("You successfully bought " + data.length + " seats!")
        this.resetAll();
      });
  }

  seatsComparator(a: Seat, b: Seat) {
    if (a.row < b.row) {
      return 1;
    }
    if (a.row > b.row) {
      return -1;
    }
    if (a.number < b.number) {
      return -1;
    }
    if (a.number > b.number) {
      return 1;
    }
    return 0;
  }

}
