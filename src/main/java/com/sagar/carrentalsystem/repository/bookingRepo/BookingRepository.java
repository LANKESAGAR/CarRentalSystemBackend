package com.sagar.carrentalsystem.repository.bookingRepo;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);

    // Custom query to check if a car is available within a date range
    @Query("SELECT b FROM Booking b WHERE b.assignedCar.id = :carId AND b.status IN ('APPROVED', 'PAID') AND " +
            "(:startDate BETWEEN b.startDate AND b.endDate OR :endDate BETWEEN b.startDate AND b.endDate OR " +
            "b.startDate BETWEEN :startDate AND :endDate OR b.endDate BETWEEN :startDate AND :endDate)")
    List<Booking> findConflictingBookings(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find bookings in PENDING status for admin review
    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByAssignedCarAndStatusIn(Car assignedCar, List<BookingStatus> statuses);
    List<Booking> findByCarVariantAndStatusIn(CarVariant carVariant, List<BookingStatus> statuses);

}
