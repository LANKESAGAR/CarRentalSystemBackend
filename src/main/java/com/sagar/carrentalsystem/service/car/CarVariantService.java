package com.sagar.carrentalsystem.service.car;

import com.sagar.carrentalsystem.constants.BookingStatus;
import com.sagar.carrentalsystem.mapper.CarVariantMapper;
import com.sagar.carrentalsystem.model.dto.CarVariantDTO;
import com.sagar.carrentalsystem.model.entity.booking.Booking;
import com.sagar.carrentalsystem.model.entity.car.Car;
import com.sagar.carrentalsystem.model.entity.car.CarVariant;
import com.sagar.carrentalsystem.repository.bookingRepo.BookingRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarRepository;
import com.sagar.carrentalsystem.repository.carRepo.CarVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarVariantService {
    @Autowired
    private CarVariantRepository carVariantRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarService carService;
    @Autowired
    private CarVariantMapper carVariantMapper;
    @Autowired
    private BookingRepository bookingRepository;

    public CarVariantDTO createCarVariant(CarVariantDTO carVariantDTO) {
        CarVariant carVariant = carVariantMapper.toEntity(carVariantDTO);
        CarVariant createdCarVariant = carVariantRepository.save(carVariant);
        return carVariantMapper.toDto(createdCarVariant);
    }

    public List <CarVariantDTO> getAllCarVariants() {
        List <CarVariant> variants = carVariantRepository.findAll();
        return variants.stream().map(variant ->
                carVariantMapper.toDto(variant)).collect(Collectors.toList());
    }

    public void deleteCarVariant(Long id) {
        CarVariant carVariant = carVariantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car variant with ID " + id + " not found."));

        // Check for active or pending bookings
        List<Booking> activeBookings = bookingRepository.findByCarVariantAndStatusIn(
                carVariant, List.of(BookingStatus.APPROVED, BookingStatus.PENDING));

        if (!activeBookings.isEmpty()) {
            throw new IllegalStateException("Cannot delete this car variant. It is referenced by one or more active or pending bookings.");
        }

        // Loop through and attempt to delete each car
        List<Car> carsToDelete = carRepository.findByCarVariant_Id(id);
        for (Car car : carsToDelete) {
            carService.deleteCar(car.getId());
        }

        // Now that all associated cars (with checks) are handled, delete the variant
        carVariantRepository.deleteById(id);
    }
}
