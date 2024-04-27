package fu.hbs.controller;

import fu.hbs.dto.MRoomDTO.*;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.service.interfaces.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ManagerRoomController {


    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomCategoryService roomCategoryService;
    @Autowired
    private RoomByCategoryPriceService categoryRoomPriceService;
    @Autowired
    private CategoryRoomPriceService categoryRoomPriceService1;

    @GetMapping("/management/viewRoom")
    public String getViewRoom(@RequestParam(name = "floor", required = false) Integer floor,
                              @RequestParam(name = "status", required = false) Integer status,
                              @RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "10") Integer size,
                              Model model) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;
        Page<ViewRoomDTO> roomDTOPage = null;
        if (status == null && floor == null) {
            roomDTOPage = roomService.findByFloorAndStatus(null, 3, defaultPage, defaultSize);
        } else if (status == 0 && floor == null) {
            roomDTOPage = roomService.findByFloorAndStatus(null, null, defaultPage, defaultSize);
        } else {
            roomDTOPage = roomService.findByFloorAndStatus(floor, status, defaultPage, defaultSize);
        }


        List<ViewRoomDTO> viewServiceDTOList = roomDTOPage.getContent();
        System.out.println(viewServiceDTOList);

        model.addAttribute("currentPage", roomDTOPage.getNumber());
        model.addAttribute("pageSize", roomDTOPage.getSize());
        model.addAttribute("totalPages", roomDTOPage.getTotalPages());

        model.addAttribute("floor", floor);
        model.addAttribute("status", status);

        if (viewServiceDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");

            return "roomManager/listRoomManager";
        }

        model.addAttribute("viewServiceDTOList", viewServiceDTOList);
        model.addAttribute("activePage", "/management/viewRoom");

        return "roomManager/listRoomManager";
    }

    @GetMapping("/management/viewRoomType")
    public String getViewRoomType(@RequestParam(name = "categoryId", required = false) Integer categoryId,
                                  @RequestParam(name = "status", required = false) Integer status,
                                  @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  Model model, HttpSession session) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewCategoryDTO> viewCategoryDTOS = roomCategoryService.searchByNameAndStatus(categoryId, status, defaultPage, defaultSize);
        List<RoomCategories> roomCategoriesList = roomCategoryService.getAllRoomCategories();
        List<ViewCategoryDTO> viewCategoryDTOList = viewCategoryDTOS.getContent();

        model.addAttribute("currentPage", viewCategoryDTOS.getNumber());
        model.addAttribute("pageSize", viewCategoryDTOS.getSize());
        model.addAttribute("totalPages", viewCategoryDTOS.getTotalPages());

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);

        if (viewCategoryDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("roomCategoriesList", roomCategoriesList);
            return "roomManager/listRoomType";
        }
        session.setAttribute("viewCategoryDTOList", viewCategoryDTOList);
        model.addAttribute("viewCategoryDTOList", viewCategoryDTOList);
        model.addAttribute("activePage", "/management/viewRoomType");
        model.addAttribute("roomCategoriesList", roomCategoriesList);
        return "roomManager/listRoomType";
    }

    @GetMapping("/management/viewRoomCatgoryPrice")
    public String viewCategoryPrice(@RequestParam(name = "categoryId", required = false) Integer categoryId,
                                    @RequestParam(name = "status", required = false) Integer status,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    Model model, HttpSession session) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 5;

        Page<ViewRoomCategoryPriceDTO> viewRoomCategoryPrices = categoryRoomPriceService.searchByCategoryIdAndFlag(categoryId, status, defaultPage, defaultSize);
        List<RoomCategories> roomCategoriesList = roomCategoryService.getAllRoomCategories();
        List<ViewRoomCategoryPriceDTO> viewRoomCategoryPriceDTOList = viewRoomCategoryPrices.getContent();


        model.addAttribute("currentPage", viewRoomCategoryPrices.getNumber());
        model.addAttribute("pageSize", viewRoomCategoryPrices.getSize());
        model.addAttribute("totalPages", viewRoomCategoryPrices.getTotalPages());

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);

        if (viewRoomCategoryPriceDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("roomCategoriesList", roomCategoriesList);
            return "roomManager/categoryRoomPrice";
        }
        session.setAttribute("viewRoomCategoryPriceDTOList", viewRoomCategoryPriceDTOList);
        model.addAttribute("viewRoomCategoryPriceDTOList", viewRoomCategoryPriceDTOList);
        model.addAttribute("roomCategoriesList", roomCategoriesList);
        model.addAttribute("activePage", "/management/viewRoomCatgoryPrice");

        return "roomManager/categoryRoomPrice";
    }

    @PostMapping("/roomManager/createCategoryRoomPrice")
    public ResponseEntity<?> createCategoryRoomPrice(@RequestBody CreateCategoryRoomPriceDTO dto) {
        try {
            categoryRoomPriceService1.createCategoryRoomPrice(dto);
            String successMessage = "Thêm mới thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exceptionMessage", e.getMessage()));
        }

    }

    @PostMapping("/roomManager/updateStatus")
    public ResponseEntity<?> updateRoomStatus(@RequestBody UpdateStatusRoomDTO data) {
        try {
            Room roomCheck = roomService.findByRoomId(data.getRoomId());
            if (roomCheck.getRoomStatusId() == data.getStatus()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Trạng thái vẫn như cũ."));
            } else {
                roomService.updateRoomByStatusId(data.getStatus(), data.getRoomId());
                String successMessage = "Cập nhật thành công";
                return ResponseEntity.ok().body(Map.of("message", successMessage));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi cập nhật:", "exceptionMessage", e.getMessage()));
        }
    }


}
