package com.sagar.carrentalsystem.controller.booking;


import com.sagar.carrentalsystem.model.dto.BookingRequestDTO;
import com.sagar.carrentalsystem.model.dto.BookingResponseDTO;
import com.sagar.carrentalsystem.service.booking.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO, Principal principal) {
        BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO, principal.getName());
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getPendingBookings() {
        List<BookingResponseDTO> bookings = bookingService.getPendingBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PutMapping("/admin/approve/{bookingId}/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> approveBooking(@PathVariable Long bookingId, @PathVariable Long carId) {
        BookingResponseDTO approvedBooking = bookingService.approveBooking(bookingId, carId);
        return new ResponseEntity<>(approvedBooking, HttpStatus.OK);
    }

    @PutMapping("/admin/reject/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> rejectBooking(@PathVariable Long bookingId) {
        BookingResponseDTO rejectedBooking = bookingService.rejectBooking(bookingId);
        return new ResponseEntity<>(rejectedBooking, HttpStatus.OK);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponseDTO>> getCustomerBookings(Principal principal) {
        List<BookingResponseDTO> bookings = bookingService.getCustomerBookings(principal.getName());
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PutMapping("/customer/cancel/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long bookingId, Principal principal) {
        BookingResponseDTO canceledBooking = bookingService.cancelBooking(bookingId, principal.getName());
        return new ResponseEntity<>(canceledBooking, HttpStatus.OK);
    }
}
