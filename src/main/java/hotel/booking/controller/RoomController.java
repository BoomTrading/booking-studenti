package hotel.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import hotel.booking.model.Room;
import hotel.booking.repository.RoomRepository;

import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/all")
    public String listRooms(Model model) {
        List<Room> rooms = roomRepository.findAll();
        model.addAttribute("rooms", rooms);
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
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        }
        return "redirect:/rooms/all";
    }
}
