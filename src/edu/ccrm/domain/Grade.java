package edu.ccrm.domain;

public enum Grade {
    S(10.0, "Outstanding"),
    A(9.0, "Excellent"),
    B(8.0, "Very Good"),
    C(7.0, "Good"),
    D(6.0, "Average"),
    E(5.0, "Pass"),
    F(0.0, "Fail");

    private final double gradePoint;
    private final String description;

    Grade(double gradePoint, String description) {
        this.gradePoint = gradePoint;
        this.description = description;
    }

    public double getGradePoint() { return gradePoint; }
    public String getDescription() { return description; }

    public static Grade fromMarks(double marks) {
        if (marks >= 90) return S;
        else if (marks >= 80) return A;
        else if (marks >= 70) return B;
        else if (marks >= 60) return C;
        else if (marks >= 50) return D;
        else if (marks >= 40) return E;
        else return F;
    }

    @Override
    public String toString() {
        return name() + " (" + description + ")";
    }
}
