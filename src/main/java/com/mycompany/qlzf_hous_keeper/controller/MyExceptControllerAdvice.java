package com.mycompany.qlzf_hous_keeper.controller;

import com.mycompany.qlzf_hous_keeper.QlzfHousKeeperApplication;
import com.mycompany.qlzf_hous_keeper.tools.OutData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 */
@RestControllerAdvice
public class MyExceptControllerAdvice {
    private static Logger logger = LoggerFactory.getLogger(MyExceptControllerAdvice.class);
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Object errorHandler(BindException ex) {
        logger.error("参数异常",ex);
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        return OutData.FormatBad(allErrors.get(allErrors.size() - 1).getDefaultMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()) {
            ConstraintViolation<?> next = iterator.next();
            return OutData.FormatBad(next.getMessage());
        }
        return OutData.FormatBad("系统异常");
    }

}
