package model;

/**
 * Simple Student data class for CSV data access.
 * This class represents a row from student_information.csv.
 */
public class Student {
    private String studentId;
    private String firstName;
    private String lastName;
    private String programId;
    private String email;
    private String recoveryEligibility;

    public Student(String studentId, String firstName, String lastName, String programId, String email, String recoveryEligibility) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.programId = programId;
        this.email = email;
        this.recoveryEligibility = recoveryEligibility;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProgramId() {
        return programId;
    }

    public String getEmail() {
        return email;
    }

    public String getRecoveryEligibility() {
        return recoveryEligibility;
    }
}
