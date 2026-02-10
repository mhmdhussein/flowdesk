//package com.flowdesk.flowdesk.api.error;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpServletRequest request
//    ) {
//        List<ApiError.FieldViolation> violations = new ArrayList<>();
//        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
//            violations.add(new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()));
//        }
//
//        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiError> handleConstraintViolation(
//            ConstraintViolationException ex,
//            HttpServletRequest request
//    ) {
//        List<ApiError.FieldViolation> violations = ex.getConstraintViolations().stream()
//                .map(v -> new ApiError.FieldViolation(v.getPropertyPath().toString(), v.getMessage()))
//                .toList();
//
//        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiError> handleIllegalArgument(
//            IllegalArgumentException ex,
//            HttpServletRequest request
//    ) {
//        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
//    }
//
//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity<ApiError> handleResponseStatus(
//            ResponseStatusException ex,
//            HttpServletRequest request
//    ) {
//        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
//        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
//        return build(status, message, request, List.of());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleGeneric(
//            Exception ex,
//            HttpServletRequest request
//    ) {
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, List.of());
//    }
//
//    private ResponseEntity<ApiError> build(
//            HttpStatus status,
//            String message,
//            HttpServletRequest request,
//            List<ApiError.FieldViolation> violations
//    ) {
//        String requestId = Optional.ofNullable(request.getHeader("X-Request-Id")).orElse(null);
//
//        ApiError body = new ApiError(
//                Instant.now(),
//                status.value(),
//                status.getReasonPhrase(),
//                message,
//                request.getRequestURI(),
//                requestId,
//                violations == null || violations.isEmpty() ? null : violations
//        );
//
//        return ResponseEntity.status(status).body(body);
//    }
//}
