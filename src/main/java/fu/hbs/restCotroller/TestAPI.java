package fu.hbs.restCotroller;

import fu.hbs.entities.Room;
import fu.hbs.service.interfaces.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class TestAPI {
    @Autowired
    private RoomService roomService;

    @GetMapping("/hbs/hello")
    public Room getRoom(@RequestParam(value = "id") Long id) {
        Room roomList = roomService.findByRoomId(id);
        return roomList;
    }
}
