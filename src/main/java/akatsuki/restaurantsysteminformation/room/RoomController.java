package akatsuki.restaurantsysteminformation.room;

import akatsuki.restaurantsysteminformation.restauranttable.RestaurantTable;
import akatsuki.restaurantsysteminformation.restauranttable.dto.RestaurantTableDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomCreateDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomUpdateDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomWithTablesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Validated
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public List<RoomWithTablesDTO> getAll() {
        return roomService.getAll().stream().map(RoomWithTablesDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RoomWithTablesDTO getOne(@PathVariable @Positive(message = "Id has to be a positive value.") long id) {
        return new RoomWithTablesDTO(roomService.getOne(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid RoomCreateDTO roomDTO) {
        roomService.create(new Room(roomDTO));
    }

    @PutMapping("/{id}")
    public void update(@RequestBody @Valid UpdateRoomDTO updateRoomDTO,
                       @Positive(message = "Id has to be a positive value.") @PathVariable long id) {
        roomService.updateByRoomDTO(updateRoomDTO, id);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "Id has to be a positive value.") long id) {
        roomService.delete(id);
    }
}
