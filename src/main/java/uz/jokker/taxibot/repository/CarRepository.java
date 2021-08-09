package uz.jokker.taxibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.jokker.taxibot.entity.Car;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car,Integer> {

    Optional<Car> findByName(String name);
}
