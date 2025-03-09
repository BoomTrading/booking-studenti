package hotel.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;

import hotel.booking.model.Autocomplete;
import hotel.booking.model.Room;
import hotel.booking.repository.RoomRepository;
import hotel.booking.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/all")
    public String listRooms(Model model, @RequestParam(required = false) String sortType) {
        List<Room> rooms = roomRepository.findAll();
        if (sortType != null && !sortType.isEmpty()) {
            rooms = rooms.stream()
                .filter(room -> room.getType().toString().equals(sortType))
                .collect(Collectors.toList());
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", Room.RoomType.values());
        return "rooms";
    }

    @GetMapping("/new")
    public String showNewRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "newRoom";
    }

    @PostMapping("/ins")
    public String saveRoom(@ModelAttribute("room") Room room) {
        roomRepository.save(room);
        return "redirect:/rooms/all";
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") int id, Model model) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + id));
        model.addAttribute("room", room);
        return "editRoom";
    }

    @PostMapping("/upd")
    public String updateRoom(@ModelAttribute("room") Room room) {
        roomRepository.save(room);
        return "redirect:/rooms/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") int id) {
        try {
            if (roomRepository.existsById(id)) {
                roomRepository.deleteById(id);
            }
            return "redirect:/rooms/all";
        } catch (DataIntegrityViolationException e) {
            return "error/roomDeleteError";
        }
    }

    @PostMapping("/search")
    public String listRoomsByPatternLike(Model model, @RequestParam String pattern) {
        List<Room> rooms = roomRepository.findByPatternLike(pattern);
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<Autocomplete> autocomplete(@RequestParam String term) {
        List<Autocomplete> autoList = new ArrayList<Autocomplete>();
        List<Room> rooms = roomRepository.findByPatternLike(term);
        for (Room room : rooms) {
            Autocomplete item = new Autocomplete();
            item.setLabel(room.getRoomNumber() + " - " + room.getType());
            item.setValue(room.getId());
            autoList.add(item);
        }
        return autoList;
    }

    @GetMapping("/available")
    @ResponseBody
    public List<RoomDTO> getAvailableRooms(
            @RequestParam Room.RoomType roomType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "0") Integer currentBookingId) {
        
        List<Room> availableRooms = roomRepository.findAvailableRoomsByType(roomType);
        return availableRooms.stream()
            .filter(room -> !bookingRepository.existsOverlappingBooking(
                room.getId(), checkIn, checkOut, currentBookingId))
            .map(room -> new RoomDTO(room.getId(), room.getRoomNumber(), room.getPrice()))
            .collect(Collectors.toList());
    }
}

class RoomDTO {
    private int id;
    private String roomNumber;
    private BigDecimal price;

    public RoomDTO(int id, String roomNumber, BigDecimal price) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
    }

    // Getters
    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public BigDecimal getPrice() { return price; }
}