package uz.jokker.taxibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.jokker.taxibot.entity.Poster;

import java.util.Optional;
import java.util.UUID;

public interface PosterRepository extends JpaRepository<Poster,Integer> {

   Optional<Poster> findByUserId(UUID user_id);

}
