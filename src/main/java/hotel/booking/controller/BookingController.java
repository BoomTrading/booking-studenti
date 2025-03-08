package hotel.booking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hotel.booking.repository.BookingRepository;
import hotel.booking.repository.GuestRepository;
import hotel.booking.repository.RoomRepository;
import hotel.booking.model.Booking;
import hotel.booking.model.BookingDetails;
import hotel.booking.model.Guest;
import hotel.booking.model.Room;;

@Controller
public class BookingController {
    
	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	private RoomRepository roomRepository;

    @GetMapping("/")
	public String viewHomePage(@RequestParam(name="sdate", required=false, defaultValue="") String sdate,  Model model) {
		// List<Booking> bookings = bookingRepository.findAll();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String strDate = LocalDateTime.now().minusDays(60).format(formatter);

		List<Map<String,BookingDetails>> bookings = bookingRepository.findAllWithGuestRoom(sdate.isEmpty()? "NOW() - INTERVAL 60 DAY" : sdate);
		model.addAttribute("bookings", bookings);
		model.addAttribute("sdate", sdate.isEmpty() ? strDate : sdate);
		return "index";	
	}

	@GetMapping("/test")
	@ResponseBody
	public List<Map<String,BookingDetails>> test(@RequestParam(name="sdate", required=false, defaultValue="NOW() - INTERVAL 60 DAY") String sdate){

		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// String strDate = LocalDateTime.now().minusDays(2).format(formatter);

		return bookingRepository.findAllWithGuestRoom(sdate);
		
	}

	@GetMapping("/login")
	public String viewLogin() {
		
		return "login";	
	}

	@GetMapping("/bookings/new")
	public String showNewForm(Model model) {
		model.addAttribute("booking", new Booking());
		return "newBooking";
	}

	@PostMapping("/bookings/ins")
	public String save(@ModelAttribute("booking") Booking booking) {

		bookingRepository.save(booking);
		return "redirect:/";
	}

	@GetMapping("/bookings/edit/{id}")
	public String showEditForm(@PathVariable("id") int id, Model model) {
		if(bookingRepository.existsById(id)) {
			Booking booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
			int guestId = booking.getGuest().getId();
			Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new IllegalArgumentException("Invalid guest Id:" + id));
			int roomId = booking.getRoom().getId();
			Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + id));
			model.addAttribute("booking", booking);
			model.addAttribute( "guestName", guest.getLastName() +" "+  guest.getFirstName());
			model.addAttribute("roomNumber", room.getRoomNumber() +" - "+ room.getType());
			return "editBooking";
		}
		else return "redirect:/";
		
	}

	@PostMapping("/bookings/upd")
	public String update(@ModelAttribute("booking") Booking booking) {
		bookingRepository.save(booking);
		return "redirect:/";
	}

	@GetMapping("/bookings/delete/{id}")
	public String delete(@PathVariable("id") int id) {
		if(bookingRepository.existsById(id)) 
			bookingRepository.deleteById(id);;
		return "redirect:/";
	}

	@GetMapping("/bookings/calculate")
	@ResponseBody
	public ResponseEntity<?> calculateBookingPrice(
	        @RequestParam int roomId,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
	        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut) {
	    
	    System.out.println("Calculate request - roomId: " + roomId + ", checkIn: " + checkIn + ", checkOut: " + checkOut);
	    
	    try {
	        Room room = roomRepository.findById(roomId)
	            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

	        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
	        if (nights <= 0) {
	            throw new IllegalArgumentException("Check-out must be after check-in");
	        }

	        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(nights));
	        
	        Map<String, Object> response = new HashMap<>();
	        response.put("totalAmount", totalPrice);
	        response.put("nights", nights);
	        response.put("pricePerNight", room.getPrice());
	        
	        System.out.println("Calculation successful - total: " + totalPrice + ", nights: " + nights);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        System.err.println("Calculation error: " + e.getMessage());
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}


}