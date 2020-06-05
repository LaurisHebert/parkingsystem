package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double duration = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant()).toMinutes() / 60.0;
		double result;
		switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				result = duration * Fare.CAR_RATE_PER_HOUR;
				break;
			}
			case BIKE: {
				result = duration * Fare.BIKE_RATE_PER_HOUR;
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
		}
		ticket.setPrice(result - (result * (ticket.getDiscount()/100.0 )));
	}
}
