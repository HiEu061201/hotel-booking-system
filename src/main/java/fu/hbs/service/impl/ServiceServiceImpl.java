/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 20/10/2023    1.0        HieuDT          First Deploy
 * 23/10/2023	 2.0		HieuDT			Update
 */
package fu.hbs.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import fu.hbs.entities.RoomService;
import fu.hbs.repository.RoomServiceRepository;
import fu.hbs.service.interfaces.ServiceService;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private RoomServiceRepository serviceRepository;

    @Override
    public Page<RoomService> getAllServices(Pageable pageable) {
        List<RoomService> allServices = serviceRepository.findAll(); // Lấy toàn bộ dịch vụ
        return paginateList(allServices, pageable); // Phân trang danh sách toàn bộ dịch vụ
    }

    @Override
    public Page<RoomService> searchByName(String serviceName, Pageable pageable) {
        List<RoomService> filteredServices = serviceRepository.findByServiceNameContaining(serviceName); // Tìm kiếm
        // trong
        // danh sách
        // dịch vụ
        return paginateList(filteredServices, pageable); // Phân trang kết quả tìm kiếm
    }

    // Phân trang danh sách
    private Page<RoomService> paginateList(List<RoomService> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<RoomService> pageList;

        if (list.size() < startItem) {
            pageList = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageList = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageList, pageable, list.size());
    }

    @Override
    public RoomService findById(Long serviceId) {
        Optional<RoomService> service = serviceRepository.findById(serviceId);
        return service.orElse(null);
    }

    @Override
    public List<RoomService> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<RoomService> getAllServicesByStatus(Boolean status) {
        return serviceRepository.findAllByStatusIs(status);
    }
}
