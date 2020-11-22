package com.oudersamir.exception;

import javax.servlet.http.HttpServletRequest;

import org.omg.CORBA.portable.UnknownException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.oudersamir.dto.BusinessResourceExceptionDTO;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)

public class GlobalHandlerControllerException  {



    @Order(value = 100)
    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<BusinessResourceExceptionDTO>  HandlerException(MethodArgumentNotValidException ex,WebRequest request ){
        System.out.println(" bad parameter");
        Map<String,String> errors =new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(er->{
            errors.put(er.getField(), er.getDefaultMessage());
        });

        BusinessResourceExceptionDTO businessResourceExceptionDTO = new BusinessResourceExceptionDTO();
        businessResourceExceptionDTO.setStatus(HttpStatus.BAD_REQUEST);
        businessResourceExceptionDTO.setErrorCode("404");
        businessResourceExceptionDTO.setErrorMessage(errors.values().toString());
        businessResourceExceptionDTO.setRequestURL(((ServletWebRequest)request).getRequest().getRequestURI().toString());
        return new ResponseEntity<BusinessResourceExceptionDTO>(businessResourceExceptionDTO,new HttpHeaders(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessResourceException.class)
    public ResponseEntity<BusinessResourceExceptionDTO> businessResourceError(HttpServletRequest req, BusinessResourceException ex) {
        BusinessResourceExceptionDTO businessResourceExceptionDTO = new BusinessResourceExceptionDTO();
        businessResourceExceptionDTO.setStatus(ex.getStatus());
        businessResourceExceptionDTO.setErrorCode(ex.getErrorCode());
        businessResourceExceptionDTO.setErrorMessage(ex.getMessage());
        businessResourceExceptionDTO.setRequestURL(req.getRequestURL().toString());
        return new ResponseEntity<BusinessResourceExceptionDTO>(businessResourceExceptionDTO, ex.getStatus());
    }
    @ExceptionHandler(Exception.class)//toutes les autres erreurs non gérées par le service sont interceptées ici
    public ResponseEntity<BusinessResourceExceptionDTO> unknowError(HttpServletRequest req, Exception ex) {
        BusinessResourceExceptionDTO response = new BusinessResourceExceptionDTO();
        response.setErrorCode("Technical Error");
        response.setErrorMessage(ex.getMessage());
        response.setRequestURL(req.getRequestURL().toString());
        return new ResponseEntity<BusinessResourceExceptionDTO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
