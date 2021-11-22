package akatsuki.restaurantsysteminformation.unregistereduser;

import akatsuki.restaurantsysteminformation.dishitem.DishItem;
import akatsuki.restaurantsysteminformation.dishitem.DishItemRepository;
import akatsuki.restaurantsysteminformation.dishitem.exception.DishItemNotFoundException;
import akatsuki.restaurantsysteminformation.enums.UserType;
import akatsuki.restaurantsysteminformation.order.OrderServiceImpl;
import akatsuki.restaurantsysteminformation.salary.Salary;
import akatsuki.restaurantsysteminformation.user.UserService;
import akatsuki.restaurantsysteminformation.user.UserServiceImpl;
import akatsuki.restaurantsysteminformation.user.exception.UserExistsException;
import akatsuki.restaurantsysteminformation.user.exception.UserTypeNotValidException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnregisteredUserServiceTest {

    @InjectMocks
    UnregisteredUserServiceImpl unregisteredUserService;

    @Mock
    UnregisteredUserRepository unregisteredUserRepositoryMock;

    @Mock
    UserServiceImpl userServiceMock;

    @Test
    void getOne() {
    }

    @Test
    void getAll() {
    }

    // in user service test invalid email, invalid phone
    @Test
    @DisplayName("When valid entity is passed, object is created.")
    void create_Valid_SavedObject() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.WAITER, false, "9999");
        unregisteredUserService.create(user);
        Mockito.verify(unregisteredUserRepositoryMock, Mockito.times(1)).save(Mockito.any(UnregisteredUser.class));
    }

    @Test
    @DisplayName("When invalid pin is passed, exception should occur.")
    void create_InvalidPin_ExceptionThrown() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.WAITER, false, "1111");
        Mockito.when(unregisteredUserRepositoryMock.findByPinCode("1111")).thenReturn(Optional.of(new UnregisteredUser()));
        Assertions.assertThrows(UserExistsException.class, () -> {
            unregisteredUserService.create(user);
        });
    }

    @Test
    @DisplayName("When invalid user type is passed, exception should occur.")
    void create_InvalidUserType_ExceptionThrown() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.MANAGER, false, "1111");
        Assertions.assertThrows(UserTypeNotValidException.class, () -> {
            unregisteredUserService.create(user);
        });
    }

    @Test
    void update_Valid_SavedObject() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.WAITER, false, "9999");
        Mockito.when(unregisteredUserRepositoryMock.findById(1L)).thenReturn(Optional.of(new UnregisteredUser()));
        unregisteredUserService.update(user, 1L);
        Mockito.verify(unregisteredUserRepositoryMock, Mockito.times(1)).save(Mockito.any(UnregisteredUser.class));
    }

    @Test
    void update_InvalidPin_ExceptionThrown() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.WAITER, false, "1111");
        user.setId(1L);
        Mockito.when(unregisteredUserRepositoryMock.findById(1L)).thenReturn(Optional.of(new UnregisteredUser()));
        UnregisteredUser un = new UnregisteredUser(); un.setId(2L);
        Mockito.when(unregisteredUserRepositoryMock.findByPinCode("1111")).thenReturn(Optional.of(un));
        Assertions.assertThrows(UserExistsException.class, () -> {
            unregisteredUserService.update(user, 1L);
        });
    }

    @Test
    @DisplayName("When invalid user type is passed, exception should occur.")
    void update_InvalidUserType_ExceptionThrown() {
        UnregisteredUser user = new UnregisteredUser("Jelena", "Stojanovic", "sekica@gmail.com", "069111223", Collections.singletonList(new Salary(LocalDateTime.now(),1000000)), UserType.MANAGER, false, "1111");
        Mockito.when(unregisteredUserRepositoryMock.findById(1L)).thenReturn(Optional.of(new UnregisteredUser()));
        Assertions.assertThrows(UserTypeNotValidException.class, () -> {
            unregisteredUserService.update(user, 1L);
        });
    }

    @Test
    void delete() {
    }

    @Test
    void checkPinCode() {
    }
}