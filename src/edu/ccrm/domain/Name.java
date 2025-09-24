package edu.ccrm.domain;

import java.util.Objects;

public final class Name {
    private final String firstName;
    private final String lastName;
    private final String fullName;

    public Name(String firstName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null").trim();
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null").trim();
        this.fullName = this.firstName + " " + this.lastName;


        assert !this.firstName.isEmpty() : "First name cannot be empty";
        assert !this.lastName.isEmpty() : "Last name cannot be empty";
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Name name = (Name) obj;
        return Objects.equals(firstName, name.firstName) &&
                Objects.equals(lastName, name.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
