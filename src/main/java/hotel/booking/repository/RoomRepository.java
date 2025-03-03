package hotel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.booking.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
}
