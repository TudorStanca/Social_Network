package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity<ID> {

    @JsonProperty("id")
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
