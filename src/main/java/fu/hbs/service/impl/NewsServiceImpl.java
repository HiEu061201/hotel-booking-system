package fu.hbs.service.impl;

import fu.hbs.dto.SaleManagerController.ViewNewsDTO;
import fu.hbs.entities.User;
import fu.hbs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fu.hbs.dto.SaleManagerController.UpdateNewsDTO;
import fu.hbs.entities.New;
import fu.hbs.repository.NewsRepository;
import fu.hbs.service.interfaces.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<New> getAllNews(Pageable pageable) {
        List<New> allNews = newsRepository.findAll(); // Lấy toàn bộ tin tức
        return paginateList(allNews, pageable); // Phân trang danh sách toàn bộ tin tức
    }

    @Override
    public Page<New> searchByTitle(String newsTitle, Pageable pageable) {
        List<New> filteredNews = newsRepository.findByTitleContaining(newsTitle); // Tìm kiếm trong danh sách tin tức
        return paginateList(filteredNews, pageable); // Phân trang kết quả tìm kiếm
    }

    // Phân trang danh sách
    private Page<New> paginateList(List<New> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<New> pageList;

        if (list.size() < startItem) {
            pageList = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageList = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageList, pageable, list.size());
    }

    @Override
    public New findById(Long newsId) {
        Optional<New> news = newsRepository.findById(newsId);
        return news.orElse(null);
    }

    @Override
    public List<New> findByUserId(Long UserId) {
        List<New> news = (List<New>) newsRepository.findByUserId(UserId);
        return news;
    }

    @Override
    public List<New> getAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public New getNewsById(Long newsId) {
        Optional<New> newsOptional = newsRepository.findById(newsId);
        return newsOptional.orElse(null);
    }

    @Override
    public void createNews(UpdateNewsDTO dto) {
        fu.hbs.entities.New news = new fu.hbs.entities.New();
        news.setTitle(dto.getTitle());
        news.setUserId(dto.getUserId());
        news.setDescriptions(dto.getDescriptions());
        news.setImage(dto.getImage());
        news.setStatus(dto.getStatus());
        news.setDate(dto.getDate());

        newsRepository.save(news);

    }

    @Override
    public fu.hbs.entities.New findByNewsId(Long newId) {
        return newsRepository.findByNewId(newId);
    }

    @Override
    public void updateNews(UpdateNewsDTO updateNewsDTO) {

        fu.hbs.entities.New news = newsRepository
                .findByNewId(updateNewsDTO.getNewId());
        if (news != null) {
            news.setDescriptions(updateNewsDTO.getDescriptions());
            news.setTitle(updateNewsDTO.getTitle());
            news.setNewId(updateNewsDTO.getNewId());
            news.setImage(updateNewsDTO.getImage());
            news.setStatus(updateNewsDTO.getStatus());
            newsRepository.save(news);
        }
    }

    @Override
    public Page<ViewNewsDTO> searchByNameAndStatus(String name, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<New> newPage = newsRepository.searchByNameAndStatus(name, status, pageRequest);

        return newPage.map(aNew -> {
            ViewNewsDTO viewNewsDTO = new ViewNewsDTO();
            User user = userRepository.findByUserId(aNew.getUserId());
            viewNewsDTO.setUser(user);
            viewNewsDTO.setANew(aNew);
            return viewNewsDTO;
        });
    }
}
