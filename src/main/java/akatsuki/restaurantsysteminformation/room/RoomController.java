package akatsuki.restaurantsysteminformation.room;

import akatsuki.restaurantsysteminformation.room.dto.RoomCreateDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomLayoutDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomTablesUpdateDTO;
import akatsuki.restaurantsysteminformation.room.dto.RoomWithTablesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Validated
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/{id}")
    public RoomWithTablesDTO getOne(@PathVariable @Positive(message = "Id has to be a positive value.") long id) {
        return new RoomWithTablesDTO(roomService.getOne(id));
    }

    @GetMapping
    public List<RoomWithTablesDTO> getAll() {
        return roomService.getAll().stream().map(RoomWithTablesDTO::new).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody @Valid RoomCreateDTO roomDTO) {
        return roomService.create(new Room(roomDTO)).getId().toString();
    }

    @PutMapping("/{id}/name")
    public void updateRoomName(@RequestBody @NotBlank String newName,
                               @Positive(message = "Id has to be a positive value.") @PathVariable long id) {
        roomService.updateName(newName, id);
    }

    @PutMapping("/{id}/layout")
    public void updateRoomLayout(@RequestBody @Valid RoomLayoutDTO layoutDTO,
                                 @Positive(message = "Id has to be a positive value.") @PathVariable long id) {
        roomService.updateLayout(layoutDTO, id);
    }

    @PutMapping("/{id}")
    public void update(@RequestBody @Valid RoomTablesUpdateDTO updateRoomDTO,
                       @Positive(message = "Id has to be a positive value.") @PathVariable long id) {
        roomService.updateByRoomDTO(updateRoomDTO, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "Id has to be a positive value.") long id) {
        roomService.delete(id);
    }
}
