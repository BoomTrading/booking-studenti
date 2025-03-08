package hotel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

import hotel.booking.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

     @NativeQuery("SELECT * FROM Rooms WHERE roomNumber LIKE %?1% AND is_available=1")
    List<Room> findByPatternLike(String pattern);

}
