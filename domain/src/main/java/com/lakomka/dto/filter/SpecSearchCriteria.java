package com.lakomka.dto.filter;

public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        super();

        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }


    public SpecSearchCriteria(boolean orPredicate, String key, SearchOperation op, Object value) {
        this.orPredicate = orPredicate;
        this.key = key;
        this.operation = op;
        this.value = value;
    }

    public boolean isOrPredicate() {
        return orPredicate;
    }

    public void setOrPredicate(boolean orPredicate) {
        this.orPredicate = orPredicate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public void setOperation(SearchOperation operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}