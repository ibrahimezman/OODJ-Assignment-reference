package domain;

/**
 * The Milestone class represents a single task within a student's recovery plan.
 * Each milestone has a week, a task description, and a status to track progress.
 * This is a simple data model class that follows the style of other domain classes.
 */
public class Milestone
{
    // The week number or identifier for when this milestone should be completed (e.g., "Week 1")
    private String week;
    
    // A description of the task the student needs to complete (e.g., "Complete Chapter 1 exercises")
    private String task;
    
    // The current status of the milestone (e.g., "Pending", "In Progress", "Completed")
    private String status;

    /**
     * Constructor to create a new Milestone with all required attributes.
     * 
     * @param week   The week identifier for this milestone
     * @param task   The task description for what the student needs to do
     * @param status The current status of this milestone
     */
    public Milestone(String week, String task, String status)
    {
        this.week = week;
        this.task = task;
        this.status = status;
    }

    /**
     * Gets the week identifier for this milestone.
     * 
     * @return The week string (e.g., "Week 1")
     */
    public String getWeek()
    {
        return week;
    }

    /**
     * Sets the week identifier for this milestone.
     * 
     * @param week The new week identifier to set
     */
    public void setWeek(String week)
    {
        this.week = week;
    }

    /**
     * Gets the task description for this milestone.
     * 
     * @return The task description string
     */
    public String getTask()
    {
        return task;
    }

    /**
     * Sets the task description for this milestone.
     * 
     * @param task The new task description to set
     */
    public void setTask(String task)
    {
        this.task = task;
    }

    /**
     * Gets the current status of this milestone.
     * 
     * @return The status string (e.g., "Pending", "Completed")
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the status of this milestone.
     * 
     * @param status The new status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Returns a string representation of this Milestone.
     * Format: week:task (useful for saving to file)
     * 
     * @return A formatted string containing the week and task
     */
    @Override
    public String toString()
    {
        return week + ":" + task;
    }
}
