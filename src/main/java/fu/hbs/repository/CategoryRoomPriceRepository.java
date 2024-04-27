package fu.hbs.repository;


import fu.hbs.entities.RoomCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.hbs.entities.CategoryRoomPrice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CategoryRoomPriceRepository extends JpaRepository<CategoryRoomPrice, Long> {
    CategoryRoomPrice findByRoomCategoryId(Long id);

    CategoryRoomPrice findByRoomCategoryIdAndFlag(Long id, int flag);

    CategoryRoomPrice findByRoomPriceId(Long id);


    @Query(value = "select * from category_room_price \n" +
            "where room_category_id = ?1 AND flag = 1 \n" +
            "order by  start_date \n", nativeQuery = true)
    CategoryRoomPrice getCategoryId(Long id);


    @Query(value = "SELECT *\n" +
            "FROM category_room_price\n" +
            "WHERE \n" +
            "  (?1 IS NULL OR room_category_id = COALESCE(?1, room_category_id))\n" +
            "  AND \n" +
            "  (?2 IS NULL OR flag = COALESCE(?2, flag));", nativeQuery = true)
    Page<CategoryRoomPrice> searchByCategoryIdAndFlag(Integer categoryId, Integer flag, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE  hotelsystem.category_room_price " +
            "SET flag = 0 " +
            "where room_category_id = ?1", nativeQuery = true)
    void updateFlag(Long roomCategoryId);
}
