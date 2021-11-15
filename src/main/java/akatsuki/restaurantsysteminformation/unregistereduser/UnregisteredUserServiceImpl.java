package akatsuki.restaurantsysteminformation.unregistereduser;

import akatsuki.restaurantsysteminformation.dishitem.DishItemService;
import akatsuki.restaurantsysteminformation.drinkitems.DrinkItems;
import akatsuki.restaurantsysteminformation.drinkitems.DrinkItemsService;
import akatsuki.restaurantsysteminformation.enums.UserType;
import akatsuki.restaurantsysteminformation.order.OrderService;
import akatsuki.restaurantsysteminformation.unregistereduser.exception.UnregisteredUserActiveException;
import akatsuki.restaurantsysteminformation.user.User;
import akatsuki.restaurantsysteminformation.user.UserService;
import akatsuki.restaurantsysteminformation.user.exception.UserDeletedException;
import akatsuki.restaurantsysteminformation.user.exception.UserExistsException;
import akatsuki.restaurantsysteminformation.user.exception.UserNotFoundException;
import akatsuki.restaurantsysteminformation.user.exception.UserTypeNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnregisteredUserServiceImpl implements UnregisteredUserService {
    private UnregisteredUserRepository unregisteredUserRepository;
    private UserService userService;
    private OrderService orderService;
    private DrinkItemsService drinkItemsService;
    private DishItemService dishItemService;
//
//    @Autowired
//    public UnregisteredUserServiceImpl(UnregisteredUserRepository unregisteredUserRepository, UserService userService,
//                                       OrderService orderService, DrinkItemsService drinkItemsService, DishItemService dishItemService) {
//        this.unregisteredUserRepository = unregisteredUserRepository;
//        this.userService = userService;
//        this.drinkItemsService = drinkItemsService;
//        this.dishItemService = dishItemService;
//        this.orderService = orderService;
//    }

    @Autowired
    public void setUnregisteredUserRepository(UnregisteredUserRepository unregisteredUserRepository, UserService userService,
                                              OrderService orderService, DrinkItemsService drinkItemsService, DishItemService dishItemService) {
        this.unregisteredUserRepository = unregisteredUserRepository;
        this.userService = userService;
        this.drinkItemsService = drinkItemsService;
        this.dishItemService = dishItemService;
        this.orderService = orderService;
    }

    @Override
    public UnregisteredUser getOne(long id) {
        return unregisteredUserRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with the id " + id + " is not found in the database.")
        );
    }

    @Override
    public void create(UnregisteredUser unregisteredUser) {
        checkPinCodeExistence(unregisteredUser.getPinCode());
        userService.checkEmailExistence(unregisteredUser.getEmailAddress());
        checkUserType(unregisteredUser.getType());
        unregisteredUserRepository.save(unregisteredUser);
    }

    private void checkUserType(UserType type) {
        if (type != UserType.WAITER && type != UserType.CHEF && type != UserType.BARTENDER) {
            throw new UserTypeNotValidException("User type for unregistered user is not valid.");
        }
    }

    @Override
    public boolean userCanBeDeleted(UnregisteredUser user) {
        if (user.getType().equals(UserType.WAITER)) {
            return orderService.isWaiterActive(user);
        } else if (user.getType().equals(UserType.BARTENDER)) {
            return drinkItemsService.isBartenderActive(user);
        } else if (user.getType().equals(UserType.CHEF)) {
            return dishItemService.isChefActive(user);
        }
        return false;
    }

    @Override
    public void delete(long id) {
        UnregisteredUser user = getOne(id);
        if (!userCanBeDeleted(user)) {
            throw new UnregisteredUserActiveException("User with the id " + id + " is currently active and cannot be deleted now.");
        }
        user.setDeleted(true);
        unregisteredUserRepository.save(user);
    }

    @Override
    public void update(UnregisteredUser unregisteredUser, long id) {
        Optional<UnregisteredUser> user = unregisteredUserRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with the id " + id + " is not found in the database.");
        }
        validateUpdate(id, unregisteredUser);

        UnregisteredUser foundUser = user.get();
        foundUser.setFirstName(unregisteredUser.getFirstName());
        foundUser.setLastName(unregisteredUser.getLastName());
        foundUser.setEmailAddress(unregisteredUser.getEmailAddress());
        foundUser.setPhoneNumber(unregisteredUser.getPhoneNumber());
        foundUser.setSalary(unregisteredUser.getSalary());
        foundUser.setPinCode(unregisteredUser.getPinCode());

        unregisteredUserRepository.save(foundUser);
    }

    private void validateUpdate(long id, UnregisteredUser unregisteredUser) {
        checkUserType(unregisteredUser.getType());
        Optional<UnregisteredUser> userByPinCode = unregisteredUserRepository.findByPinCode(unregisteredUser.getPinCode());
        Optional<User> userByEmail = userService.findByEmail(unregisteredUser.getEmailAddress());
        Optional<User> userByPhoneNumber = userService.findByPhoneNumber(unregisteredUser.getPhoneNumber());

        if (userByPinCode.isPresent() && id != userByPinCode.get().getId()) {
            throw new UserExistsException("User with the pin code " + unregisteredUser.getPinCode() + " already exists in the database.");
        }

        if (userByEmail.isPresent() && id != userByEmail.get().getId()) {
            throw new UserExistsException("User with the email " + unregisteredUser.getEmailAddress() + " already exists in the database.");
        }

        if (userByPhoneNumber.isPresent() && id != userByPhoneNumber.get().getId()) {
            throw new UserExistsException("User with the phone number " + unregisteredUser.getPhoneNumber() + " already exists in the database.");
        }
    }

    private void checkPinCodeExistence(String pinCode) {
        Optional<UnregisteredUser> user = unregisteredUserRepository.findByPinCode(pinCode);
        if (user.isPresent()) {
            throw new UserExistsException("User with the pin code " + pinCode + " already exists in the database.");
        }
    }

    @Override
    public List<UnregisteredUser> getAll() {
        return unregisteredUserRepository.findAll();
    }

    @Override
    public UnregisteredUser checkPinCode(int pinCode, UserType type) {
        UnregisteredUser user = unregisteredUserRepository.findByPinCode(pinCode + "")
                .orElseThrow(() -> new UserNotFoundException("User with the pin code " + pinCode + " is not found in the database."));
        if (!user.getType().equals(type))
            throw new UserNotFoundException("User with the pin code " + pinCode + " is not a " + type.name().toLowerCase());
        return user;
    }

}
