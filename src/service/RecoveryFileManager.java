package service;

import domain.Milestone;
import domain.RecoveryPlan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The RecoveryFileManager class handles reading and writing RecoveryPlan data to a text file.
 * It uses a pipe-delimited format for simplicity and follows the data access patterns
 * established in this project.
 * 
 * File Format:
 * StudentID|CourseID|Recommendation|Status|Milestone1Week:Milestone1Task,Milestone2Week:Milestone2Task
 * 
 * Example:
 * S001|C201|Focus on fundamentals|Active|Week 1:Complete exercises,Week 2:Practice problems
 */
public class RecoveryFileManager
{
    // The path to the file where recovery plans are stored
    // Using the data folder as specified in the requirements
    private final String RECOVERY_FILE_PATH = "data/recovery_plans.txt";

    /**
     * Saves a recovery plan to the file by appending it to the end.
     * If the file doesn't exist, it will be created automatically.
     * 
     * The format used is pipe-delimited:
     * StudentID|CourseID|Recommendation|Status|Week1:Task1,Week2:Task2
     * 
     * @param plan The RecoveryPlan object to save to the file
     * @return true if the save was successful, false otherwise
     */
    public boolean savePlan(RecoveryPlan plan)
    {
        // Use try-with-resources to ensure the FileWriter is closed properly
        // The second parameter (true) enables append mode so we don't overwrite existing data
        try (FileWriter writer = new FileWriter(RECOVERY_FILE_PATH, true))
        {
            // Build the line to write to the file
            // Start with the main plan fields separated by pipes
            StringBuilder line = new StringBuilder();
            line.append(plan.getStudentId());
            line.append("|");
            line.append(plan.getCourseId());
            line.append("|");
            line.append(plan.getRecommendation());
            line.append("|");
            line.append(plan.getStatus());
            line.append("|");

            // Now add the milestones
            // Each milestone is formatted as Week:Task and separated by commas
            ArrayList<Milestone> milestones = new ArrayList<>(plan.getMilestones());
            for (int i = 0; i < milestones.size(); i++)
            {
                Milestone milestone = milestones.get(i);
                // Add the milestone in Week:Task format
                line.append(milestone.getWeek());
                line.append(":");
                line.append(milestone.getTask());

                // Add a comma between milestones, but not after the last one
                if (i < milestones.size() - 1)
                {
                    line.append(",");
                }
            }

            // Add a newline at the end so the next plan goes on a new line
            line.append("\n");

            // Write the line to the file
            writer.write(line.toString());

            // Print a message for debugging purposes (following the pattern in StudentDAO)
            System.out.println("Successfully saved recovery plan for student: " + plan.getStudentId());

            return true;
        }
        catch (IOException e)
        {
            // Print error message if something goes wrong
            System.out.println("Error saving recovery plan: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all recovery plans from the file and returns them as an ArrayList.
     * Each line in the file represents one recovery plan.
     * 
     * The expected format is:
     * StudentID|CourseID|Recommendation|Status|Week1:Task1,Week2:Task2
     * 
     * @return An ArrayList of RecoveryPlan objects loaded from the file.
     *         Returns an empty list if the file doesn't exist or if there's an error.
     */
    public ArrayList<RecoveryPlan> loadPlans()
    {
        // Create an empty ArrayList to hold the plans we load
        ArrayList<RecoveryPlan> plans = new ArrayList<>();

        // First, check if the file exists
        // If it doesn't exist, return an empty list (no plans yet)
        File file = new File(RECOVERY_FILE_PATH);
        if (!file.exists())
        {
            System.out.println("Recovery plans file does not exist yet. Returning empty list.");
            return plans;
        }

        // Use try-with-resources to read the file
        // BufferedReader is efficient for reading text files line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(RECOVERY_FILE_PATH)))
        {
            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null)
            {
                // Skip empty lines
                if (line.trim().isEmpty())
                {
                    continue;
                }

                // Split the line by the pipe character to get each field
                String[] parts = line.split("\\|");

                // We need at least 4 parts: studentId, courseId, recommendation, status
                // The 5th part (milestones) is optional
                if (parts.length >= 4)
                {
                    // Extract the main fields
                    String studentId = parts[0].trim();
                    String courseId = parts[1].trim();
                    String recommendation = parts[2].trim();
                    String status = parts[3].trim();

                    // Create a new RecoveryPlan object
                    RecoveryPlan plan = new RecoveryPlan(studentId, courseId, recommendation, status);

                    // Now parse the milestones if they exist (5th field)
                    if (parts.length >= 5 && !parts[4].trim().isEmpty())
                    {
                        String milestonesStr = parts[4].trim();

                        // Split the milestones by comma
                        String[] milestoneArray = milestonesStr.split(",");

                        // Parse each milestone
                        for (int i = 0; i < milestoneArray.length; i++)
                        {
                            String milestoneStr = milestoneArray[i].trim();

                            // Each milestone is in Week:Task format
                            // Split by colon to get week and task
                            int colonIndex = milestoneStr.indexOf(":");
                            if (colonIndex > 0)
                            {
                                String week = milestoneStr.substring(0, colonIndex);
                                String task = milestoneStr.substring(colonIndex + 1);

                                // Create a new Milestone with default status "Pending"
                                Milestone milestone = new Milestone(week, task, "Pending");

                                // Add it to the plan
                                plan.addMilestone(milestone);
                            }
                        }
                    }

                    // Add the completed plan to our list
                    plans.add(plan);
                }
            }

            System.out.println("Successfully loaded " + plans.size() + " recovery plans.");
        }
        catch (IOException e)
        {
            // Print error message if something goes wrong
            System.out.println("Error loading recovery plans: " + e.getMessage());
        }

        return plans;
    }
}
