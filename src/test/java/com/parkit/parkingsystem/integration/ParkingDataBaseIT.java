package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        Assertions.assertNotNull(ticket);

        ParkingSpot spot = ticket.getParkingSpot();
        Assertions.assertNotNull(spot);

        Date entryTimeNotNull = ticket.getInTime();

        Date exitTimeNull = ticket.getOutTime();

        boolean validityOfPlace = spot.isAvailable();

        int nextAvailableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        Assertions.assertEquals(1,spot.getId());
        Assertions.assertEquals(ParkingType.CAR, spot.getParkingType());
        Assertions.assertEquals("ABCDEF" , ticket.getVehicleRegNumber());
        Assertions.assertNotNull(entryTimeNotNull);
        Assertions.assertNull(exitTimeNull);
        Assertions.assertFalse(validityOfPlace);
        Assertions.assertEquals(2, nextAvailableSpot);
    }
    @Test
    public void testParkingLotExit() throws InterruptedException {
        testParkingACar();
        Thread.sleep(100);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        Assertions.assertNotNull(ticket);

        ParkingSpot spot = ticket.getParkingSpot();
        Assertions.assertNotNull(spot);

        Date entryTimeNotNull = ticket.getInTime();

        Date exitTimeNotNull = ticket.getOutTime();

        boolean validityOfPlace = spot.isAvailable();

        int nextAvailableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        Assertions.assertEquals(1,spot.getId());
        Assertions.assertEquals(ParkingType.CAR, spot.getParkingType());
        Assertions.assertEquals("ABCDEF" , ticket.getVehicleRegNumber());
        Assertions.assertNotNull(entryTimeNotNull);
        Assertions.assertNotNull(exitTimeNotNull);
        Assertions.assertTrue(validityOfPlace);
        Assertions.assertEquals(1, nextAvailableSpot);
    }
}
