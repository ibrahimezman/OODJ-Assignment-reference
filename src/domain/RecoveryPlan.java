package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The RecoveryPlan class represents a recovery plan for a student in a specific course.
 * A recovery plan contains information about the student, course, recommendations,
 * and a list of milestones the student needs to complete to recover their academic standing.
 * This is a simple data model class that follows the style of other domain classes.
 */
public class RecoveryPlan
{
    // The unique identifier of the student this plan is for (e.g., "S001")
    private String studentId;
    
    // The unique identifier of the course this recovery plan is for (e.g., "C201")
    private String courseId;
    
    // A recommendation or description of what the student should focus on
    private String recommendation;
    
    // The current status of this recovery plan (e.g., "Active", "Completed", "Cancelled")
    private String status;
    
    // A list of milestones that make up this recovery plan
    private List<Milestone> milestones;

    /**
     * Constructor to create a new RecoveryPlan with all required attributes.
     * Initializes an empty list of milestones that can be added to later.
     * 
     * @param studentId      The student's unique identifier
     * @param courseId       The course's unique identifier
     * @param recommendation The recommendation for the student
     * @param status         The current status of the plan
     */
    public RecoveryPlan(String studentId, String courseId, String recommendation, String status)
    {
        this.studentId = studentId;
        this.courseId = courseId;
        this.recommendation = recommendation;
        this.status = status;
        // Initialize an empty ArrayList to hold milestones
        this.milestones = new ArrayList<>();
    }

    /**
     * Gets the student ID for this recovery plan.
     * 
     * @return The student's unique identifier
     */
    public String getStudentId()
    {
        return studentId;
    }

    /**
     * Sets the student ID for this recovery plan.
     * 
     * @param studentId The new student ID to set
     */
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    /**
     * Gets the course ID for this recovery plan.
     * 
     * @return The course's unique identifier
     */
    public String getCourseId()
    {
        return courseId;
    }

    /**
     * Sets the course ID for this recovery plan.
     * 
     * @param courseId The new course ID to set
     */
    public void setCourseId(String courseId)
    {
        this.courseId = courseId;
    }

    /**
     * Gets the recommendation for this recovery plan.
     * 
     * @return The recommendation string
     */
    public String getRecommendation()
    {
        return recommendation;
    }

    /**
     * Sets the recommendation for this recovery plan.
     * 
     * @param recommendation The new recommendation to set
     */
    public void setRecommendation(String recommendation)
    {
        this.recommendation = recommendation;
    }

    /**
     * Gets the current status of this recovery plan.
     * 
     * @return The status string (e.g., "Active", "Completed")
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status of this recovery plan.
     * 
     * @param status The new status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Gets the list of milestones for this recovery plan.
     * 
     * @return The list of Milestone objects
     */
    public List<Milestone> getMilestones()
    {
        return milestones;
    }

    /**
     * Sets the list of milestones for this recovery plan.
     * 
     * @param milestones The new list of milestones to set
     */
    public void setMilestones(List<Milestone> milestones)
    {
        this.milestones = milestones;
    }

    /**
     * Adds a single milestone to this recovery plan.
     * This is a convenience method to add milestones one at a time.
     * 
     * @param milestone The milestone to add to the plan
     */
    public void addMilestone(Milestone milestone)
    {
        this.milestones.add(milestone);
    }

    /**
     * Returns a string representation of this RecoveryPlan.
     * Includes all fields for easy debugging and display.
     * 
     * @return A formatted string with all plan details
     */
    @Override
    public String toString()
    {
        return "RecoveryPlan{" +
                "studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", recommendation='" + recommendation + '\'' +
                ", status='" + status + '\'' +
                ", milestones=" + milestones +
                '}';
    }
}
