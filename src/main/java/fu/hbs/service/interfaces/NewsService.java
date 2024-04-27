package fu.hbs.service.interfaces;


import java.util.List;

import fu.hbs.dto.SaleManagerController.ViewNewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fu.hbs.dto.SaleManagerController.UpdateNewsDTO;
import fu.hbs.entities.New;

public interface NewsService {
    Page<New> searchByTitle(String newsTitle, Pageable pageable);

    Page<New> getAllNews(Pageable pageable);

    New findById(Long newsId);

    List<New> findByUserId(Long UserId);

    List<New> getAllNews();

    New getNewsById(Long newsId);

    void createNews(UpdateNewsDTO dto);

    fu.hbs.entities.New findByNewsId(Long newId);

    void updateNews(UpdateNewsDTO updateNewsDTO);


    Page<ViewNewsDTO> searchByNameAndStatus(String name, Integer status, int page, int size);
}

