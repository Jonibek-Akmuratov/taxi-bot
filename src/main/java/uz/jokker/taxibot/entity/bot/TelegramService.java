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
import uz.jokker.taxibot.entity.Car;
import uz.jokker.taxibot.entity.Poster;
import uz.jokker.taxibot.entity.Region;
import uz.jokker.taxibot.entity.User;
import uz.jokker.taxibot.entity.enums.UserType;
import uz.jokker.taxibot.repository.CarRepository;
import uz.jokker.taxibot.repository.PosterRepository;
import uz.jokker.taxibot.repository.RegionRepository;
import uz.jokker.taxibot.repository.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TelegramService {
    @Autowired
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final PosterRepository posterRepository;
    private final CarRepository carRepository;


    public TelegramService(UserRepository userRepository, RegionRepository regionRepository, PosterRepository posterRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.posterRepository = posterRepository;
        this.carRepository = carRepository;
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


        if (update.getMessage().getText().equals(Constant.taxi)) {
            user.setUserType(UserType.TAXI);
            user.setState(State.CAR_TYPE);
            user.setChatId(update.getMessage().getChatId());
            userRepository.save(user);
            return carTypeniTanlash(update, Constant.car_type_select);
        }
        if (update.getMessage().getText().equals(Constant.client)) {
            user.setUserType(UserType.CLIENT);

            user.setState(State.MENU);
            user.setChatId(update.getMessage().getChatId());
            userRepository.save(user);

            return menu(update, Constant.system_client);
        } else {
            sendMessage.setText(Constant.buttonTanla);
            return sendMessage;
        }
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

        Poster poster = posterRepository.findByUserId(user.getId()).orElseGet(Poster::new);


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

        Poster poster = posterRepository.findByUserId(user.getId()).get();
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

        sendMessage.setText(Constant.deffinitiongakeldik);

        ReplyKeyboardMarkup replyKeyboardMarkup = menuButton();

        user.setState(State.YAKUN);
//        Poster poster = posterRepository.findByUserId(user.getId()).orElseGet(Poster::new);
//        Region region = regionRepository.findByName(text).get();
//        poster.setQayerdan(region);
//        posterRepository.save(poster);
        userRepository.save(user);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;

    }

    public SendMessage elonSave(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        String deffination = update.getMessage().getText();
        User user = userRepository.findByChatId(update.getMessage().getChatId()).get();
        if (deffination.equals(Constant.menu)) {
            user.setState(State.MENU);
            return menu(update, Constant.menuga_qaytish);
        }

        Poster poster = posterRepository.findByUserId(user.getId()).get();
        poster.setDefination(deffination);
        posterRepository.save(poster);


        sendMessage.setText("E'lon saqlandi. Bu e'loningizni amal qilish muddatini kiring ");
        user.setState(State.DEFFINATION_SAQLANDI);
        ReplyKeyboardMarkup replyKeyboardMarkup = vaqtDeadlineTime();
        userRepository.save(user);
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


        if (text.equals(Constant.min5x60) || text.equals(Constant.min360) || text.equals(Constant.min10) ||
                text.equals(Constant.min120) || text.equals(Constant.min30) || text.equals(Constant.min60)) {
            Poster poster = posterRepository.findByUserId(user.getId()).get();
            if (text.equals(Constant.min10)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 10l));
            } else if (text.equals(Constant.min30)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 30l));
            } else if (text.equals(Constant.min60)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60l));
            } else if (text.equals(Constant.min120)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 120l));
            } else if (text.equals(Constant.min360)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 360l));
            } else if (text.equals(Constant.min5x60)) {
                poster.setActiveTime(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 5l));
            }
            poster.setActive(true);
            posterRepository.save(poster);
        } else {
            user.setState(State.MENU);
            return menu(update, Constant.VaqtButtonlarniTanlamasa);
        }

        return menu(update, Constant.tugadiElonBerish);
    }

    public SendMessage carTypeniTanlash(Update update, String sendmessag) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        List<Car> all = carRepository.findAll();

        for (int i = 0; i < all.size() - 1; i += 2) {
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton1 = new KeyboardButton();
            KeyboardButton keyboardButton2 = new KeyboardButton();
            keyboardButton1.setText(all.get(i).getName());
            keyboardButton2.setText(all.get(i + 1).getName());
            keyboardRow.add(keyboardButton1);
            keyboardRow.add(keyboardButton2);
            keyboardRows.add(keyboardRow);
        }
        sendMessage.setText(sendmessag);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage carSaved(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        String carType = update.getMessage().getText();
        List<Car> all = carRepository.findAll();
        boolean tek = true;
        for (Car car : all) {
            if (car.getName().equals(carType)) {
                tek = false;
            }
        }
        if (tek) {
            sendMessage.setText("Moshina turini tanlashingiz kk edi. Iltimos qaytatdan urinib ko'ring!!!");
            return sendMessage;
        } else {
            User user = userRepository.findByChatId(update.getMessage().getChatId()).get();
            user.setState(State.MENU);
            Car car = carRepository.findByName(carType).get();
            user.setCarType(car);
            userRepository.save(user);
            return menu(update, (Constant.system_taxi + " " + user.getCarType().getName() + " " + Constant.qiliptayinlandi + " \n" + Constant.system));
        }
    }
}