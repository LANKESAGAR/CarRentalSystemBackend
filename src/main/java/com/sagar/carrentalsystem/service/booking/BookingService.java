package com.sagar.carrentalsystem.service.booking;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.mapper.BookingMapper;
import com.sagar.carrentalsystem.model.dto.BookingRequestDTO;
import com.sagar.carrentalsystem.model.dto.BookingResponseDTO;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.user.User;
import com.sagar.carrentalsystem.repository.bookingRepo.BookingRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarVariantRepository;
import com.sagar.carrentalsystem.repository.userRepo.UserRepository;
import com.sagar.carrentalsystem.service.car.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarVariantRepository carVariantRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private CarService carService;

    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequest, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found."));

        CarVariant carVariant = carVariantRepository.findById(bookingRequest.getCarVariantId())
                .orElseThrow(() -> new EntityNotFoundException("Car variant not found."));

        List<Car> availableCars = carService.getAvailableCarsByVariant(bookingRequest.getCarVariantId(), bookingRequest.getStartDate(), bookingRequest.getEndDate());
        if (availableCars.isEmpty()) {
            throw new IllegalStateException("No cars of this variant are available for the requested dates.");
        }

        long rentalDays = Duration.between(
                bookingRequest.getStartDate().atStartOfDay(),
                bookingRequest.getEndDate().atStartOfDay()
        ).toDays();
        if (rentalDays <= 0) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
        double totalCost = carVariant.getRentalRatePerDay() * rentalDays;

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setCarVariant(carVariant);
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setTotalCost(totalCost);
        booking.setStatus(BookingStatus.PENDING);

        Booking createdBooking =  bookingRepository.save(booking);
        return bookingMapper.toDto(createdBooking);
    }

    public BookingResponseDTO approveBooking(Long bookingId, Long carId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found."));

        // Check if the car is already booked for the given dates
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(car.getId(), booking.getStartDate(), booking.getEndDate());
        if (!conflictingBookings.isEmpty()) {
            throw new IllegalStateException("The selected car is not available for the requested dates.");
        }

        // Check if the car's variant matches the booking's requested variant
        if (!car.getCarVariant().getId().equals(booking.getCarVariant().getId())) {
            throw new IllegalArgumentException("The selected car's variant does not match the booking.");
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setAssignedCar(car);

        Booking approvedBooking =  bookingRepository.save(booking);
        return bookingMapper.toDto(approvedBooking);

    }

    public BookingResponseDTO rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected.");
        }
        booking.setStatus(BookingStatus.REJECTED);
        Booking rejectedBooking =  bookingRepository.save(booking);
        return bookingMapper.toDto(rejectedBooking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        List<Booking> allBookings = bookingRepository.findAll();
        return allBookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> getPendingBookings() {
        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);
        return pendingBookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> getCustomerBookings(String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found."));
        List<Booking>  customerBookings = bookingRepository.findByCustomerId(customer.getId());
        return customerBookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found."));

        // Ensure only the customer who made the booking can cancel it
        if (!booking.getCustomer().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.PAID) {
            throw new IllegalStateException("Paid bookings cannot be canceled directly. Contact support for a refund.");
        }

        booking.setStatus(BookingStatus.REJECTED);

        // If a car was already assigned, make it available again
        if (booking.getAssignedCar() != null) {
            Car assignedCar = booking.getAssignedCar();
            assignedCar.setAvailable(true);
            carRepository.save(assignedCar);
        }
        Booking canceledBooking =  bookingRepository.save(booking);
        return bookingMapper.toDto(canceledBooking);
    }
}
