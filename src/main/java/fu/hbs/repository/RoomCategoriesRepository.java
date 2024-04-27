package fu.hbs.repository;

import java.util.List;

import fu.hbs.entities.Room;
import fu.hbs.entities.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.hbs.entities.RoomCategories;

@Repository
public interface RoomCategoriesRepository extends JpaRepository<RoomCategories, Long> {

    RoomCategories findByRoomCategoryId(Long id);

    List<RoomCategories> findByNumberPersonGreaterThanEqual(int numberOfPeople);

    RoomCategories findDistinctByRoomCategoryId(Long id);


    RoomCategories deleteByRoomCategoryId(Long id);


    @Query(value = "SELECT *\n" +
            "FROM room_categories\n" +
            "WHERE \n" +
            "  (?1 IS NULL OR room_category_id = COALESCE(?1, room_category_id))\n" +
            "  AND \n" +
            "  (?2 IS NULL OR status = COALESCE(?2, status));", nativeQuery = true)
    Page<RoomCategories> searchByNameAndStatus(Integer categoryId, Integer status, Pageable pageable);

}
