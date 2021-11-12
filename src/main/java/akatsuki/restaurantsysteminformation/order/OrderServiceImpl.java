package akatsuki.restaurantsysteminformation.order;

import akatsuki.restaurantsysteminformation.dishitem.DishItem;
import akatsuki.restaurantsysteminformation.drinkitem.DrinkItem;
import akatsuki.restaurantsysteminformation.drinkitems.DrinkItems;
import akatsuki.restaurantsysteminformation.enums.ItemType;
import akatsuki.restaurantsysteminformation.enums.UserType;
import akatsuki.restaurantsysteminformation.order.exception.OrderDiscardException;
import akatsuki.restaurantsysteminformation.order.exception.OrderDiscardNotActiveException;
import akatsuki.restaurantsysteminformation.order.exception.OrderNotFoundException;
import akatsuki.restaurantsysteminformation.user.exception.UserNotFoundException;
import akatsuki.restaurantsysteminformation.user.exception.UserTypeNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getOne(long id) {
        return orderRepository.findOrderByIdFetchWaiter(id).orElseThrow(
                () -> new UserNotFoundException("Order with the id " + id + " is not found in the database."));
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAllFetchWaiter().orElseThrow(
                () -> new UserNotFoundException("There's no order created."));
    }

    @Override
    public void create(Order order) {
        if (order.getWaiter().getType() != UserType.WAITER) {
            throw new UserTypeNotValidException("User has to be waiter!");
        }
        double dishesPrice = calculateDishesPrice(order.getDishes());
        double drinksPrice = calculateDrinksPrice(order.getDrinks());
        order.setTotalPrice(dishesPrice + drinksPrice);
        orderRepository.save(order);
    }

    private double calculateDishesPrice(List<DishItem> dishes) {
        double dishesPrice = 0;
        for (DishItem dishItem : dishes) {
            if (dishItem.getItem().getType() != ItemType.DISH) {
                throw new UserTypeNotValidException("Not correct type of dish item!");
            }
            dishesPrice += dishItem.getAmount() * dishItem.getItem().getLastDefinedPrice().getValue();
        }
        return dishesPrice;
    }

    private double calculateDrinksPrice(List<DrinkItems> drinkItemsList) {
        double drinksPrice = 0;
        for (DrinkItems drinkItems : drinkItemsList) {
            List<DrinkItem> drinkItemList = drinkItems.getDrinkItems();
            for (DrinkItem drinkItem : drinkItemList) {
                if (drinkItem.getItem().getType() != ItemType.DRINK) {
                    throw new UserTypeNotValidException("Not correct type of drink item!");
                }
                drinksPrice += drinkItem.getAmount() * drinkItem.getItem().getLastDefinedPrice().getValue();
            }
        }
        return drinksPrice;
    }

    @Override
    public void discard(long id) {
        Order order = checkOrderExistence(id);
        if (order.isDiscarded()) {
            throw new OrderDiscardException("Order with the id " + id + " is already discarded.");
        }
        if (!order.isActive()) {
            throw new OrderDiscardNotActiveException("Order with the id " + id + " is not active, can't be discarded.");
        }
        // TODO Vidi dal treba jos prolaziti kroz liste i podesavati neke njihove flagove, npr. isActive kod DrinkItems
        order.setDiscarded(true);
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public void charge(long id) {
        Order order = checkOrderExistence(id);
        if (order.isDiscarded()) {
            throw new OrderDiscardException("Order with the id " + id + " is discarded, can't be charged.");
        }
        if (!order.isActive()) {
            throw new OrderDiscardNotActiveException("Order with the id " + id + " is not active, can't be charged.");
        }
        // TODO Vidi dal treba jos prolaziti kroz liste i podesavati neke njihove flagove
        order.setActive(false);
        orderRepository.save(order);
    }

    private Order checkOrderExistence(long id) {
        Optional<Order> foundOrder = orderRepository.findById(id);
        if (foundOrder.isEmpty()) {
            throw new OrderNotFoundException("Order with the id " + id + " is not found in the database.");
        }
        return foundOrder.get();
    }
}
