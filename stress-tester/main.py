import sys
import time

import requests
import os
import random
import logging


logging.basicConfig(stream=sys.stdout, filemode='w', level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger('LOGGER_NAME')


def get_available_shows(server_host):
    url = server_host + "/api/shows"
    response = requests.get(url=url)
    response_data = response.json()
    logger.info("Received %d shows", len(response_data))
    return response_data


def get_all_seats(server_host, show):
    url = server_host + "/api/tickets"
    params = {'date': show["date"], 'theater': show["theater"]}
    response = requests.get(url=url, params=params)
    response_data = response.json()
    logger.info("Received %d seats", len(response_data))
    return response_data


def buy_tickets(server_host, seats):
    url = server_host + "/api/tickets/bulk"
    seats_keys = list(map(seat_to_seat_requests, seats))
    response = requests.put(url=url, json=seats_keys)
    response_data = response.json()
    logger.info("Bought %d tickets", len(response_data))
    return response_data


def return_tickets(server_host, seats_keys):
    url = server_host + "/api/tickets/bulk/return"
    response = requests.put(url=url, json=list(seats_keys))
    response_data = response.json()
    logger.info("Returned %d tickets", len(response_data))
    return response_data


def seat_to_seat_requests(seat):
    return {'date': seat["date"],
            'theater': seat["theater"],
            'row': seat["row"],
            'number': seat["number"],
            'discount': "SPECIAL"}


def select_random_free_seats(all_seats, max_bulk_size):
    free_seats = list(filter(lambda seat: not seat["taken"], all_seats))
    to_select = random.randint(3, min(len(free_seats), max_bulk_size))
    return random.sample(population=free_seats, k=to_select)


def select_random_owned_seats(owned_seats):
    to_select = random.randint(1, len(owned_seats))
    return random.sample(population=owned_seats, k=to_select)


def main():
    SERVER_HOST = os.getenv("SRDS_SERVER_HOST")
    MAX_SEATS_BULK = int(os.getenv("SRDS_MAX_SEATS_BULK"))

    owned_tickets = []
    shows = get_available_shows(SERVER_HOST)
    while True:
        try:
            if random.randint(0, 10) <= 4:
                shows = get_available_shows(SERVER_HOST)

            all_seats = get_all_seats(SERVER_HOST, random.choice(shows))
            free_seats = select_random_free_seats(all_seats, MAX_SEATS_BULK)
            if len(free_seats) > 0:
                owned_tickets += buy_tickets(SERVER_HOST, free_seats)
            else:
                logger.info("No free seats available!")

            if random.randint(0, 10) <= 4:
                seats_to_return = select_random_owned_seats(owned_tickets)
                owned_tickets = [item for item in owned_tickets if item not in seats_to_return]
                return_tickets(SERVER_HOST, seats_to_return)
            time.sleep(random.randint(0, 10) / 100)

        except requests.exceptions.HTTPError as errh:
            logger.error("HTTP Error: %s", str(errh))
        except requests.exceptions.ConnectionError as errc:
            logger.error("Error Connecting: %s", str(errc))
        except requests.exceptions.Timeout as errt:
            logger.error("Timeout Error: %s", str(errt))
        except requests.exceptions.RequestException as err:
            logger.error("Unexpected Error: %s", str(err))


if __name__ == "__main__":
    main()
