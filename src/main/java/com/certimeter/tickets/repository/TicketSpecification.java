package com.certimeter.tickets.repository;

import com.certimeter.tickets.model.Ticket;
import com.certimeter.tickets.enumeration.MatchMode;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class TicketSpecification {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Specification<Ticket> matchMode(String field, String value, MatchMode matchMode) {
        switch (matchMode) {
            case STARTS_WITH:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), value + "%");
            case CONTAINS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value + "%");
            case NOT_CONTAINS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(root.get(field), "%" + value + "%");
            case ENDS_WITH:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value);
            case EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value);
            case NOT_EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), value);
            case DATE_BEFORE:
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
            case DATE_AFTER:
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
            case DATE_IS:
                LocalDateTime dateTime = LocalDate.parse(value, formatter).atStartOfDay();
                return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(field), dateTime, dateTime.plusDays(1));
            case DATE_IS_NOT:
                LocalDateTime dateTimeNot = LocalDate.parse(value, formatter).atStartOfDay();
                return (root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.between(root.get(field), dateTimeNot, dateTimeNot.plusDays(1)));
            default:
                return null;
        }
    }

    public static Specification<Ticket> matchModeInJoin(String joinField, String field, String value, MatchMode matchMode) {
        return (root, query, criteriaBuilder) -> {
            Join<Ticket, ?> join = root.join(joinField);
            switch (matchMode) {
                case STARTS_WITH:
                    return criteriaBuilder.like(join.get(field), value + "%");
                case CONTAINS:
                    return criteriaBuilder.like(join.get(field), "%" + value + "%");
                case NOT_CONTAINS:
                    return criteriaBuilder.notLike(join.get(field), "%" + value + "%");
                case ENDS_WITH:
                    return criteriaBuilder.like(join.get(field), "%" + value);
                case EQUALS:
                    return criteriaBuilder.equal(join.get(field), value);
                case NOT_EQUALS:
                    return criteriaBuilder.notEqual(join.get(field), value);
                case DATE_BEFORE:
                    return criteriaBuilder.lessThan(join.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
                case DATE_AFTER:
                    return criteriaBuilder.greaterThan(join.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
                case DATE_IS:
                    return criteriaBuilder.equal(join.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
                case DATE_IS_NOT:
                    return criteriaBuilder.notEqual(join.get(field), Timestamp.valueOf(LocalDate.parse(value, formatter).atStartOfDay()));
                default:
                    return criteriaBuilder.conjunction();
            }
        };
    }
}