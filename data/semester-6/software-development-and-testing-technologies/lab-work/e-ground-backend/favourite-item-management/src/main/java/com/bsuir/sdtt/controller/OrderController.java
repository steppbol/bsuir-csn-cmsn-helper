package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.dto.OrderDTO;
import com.bsuir.sdtt.entity.Order;
import com.bsuir.sdtt.service.OrderService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of Order REST Controller. Contains CRUD methods.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "api/v1/inventory/orders")
public class OrderController {
    /**
     * Field of Order Service.
     */
    private final OrderService orderService;

    /**
     * Field of Model Mapper converter.
     */
    private ModelMapper modelMapper;

    /**
     * Constructor that accepts a object OrderService class.
     *
     * @param orderService object of OrderService class
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        this.modelMapper = new ModelMapper();
    }

    /**
     * Method that converts DTO to class object and create it.
     *
     * @param orderDTO data transfer object
     * @return created object of Order class
     */
    @PostMapping
    public OrderDTO create(@ApiParam(name = "date", value = "Example date: 2018-12-12T15:15:15") @Validated @RequestBody OrderDTO orderDTO) {
        log.debug("In create method order controller");
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        OrderDTO orderDTOTemp = new OrderDTO();
        modelMapper.map(orderService.create(order), orderDTOTemp);
        return orderDTOTemp;
    }

    /**
     * Method that save updated object.
     *
     * @param orderDTO updated order that needs to save
     * @return updated and saved order
     */
    @PutMapping
    public OrderDTO update(@Validated @RequestBody OrderDTO orderDTO) {
        log.debug("In update method order controller");
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        OrderDTO orderDTOTemp = new OrderDTO();
        modelMapper.map(orderService.update(order), orderDTOTemp);
        return orderDTOTemp;
    }

    /**
     * Method that finds an object.
     *
     * @param id UUID of the object to be found
     * @return founded object or NullPointerException
     */
    @GetMapping(path = "/{id}")
    public OrderDTO getById(@PathVariable("id") UUID id) {
        log.debug("In getById method order controller");
        OrderDTO orderDTOTemp = new OrderDTO();
        modelMapper.map(orderService.findById(id), orderDTOTemp);
        return orderDTOTemp;
    }

    @GetMapping(path = "/customers/{customerId}")
    public List<OrderDTO> getByIdCustomerId(@PathVariable("customerId") UUID customerId) {
        log.debug("In getByIdCustomerId method order controller");
        List<OrderDTO> orderDTOTemp = new ArrayList<>();
        toOrdersDTOList(orderService.findByCustomerId(customerId), orderDTOTemp);
        return orderDTOTemp;
    }

    /**
     * Method that finds all objects.
     *
     * @return founded objects
     */
    @GetMapping
    public List<OrderDTO> getAll() {
        log.debug("In getALl method order controller");
        List<OrderDTO> ordersDTOTemp = new ArrayList<>();
        List<Order> orders = orderService.findAll();
        toOrdersDTOList(orders, ordersDTOTemp);
        return ordersDTOTemp;
    }

    /**
     * Method that delete object.
     *
     * @param id object that needs to delete
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") UUID id) {

        log.debug("In delete method order controller");
        orderService.delete(id);
    }

    private void toOrdersDTOList(List<Order> orders, List<OrderDTO> ordersDto) {
        for (Order order : orders) {
            OrderDTO orderDto = new OrderDTO();
            modelMapper.map(order, orderDto);
            ordersDto.add(orderDto);
        }
    }
}
