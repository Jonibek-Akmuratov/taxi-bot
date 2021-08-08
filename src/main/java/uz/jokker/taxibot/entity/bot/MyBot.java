package uz.jokker.taxibot.entity.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.jokker.taxibot.entity.User;
import uz.jokker.taxibot.repository.UserRepository;

import java.util.Optional;

@Component
public class MyBot extends TelegramLongPollingBot {
    @Value("${bot.token}")
    String botToken;

    @Value("${bot.username}")
    String botUsername;


    @Autowired
    UserRepository userRepository;
    @Autowired
    TelegramService telegramService;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());

            Message message = update.getMessage();
            String text = message.getText();
            if (message.getText() == null && message.getContact().getPhoneNumber() != null) {
                execute(telegramService.registerEnd(update));
            }
            if (message.hasText()) {
                if (text.equals("/start") || message.hasContact()) {
                    execute(telegramService.registerFirstName(update));

                } else {
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        String state = user.getState();

                        switch (state) {
                            case State.REGISTER_FIRST_NAME:
                                execute(telegramService.registerLastName(update));
                                break;
                            case State.REGISTER_LAST_NAME:
                                execute(telegramService.registerPhoneNumber(update));
                                break;
                            case State.REGISTER_PHONE_NUMBER:
                                execute(telegramService.registerEnd(update));
                                break;
                            case State.REGISTER_STAFF_TYPE:
                                execute(telegramService.registerStaff(update));
                                break;
                            case State.MENU:
                                execute(telegramService.menu(update));
                                break;
                            case State.nulll:
                                telegramService.menuSelected(update);
                        }
                        User userEdit = userRepository.findByChatId(update.getMessage().getChatId()).get();
                        switch (userEdit.getState()) {

                            case Constant.elon_berish:
                                execute(telegramService.posterTypeStaff(update));
                                break;
                            case Constant.elon_berish_yonalish:
                                execute(telegramService.elonQayerdan(update));
                                break;
                            case Constant.elon_qayerga:
                                execute(telegramService.elonQayerga(update));
                                break;

                            case State.DEFFINATION:
                                execute(telegramService.deffination(update));

                                break;
                            case State.YAKUN:
                                execute(telegramService.elonSave(update));
                                break;
                            case Constant.setting:

                                break;
                            case Constant.elonlarimniOchirish:

                                break;
                        }

                    }
                }
            }
        }
        if (update.hasCallbackQuery()) {


        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
