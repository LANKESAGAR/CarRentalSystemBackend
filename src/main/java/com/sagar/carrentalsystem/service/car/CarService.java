package com.sagar.carrentalsystem.service.car;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.mapper.CarMapper;
import com.sagar.carrentalsystem.model.dto.CarDTO;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.repository.bookingRepo.BookingRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarVariantRepository carVariantRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarMapper carMapper;

    public CarDTO addCar(Car car, Long carVariantId) {
        CarVariant carVariant = carVariantRepository.findById(carVariantId)
                .orElseThrow(() -> new EntityNotFoundException("Car variant not found with ID: " + carVariantId));
        car.setCarVariant(carVariant);
        car.setAvailable(true);
        Car savedCar = carRepository.save(car);
        return carMapper.toCarDTO(savedCar);
    }

    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream()
                .map(carMapper::toCarDTO)
                .toList();
    }

    public List<Car> getAvailableCarsByVariant(Long carVariantId, LocalDate startDate, LocalDate endDate) {
        // Find all cars of the requested variant
        List<Car> allCarsOfVariant = carRepository.findByCarVariant_Id(carVariantId);

        // Find all cars that are already booked for the given date range
        List<Car> bookedCars = bookingRepository.findAll().stream()
                .filter(b -> b.getAssignedCar() != null && b.getStatus() != BookingStatus.REJECTED &&
                        (startDate.isBefore(b.getEndDate()) && endDate.isAfter(b.getStartDate())))
                .map(Booking::getAssignedCar)
                .collect(Collectors.toList());

        // Return the list of cars that are available
        return allCarsOfVariant.stream()
                .filter(car -> !bookedCars.contains(car))
                .collect(Collectors.toList());
    }

    public void deleteCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car with ID " + carId + " not found."));

        // Check for active or pending bookings
        List<Booking> activeBookings = bookingRepository.findByAssignedCarAndStatusIn(
                car, List.of(BookingStatus.APPROVED, BookingStatus.PENDING));

        if (!activeBookings.isEmpty()) {
            throw new IllegalStateException("Cannot delete this car. It is assigned to one or more active or pending bookings.");
        }

        carRepository.deleteById(carId);
    }

    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
        return carMapper.toCarDTO(car);
    }

    public CarDTO updateCar(Long id, CarDTO carDetails) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));

        // Check for active or pending bookings before allowing updates to availability
        if (car.isAvailable() != carDetails.isAvailable()) {
            List<Booking> activeBookings = bookingRepository.findByAssignedCarAndStatusIn(
                    car, List.of(BookingStatus.APPROVED, BookingStatus.PENDING));

            if (!activeBookings.isEmpty()) {
                throw new IllegalStateException("Cannot change car availability. It is assigned to active or pending bookings.");
            }
        }

        // Update car properties
        car.setLicensePlate(carDetails.getLicensePlate());
        car.setAvailable(carDetails.isAvailable());
        car.setLastServiceDate(carDetails.getLastServiceDate());

        Car updatedCar = carRepository.save(car);
        return carMapper.toCarDTO(updatedCar);
    }
}

