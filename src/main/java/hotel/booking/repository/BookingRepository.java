package hotel.booking.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import hotel.booking.model.Booking;
import hotel.booking.model.BookingDetails;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @NativeQuery(value = "select b.*, g.lastName, g.firstName, r.roomNumber, r.`type` as roomType "
            + " from Bookings b, Guests g, Rooms r "
            + " where g.id = b.Guests_id"
            + " and r.id = b.Rooms_id"
            + " and b.check_in >= ?1 "
            + " order by check_in ")
    List<Map<String,BookingDetails>> findAllWithGuestRoom(String sdate);
}
