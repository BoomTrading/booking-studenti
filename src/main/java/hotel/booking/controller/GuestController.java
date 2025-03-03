package hotel.booking.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import hotel.booking.model.Guest;
import hotel.booking.repository.GuestRepository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/guests")
public class GuestController {

	@Autowired
	private GuestRepository guestRepository;

	@GetMapping("/all")
	public String listGuests(Model model) {
		List<Guest> guests = guestRepository.findAll();
		model.addAttribute("guests", guests);
		return "guests";
	}

	@GetMapping("/new")
	public String showNewGuestForm(Model model) {
		model.addAttribute("guest", new Guest());
		return "newGuest";
	}

	@PostMapping("/ins")
	public String saveGuest(@ModelAttribute("guest") Guest guest) {
		guestRepository.save(guest);
		return "redirect:/guests/all";
	}

	@GetMapping("/edit/{id}")
	public String showEditGuestForm(@PathVariable("id") int id, Model model) {
		Guest guest = guestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid guest Id:" + id));
		model.addAttribute("guest", guest);
		return "editGuest";
	}

	@PostMapping("/upd")
	public String updateGuest(@ModelAttribute("guest") Guest guest) {
		guestRepository.save(guest);
		return "redirect:/guests/all";
	}

	@GetMapping("/delete/{id}")
	public String deleteGuest(@PathVariable("id") int id) {
		// Guest guest = guestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid guest Id:" + id));
		if(guestRepository.existsById(id)) 
			guestRepository.deleteById(id);;
		return "redirect:/guests/all";
	}

	
	// Add more CRUD methods for each entity as needed
}
