package uz.jokker.taxibot.entity.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.jokker.taxibot.entity.Poster;
import uz.jokker.taxibot.entity.Region;
import uz.jokker.taxibot.entity.User;
import uz.jokker.taxibot.entity.enums.UserType;
import uz.jokker.taxibot.repository.PosterRepository;
import uz.jokker.taxibot.repository.RegionRepository;
import uz.jokker.taxibot.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TelegramService {
    @Autowired
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final PosterRepository posterRepository;

    public TelegramService(UserRepository userRepository, RegionRepository regionRepository, PosterRepository posterRepository) {
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.posterRepository = posterRepository;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    public SendMessage registerFirstName(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);
        user.setState(State.REGISTER_FIRST_NAME);
        user.setChatId(update.getMessage().getChatId());
        userRepository.save(user);

        sendMessage.setText(Constant.firstname);


        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage registerLastName(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);
        user.setFirstName(update.getMessage().getText());
        user.setState(State.REGISTER_LAST_NAME);
        user.setChatId(update.getMessage().getChatId());
        userRepository.save(user);

        sendMessage.setText(Constant.lastname);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;

    }

    public SendMessage registerPhoneNumber(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);
        user.setLastName(update.getMessage().getText());
        user.setState(State.REGISTER_PHONE_NUMBER);
        user.setChatId(update.getMessage().getChatId());
        userRepository.save(user);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton sendContact = new KeyboardButton();

        sendContact.setRequestContact(true);

        sendContact.setText(Constant.share_contact);

        sendMessage.setText(Constant.share_contact);

        keyboardRow.add(sendContact);
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText(Constant.share_contact);
        return sendMessage;
    }

    public SendMessage registerEnd(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);

        String phoneNumber = update.getMessage().getContact().getPhoneNumber();

        user.setPhoneNumber(phoneNumber);

        if (update.getMessage().hasContact()) {
            user.setPhoneNumber(phoneNumber);
        } else {
            user.setPhoneNumber(update.getMessage().getText());
        }
        user.setState(State.REGISTER_STAFF_TYPE);
        user.setChatId(update.getMessage().getChatId());
        userRepository.save(user);
        sendMessage.setText(Constant.register);


        ReplyKeyboardMarkup replyKeyboardMarkup = klientVaTaxi();

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText(Constant.client_taxi);
        return sendMessage;
    }

    public SendMessage registerStaff(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);

        if (update.getMessage().getText().equals(Constant.client)) {
            user.setUserType(UserType.CLIENT);
        }
        if (update.getMessage().getText().equals(Constant.taxi)) {
            user.setUserType(UserType.TAXI);
        }
        user.setState(State.MENU);
        user.setChatId(update.getMessage().getChatId());
        userRepository.save(user);

        sendMessage.setText(Constant.system);

        return menu(update);

    }

    public SendMessage menu(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);


        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);
        user.setState(State.nulll);
        userRepository.save(user);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();
        KeyboardButton keyboardButton3 = new KeyboardButton();
        KeyboardButton keyboardButton4 = new KeyboardButton();
        KeyboardButton keyboardButton5 = new KeyboardButton();

        keyboardButton1.setText(Constant.elon_korish);
        keyboardButton2.setText(Constant.elon_berish);
        keyboardButton3.setText(Constant.mening_elonlarim);
        keyboardButton4.setText(Constant.setting);
        keyboardButton5.setText(Constant.elonlarimniOchirish);


        keyboardRow1.add(keyboardButton1);
        keyboardRow2.add(keyboardButton2);
        keyboardRow3.add(keyboardButton3);
        keyboardRow3.add(keyboardButton4);
        keyboardRow3.add(keyboardButton5);

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        sendMessage.setText(Constant.system);

        return sendMessage;
    }

    public void menuSelected(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);

        String text = update.getMessage().getText();
        switch (text) {
            case Constant.elon_korish:
                user.setState(Constant.elon_korish);
                break;
            case Constant.elon_berish:
                user.setState(Constant.elon_berish);
                break;
            case Constant.mening_elonlarim:
                user.setState(Constant.mening_elonlarim);
                break;
            case Constant.setting:
                user.setState(Constant.setting);
                break;
            case Constant.elonlarimniOchirish:
                user.setState(Constant.elonlarimniOchirish);
                break;
        }
        userRepository.save(user);
    }

    public SendMessage elonQayerdan(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        sendMessage.setText(Constant.elon_qayerdan);

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.get();
        user.setState(Constant.elon_qayerga);

        String text = update.getMessage().getText();
        if (!text.equals(Constant.client) && !text.equals(Constant.taxi)) {
            user.setState(State.MENU);
            return menu(update, Constant.buttonTanla);
        }
//        Poster poster = posterRepository.findByUserId(user.getId()).get();
//        Region region = regionRepository.findByName(text).get();

        posterSaveUserType(update);

        ReplyKeyboardMarkup replyKeyboardMarkup = regionsDinamicbir();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        userRepository.save(user);
        return sendMessage;
    }

    public void posterSaveUserType(Update update) {

        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.get();

        Poster poster = posterRepository.findByUserId(user.getId()).get();
        poster.setUser(user);


        String text = update.getMessage().getText();
        if (text.equals(Constant.client)) {
            poster.setPosterType(UserType.CLIENT);
        } else if (text.equals(Constant.taxi)) {
            poster.setPosterType(UserType.TAXI);
        }
        posterRepository.save(poster);
    }

    public SendMessage elonQayerga(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        User user = userRepository.findByChatId(update.getMessage().getChatId()).get();

        user.setState(State.DEFFINATION);
        sendMessage.setText(Constant.elon_qayerga);

        String qayerdan = update.getMessage().getText();
        if (qayerdan.equals(Constant.menu)) {
            user.setState(State.MENU);
            return menu(update);
        }
        boolean tek = true;
        List<uz.jokker.taxibot.entity.Region> all = regionRepository.findAll();
        for (Region region : all) {
            if (region.getName().equals(qayerdan)) {
                tek = false;
            }
        }
        if (tek) {
            user.setState(State.MENU);
            return menu(update, Constant.buttonTanlaViloyat);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = regionsDinamicikki(qayerdan);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        Region region = regionRepository.findByName(qayerdan).get();

        Poster poster = new Poster();
        poster.setUser(user);
        poster.setQayerdan(region);
        posterRepository.save(poster);
        userRepository.save(user);
        return sendMessage;
    }

    public ReplyKeyboardMarkup regionsDinamicbir() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        List<uz.jokker.taxibot.entity.Region> all = regionRepository.findAll();
        if (all.size() % 2 == 0) {
            for (int i = 0; i < all.size() - 1; i += 2) {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(all.get(i).getName());
                KeyboardButton keyboardButton1 = new KeyboardButton();
                keyboardButton1.setText(all.get(i + 1).getName());
                keyboardRow.add(keyboardButton);
                keyboardRow.add(keyboardButton1);
                keyboardRows.add(keyboardRow);
            }
            KeyboardButton menu = new KeyboardButton();
            menu.setText(Constant.menu);
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(menu);
            keyboardRows.add(keyboardRow);
        } else {
            for (int i = 0; i < all.size() - 1; i += 2) {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(all.get(i).getName());
                KeyboardButton keyboardButton1 = new KeyboardButton();
                keyboardButton1.setText(all.get(i + 1).getName());
                keyboardRow.add(keyboardButton);
                keyboardRow.add(keyboardButton1);
                keyboardRows.add(keyboardRow);
            }
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(all.get(all.size() - 1).getName());

            KeyboardButton menu = new KeyboardButton();
            menu.setText(Constant.menu);
            keyboardRow.add(keyboardButton);
            keyboardRow.add(menu);
            keyboardRows.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup regionsDinamicikki(String region) {

        List<Region> all = regionRepository.findAllByNameNot(region);

//        Optional<uz.jokker.taxibot.entity.Region> region = regionRepository.findById(regionId);
//        regionRepository.delete(region.get());
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        List<uz.jokker.taxibot.entity.Region> all = regionRepository.findAll();
        if (all.size() % 2 == 0) {
            for (int i = 0; i < all.size(); i += 2) {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(all.get(i).getName());
                KeyboardButton keyboardButton1 = new KeyboardButton();
                keyboardButton1.setText(all.get(i + 1).getName());
                keyboardRow.add(keyboardButton);
                keyboardRow.add(keyboardButton1);
                keyboardRows.add(keyboardRow);
            }
            KeyboardButton menu = new KeyboardButton();
            menu.setText(Constant.menu);
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(menu);
            keyboardRows.add(keyboardRow);
        } else {
            for (int i = 0; i < all.size() - 1; i += 2) {
                KeyboardRow keyboardRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(all.get(i).getName());
                KeyboardButton keyboardButton1 = new KeyboardButton();
                keyboardButton1.setText(all.get(i + 1).getName());
                keyboardRow.add(keyboardButton);
                keyboardRow.add(keyboardButton1);
                keyboardRows.add(keyboardRow);
            }
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(all.get(all.size() - 1).getName());
            keyboardRow.add(keyboardButton);


            KeyboardButton menu = new KeyboardButton();
            menu.setText(Constant.menu);

            keyboardRow.add(menu);
            keyboardRows.add(keyboardRow);
        }
//        regionRepository.save(region.get());
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public SendMessage posterTypeStaff(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        User user = userRepository.findByChatId(update.getMessage().getChatId()).get();
        user.setState(State.elon_berish_yonalish);
        userRepository.save(user);

        ReplyKeyboardMarkup replyKeyboardMarkup = klientVaTaxi();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText(Constant.elon_taxi_klient);

        return sendMessage;

    }

    //    klient va taxi degan buttonlarni yasab beradi
    public ReplyKeyboardMarkup klientVaTaxi() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton client = new KeyboardButton();
        KeyboardButton taxi = new KeyboardButton();

        client.setText(Constant.client);
        taxi.setText(Constant.taxi);

        keyboardRow.add(client);
        keyboardRow.add(taxi);
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    public SendMessage deffination(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        User user = userRepository.findByChatId(update.getMessage().getChatId()).orElseGet(User::new);
        String text = update.getMessage().getText();

        if (text.equals(Constant.menu)) {
            user.setState(State.MENU);
            return menu(update);
        }
        boolean tek = true;
        List<uz.jokker.taxibot.entity.Region> all = regionRepository.findAll();
        for (Region region : all) {
            if (region.getName().equals(text)) {
                tek = false;
            }
        }
        if (tek) {
            user.setState(State.MENU);
            return menu(update, Constant.buttonTanlaViloyat);
        }
        Region regionSave = regionRepository.findByName(text).get();
        Poster posterEdited = posterRepository.findByUserId(user.getId()).get();
        posterEdited.setQayerga(regionSave);
        posterRepository.save(posterEdited);

        sendMessage.setText("Salomlar Deffination ga kelding");

        ReplyKeyboardMarkup replyKeyboardMarkup = menuButton();

        user.setState(State.YAKUN);
        Poster poster = posterRepository.findByUserId(user.getId()).orElseGet(Poster::new);
        Region region = regionRepository.findByName(text).get();
        poster.setQayerdan(region);
        posterRepository.save(poster);
        userRepository.save(user);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;

    }

    public SendMessage elonSave(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        String text = update.getMessage().getText();
        User user = userRepository.findByChatId(update.getMessage().getChatId()).get();
        if (text.equals(Constant.menu)) {
            user.setState(State.MENU);
            return menu(update);
        }

        Poster poster = posterRepository.findByUserId(user.getId()).get();
        poster.setDefination(text);
        posterRepository.save(poster);

        sendMessage.setText("E'lon saqlandi. Bu e'loningizni amal qilish muddatini kiring ");
        user.setState(State.DEADLINE_TIME);
        ReplyKeyboardMarkup replyKeyboardMarkup = vaqtDeadlineTime();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    //vaqtni buttonlarni yasaydi.
    public ReplyKeyboardMarkup vaqtDeadlineTime() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();

        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();
        KeyboardButton keyboardButton3 = new KeyboardButton();
        KeyboardButton keyboardButton4 = new KeyboardButton();
        KeyboardButton keyboardButton5 = new KeyboardButton();
        KeyboardButton keyboardButton6 = new KeyboardButton();
        keyboardButton1.setText(Constant.min10);
        keyboardButton2.setText(Constant.min30);
        keyboardButton3.setText(Constant.min60);
        keyboardButton4.setText(Constant.min120);
        keyboardButton5.setText(Constant.min360);
        keyboardButton6.setText(Constant.min5x60);

        keyboardRow1.add(keyboardButton1);
        keyboardRow1.add(keyboardButton2);
        keyboardRow1.add(keyboardButton3);
        keyboardRow2.add(keyboardButton4);
        keyboardRow2.add(keyboardButton5);
        keyboardRow2.add(keyboardButton6);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    //    Menu degan buttonni yasaydi tamam
    public ReplyKeyboardMarkup menuButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(Constant.menu);
        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public SendMessage menu(Update update, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);


        Optional<User> optionalUser = userRepository.findByChatId(update.getMessage().getChatId());
        User user = optionalUser.orElseGet(User::new);
        user.setState(State.nulll);
        userRepository.save(user);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();
        KeyboardButton keyboardButton3 = new KeyboardButton();
        KeyboardButton keyboardButton4 = new KeyboardButton();
        KeyboardButton keyboardButton5 = new KeyboardButton();

        keyboardButton1.setText(Constant.elon_korish);
        keyboardButton2.setText(Constant.elon_berish);
        keyboardButton3.setText(Constant.mening_elonlarim);
        keyboardButton4.setText(Constant.setting);
        keyboardButton5.setText(Constant.elonlarimniOchirish);


        keyboardRow1.add(keyboardButton1);
        keyboardRow2.add(keyboardButton2);
        keyboardRow3.add(keyboardButton3);
        keyboardRow3.add(keyboardButton4);
        keyboardRow3.add(keyboardButton5);

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        sendMessage.setText(message);

        return sendMessage;
    }


    public SendMessage elonNiAmalQilishVaqtiniSaqlash(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        String text = update.getMessage().getText();
        User user = userRepository.findByChatId(update.getMessage().getChatId()).get();
        boolean tek = true;

        if (text == Constant.min5x60 || text == Constant.min360 || text == Constant.min10 ||
                text == Constant.min120 || text == Constant.min30 || text == Constant.min60) {
            Poster poster = posterRepository.findByUserId(user.getId()).get();
//            if (text == Constant.min10) {poster.setActiveTime((System.currentTimeMillis()+1000*60*10));   }


        } else {
            user.setState(State.MENU);
            return menu(update, Constant.VaqtButtonlarniTanlamasa);
        }
    }
}