package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RecoveryService provides methods to identify students who need recovery
 * based on their exam and assignment scores and their recovery eligibility status.
 * 
 * This service reads data from:
 * - student_enrollment_information.csv: Contains ExamScore and AssignmentScore
 * - student_information.csv: Contains RecoveryEligibility status
 */
public class RecoveryService {
    
    // File paths for the data sources
    private final String ENROLLMENT_FILE_PATH = "data/student_enrollment_information.csv";
    private final String STUDENT_FILE_PATH = "data/student_information.csv";
    
    // The pass mark threshold - scores below this are considered failing
    private static final int PASS_MARK = 40;
    
    /**
     * Gets a list of students who need recovery based on their scores
     * and recovery eligibility.
     * 
     * Logic:
     * 1. Read student_enrollment_information.csv
     * 2. Flag students with ExamScore < 40 OR AssignmentScore < 40 as "At Risk"
     * 3. Cross-reference with student_information.csv to check RecoveryEligibility
     * 4. Only include students whose RecoveryEligibility is "True" or "Eligible"
     * 
     * @return ArrayList of FailedStudent objects containing StudentID, CourseID, and FailedComponent
     */
    public ArrayList<FailedStudent> getStudentsNeedingRecovery() {
        // Step 1: Create a list to store the final results
        ArrayList<FailedStudent> failedStudents = new ArrayList<>();
        
        // Step 2: Load the recovery eligibility data from student_information.csv
        // This creates a map of StudentID -> RecoveryEligibility for quick lookup
        Map<String, String> eligibilityMap = loadRecoveryEligibility();
        
        // Step 3: Read the enrollment data and identify students at risk
        // Parse each enrollment record and check if scores are below the pass mark
        ArrayList<String[]> enrollments = readCSV(ENROLLMENT_FILE_PATH);
        
        // Step 4: Process each enrollment record
        // CSV columns: EnrollmentID(0), StudentID(1), CourseID(2), Year(3), Semester(4), ExamScore(5), AssignmentScore(6)
        for (String[] enrollment : enrollments) {
            // Ensure we have all required columns
            if (enrollment.length < 7) {
                continue;
            }
            
            // Extract relevant fields from the enrollment record
            String studentId = enrollment[1].trim();
            String courseId = enrollment[2].trim();
            String examScoreStr = enrollment[5].trim();
            String assignmentScoreStr = enrollment[6].trim();
            
            // Step 5: Parse the scores and check if they are below the pass mark
            // Handle potential parsing errors for non-numeric values
            int examScore = parseScore(examScoreStr);
            int assignmentScore = parseScore(assignmentScoreStr);
            
            // Step 6: Check if the student is "At Risk" (failed exam or assignment)
            boolean failedExam = examScore < PASS_MARK;
            boolean failedAssignment = assignmentScore < PASS_MARK;
            
            // If the student hasn't failed anything, skip to next enrollment
            if (!failedExam && !failedAssignment) {
                continue;
            }
            
            // Step 7: Cross-reference with recovery eligibility
            // Only include students whose RecoveryEligibility is "True" or "Eligible"
            String eligibility = eligibilityMap.get(studentId);
            if (!isEligibleForRecovery(eligibility)) {
                continue;
            }
            
            // Step 8: Create FailedStudent entries for each failed component
            // A student can fail both exam and assignment, so we may create two entries
            if (failedExam) {
                FailedStudent failed = new FailedStudent(studentId, courseId, "Exam");
                failedStudents.add(failed);
            }
            
            if (failedAssignment) {
                FailedStudent failed = new FailedStudent(studentId, courseId, "Assignment");
                failedStudents.add(failed);
            }
        }
        
        // Return the list of students needing recovery
        return failedStudents;
    }
    
    /**
     * Loads recovery eligibility data from student_information.csv.
     * Creates a map of StudentID to RecoveryEligibility for quick lookup.
     * 
     * CSV columns: StudentID(0), FirstName(1), LastName(2), ProgramID(3), Email(4), RecoveryEligibility(5)
     * 
     * @return Map with StudentID as key and RecoveryEligibility as value
     */
    private Map<String, String> loadRecoveryEligibility() {
        Map<String, String> eligibilityMap = new HashMap<>();
        
        // Read the student information CSV file
        ArrayList<String[]> students = readCSV(STUDENT_FILE_PATH);
        
        // Parse each student record and extract StudentID and RecoveryEligibility
        for (String[] student : students) {
            // Ensure we have the RecoveryEligibility column (index 5)
            if (student.length >= 6) {
                String studentId = student[0].trim();
                String eligibility = student[5].trim();
                eligibilityMap.put(studentId, eligibility);
            }
        }
        
        return eligibilityMap;
    }
    
    /**
     * Helper method to read a CSV file and return its contents as a list of string arrays.
     * Each array represents one row, with each element being a column value.
     * 
     * The first row (header) is skipped as it contains column names.
     * 
     * @param filePath Path to the CSV file to read
     * @return ArrayList of string arrays, each representing a row of data
     */
    private ArrayList<String[]> readCSV(String filePath) {
        ArrayList<String[]> records = new ArrayList<>();
        
        // Use try-with-resources to ensure the BufferedReader is closed properly
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // Skip the header row (first line contains column names)
            br.readLine();
            
            // Read each subsequent line and split by comma
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split the line by comma to get individual column values
                String[] values = line.split(",");
                records.add(values);
            }
        } catch (IOException e) {
            // Log error message following the pattern used in other DAO classes
            System.err.println("ERROR: Failed to read CSV file at " + filePath + ": " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * Helper method to parse a score string to an integer.
     * Returns 0 if the string cannot be parsed (handles empty or non-numeric values).
     * 
     * @param scoreStr The score as a string
     * @return The score as an integer, or 0 if parsing fails
     */
    private int parseScore(String scoreStr) {
        try {
            return Integer.parseInt(scoreStr.trim());
        } catch (NumberFormatException e) {
            // If the score cannot be parsed, treat it as 0 (which would be a failing score)
            return 0;
        }
    }
    
    /**
     * Checks if a student is eligible for recovery based on their eligibility status.
     * A student is eligible if their RecoveryEligibility value is "True" or "Eligible".
     * 
     * @param eligibility The eligibility status from the student_information.csv
     * @return true if the student is eligible for recovery, false otherwise
     */
    private boolean isEligibleForRecovery(String eligibility) {
        if (eligibility == null) {
            return false;
        }
        
        // Check if eligibility is "True" or "Eligible" (case-insensitive)
        String eligibilityLower = eligibility.toLowerCase().trim();
        return eligibilityLower.equals("true") || eligibilityLower.equals("eligible");
    }
}
