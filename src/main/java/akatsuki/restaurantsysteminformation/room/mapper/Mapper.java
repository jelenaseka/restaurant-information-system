package akatsuki.restaurantsysteminformation.room.mapper;

import akatsuki.restaurantsysteminformation.enums.TableShape;
import akatsuki.restaurantsysteminformation.enums.TableState;
import akatsuki.restaurantsysteminformation.restauranttable.RestaurantTable;
import akatsuki.restaurantsysteminformation.restauranttable.dto.CreateRestaurantTableDTO;
import akatsuki.restaurantsysteminformation.restauranttable.dto.UpdateRestaurantTableDTO;
import akatsuki.restaurantsysteminformation.restauranttable.exception.RestaurantTableShapeNotValidException;
import akatsuki.restaurantsysteminformation.restauranttable.exception.RestaurantTableStateNotValidException;
import akatsuki.restaurantsysteminformation.room.Room;
import akatsuki.restaurantsysteminformation.room.dto.RoomDTO;

import java.util.ArrayList;

public class Mapper {

    public static Room convertRoomDTOToRoom(RoomDTO roomDTO) {
        return new Room(roomDTO.getName(), false, new ArrayList<>());
    }

    public static RestaurantTable convertCreateRestaurantTableDTOToRestaurantTable(CreateRestaurantTableDTO createRestaurantTableDTO) {
        restaurantTableEnumsCheck(createRestaurantTableDTO.getState(), createRestaurantTableDTO.getShape());
        return new RestaurantTable(
                createRestaurantTableDTO.getName(),
                TableState.valueOf(createRestaurantTableDTO.getState()),
                TableShape.valueOf(createRestaurantTableDTO.getShape()),
                false,
                null
        );
    }

    public static RestaurantTable convertCreateRestaurantTableDTOToRestaurantTable(UpdateRestaurantTableDTO updateRestaurantTableDTO) {
        restaurantTableEnumsCheck(updateRestaurantTableDTO.getState(), updateRestaurantTableDTO.getShape());
        return new RestaurantTable(
                updateRestaurantTableDTO.getName(),
                TableState.valueOf(updateRestaurantTableDTO.getState()),
                TableShape.valueOf(updateRestaurantTableDTO.getShape()),
                false,
                null
        );
    }

    private static void restaurantTableEnumsCheck(String state, String shape) {
        try {
            TableState.valueOf(state);

        } catch (RuntimeException e) {
            throw new RestaurantTableStateNotValidException("Restaurant table state is not valid.");
        }
        try {
            TableShape.valueOf(shape);
        } catch (RuntimeException e) {
            throw new RestaurantTableShapeNotValidException("Restaurant table shape is not valid.");
        }
    }
}
