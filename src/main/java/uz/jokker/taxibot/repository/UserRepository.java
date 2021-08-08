package uz.jokker.taxibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.jokker.taxibot.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByChatId(Long chatId);

}
