package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Tuple<E> {

    @JsonProperty("first")
    private E first;

    @JsonProperty("second")
    private E second;

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
    public Tuple(E first, E second) {
        this.first = first;
        this.second = second;
    }

    public E getFirst() {
        return first;
    }

    public void setFirst(E first) {
        this.first = first;
    }

    public E getSecond() {
        return second;
    }

    public void setSecond(E second) {
        this.second = second;
    }

    /**
     * Verify if tuple contains an entity
     *
     * @param entity The entity that is checked
     * @return True / False
     */
    public boolean hasEntity(E entity) {
        return first.equals(entity) || second.equals(entity);
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
        Tuple<?> other = (Tuple<?>) obj;
        return first.equals(other.first) && second.equals(other.second) || first.equals(other.second) && second.equals(other.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}