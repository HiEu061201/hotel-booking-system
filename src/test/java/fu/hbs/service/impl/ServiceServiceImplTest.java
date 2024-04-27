package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import fu.hbs.entities.RoomService;
import fu.hbs.repository.RoomServiceRepository;

@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @InjectMocks
    private ServiceServiceImpl serviceService;

    @Mock
    private RoomServiceRepository serviceRepository;

    @Mock
    private Pageable pageable;

    @Test
    void getAllServices_PaginateListReturnsSubset_ReturnsPageWithSubset() {
        // Mock data
        List<RoomService> allServices = Arrays.asList(new RoomService(), new RoomService(), new RoomService());
        Page<RoomService> expectedPage = new PageImpl<>(allServices, pageable, allServices.size());

        // Mock behavior
        when(serviceRepository.findAll()).thenReturn(allServices);
        when(pageable.getPageSize()).thenReturn(allServices.size()); // Set page size to match the data size
        when(pageable.getPageNumber()).thenReturn(0);

        // Call the method
        Page<RoomService> result = serviceService.getAllServices(pageable);

        // Assertions
        assertEquals(expectedPage, result);
    }


    @Test
    void searchByName_PaginateListReturnsSubset_ReturnsPageWithSubset() {
        // Mock data
        String serviceName = "Service";
        List<RoomService> filteredServices = Arrays.asList(new RoomService(), new RoomService(), new RoomService());
        Page<RoomService> expectedPage = new PageImpl<>(filteredServices, pageable, filteredServices.size());

        // Mock behavior
        when(serviceRepository.findByServiceNameContaining(serviceName)).thenReturn(filteredServices);
        when(pageable.getPageSize()).thenReturn(filteredServices.size()); // Set page size to match the data size
        when(pageable.getPageNumber()).thenReturn(0);

        // Call the method
        Page<RoomService> result = serviceService.searchByName(serviceName, pageable);

        // Assertions
        assertEquals(expectedPage, result);
    }


    @Test
    void searchByName_ListSizeLessThanStartItem_ReturnsEmptyPage() {
        // Mock data
        String serviceName = "Service";
        List<RoomService> filteredServices = Arrays.asList(new RoomService(), new RoomService()); // Assuming size is 2

        // Mock behavior
        when(serviceRepository.findByServiceNameContaining(serviceName)).thenReturn(filteredServices);

        // Set pageable settings
        when(pageable.getPageSize()).thenReturn(5); // Set a larger page size
        when(pageable.getPageNumber()).thenReturn(1); // Set a page number where startItem will be greater than list.size()

        // Call the method
        Page<RoomService> result = serviceService.searchByName(serviceName, pageable);

        // Assertions
        assertTrue(result.isEmpty());
    }


    @Test
    void findById_ServiceExists_ReturnsService() {
        // Mock data
        Long serviceId = 1L;
        RoomService expectedService = new RoomService();

        // Mock behavior
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(expectedService));

        // Call the method
        RoomService result = serviceService.findById(serviceId);

        // Assertions
        assertEquals(expectedService, result);
    }

    @Test
    void findById_ServiceDoesNotExist_ReturnsNull() {
        // Mock data
        Long serviceId = 1L;

        // Mock behavior
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Call the method
        RoomService result = serviceService.findById(serviceId);

        // Assertions
        assertNull(result);
    }

    @Test
    void getAllServices_ReturnsAllServices() {
        // Mock data
        List<RoomService> allServices = Arrays.asList(new RoomService(), new RoomService(), new RoomService());

        // Mock behavior
        when(serviceRepository.findAll()).thenReturn(allServices);

        // Call the method
        List<RoomService> result = serviceService.getAllServices();

        // Assertions
        assertEquals(allServices.size(), result.size());
        assertEquals(allServices, result);
    }

    @Test
    void getAllServicesByStatus_ReturnsFilteredServices() {
        // Mock data
        Boolean status = true;
        List<RoomService> filteredServices = Arrays.asList(new RoomService(), new RoomService());

        // Mock behavior
        when(serviceRepository.findAllByStatusIs(status)).thenReturn(filteredServices);

        // Call the method
        List<RoomService> result = serviceService.getAllServicesByStatus(status);

        // Assertions
        assertEquals(filteredServices, result);
    }

    // Additional test cases can be added as needed

}
