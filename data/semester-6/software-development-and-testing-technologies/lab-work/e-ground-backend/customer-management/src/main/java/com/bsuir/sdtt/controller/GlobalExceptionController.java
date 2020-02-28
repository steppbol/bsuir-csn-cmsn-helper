package com.bsuir.sdtt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

/**
 * Class of Controller Advice.
 *
 * @author Stsiapan Balashenka, Eugene Korenik
 * @version 1.1
 */
@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionController {
    private static final String ERROR_PREFIX = "ERROR: ";


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    private String handleEntityNotFoundException
            (EntityNotFoundException exception) {
        return errorBuilderMessage(exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    private String handleException(Exception exception) {
        return errorBuilderMessage(exception);
    }


    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private String handleEntityExistsExcpetion(EntityExistsException exception) {
        return errorBuilderMessage(exception);
    }

    private String errorBuilderMessage(Exception exception) {
        log.error("ERROR: ", exception);
        return ERROR_PREFIX + exception.getMessage();
    }
}
