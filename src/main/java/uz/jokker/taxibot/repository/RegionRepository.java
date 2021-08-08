package uz.jokker.taxibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.jokker.taxibot.entity.Region;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region,Integer> {

    Optional<Region> findByName(String name);

    List<Region> findAllByNameNot(String name);

}
