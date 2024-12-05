package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Tuple<E1, E2> {

    @JsonProperty("first")
    private E1 first;

    @JsonProperty("second")
    private E2 second;

    /**
     * Default constructor for JSON
     */
    private Tuple() {
    } // I NEED THIS FOR JSON TO WORK

    /**
     * The constructor
     *
     * @param first  the first element of the tuple
     * @param second the second element of the tuple
     */
    public Tuple(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }

    public E1 getFirst() {
        return first;
    }

    public void setFirst(E1 first) {
        this.first = first;
    }

    public E2 getSecond() {
        return second;
    }

    public void setSecond(E2 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Tuple<?, ?> other = (Tuple<?, ?>) obj;
        return first.equals(other.first) && second.equals(other.second) || first.equals(other.second) && second.equals(other.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}