package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import fu.hbs.dto.SaleManagerController.UpdateNewsDTO;
import fu.hbs.dto.SaleManagerController.ViewNewsDTO;
import fu.hbs.entities.New;
import fu.hbs.entities.User;
import fu.hbs.repository.NewsRepository;
import fu.hbs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

	@Mock
	private NewsRepository newsRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private NewsServiceImpl newsService;

	private List<New> sampleNewsList;

	@BeforeEach
	void setUp() {
		sampleNewsList = new ArrayList<>();
		// Populate sample news list as needed for tests
	}

	@Test
    void testGetAllNews1() {
        when(newsRepository.findAll()).thenReturn(sampleNewsList);
        Pageable pageable = PageRequest.of(0, 10);
        Page<New> result = newsService.getAllNews(pageable);

        // Assertions
        assertNotNull(result);
        // Add more assertions based on your logic

        // Verify that the repository methods were called
        verify(newsRepository, times(1)).findAll();

    }

	@Test
	void testSearchByTitle() {
		String searchTitle = "SampleTitle";
		when(newsRepository.findByTitleContaining(searchTitle)).thenReturn(sampleNewsList);
		Pageable pageable = PageRequest.of(0, 10);
		Page<New> result = newsService.searchByTitle(searchTitle, pageable);
		// Add assertions based on your requirements
		assertNotNull(result);
	}

	@Test
    void testSearchByTitleWhenListSizeIsLessThanStartItem() {
        // Mocking the repository
        when(newsRepository.findByTitleContaining(eq("Title"))).thenReturn(Collections.singletonList(createNew()));

        // Calling the service method
        Page<New> result = newsService.searchByTitle("Title", PageRequest.of(1, 10));

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

	@Test
	void testFindById_WhenNewsExists() {
		// Mock data
		Long newsId = 1L;
		New expectedNews = new New(/* your constructor parameters here */);

		when(newsRepository.findById(newsId)).thenReturn(Optional.of(expectedNews));

		// Call the method
		New result = newsService.findById(newsId);

		// Assertions
		assertNotNull(result);
		assertEquals(expectedNews, result);

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findById(newsId);
	}

	@Test
	void testFindById_WhenNewsDoesNotExist() {
		// Mock data
		Long newsId = 2L;

		when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

		// Call the method
		New result = newsService.findById(newsId);

		// Assertions
		assertNull(result);

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findById(newsId);
	}

	@Test
	void testFindByUserId() {
		// Mock data
		Long userId = 1L;
		List<New> expectedNewsList = Collections.singletonList(new New(/* constructor parameters here */));

		when(newsRepository.findByUserId(userId)).thenReturn(expectedNewsList);

		// Call the method
		List<New> result = newsService.findByUserId(userId);

		// Assertions
		assertNotNull(result);
		assertEquals(expectedNewsList, result);

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findByUserId(userId);
	}

	@Test
	void testGetAllNews() {
		// Mock data
		List<New> expectedNewsList = Collections.singletonList(new New(/* constructor parameters here */));

		when(newsRepository.findAll()).thenReturn(expectedNewsList);

		// Call the method
		List<New> result = newsService.getAllNews();

		// Assertions
		assertNotNull(result);
		assertEquals(expectedNewsList, result);

		// Verify that the repository method was called
		verify(newsRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("testGetNewsByIdUTCID01")
	void testGetNewsById_WhenNewsExists() {
		// Mock data
		Long newsId = 1L;
		New expectedNews = new New(/* constructor parameters here */);

		when(newsRepository.findById(newsId)).thenReturn(Optional.of(expectedNews));

		// Call the method
		New result = newsService.getNewsById(newsId);

		// Assertions
		assertNotNull(result);
		assertEquals(expectedNews, result);

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findById(newsId);
	}

	@Test
	@DisplayName("testGetNewsByIdUTCID01")
	void testGetNewsById_WhenNewsDoesNotExist() {
		// Mock data
		Long newsId = 2L;

		when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

		// Call the method
		New result = newsService.getNewsById(newsId);

		// Assertions
		assertNull(result);

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findById(newsId);
	}

	@Test
	@DisplayName("testCreateNewsUTCID01")
	void testCreateNews() {
		UpdateNewsDTO updateNewsDTO = new UpdateNewsDTO();
		// Set properties of updateNewsDTO as needed
		newsService.createNews(updateNewsDTO);
		// Add assertions based on your requirements
		verify(newsRepository, times(1)).save(any(New.class));
	}

	@Test
	void testUpdateNews() {
		UpdateNewsDTO updateNewsDTO = new UpdateNewsDTO();
		// Set properties of updateNewsDTO as needed
		when(newsRepository.findByNewId(updateNewsDTO.getNewId())).thenReturn(new New());
		newsService.updateNews(updateNewsDTO);
		// Add assertions based on your requirements
		verify(newsRepository, times(1)).save(any(New.class));
	}

	@Test
	@DisplayName("Test findByNewsId")
	public void testFindByNewsId() {
		// Mock data
		Long newsId = 1L;
		New mockNew = new New();
		mockNew.setNewId(newsId);
		// Mock repository response
		when(newsRepository.findByNewId(newsId)).thenReturn(mockNew);

		// Call the method
		New result = newsService.findByNewsId(newsId);

		// Verify the result
		assertNotNull(result);
		assertEquals(newsId, result.getNewId());

		// Verify that the repository method was called with the correct argument
		verify(newsRepository, times(1)).findByNewId(newsId);
	}

	@Test
	void testSearchByNameAndStatus1() {
		// Mocking the repository
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<New> mockedNewPage = new PageImpl<>(Collections.singletonList(createNew()));
		when(newsRepository.searchByNameAndStatus(eq("Name"), eq(1), eq(pageRequest))).thenReturn(mockedNewPage);

		// Mocking the userRepository
		when(userRepository.findByUserId(anyLong())).thenReturn(createUser());

		// Calling the service method
		Page<ViewNewsDTO> result = newsService.searchByNameAndStatus("Name", 1, 0, 10);

		// Verifying interactions
		verify(newsRepository, times(1)).searchByNameAndStatus(eq("Name"), eq(1), eq(pageRequest));
		verify(userRepository, times(1)).findByUserId(anyLong());

		// Assertions
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
	}

	private New createNew() {
		New news = new New();
		news.setUserId(1L);
		news.setTitle("Title");
		news.setDescriptions("Description");
		news.setUserId(1L);
		return news;
	}

	private User createUser() {
		User user = new User();
		user.setUserId(1L);
		user.setName("Username");
		// Set other user properties as needed
		return user;
	}
}
