package service;

/**
 * FailedStudent is a helper class that represents a student who has failed
 * a component of a course and needs recovery.
 * 
 * This class contains:
 * - StudentID: The unique identifier for the student
 * - CourseID: The course in which the student failed
 * - FailedComponent: The component they failed (e.g., "Exam" or "Assignment")
 */
public class FailedStudent {
    
    private String studentId;
    private String courseId;
    private String failedComponent;
    
    /**
     * Constructor for FailedStudent.
     * 
     * @param studentId The unique identifier for the student
     * @param courseId The course in which the student failed
     * @param failedComponent The component they failed (e.g., "Exam" or "Assignment")
     */
    public FailedStudent(String studentId, String courseId, String failedComponent) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.failedComponent = failedComponent;
    }
    
    /**
     * Gets the student ID.
     * @return The student ID
     */
    public String getStudentId() {
        return studentId;
    }
    
    /**
     * Gets the course ID.
     * @return The course ID
     */
    public String getCourseId() {
        return courseId;
    }
    
    /**
     * Gets the failed component.
     * @return The failed component (e.g., "Exam" or "Assignment")
     */
    public String getFailedComponent() {
        return failedComponent;
    }
    
    @Override
    public String toString() {
        return "FailedStudent{" +
                "studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", failedComponent='" + failedComponent + '\'' +
                '}';
    }
}
